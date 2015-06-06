package com.netbrasoft.gnuob.shop.wishlist;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.netbrasoft.gnuob.shop.shopper.Shopper;

public class WishListViewPanel extends Panel {

   private static final long serialVersionUID = -4406441947235524118L;

   public WishListViewPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }

}
