package com.netbrasoft.gnuob.shop.page;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.ORDER_DATA_PROVIDER_NAME;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.order.IGenericOrderCheckoutDataProvider;
import com.netbrasoft.gnuob.api.order.OrderDataProvider.PaymentProviderEnum;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;

@MountPath(PagseguroNotificationPage.PAGSEGURO_NOTIFICATIONS_VALUE)
public class PagseguroNotificationPage extends BasePage {

  private static final String NOTIFICATION_CODE = "notificationCode";

  private static final String POST = "POST";

  protected static final String PAGSEGURO_NOTIFICATIONS_VALUE = "pagseguro_notifications";

  private static final long serialVersionUID = -2980296583669048069L;

  private static final Logger LOGGER = LoggerFactory.getLogger(PagseguroNotificationPage.class);

  @SpringBean(name = ORDER_DATA_PROVIDER_NAME, required = true)
  private IGenericOrderCheckoutDataProvider<Order> orderDataProvider;

  private void doPagSeguroNotification() {
    final HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
    final String notificationCode = request.getParameter(NOTIFICATION_CODE);
    if (POST.equalsIgnoreCase(request.getMethod()) && notificationCode != null) {
      LOGGER.info("Retrieve notifcation request from PagSeguro.");
      Order order = new Order();
      order.setActive(true);
      order.setNotificationId(notificationCode);
      order = orderDataProvider.doNotification(order);
    } else {
      LOGGER.warn(
          "Retrieve notifcation request from PagSeguro without a notificationCode parameter or not a POST method.");
    }
  }

  private boolean isSignedIn() {
    return AuthenticatedWebSession.get().isSignedIn();
  }

  @Override
  protected void onInitialize() {
    if (!isSignedIn()) {
      final String site = getRequest().getClientUrl().getHost();
      signIn(System.getProperty("gnuob." + site + ".username", "guest"),
          System.getProperty("gnuob." + site + ".password", "guest"));
    }
    orderDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    orderDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    orderDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    orderDataProvider.setType(new Order());
    orderDataProvider.getType().setActive(true);
    orderDataProvider.setOrderBy(OrderBy.NONE);
    orderDataProvider.setPaymentProvider(PaymentProviderEnum.PAGSEGURO);
    super.onInitialize();
    doPagSeguroNotification();
  }

  private boolean signIn(final String username, final String password) {
    return AuthenticatedWebSession.get().signIn(username, password);
  }
}
