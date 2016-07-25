/*
 * Copyright 2016 Netbrasoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package br.com.netbrasoft.gnuob.shop.specification;

import static br.com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.CONTRACT_DATA_PROVIDER_NAME;
import static br.com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.ORDER_DATA_PROVIDER_NAME;
import static br.com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.POSTAL_CODE_DATA_PROVIDER_NAME;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SHOPPER_DATA_PROVIDER_NAME;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;

import br.com.netbrasoft.gnuob.api.Contract;
import br.com.netbrasoft.gnuob.api.Option;
import br.com.netbrasoft.gnuob.api.Order;
import br.com.netbrasoft.gnuob.api.OrderBy;
import br.com.netbrasoft.gnuob.api.OrderRecord;
import br.com.netbrasoft.gnuob.api.PostalCode;
import br.com.netbrasoft.gnuob.api.generic.IGenericTypeDataProvider;
import br.com.netbrasoft.gnuob.api.generic.converter.CurrencyConverter;
import br.com.netbrasoft.gnuob.api.order.IGenericOrderCheckoutDataProvider;
import br.com.netbrasoft.gnuob.api.order.OrderDataProvider.PaymentProviderEnum;
import br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants;
import br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.security.ShopRoles;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.LoadingBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.typeaheadV10.DataSet;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.typeaheadV10.Typeahead;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.typeaheadV10.TypeaheadConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.typeaheadV10.bloodhound.Bloodhound;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.typeaheadV10.bloodhound.BloodhoundConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;

/**
 * Panel for viewing, selecting and editing {@link Order} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class SpecificationEmptyOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class SpecificationEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
    class SpecificationEditContainer extends WebMarkupContainer {

      class BloodhoundPlaceNames extends Bloodhound<String> {

        private static final String VALUE_S_FORMAT = "{\"value\":\"%s\"}";

        private static final long serialVersionUID = 697786098676195271L;

        public BloodhoundPlaceNames(final String name, final BloodhoundConfig config) {
          super(name, config);
        }

        @Override
        public Iterable<String> getChoices(final String input) {
          final List<String> cityNames = new ArrayList<String>();
          postalCodeDataProvider.getType().setPlaceName(input + "%");
          postalCodeDataProvider.setOrderBy(OrderBy.PLACE_NAME_A_Z);
          for (final Iterator<? extends PostalCode> iterator = postalCodeDataProvider.iterator(0, 5); iterator
              .hasNext();) {
            cityNames.add(iterator.next().getPlaceName());
          }
          return cityNames;
        }

        @Override
        public String renderChoice(final String choice) {
          return String.format(VALUE_S_FORMAT, choice);
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
      class SpecificationEditTable extends WebMarkupContainer {

        @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
        class SaveAjaxButton extends BootstrapAjaxButton implements IAjaxIndicatorAware {

          private static final String VEIL_CART_LOADING = "veil-cart-loading";

          private static final String PAYPAL_CHECKOUT_URL_PROPERTY_VALUE =
              "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";

          private static final String PAGSEGURO_CHECKOUT_URL_PROPERTY_VALUE =
              "https://sandbox.pagseguro.uol.com.br/v2/checkout/payment.html?code=";

          private static final String PAYPAL_CHECKOUT_URL_PROPERTY = "paypal.checkout.url";

          private static final String PAGSEGURO_CHECKOUT_URL_PROPERTY = "pagseguro.checkout.url";

          private static final long serialVersionUID = 2695394292963384938L;

          public SaveAjaxButton(final String id, final IModel<String> model, final Form<Order> form,
              final Buttons.Type type) {
            super(id, model, form, type);
            setSize(Buttons.Size.Small);
            add(new LoadingBehavior(Model.of(SpecificationEmptyOrEditPanel.this
                .getString(NetbrasoftShopConstants.DIRECTING_TO_PAYMENT_PROVIDER_MESSAGE_KEY))));
          }

          @Override
          public String getAjaxIndicatorMarkupId() {
            return VEIL_CART_LOADING;
          }

          @Override
          protected void onError(final AjaxRequestTarget target, final Form<?> form) {
            form.add(new TooltipValidation());
            target.add(form);
            target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(SpecificationEmptyOrEditPanel.this
                .getString(NetbrasoftShopConstants.DIRECTING_TO_PAYMENT_PROVIDER_MESSAGE_KEY)))));
          }

          @Override
          protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
            ((Order) form.getDefaultModelObject()).getInvoice().getAddress().setCountry(BR_COUNTRY_CODE);
            ((Order) form.getDefaultModelObject()).getShipment().getAddress().setCountry(BR_COUNTRY_CODE);

            if (((Order) form.getDefaultModelObject()).getId() == 0) {
              SpecificationEditTable.this.setDefaultModelObject(orderDataProvider.doCheckout(
                  orderDataProvider.findById(orderDataProvider.persist((Order) form.getDefaultModelObject()))));
            } else {
              SpecificationEditTable.this.setDefaultModelObject(orderDataProvider.doCheckout(
                  orderDataProvider.findById(orderDataProvider.merge((Order) form.getDefaultModelObject()))));
            }
            if (PaymentProviderEnum.PAGSEGURO.equals(orderDataProvider.getPaymentProvider())) {
              throw new RedirectToUrlException(
                  System.getProperty(PAGSEGURO_CHECKOUT_URL_PROPERTY, PAGSEGURO_CHECKOUT_URL_PROPERTY_VALUE)
                      + ((Order) SpecificationEditTable.this.getDefaultModelObject()).getToken());
            } else { // PayPal
              throw new RedirectToUrlException(
                  System.getProperty(PAYPAL_CHECKOUT_URL_PROPERTY, PAYPAL_CHECKOUT_URL_PROPERTY_VALUE)
                      + ((Order) SpecificationEditTable.this.getDefaultModelObject()).getToken());
            }
          }
        }

        @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
        class SpecificationDataviewContainer extends WebMarkupContainer {

          class SpecificationDataView extends DataView<OrderRecord> {

            private static final String AMOUNT_TOTAL_ID = "amountTotal";

            private static final String ITEM_TOTAL_ID = "itemTotal";

            private static final String QUANTITY_ID = "quantity";

            private static final String OPTIONS_ID = "options";

            private static final String NAME_ID = NAME_DISPLAY_EXPRESSION;

            private static final long serialVersionUID = -349956116786592591L;

            private static final String CLICK_EVENT = "click";

            private static final String INFO_VALUE = "info";

            private static final String CLASS_ATTRIBUTE = "class";

            private int index = 0;

            protected SpecificationDataView(final String id, final IDataProvider<OrderRecord> dataProvider,
                final long itemsPerPage) {
              super(id, dataProvider, itemsPerPage);
            }

            private String getOptions(final List<Option> options) {
              final StringBuilder optionStringBuilder = new StringBuilder();
              for (final Option option : options) {
                optionStringBuilder.append(option.getValue()).append(": ")
                    .append(option.getSubOptions().iterator().next().getValue()).append(" ");
              }
              return optionStringBuilder.toString();
            }

            @Override
            protected Item<OrderRecord> newItem(final String id, final int index, final IModel<OrderRecord> model) {
              final Item<OrderRecord> item = super.newItem(id, index, model);
              if (this.index == index) {
                item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
              }
              return item;
            }

            @Override
            protected void onConfigure() {
              final IModel<Order> model = (IModel<Order>) SpecificationDataviewContainer.this.getDefaultModel();
              if (!model.getObject().getRecords().isEmpty()) {
                specificationViewOrEditPanel.removeAll();
                specificationViewOrEditPanel.setSelectedModel(Model.of(model.getObject().getRecords().get(index)));
                specificationViewOrEditPanel
                    .add(specificationViewOrEditPanel.new SpecificationOfferRecordEditFragment())
                    .setOutputMarkupId(true);
              }
              super.onConfigure();
            }

            @Override
            protected void populateItem(final Item<OrderRecord> item) {
              final BigDecimal productAmount = item.getModelObject().getProduct().getAmount();
              final BigDecimal productTax = item.getModelObject().getProduct().getTax();
              final BigDecimal productDiscount = item.getModelObject().getProduct().getDiscount();
              final BigDecimal quantity = BigDecimal.valueOf(item.getModelObject().getQuantity().intValue());
              final BigDecimal itemTotal = productAmount.add(productTax).subtract(productDiscount).multiply(quantity);
              final BigDecimal amountTotal = productAmount.add(productTax).multiply(quantity);

              final Label nameLabel = new Label(NAME_ID);
              final Label optionsLabel =
                  new Label(OPTIONS_ID, Model.of(getOptions(item.getModelObject().getOptions())));
              final Label quantityLabel = new Label(QUANTITY_ID);
              final Label itemTotalLabel =
                  new Label(ITEM_TOTAL_ID, Model.of(NumberFormat.getCurrencyInstance().format(itemTotal)));
              final Label amountLabel =
                  new Label(AMOUNT_TOTAL_ID, Model.of(NumberFormat.getCurrencyInstance().format(amountTotal)));
              final AjaxEventBehavior ajaxEventBehavior = new AjaxEventBehavior(CLICK_EVENT) {

                private static final long serialVersionUID = 1L;

                @Override
                public void onEvent(final AjaxRequestTarget target) {
                  index = item.getIndex();
                  specificationViewOrEditPanel.setSelectedModel(item.getModel());
                  specificationViewOrEditPanel.removeAll();
                  target.add(specificationDataviewContainer.setOutputMarkupId(true));
                  target.add(specificationViewOrEditPanel
                      .add(specificationViewOrEditPanel.new SpecificationOfferRecordEditFragment())
                      .setOutputMarkupId(true));
                }
              };

              item.setModel(new CompoundPropertyModel<OrderRecord>(item.getModelObject()));
              item.add(nameLabel.setOutputMarkupId(true));
              item.add(optionsLabel.setOutputMarkupId(true));
              item.add(quantityLabel.setOutputMarkupId(true));
              item.add(itemTotalLabel.setOutputMarkupId(true));
              item.add(amountLabel.setOutputMarkupId(true));
              item.add(ajaxEventBehavior);
            }
          }

          private static final long serialVersionUID = -7857928055209786316L;

          private static final String SPECIFICATION_DATAVIEW_ID = "specificationDataview";

          private final ListDataProvider<OrderRecord> specificationListDataProvider;

          private final SpecificationDataView specificationDataView;

          public SpecificationDataviewContainer(final String id, final IModel<Order> model) {
            super(id, model);
            specificationListDataProvider = new ListDataProvider<OrderRecord>() {

              private static final long serialVersionUID = -3261859241046697057L;

              @Override
              protected List<OrderRecord> getData() {
                return ((Order) SpecificationDataviewContainer.this.getDefaultModelObject()).getRecords();
              }
            };
            specificationDataView =
                new SpecificationDataView(SPECIFICATION_DATAVIEW_ID, specificationListDataProvider, Integer.MAX_VALUE);
          }

          @Override
          protected void onInitialize() {
            add(specificationDataView.setOutputMarkupId(true));
            super.onInitialize();
          }
        }

        private static final String SPECIFICATION_DATAVIEW_CONTAINER_ID = "specificationDataviewContainer";

        private static final String SAVE_ID = "save";

        private static final String SHIPPING_TOTAL_ID = "shippingTotal";

        private static final String ORDER_TOTAL_ID = "orderTotal";

        private static final String DISCOUNT_TOTAL_ID = "discountTotal";

        private static final long serialVersionUID = -2015192973310978723L;

        private final Label discountTotalLabel;

        private final Label orderTotalLabel;

        private final Label shippingTotalLabel;

        private final SaveAjaxButton saveAjaxButton;

        private final SpecificationDataviewContainer specificationDataviewContainer;

        public SpecificationEditTable(final String id, final IModel<Order> model) {
          super(id, new CompoundPropertyModel<Order>(model));
          discountTotalLabel = new Label(DISCOUNT_TOTAL_ID) {

            private static final long serialVersionUID = -4143367505737220689L;

            @Override
            public <C> IConverter<C> getConverter(final Class<C> type) {
              return (IConverter<C>) new CurrencyConverter();
            }
          };
          orderTotalLabel = new Label(ORDER_TOTAL_ID) {

            private static final long serialVersionUID = -4143367505737220689L;

            @Override
            public <C> IConverter<C> getConverter(final Class<C> type) {
              return (IConverter<C>) new CurrencyConverter();
            }
          };
          shippingTotalLabel = new Label(SHIPPING_TOTAL_ID) {

            private static final long serialVersionUID = -4143367505737220689L;

            @Override
            public <C> IConverter<C> getConverter(final Class<C> type) {
              return (IConverter<C>) new CurrencyConverter();
            }
          };
          saveAjaxButton = new SaveAjaxButton(SAVE_ID,
              Model.of(SpecificationEmptyOrEditPanel.this.getString(NetbrasoftShopConstants.PAY_MESSAGE_KEY)),
              specificationEditForm, Type.Primary);
          specificationDataviewContainer = new SpecificationDataviewContainer(SPECIFICATION_DATAVIEW_CONTAINER_ID,
              (IModel<Order>) SpecificationEditTable.this.getDefaultModel());
        }

        @Override
        protected void onInitialize() {
          add(discountTotalLabel.setOutputMarkupId(true));
          add(orderTotalLabel.setOutputMarkupId(true));
          add(shippingTotalLabel.setOutputMarkupId(true));
          add(saveAjaxButton.setOutputMarkupId(true));
          add(specificationDataviewContainer.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String SPECIFICATION_VIEW_OR_EDIT_PANEL_ID = "specificationViewOrEditPanel";

      private static final String SPECIFICATION_EDIT_TABLE_ID = "specificationEditTable";

      private static final String NAME_DISPLAY_EXPRESSION = "name";

      private static final String PAYMENT_OPTION_ID = "paymentOption";

      private static final String PAGSEGURO_ID = "pagseguro";

      private static final String PAY_PAL_ID = "payPal";

      private static final String PATTERN_0_9_5_0_9_3 = "([0-9]){5}([-])([0-9]){3}";

      private static final String REMOTE_NAME = "remote";

      private static final String SHIPMENT_ADDRESS_STATE_OR_PROVINCE_ID = "shipment.address.stateOrProvince";

      private static final String SHIPMENT_ADDRESS_POSTAL_CODE_ID = "shipment.address.postalCode";

      private static final String SHIPMENT_ADDRESS_CITY_NAME_ID = "shipment.address.cityName";

      private static final String SHIPMENT_ADDRESS_COUNTRY_ID = "shipment.address.country";

      private static final String SHIPMENT_ADDRESS_STREET2_ID = "shipment.address.street2";

      private static final String SHIPMENT_ADDRESS_STREET1_ID = "shipment.address.street1";

      private static final String INVOICE_ADDRESS_STATE_OR_PROVINCE_ID = "invoice.address.stateOrProvince";

      private static final String INVOICE_ADDRESS_POSTAL_CODE_ID = "invoice.address.postalCode";

      private static final String INVOICE_ADDRESS_COUNTRY_ID = "invoice.address.country";

      private static final String INVOICE_ADDRESS_STREET2_ID = "invoice.address.street2";

      private static final String INVOICE_ADDRESS_STREET1_ID = "invoice.address.street1";

      private static final String CONTRACT_CUSTOMER_LAST_NAME_ID = "contract.customer.lastName";

      private static final String CONTRACT_CUSTOMER_FIRST_NAME_ID = "contract.customer.firstName";

      private static final String CONTRACT_CUSTOMER_BUYER_EMAIL_ID = "contract.customer.buyerEmail";

      private static final long serialVersionUID = 4017249494388406640L;

      private static final String SPECIFICATION_EDIT_FORM_COMPONENT_ID = "specificationEditForm";

      private static final String INVOICE_ADDRESS_CITY_NAME_ID = "invoice.address.cityName";

      private final RequiredTextField<String> contractCustomerBuyerEmailTextField;

      private final RequiredTextField<String> contractCustomerFirstNameTextField;

      private final RequiredTextField<String> contractCustomerLastNameTextField;

      private final RequiredTextField<String> shipmentAddressStreet1TextField;

      private final TextField<String> shipmentAddressStreet2TextField;

      private final TextField<String> shipmentAddressCountryTextField;

      private final Typeahead<String> shipmentAddressCityNameTextField;

      private final RequiredTextField<String> shipmentAddressPostalCodeTextField;

      private final BootstrapSelect<State> shipmentAddressStateOrProvinceDropDownChoice;

      private final RequiredTextField<String> invoiceAddressStreet1TextField;

      private final TextField<String> invoiceAddressStreet2TextField;

      private final TextField<String> invoiceAddressCountryTextField;

      private final Typeahead<String> invoiceAddressCityNameTextField;

      private final RequiredTextField<String> invoiceAddressPostalCodeTextField;

      private final BootstrapSelect<State> invoiceAddressStateOrProvinceDropDownChoice;

      private final BootstrapForm<Order> specificationEditForm;

      private final RadioGroup<PaymentProviderEnum> paymentOptions;

      private final SpecificationEditTable specificationEditTable;

      private final SpecificationViewOrEditPanel specificationViewOrEditPanel;

      public SpecificationEditContainer(final String id, final IModel<Order> model) {
        super(id, model);
        final BloodhoundPlaceNames invoiceAddressCityBloodhoundPlaceNames =
            new BloodhoundPlaceNames(REMOTE_NAME, new BloodhoundConfig());
        final TypeaheadConfig<String> invoiceAddressCityNameConfig =
            new TypeaheadConfig<>(new DataSet<>(invoiceAddressCityBloodhoundPlaceNames));
        final BloodhoundPlaceNames shipmentAddressCityBloodhoundPlaceNames =
            new BloodhoundPlaceNames(REMOTE_NAME, new BloodhoundConfig());
        final TypeaheadConfig<String> shipmentAddressCityNameConfig =
            new TypeaheadConfig<>(new DataSet<>(shipmentAddressCityBloodhoundPlaceNames));

        specificationEditForm = new BootstrapForm<Order>(SPECIFICATION_EDIT_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Order>((IModel<Order>) SpecificationEditContainer.this.getDefaultModel()));
        contractCustomerBuyerEmailTextField = new RequiredTextField<String>(CONTRACT_CUSTOMER_BUYER_EMAIL_ID);
        contractCustomerFirstNameTextField = new RequiredTextField<String>(CONTRACT_CUSTOMER_FIRST_NAME_ID);
        contractCustomerLastNameTextField = new RequiredTextField<String>(CONTRACT_CUSTOMER_LAST_NAME_ID);
        shipmentAddressStreet1TextField = new RequiredTextField<String>(SHIPMENT_ADDRESS_STREET1_ID);
        shipmentAddressStreet2TextField = new TextField<String>(SHIPMENT_ADDRESS_STREET2_ID);
        shipmentAddressCountryTextField = new TextField<String>(SHIPMENT_ADDRESS_COUNTRY_ID, Model.of("Brasil"));
        shipmentAddressCityNameTextField =
            new Typeahead<String>(SHIPMENT_ADDRESS_CITY_NAME_ID, null, shipmentAddressCityNameConfig);
        shipmentAddressPostalCodeTextField = new RequiredTextField<String>(SHIPMENT_ADDRESS_POSTAL_CODE_ID);
        shipmentAddressStateOrProvinceDropDownChoice = new BootstrapSelect<State>(SHIPMENT_ADDRESS_STATE_OR_PROVINCE_ID,
            getStatesOfBrazil(), new ChoiceRenderer<State>(NAME_DISPLAY_EXPRESSION, ""));
        invoiceAddressStreet1TextField = new RequiredTextField<String>(INVOICE_ADDRESS_STREET1_ID);
        invoiceAddressStreet2TextField = new TextField<String>(INVOICE_ADDRESS_STREET2_ID);
        invoiceAddressCountryTextField = new TextField<String>(INVOICE_ADDRESS_COUNTRY_ID, Model.of("Brasil"));
        invoiceAddressCityNameTextField =
            new Typeahead<String>(INVOICE_ADDRESS_CITY_NAME_ID, null, invoiceAddressCityNameConfig);
        invoiceAddressPostalCodeTextField = new RequiredTextField<String>(INVOICE_ADDRESS_POSTAL_CODE_ID);
        invoiceAddressStateOrProvinceDropDownChoice = new BootstrapSelect<State>(INVOICE_ADDRESS_STATE_OR_PROVINCE_ID,
            getStatesOfBrazil(), new ChoiceRenderer<State>(NAME_DISPLAY_EXPRESSION, ""));
        paymentOptions = new RadioGroup<PaymentProviderEnum>(PAYMENT_OPTION_ID,
            new Model<PaymentProviderEnum>(orderDataProvider.getPaymentProvider()));
        specificationViewOrEditPanel = new SpecificationViewOrEditPanel(SPECIFICATION_VIEW_OR_EDIT_PANEL_ID,
            (IModel<Order>) SpecificationEditContainer.this.getDefaultModel());
        specificationEditTable = new SpecificationEditTable(SPECIFICATION_EDIT_TABLE_ID,
            (IModel<Order>) SpecificationEditContainer.this.getDefaultModel());
      }

      // FIXME Get this information from http://www.geonames.org/ to support state formats
      public List<State> getStatesOfBrazil() {
        final List<State> states = new ArrayList<>();
        final String[][] statesOfBrazil = new String[][] {{"AC", "Acre"}, {"AL", "Alagoas"}, {"AP", "Amapá"},
            {"AM", "Amazonas"}, {"BA", "Bahia"}, {"CE", "Ceará"}, {"ES", "Espírito Santo"}, {"GO", "Goiás"},
            {"MA", "Maranhão"}, {"MT", "Mato Grosso"}, {"MS", "Mato Grosso do Sul"}, {"MG", "Minas Gerais"},
            {"PA", "Pará"}, {"PB", "Paraíba"}, {"PR", "Paraná"}, {"PE", "Pernambuco"}, {"PI", "Piauí"},
            {"RJ", "Rio de Janeiro"}, {"RN", "Rio Grande do Norte"}, {"RS", "Rio Grande do Sul"}, {"RO", "Rondônia"},
            {"RR", "Roraima"}, {"SC", "Santa Catarina"}, {"SP", "São Paulo"}, {"SE", "Sergipe"}, {"TO", "Tocantins"}};

        for (final String[] state : statesOfBrazil) {
          states.add(new State(state[0], state[1]));
        }
        return states;
      }

      @Override
      protected void onInitialize() {
        final AjaxFormChoiceComponentUpdatingBehavior ajaxFormChoiceComponentUpdatingBehavior =
            new AjaxFormChoiceComponentUpdatingBehavior() {

              private static final long serialVersionUID = 1337286606852919595L;

              @Override
              protected void onUpdate(final AjaxRequestTarget target) {
                orderDataProvider.setPaymentProvider((PaymentProviderEnum) paymentOptions.getDefaultModelObject());
              }
            };
        final Radio<PaymentProviderEnum> pagseguroRadio =
            new Radio<PaymentProviderEnum>(PAGSEGURO_ID, new Model<PaymentProviderEnum>(PaymentProviderEnum.PAGSEGURO));
        final Radio<PaymentProviderEnum> payPalRadio =
            new Radio<PaymentProviderEnum>(PAY_PAL_ID, new Model<PaymentProviderEnum>(PaymentProviderEnum.PAY_PAL));

        contractCustomerBuyerEmailTextField.setLabel(
            Model.of(SpecificationEmptyOrEditPanel.this.getString(NetbrasoftShopConstants.BUYER_EMAIL_MESSAGE_KEY)));
        contractCustomerBuyerEmailTextField.add(EmailAddressValidator.getInstance());
        contractCustomerBuyerEmailTextField.add(StringValidator.maximumLength(60));
        contractCustomerFirstNameTextField.setLabel(
            Model.of(SpecificationEmptyOrEditPanel.this.getString(NetbrasoftShopConstants.FIRST_NAME_MESSAGE_KEY)));
        contractCustomerFirstNameTextField.add(StringValidator.maximumLength(40));
        contractCustomerLastNameTextField.setLabel(
            Model.of(SpecificationEmptyOrEditPanel.this.getString(NetbrasoftShopConstants.LAST_NAME_MESSAGE_KEY)));
        contractCustomerLastNameTextField.add(StringValidator.maximumLength(40));
        shipmentAddressStreet1TextField.setLabel(
            Model.of(SpecificationEmptyOrEditPanel.this.getString(NetbrasoftShopConstants.STREET1_MESSAGE_KEY)));
        shipmentAddressStreet1TextField.add(StringValidator.maximumLength(40));
        shipmentAddressStreet2TextField.add(StringValidator.maximumLength(40));
        shipmentAddressCountryTextField.setLabel(
            Model.of(SpecificationEmptyOrEditPanel.this.getString(NetbrasoftShopConstants.COUNTRY_NAME_MESSAGE_KEY)));
        shipmentAddressCountryTextField.setEnabled(false);
        shipmentAddressCityNameTextField.setRequired(true);
        shipmentAddressCityNameTextField.setLabel(
            Model.of(SpecificationEmptyOrEditPanel.this.getString(NetbrasoftShopConstants.CITY_NAME_MESSAGE_KEY)));
        shipmentAddressCityNameTextField.add(StringValidator.maximumLength(40)).setOutputMarkupId(true);
        shipmentAddressPostalCodeTextField.setLabel(
            Model.of(SpecificationEmptyOrEditPanel.this.getString(NetbrasoftShopConstants.POSTAL_CODE_MESSAGE_KEY)));
        shipmentAddressPostalCodeTextField.add(new PatternValidator(PATTERN_0_9_5_0_9_3));
        shipmentAddressStateOrProvinceDropDownChoice.setRequired(true);
        shipmentAddressStateOrProvinceDropDownChoice.setLabel(Model
            .of(SpecificationEmptyOrEditPanel.this.getString(NetbrasoftShopConstants.STATE_OR_PROVINCE_MESSAGE_KEY)));
        invoiceAddressStreet1TextField.setLabel(
            Model.of(SpecificationEmptyOrEditPanel.this.getString(NetbrasoftShopConstants.STREET1_MESSAGE_KEY)));
        invoiceAddressStreet1TextField.add(StringValidator.maximumLength(40));
        invoiceAddressStreet2TextField.add(StringValidator.maximumLength(40));
        invoiceAddressCountryTextField.setLabel(
            Model.of(SpecificationEmptyOrEditPanel.this.getString(NetbrasoftShopConstants.COUNTRY_NAME_MESSAGE_KEY)));
        invoiceAddressCountryTextField.setEnabled(false);
        invoiceAddressCityNameTextField.setRequired(true);
        invoiceAddressCityNameTextField.setLabel(
            Model.of(SpecificationEmptyOrEditPanel.this.getString(NetbrasoftShopConstants.CITY_NAME_MESSAGE_KEY)));
        invoiceAddressCityNameTextField.add(StringValidator.maximumLength(40)).setOutputMarkupId(true);
        invoiceAddressPostalCodeTextField.setLabel(
            Model.of(SpecificationEmptyOrEditPanel.this.getString(NetbrasoftShopConstants.POSTAL_CODE_MESSAGE_KEY)));
        invoiceAddressPostalCodeTextField.add(new PatternValidator(PATTERN_0_9_5_0_9_3));
        invoiceAddressStateOrProvinceDropDownChoice.setRequired(true);
        invoiceAddressStateOrProvinceDropDownChoice.setLabel(Model
            .of(SpecificationEmptyOrEditPanel.this.getString(NetbrasoftShopConstants.STATE_OR_PROVINCE_MESSAGE_KEY)));
        paymentOptions.add(pagseguroRadio);
        paymentOptions.add(payPalRadio);
        paymentOptions.add(ajaxFormChoiceComponentUpdatingBehavior);
        specificationEditForm.add(contractCustomerBuyerEmailTextField.setOutputMarkupId(true));
        specificationEditForm.add(contractCustomerFirstNameTextField.setOutputMarkupId(true));
        specificationEditForm.add(contractCustomerLastNameTextField.setOutputMarkupId(true));
        specificationEditForm.add(shipmentAddressStreet1TextField.setOutputMarkupId(true));
        specificationEditForm.add(shipmentAddressStreet2TextField.setOutputMarkupId(true));
        specificationEditForm.add(shipmentAddressCountryTextField.setOutputMarkupId(true));
        specificationEditForm.add(shipmentAddressCityNameTextField.setOutputMarkupId(true));
        specificationEditForm.add(shipmentAddressPostalCodeTextField.setOutputMarkupId(true));
        specificationEditForm.add(shipmentAddressStateOrProvinceDropDownChoice.setOutputMarkupId(true));
        specificationEditForm.add(invoiceAddressStreet1TextField.setOutputMarkupId(true));
        specificationEditForm.add(invoiceAddressStreet2TextField.setOutputMarkupId(true));
        specificationEditForm.add(invoiceAddressCountryTextField.setOutputMarkupId(true));
        specificationEditForm.add(invoiceAddressCityNameTextField.setOutputMarkupId(true));
        specificationEditForm.add(invoiceAddressPostalCodeTextField.setOutputMarkupId(true));
        specificationEditForm.add(invoiceAddressStateOrProvinceDropDownChoice.setOutputMarkupId(true));
        specificationEditForm.add(paymentOptions);
        specificationEditForm.add(new FormBehavior(FormType.Horizontal));
        specificationEditForm.add(specificationViewOrEditPanel
            .add(specificationViewOrEditPanel.new SpecificationOfferRecordEditFragment()).setOutputMarkupId(true));
        specificationEditForm.add(specificationEditTable.add(new TableBehavior()).setOutputMarkupId(true));
        add(specificationEditForm.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    class State implements IClusterable {

      private static final long serialVersionUID = 537050311855603864L;

      private final String code;
      private final String name;

      public State(final String code, final String name) {
        this.code = code;
        this.name = name;
      }

      public String getName() {
        return name;
      }

      @Override
      public String toString() {
        return code;
      }
    }

    private static final String SPECIFICATION_EDIT_CONTAINER_ID = "specificationEditContainer";

    private static final String SPECIFICATION_EDIT_FRAGMENT_MARKUP_ID = "specificationEditFragment";

    private static final String SPECIFICATION_EMPTY_OR_EDIT_FRAGMENT_ID = "specificationEmptyOrEditFragment";

    private static final long serialVersionUID = 9159244637681177882L;

    private final SpecificationEditContainer specificationEditContainer;

    public SpecificationEditFragment() {
      super(SPECIFICATION_EMPTY_OR_EDIT_FRAGMENT_ID, SPECIFICATION_EDIT_FRAGMENT_MARKUP_ID,
          SpecificationEmptyOrEditPanel.this, SpecificationEmptyOrEditPanel.this.getDefaultModel());
      specificationEditContainer = new SpecificationEditContainer(SPECIFICATION_EDIT_CONTAINER_ID,
          (IModel<Order>) SpecificationEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(specificationEditContainer.setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final String BR_COUNTRY_CODE = "BR";

  private static final long serialVersionUID = 293941244262646336L;

  @SpringBean(name = SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  @SpringBean(name = CONTRACT_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Contract> contractDataProvider;

  @SpringBean(name = ORDER_DATA_PROVIDER_NAME, required = true)
  private transient IGenericOrderCheckoutDataProvider<Order> orderDataProvider;

  @SpringBean(name = POSTAL_CODE_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<PostalCode> postalCodeDataProvider;

  public SpecificationEmptyOrEditPanel(final String id, final IModel<Order> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    orderDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    orderDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    orderDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    orderDataProvider.setType(new Order());
    orderDataProvider.getType().setActive(true);
    orderDataProvider.setOrderBy(OrderBy.NONE);
    orderDataProvider.setPaymentProvider(PaymentProviderEnum.PAGSEGURO);

    postalCodeDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    postalCodeDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    postalCodeDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    postalCodeDataProvider.setType(new PostalCode());
    postalCodeDataProvider.getType().setCountryCode(BR_COUNTRY_CODE); // Fixed value for Brazil
                                                                      // addresses only.
    super.onInitialize();
  }
}
