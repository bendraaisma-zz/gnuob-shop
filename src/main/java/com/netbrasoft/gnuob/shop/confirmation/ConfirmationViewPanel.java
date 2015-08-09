package com.netbrasoft.gnuob.shop.confirmation;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class ConfirmationViewPanel extends Panel {

   class ConfirmationViewFragement extends Fragment {

      private static final long serialVersionUID = 1948798072333311170L;

      public ConfirmationViewFragement() {
         super("confirmationCustomerViewFragement", "confirmationViewFragement", ConfirmationViewPanel.this, ConfirmationViewPanel.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
         Shopper shopper = shopperDataProvider.find(new Shopper());

         add(new Label("orderId", Model.of(shopper.getOrderId())));
         add(new Label("buyerEmail", Model.of(shopper.getContract().getCustomer().getBuyerEmail())));

         shopper.setOrderId(null);

         shopperDataProvider.merge(shopper);

         super.onInitialize();
      }
   }

   private static final long serialVersionUID = 4629799686885772339L;

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   public ConfirmationViewPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }
}
