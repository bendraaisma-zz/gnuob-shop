package com.netbrasoft.gnuob.shop.page;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

   private static final String GNUOB_SITE_PAYPAL_NOTIFICATION_VALIDATION_DISABLED = "gnuob.site.paypal.notification.validation.disabled";
   private static final String PAYPAL_COM_CGI_BIN_WEBSCR_VALUE = "https://www.sandbox.paypal.com/cgi-bin/webscr";
   private static final String PAYPAL_COM_CGI_BIN_WEBSCR_PROPERTY = "gnuob.site.paypal.cgi.bin.webscr";


   private static final long serialVersionUID = -2980296583669048069L;

   private static Logger LOGGER = LoggerFactory.getLogger(PayPalNotificationPage.class);

   @SpringBean(name = "OrderDataProvider", required = true)
   private GenericOrderCheckoutDataProvider<Order> orderDataProvider;

   private void doPayPalNotification() {
      final HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();

      final Map<String, String[]> parameterMap = request.getParameterMap();

      if ("POST".equalsIgnoreCase(request.getMethod()) && !parameterMap.isEmpty()) {
         try {
            LOGGER.info("Retrieve notifcation request from PayPal with parameters.");

            final StringBuilder payload = new StringBuilder();
            payload.append("cmd=_notify-validate");

            for (final Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
               payload.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue()[0], parameterMap.get("charset")[0]));
            }

            final HttpsURLConnection connection = (HttpsURLConnection) new URL(System.getProperty(PAYPAL_COM_CGI_BIN_WEBSCR_PROPERTY, PAYPAL_COM_CGI_BIN_WEBSCR_VALUE)).openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(payload.toString().length()));
            connection.setRequestProperty("Host", new URL(System.getProperty(PAYPAL_COM_CGI_BIN_WEBSCR_PROPERTY, PAYPAL_COM_CGI_BIN_WEBSCR_VALUE)).getHost());

            final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(payload.toString());
            writer.flush();
            writer.close();

            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
               final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8.name()));

               final String response = reader.readLine();
               reader.close();

               if ("VERIFIED".equals(response)) {
                  Order order = new Order();
                  order.setNotificationId(parameterMap.get("txn_id")[0]);
                  order = orderDataProvider.doNotification(order);
               } else {
                  LOGGER.warn("Retrieve notifcation request from PayPal but it isn't a valid request. ");

                  if("TRUE".equalsIgnoreCase(System.getProperty(GNUOB_SITE_PAYPAL_NOTIFICATION_VALIDATION_DISABLED, "false"))) {
                     Order order = new Order();
                     order.setNotificationId(parameterMap.get("txn_id")[0]);
                     order = orderDataProvider.doNotification(order);
                  }
               }

            }
         } catch (final IOException e) {
            LOGGER.warn("Retrieve notifcation request from PayPal but can't send a validation request. ", e);
         }
      } else {
         LOGGER.warn("Retrieve notifcation request from PayPal without a notification parameter or not a POST method.");
      }
   }

   private boolean isSignedIn() {
      return AuthenticatedWebSession.get().isSignedIn();
   }

   @Override
   protected void onInitialize() {
      if (!isSignedIn()) {
         final String site = getRequest().getClientUrl().getHost();
         signIn(System.getProperty("gnuob." + site + ".username", "guest"), System.getProperty("gnuob." + site + ".password", "guest"));
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
