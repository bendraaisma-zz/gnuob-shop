package com.netbrasoft.gnuob.shop.cart;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class CartPanel extends Panel {

   private static final long serialVersionUID = 2034566325989232879L;

   private CartViewPanel cartViewPanel = new CartViewPanel("cartViewPanel", (IModel<Shopper>) getDefaultModel());

   public CartPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
      add(cartViewPanel.add(cartViewPanel.new OfferRecordViewFragement().setOutputMarkupId(true)).setOutputMarkupId(true));
      super.onInitialize();
   }
}
