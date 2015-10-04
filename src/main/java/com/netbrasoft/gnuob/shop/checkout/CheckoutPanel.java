package com.netbrasoft.gnuob.shop.checkout;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CheckoutPanel extends Panel {

  private static final long serialVersionUID = 2034566325989232879L;

  private final CheckoutViewPanel checkoutViewPanel;

  @SpringBean(name = "ShopperDataProvider", required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  @SpringBean(name = "OfferDataProvider", required = true)
  private GenericTypeDataProvider<Offer> offerDataProvider;

  public CheckoutPanel(final String id, final IModel<Shopper> model) {
    super(id, model);
    checkoutViewPanel = new CheckoutViewPanel("checkoutViewPanel", model);
  }

  @Override
  protected void onInitialize() {
    offerDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    offerDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    offerDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    offerDataProvider.setType(new Offer());
    offerDataProvider.getType().setActive(true);
    offerDataProvider.setOrderBy(OrderBy.NONE);
    offerDataProvider.getType().setContract(shopperDataProvider.find((Shopper) getDefaultModelObject()).getContract());

    if (offerDataProvider.size() > 0) {
      add(checkoutViewPanel.add(checkoutViewPanel.new CheckoutViewFragment()).setOutputMarkupId(true));
    } else {
      add(checkoutViewPanel.add(checkoutViewPanel.new EmptyCheckoutViewFragment()).setOutputMarkupId(true));
    }
    super.onInitialize();
  }
}
