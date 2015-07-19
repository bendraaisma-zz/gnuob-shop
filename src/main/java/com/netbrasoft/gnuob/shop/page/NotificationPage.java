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

@MountPath("notification.html")
public class NotificationPage extends BasePage {

   private static final String PAYPAL_COM = "paypal.com";

   private static final String PAGSEGURO_UOL_COM_BR = "pagseguro.uol.com.br";

   private static final long serialVersionUID = -2980296583669048069L;

   private static Logger LOGGER = LoggerFactory.getLogger(NotificationPage.class);

   @SpringBean(name = "OrderDataProvider", required = true)
   private GenericOrderCheckoutDataProvider<Order> orderDataProvider;

   private void doNotificationRequest() {
      final HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();

      switch (request.getHeader("Host")) {
      case PAGSEGURO_UOL_COM_BR:
         LOGGER.info("Retrieve notifcation request from PagSeguro.");
         doPagSeguroNotification();
         break;

      case PAYPAL_COM : {
         LOGGER.info("Retrieve notifcation request from PayPal.");
         doPayPalNotification();
         break;
      }

      default:
         LOGGER.warn("Retrieve notifcation request from invalid host [{}].", request.getRemoteHost());
         break;
      }
   }

   private void doPagSeguroNotification() {
      final HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();

      final String notificationCode = request.getParameter("notificationCode");

      if(notificationCode != null) {
         LOGGER.debug("Retrieve notifcation request from PagSeguro with notificationCode parameter value = [{}]", notificationCode);

         Order order = new Order();
         order.setNotificationId(notificationCode);

         order = orderDataProvider.doNotification(order);
      } else {
         LOGGER.warn("Retrieve notifcation request from PagSeguro without a notificationCode parameter");
      }
   }

   private void doPayPalNotification() {

   }

   private boolean isSignedIn() {
      return AuthenticatedWebSession.get().isSignedIn();
   }

   @Override
   protected void onInitialize() {
      if (!isSignedIn()) {
         signIn(System.getProperty("gnuob.site.username", "guest"), System.getProperty("gnuob.site.password", "guest"));
      }

      orderDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
      orderDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
      orderDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
      orderDataProvider.setType(new Order());
      orderDataProvider.getType().setActive(true);
      orderDataProvider.setOrderBy(OrderBy.NONE);
      orderDataProvider.setCheckOut(CheckOut.PAGSEGURO);

      super.onInitialize();

      doNotificationRequest();
   }

   private boolean signIn(String username, String password) {
      return AuthenticatedWebSession.get().signIn(username, password);
   }
}
