package com.netbrasoft.gnuob.shop.specification;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.api.Contract;
import com.netbrasoft.gnuob.shop.cart.CartViewPanel;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class SpecificationViewPanel extends CartViewPanel {

   class SpecificationViewFragement extends Fragment {

      private static final long serialVersionUID = 9159244637681177882L;

      public SpecificationViewFragement() {
         super("specificationCustomerViewFragement", "specificationViewFragement", SpecificationViewPanel.this, SpecificationViewPanel.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
         Form<Contract> specificationEditForm = new Form<Contract>("specificationEditForm");
         Form<Contract> deliveryEditForm = new Form<Contract>("deliveryEditForm");
         specificationEditForm.setModel(new CompoundPropertyModel<Contract>(Model.of(shopperDataProvider.find(new Shopper()).getContract())));
         specificationEditForm.add(new TextField<String>("contractId"));
         specificationEditForm.add(new TextField<String>("customer.firstName"));
         specificationEditForm.add(new TextField<String>("customer.lastName"));

         deliveryEditForm.setModel(new CompoundPropertyModel<Contract>(Model.of(shopperDataProvider.find(new Shopper()).getContract())));
         deliveryEditForm.add(new TextField<String>("customer.address.street1"));
         deliveryEditForm.add(new TextField<String>("customer.address.street2"));
         deliveryEditForm.add(new TextField<String>("customer.address.countryName"));
         deliveryEditForm.add(new TextField<String>("customer.address.cityName"));
         deliveryEditForm.add(new TextField<String>("customer.address.postalCode"));
         deliveryEditForm.add(new TextField<String>("customer.address.stateOrProvince"));

         add(specificationEditForm.setOutputMarkupId(true));
         add(deliveryEditForm.setOutputMarkupId(true));
         add(offerRecordProductDataViewContainer.setOutputMarkupId(true));
         add(offerRecordDataviewContainer.setOutputMarkupId(true));
         super.onInitialize();
      }
   }

   private static final long serialVersionUID = 293941244262646336L;

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   public SpecificationViewPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }
}
