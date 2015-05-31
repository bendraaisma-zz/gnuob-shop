package com.netbrasoft.gnuob.shop.confirmation;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class ConfirmationPanel extends Panel {

   private static final long serialVersionUID = -305453593387396902L;

   public ConfirmationPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }

   private ConfirmationViewPanel confirmationViewPanel = new ConfirmationViewPanel("confirmationViewPanel", (IModel<Shopper>) getDefaultModel());

   @Override
   protected void onInitialize() {
      add(confirmationViewPanel.add(confirmationViewPanel.new ConfirmationViewFragement()).setOutputMarkupId(true));
      super.onInitialize();
   }

}
