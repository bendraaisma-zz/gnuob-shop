package com.netbrasoft.gnuob.shop.account;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.Contract;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.authentication.AuthorizationPanel;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class AccountViewPanel extends Panel {

   class AccountViewFragement extends Fragment {

      private static final long serialVersionUID = 1948798072333311170L;

      public AccountViewFragement() {
         super("accountCustomerViewFragement", "accountViewFragement", AccountViewPanel.this, AccountViewPanel.this.getDefaultModel());
      }

      private List<Locale> getSortedISOCountries() {
         SortedMap<String, Locale> sortedISOCountries = new TreeMap<String, Locale>();
         String[] locales = Locale.getISOCountries();

         for (String countryCode : locales) {
            Locale locale = new Locale("", countryCode);
            sortedISOCountries.put(locale.getDisplayCountry(), locale);
         }

         return Arrays.asList(sortedISOCountries.values().toArray(new Locale[sortedISOCountries.size()]));
      }

      @Override
      protected void onInitialize() {
         BootstrapForm<Contract> customerEditForm = new BootstrapForm<Contract>("customerEditForm");
         customerEditForm.setModel(new CompoundPropertyModel<Contract>(Model.of(shopperDataProvider.find(new Shopper()).getContract())));
         customerEditForm.add(new RequiredTextField<String>("customer.buyerEmail").setLabel(Model.of(getString("buyerEmailMessage"))).add(EmailAddressValidator.getInstance()).add(StringValidator.maximumLength(40)));
         customerEditForm.add(new RequiredTextField<String>("customer.firstName").setLabel(Model.of(getString("firstNameMessage"))).add(StringValidator.maximumLength(40)));
         customerEditForm.add(new RequiredTextField<String>("customer.lastName").setLabel(Model.of(getString("lastNameMessage"))).add(StringValidator.maximumLength(40)));
         customerEditForm.add(new TextField<String>("customer.address.phone").add(StringValidator.maximumLength(40)));
         customerEditForm.add(new RequiredTextField<String>("customer.address.street1").setLabel(Model.of(getString("street1Message"))).add(StringValidator.maximumLength(40)));
         customerEditForm.add(new TextField<String>("customer.address.street2").add(StringValidator.maximumLength(40)));
         customerEditForm.add(new DropDownChoice<Locale>("customer.address.country", getSortedISOCountries(), new ChoiceRenderer<Locale>("displayCountry", "")).setRequired(true).setLabel(Model.of(getString("countryNameMessage"))));
         customerEditForm.add(new RequiredTextField<String>("customer.address.cityName").setLabel(Model.of(getString("cityNameMessage"))).add(StringValidator.maximumLength(40)));
         customerEditForm.add(new RequiredTextField<String>("customer.address.postalCode").setLabel(Model.of(getString("postalCodeMessage"))).add(StringValidator.maximumLength(15)));
         customerEditForm.add(new RequiredTextField<String>("customer.address.stateOrProvince").setLabel(Model.of(getString("stateOrProvinceMessage"))).add(StringValidator.maximumLength(40)));

         customerEditForm.add(new SaveAjaxButton(customerEditForm).setOutputMarkupId(true));
         add(customerEditForm.setOutputMarkupId(true));
         super.onInitialize();
      }
   }

   class LoginViewFragement extends Fragment {
      private static final long serialVersionUID = 1193409377850497931L;

      public LoginViewFragement() {
         super("accountCustomerViewFragement", "loginViewFragement", AccountViewPanel.this, AccountViewPanel.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
         AuthorizationPanel authorizationPanel = new AuthorizationPanel("authorizationPanel", (IModel<Shopper>) AccountViewPanel.this.getDefaultModel());
         add(authorizationPanel);
         super.onInitialize();
      }
   }

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class SaveAjaxButton extends AjaxButton {

      private static final long serialVersionUID = 2695394292963384938L;

      public SaveAjaxButton(Form<Contract> form) {
         super("save", form);
      }

      @Override
      protected void onError(AjaxRequestTarget target, Form<?> form) {
         form.add(new TooltipValidation());
         target.add(form);
      }

      @Override
      protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
         try {
            saveContract(form);
         } catch (RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
         }
         throw new RedirectToUrlException("account.html");
      }

      private void saveContract(Form<?> form) {
         Contract contract = (Contract) form.getDefaultModelObject();
         Shopper shopper = shopperDataProvider.find(new Shopper());

         contract = contractDataProvider.merge(contract);
         shopper.setContract(contractDataProvider.findById(contract));

         shopperDataProvider.merge(shopper);
      }
   }

   private static final Logger LOGGER = LoggerFactory.getLogger(AccountViewPanel.class);

   private static final long serialVersionUID = -4406441947235524118L;

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   @SpringBean(name = "ContractDataProvider", required = true)
   private GenericTypeDataProvider<Contract> contractDataProvider;

   public AccountViewPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
      if (!shopperDataProvider.find(new Shopper()).loggedIn()) {
         removeAll();
         add(new LoginViewFragement());
      }

      contractDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
      contractDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
      contractDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
      contractDataProvider.setType(new Contract());
      contractDataProvider.getType().setActive(true);
      contractDataProvider.setOrderBy(OrderBy.NONE);

      super.onInitialize();
   }
}
