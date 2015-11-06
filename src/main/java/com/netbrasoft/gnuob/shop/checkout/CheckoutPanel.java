package com.netbrasoft.gnuob.shop.checkout;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.order.GenericOrderCheckoutDataProvider;
import com.netbrasoft.gnuob.api.order.OrderDataProvider;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;
import com.netbrasoft.gnuob.shop.shopper.ShopperDataProvider;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CheckoutPanel extends Panel {

  private static final String CHECKOUT_EMPTY_OR_EDIT_PANEL_ID = "checkoutEmptyOrEditPanel";

  private static final long serialVersionUID = 2034566325989232879L;

  private final CheckoutEmptyOrEditPanel checkoutEmptyOrEditPanel;

  @SpringBean(name = ShopperDataProvider.SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  @SpringBean(name = OrderDataProvider.ORDER_DATA_PROVIDER_NAME, required = true)
  private transient GenericOrderCheckoutDataProvider<Order> orderDataProvider;

  public CheckoutPanel(final String id, final IModel<Order> model) {
    super(id, model);
    checkoutEmptyOrEditPanel = new CheckoutEmptyOrEditPanel(CHECKOUT_EMPTY_OR_EDIT_PANEL_ID, (IModel<Order>) CheckoutPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    orderDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    orderDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    orderDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    orderDataProvider.setType(new Order());
    orderDataProvider.getType().setActive(true);
    orderDataProvider.setOrderBy(OrderBy.CREATION_Z_A);
    orderDataProvider.getType().setContract(shopperDataProvider.find(new Shopper()).getContract());
    if (orderDataProvider.size() > 0) {
      add(checkoutEmptyOrEditPanel.add(checkoutEmptyOrEditPanel.new CheckoutEditFragment()).setOutputMarkupId(true));
    } else {
      add(checkoutEmptyOrEditPanel.add(checkoutEmptyOrEditPanel.new CheckoutEmptyFragment()).setOutputMarkupId(true));
    }
    super.onInitialize();
  }
}
