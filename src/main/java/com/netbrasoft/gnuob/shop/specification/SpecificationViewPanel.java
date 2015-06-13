package com.netbrasoft.gnuob.shop.specification;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.Address;
import com.netbrasoft.gnuob.api.Contract;
import com.netbrasoft.gnuob.api.Invoice;
import com.netbrasoft.gnuob.api.OfferRecord;
import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.OrderRecord;
import com.netbrasoft.gnuob.api.Shipment;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.api.order.GenericOrderCheckoutDataProvider;
import com.netbrasoft.gnuob.api.order.OrderDataProvider.CheckOut;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.cart.CartViewPanel;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.LoadingBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class SpecificationViewPanel extends CartViewPanel {

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class SaveAjaxButton extends BootstrapAjaxButton implements IAjaxIndicatorAware {

      private static final String VEIL_CART_LOADING = "veil-cart-loading";
      private static final String PAYPAL_CHECKOUT_URL_PROPERTY_VALUE = "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";
      private static final String PAGSEGURO_CHECKOUT_URL_PROPERTY_VALUE = "https://sandbox.pagseguro.uol.com.br/v2/checkout/payment.html?code=";
      private static final String PAYPAL_CHECKOUT_URL_PROPERTY = "paypal.checkout.url";
      private static final String PAGSEGURO_CHECKOUT_URL_PROPERTY = "pagseguro.checkout.url";
      private static final long serialVersionUID = 2695394292963384938L;

      public SaveAjaxButton(Form<Contract> form) {
         super("save", Model.of(SpecificationViewPanel.this.getString("payMessage")), form, Buttons.Type.Primary);
         setSize(Buttons.Size.Small);
         add(new LoadingBehavior(Model.of(SpecificationViewPanel.this.getString("directingToMessage"))));
      }

      @Override
      protected void onError(AjaxRequestTarget target, Form<?> form) {
         form.add(new TooltipValidation());
         target.add(form);
         target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(SpecificationViewPanel.this.getString("directingToMessage")))));
      }

      @Override
      protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
         saveContract((Contract) form.getDefaultModelObject());
         saveOrderAndDoCheckout();
      }

      private void saveContract(Contract contract) {
         Shopper shopper = shopperDataProvider.find(new Shopper());

         if (contract.getId() == 0) {
            contract = contractDataProvider.persist(contract);
         } else {
            contract = contractDataProvider.merge(contract);
         }

         shopper.setContract(contractDataProvider.findById(contract));
         shopperDataProvider.merge(shopper);
      }

      private Order saveOrder(Shopper shopper) {
         Order order = new Order();
         order.setActive(true);
         order.setInsuranceTotal(BigDecimal.ZERO);
         order.setHandlingTotal(BigDecimal.ZERO);
         order.setShippingDiscount(BigDecimal.ZERO);
         order.setInvoice(new Invoice());
         order.setShipment(new Shipment());
         order.getShipment().setAddress(new Address());
         order.getInvoice().setAddress(new Address());
         order.getShipment().getAddress().setStreet1(shopper.getContract().getCustomer().getAddress().getStreet1());
         order.getShipment().getAddress().setStreet2(shopper.getContract().getCustomer().getAddress().getStreet2());
         order.getShipment().getAddress().setCountry(shopper.getContract().getCustomer().getAddress().getCountry());
         order.getShipment().getAddress().setCityName(shopper.getContract().getCustomer().getAddress().getCityName());
         order.getShipment().getAddress().setPostalCode(shopper.getContract().getCustomer().getAddress().getPostalCode());
         order.getShipment().getAddress().setStateOrProvince(shopper.getContract().getCustomer().getAddress().getStateOrProvince());
         order.getShipment().getAddress().setPhone(shopper.getContract().getCustomer().getAddress().getPhone());
         order.getInvoice().getAddress().setStreet1(shopper.getContract().getCustomer().getAddress().getStreet1());
         order.getInvoice().getAddress().setStreet2(shopper.getContract().getCustomer().getAddress().getStreet2());
         order.getInvoice().getAddress().setCountry(shopper.getContract().getCustomer().getAddress().getCountry());
         order.getInvoice().getAddress().setCityName(shopper.getContract().getCustomer().getAddress().getCityName());
         order.getInvoice().getAddress().setPostalCode(shopper.getContract().getCustomer().getAddress().getPostalCode());
         order.getInvoice().getAddress().setStateOrProvince(shopper.getContract().getCustomer().getAddress().getStateOrProvince());
         order.getInvoice().getAddress().setPhone(shopper.getContract().getCustomer().getAddress().getPhone());

         for (OfferRecord offerRecord : shopper.getCart()) {
            OrderRecord orderRecord = new OrderRecord();
            orderRecord.setProduct(offerRecord.getProduct());
            orderRecord.setQuantity(offerRecord.getQuantity());
            order.getRecords().add(orderRecord);
         }

         order = orderDataProvider.persist(order);
         order.setContract(shopper.getContract());
         order = orderDataProvider.merge(order);
         order = orderDataProvider.doCheckout(orderDataProvider.findById(order));

         shopper.setContract(orderDataProvider.findById(order).getContract());
         return order;
      }

      private void saveOrderAndDoCheckout() {
         Shopper shopper = shopperDataProvider.find(new Shopper());
         Order order = saveOrder(shopper);
         shopperDataProvider.merge(shopper);

         switch (orderDataProvider.getCheckOut()) {
         case PAGSEGURO:
            throw new RedirectToUrlException(System.getProperty(PAGSEGURO_CHECKOUT_URL_PROPERTY, PAGSEGURO_CHECKOUT_URL_PROPERTY_VALUE) + order.getToken());
         case PAY_PAL:
            throw new RedirectToUrlException(System.getProperty(PAYPAL_CHECKOUT_URL_PROPERTY, PAYPAL_CHECKOUT_URL_PROPERTY_VALUE) + order.getToken());
         }
      }

      @Override
      public String getAjaxIndicatorMarkupId() {
         return VEIL_CART_LOADING;
      }
   }

   class SpecificationViewFragement extends Fragment {

      private static final long serialVersionUID = 9159244637681177882L;

      public SpecificationViewFragement() {
         super("specificationCustomerViewFragement", "specificationViewFragement", SpecificationViewPanel.this, SpecificationViewPanel.this.getDefaultModel());
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
         customerEditForm.add(new RequiredTextField<String>("customer.buyerEmail").setLabel(Model.of(getString("buyerEmailMessage"))).add(EmailAddressValidator.getInstance()).add(StringValidator.maximumLength(60)));
         customerEditForm.add(new RequiredTextField<String>("customer.firstName").setLabel(Model.of(getString("firstNameMessage"))).add(StringValidator.maximumLength(40)));
         customerEditForm.add(new RequiredTextField<String>("customer.lastName").setLabel(Model.of(getString("lastNameMessage"))).add(StringValidator.maximumLength(40)));
         customerEditForm.add(new RequiredTextField<String>("customer.address.street1").setLabel(Model.of(getString("street1Message"))).add(StringValidator.maximumLength(40)));
         customerEditForm.add(new TextField<String>("customer.address.street2").add(StringValidator.maximumLength(40)));
         customerEditForm.add(new DropDownChoice<Locale>("customer.address.country", getSortedISOCountries(), new ChoiceRenderer<Locale>("displayCountry", "")).setRequired(true).setLabel(Model.of(getString("countryNameMessage"))));
         customerEditForm.add(new RequiredTextField<String>("customer.address.cityName").setLabel(Model.of(getString("cityNameMessage"))).add(StringValidator.maximumLength(40)));
         customerEditForm.add(new RequiredTextField<String>("customer.address.postalCode").setLabel(Model.of(getString("postalCodeMessage"))).add(StringValidator.maximumLength(15)));
         customerEditForm.add(new RequiredTextField<String>("customer.address.stateOrProvince").setLabel(Model.of(getString("stateOrProvinceMessage"))).add(StringValidator.maximumLength(2)));

         RadioGroup<CheckOut> paymentOptions = new RadioGroup<CheckOut>("paymentOption", new Model<CheckOut>(orderDataProvider.getCheckOut()));
         paymentOptions.add(new Radio<CheckOut>("pagseguro", new Model<CheckOut>(CheckOut.PAGSEGURO)));
         paymentOptions.add(new Radio<CheckOut>("payPal", new Model<CheckOut>(CheckOut.PAY_PAL)));
         paymentOptions.add(new AjaxFormChoiceComponentUpdatingBehavior() {

            private static final long serialVersionUID = 1337286606852919595L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
               orderDataProvider.setCheckOut((CheckOut) paymentOptions.getDefaultModelObject());
            }
         });
         customerEditForm.add(paymentOptions);
         add(new SaveAjaxButton(customerEditForm).setOutputMarkupId(true));
         add(customerEditForm.setOutputMarkupId(true));
         add(offerRecordProductDataViewContainer.setOutputMarkupId(true));
         add(offerRecordDataviewContainer.setOutputMarkupId(true));
         add(new Label("totalDiscount", Model.of(NumberFormat.getCurrencyInstance().format(shopperDataProvider.find(new Shopper()).getChartTotalDiscount()))));
         add(new Label("total", Model.of(NumberFormat.getCurrencyInstance().format(shopperDataProvider.find(new Shopper()).getChartTotal()))));
         super.onInitialize();
      }
   }

   private static final long serialVersionUID = 293941244262646336L;

   private static final Logger LOGGER = LoggerFactory.getLogger(SpecificationViewPanel.class);

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   @SpringBean(name = "ContractDataProvider", required = true)
   private GenericTypeDataProvider<Contract> contractDataProvider;

   @SpringBean(name = "OrderDataProvider", required = true)
   private GenericOrderCheckoutDataProvider<Order> orderDataProvider;

   public SpecificationViewPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();
      contractDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
      contractDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
      contractDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
      contractDataProvider.setType(new Contract());
      contractDataProvider.getType().setActive(true);
      contractDataProvider.setOrderBy(OrderBy.NONE);

      orderDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
      orderDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
      orderDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
      orderDataProvider.setType(new Order());
      orderDataProvider.getType().setActive(true);
      orderDataProvider.setOrderBy(OrderBy.NONE);
      orderDataProvider.setCheckOut(CheckOut.PAGSEGURO);
   }
}
