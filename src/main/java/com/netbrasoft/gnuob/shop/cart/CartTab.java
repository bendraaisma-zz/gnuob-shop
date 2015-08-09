package com.netbrasoft.gnuob.shop.cart;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.shop.shopper.Shopper;

public class CartTab extends AbstractTab {

   private static final long serialVersionUID = 4835579949680085443L;

   public CartTab(final IModel<String> title) {
      super(title);
   }

   @Override
   public WebMarkupContainer getPanel(final String panelId) {
      return new CartPanel(panelId, Model.of(new Shopper()));
   }
}
