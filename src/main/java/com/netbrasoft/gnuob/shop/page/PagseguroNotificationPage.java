package com.netbrasoft.gnuob.shop.page;

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

@MountPath("pagseguro_notifications")
public class PagseguroNotificationPage extends BasePage {

   private static final long serialVersionUID = -2980296583669048069L;

   private static Logger LOGGER = LoggerFactory.getLogger(PagseguroNotificationPage.class);

   @SpringBean(name = "OrderDataProvider", required = true)
   private GenericOrderCheckoutDataProvider<Order> orderDataProvider;

   private void doPagSeguroNotification() {
      final HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();

      final String notificationCode = request.getParameter("notificationCode");

      if("POST".equalsIgnoreCase(request.getMethod()) && notificationCode != null) {
         LOGGER.info("Retrieve notifcation request from PagSeguro.");

         Order order = new Order();
         order.setNotificationId(notificationCode);

         order = orderDataProvider.doNotification(order);
      } else {
         LOGGER.warn("Retrieve notifcation request from PagSeguro without a notificationCode parameter or not a POST method.");
      }
   }

   private boolean isSignedIn() {
      return AuthenticatedWebSession.get().isSignedIn();
   }

   @Override
   protected void onInitialize() {
      if (!isSignedIn()) {
         final String host =  getRequest().getClientUrl().getHost();
         signIn(System.getProperty("gnuob." + host + ".username", "guest"), System.getProperty("gnuob." + host + ".password", "guest"));
      }

      orderDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
      orderDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
      orderDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
      orderDataProvider.setType(new Order());
      orderDataProvider.getType().setActive(true);
      orderDataProvider.setOrderBy(OrderBy.NONE);
      orderDataProvider.setCheckOut(CheckOut.PAGSEGURO);

      super.onInitialize();

      doPagSeguroNotification();
   }

   private boolean signIn(String username, String password) {
      return AuthenticatedWebSession.get().signIn(username, password);
   }
}
