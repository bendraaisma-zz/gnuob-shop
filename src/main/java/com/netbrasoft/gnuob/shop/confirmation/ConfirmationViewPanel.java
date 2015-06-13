package com.netbrasoft.gnuob.shop.confirmation;

import java.text.NumberFormat;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;
import com.netbrasoft.gnuob.shop.specification.SpecificationViewPanel;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class ConfirmationViewPanel extends SpecificationViewPanel {

   class ConfirmationViewFragement extends Fragment {

      private static final long serialVersionUID = 1948798072333311170L;

      public ConfirmationViewFragement() {
         super("confirmationCustomerViewFragement", "confirmationViewFragement", ConfirmationViewPanel.this, ConfirmationViewPanel.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
         add(offerRecordProductDataViewContainer.setOutputMarkupId(true));
         add(offerRecordDataviewContainer.setOutputMarkupId(true));
         add(new Label("totalDiscount", Model.of(NumberFormat.getCurrencyInstance().format(shopperDataProvider.find(new Shopper()).getChartTotalDiscount()))));
         add(new Label("total", Model.of(NumberFormat.getCurrencyInstance().format(shopperDataProvider.find(new Shopper()).getChartTotal()))));
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
