package com.netbrasoft.gnuob.shop.checkout;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.netbrasoft.gnuob.shop.shopper.Shopper;

public class CheckoutViewPanel extends Panel {

   private static final long serialVersionUID = -4406441947235524118L;

   public CheckoutViewPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }

}
