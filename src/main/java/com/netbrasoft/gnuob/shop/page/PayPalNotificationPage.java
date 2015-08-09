package com.netbrasoft.gnuob.shop.page;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.order.GenericOrderCheckoutDataProvider;
import com.netbrasoft.gnuob.api.order.OrderDataProvider.CheckOut;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;

@MountPath("paypal_notifications")
public class PayPalNotificationPage extends BasePage {

   private static final long serialVersionUID = -2980296583669048069L;

   private static Logger LOGGER = LoggerFactory.getLogger(PayPalNotificationPage.class);

   @SpringBean(name = "OrderDataProvider", required = true)
   private GenericOrderCheckoutDataProvider<Order> orderDataProvider;

   private void doPayPalNotification() {
      final HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();

      final String method = request.getMethod();
      final Map<String, String[]> parameterMap = request.getParameterMap();

      if ("POST".equalsIgnoreCase(method) && !parameterMap.isEmpty()) {
         try {
            LOGGER.debug("Retrieve notifcation request from PayPal with parameters = [{}]", parameterMap.size());

            final HttpsURLConnection connection = (HttpsURLConnection) new URL("https://www.paypal.com/cgi-bin/webscr").openConnection();

            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", request.getContentType());
            connection.setDoOutput(true);

            final DataOutputStream writer = new DataOutputStream(connection.getOutputStream());

            writer.writeBytes("cmd=_notify-validate");

            for (final Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
               writer.writeBytes("&" + entry.getKey() + "=" + entry.getValue()[0]);
            }

            writer.flush();
            writer.close();

            if (connection.getResponseCode() == 200) {
               final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

               final String response = reader.readLine();

               if ("VERIFIED".equals(response)) {
                  Order order = new Order();
                  order.setNotificationId(parameterMap.get("txn_id")[0]);

                  order = orderDataProvider.doNotification(order);
               } else {
                  LOGGER.warn("Retrieve notifcation request from PayPal but isn't a valid request ");
               }

               reader.close();
            }
         } catch (final IOException e) {
            LOGGER.warn("Retrieve notifcation request from PayPal but can't send a validation request ", e);
         }
      } else {
         LOGGER.warn("Retrieve notifcation request from PayPal without a notificationCode parameter or not a POST method.");
      }
   }

   private boolean isSignedIn() {
      return AuthenticatedWebSession.get().isSignedIn();
   }

   @Override
   protected void onInitialize() {
      if (!isSignedIn()) {
         final String host = getRequest().getClientUrl().getHost();
         signIn(System.getProperty("gnuob." + host + ".username", "guest"), System.getProperty("gnuob." + host + ".password", "guest"));
      }

      orderDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
      orderDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
      orderDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
      orderDataProvider.setType(new Order());
      orderDataProvider.getType().setActive(true);
      orderDataProvider.setOrderBy(OrderBy.NONE);
      orderDataProvider.setCheckOut(CheckOut.PAY_PAL);

      super.onInitialize();

      doPayPalNotification();
   }

   private boolean signIn(String username, String password) {
      return AuthenticatedWebSession.get().signIn(username, password);
   }
}
