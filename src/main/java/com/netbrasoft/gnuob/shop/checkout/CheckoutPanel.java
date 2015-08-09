package com.netbrasoft.gnuob.shop.checkout;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class CheckoutPanel extends Panel {

   private static final long serialVersionUID = 2034566325989232879L;

   private CheckoutViewPanel checkoutViewPanel = new CheckoutViewPanel("checkoutViewPanel", (IModel<Shopper>) getDefaultModel());

   public CheckoutPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
      add(checkoutViewPanel.add(checkoutViewPanel.new CheckoutViewFragment()).setOutputMarkupId(true));
      super.onInitialize();
   }
}
