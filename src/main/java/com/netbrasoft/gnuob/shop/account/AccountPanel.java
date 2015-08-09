package com.netbrasoft.gnuob.shop.account;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class AccountPanel extends Panel {

   private static final long serialVersionUID = 2034566325989232879L;

   private AccountViewPanel accountViewPanel = new AccountViewPanel("accountViewPanel", (IModel<Shopper>) getDefaultModel());

   public AccountPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
      add(accountViewPanel.add(accountViewPanel.new AccountViewFragement()).setOutputMarkupId(true));
      super.onInitialize();
   }
}
