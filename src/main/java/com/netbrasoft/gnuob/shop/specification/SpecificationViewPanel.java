package com.netbrasoft.gnuob.shop.specification;

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
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;

import com.google.common.net.MediaType;
import com.netbrasoft.gnuob.api.Address;
import com.netbrasoft.gnuob.api.Content;
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
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.product.ProductCarousel;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.LoadingBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.CarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.ICarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverConfig;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig.Placement;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class SpecificationViewPanel extends Panel {

   class OfferRecordDataProvider implements IDataProvider<OfferRecord> {

      private static final long serialVersionUID = 3755475588885853693L;

      @Override
      public void detach() {
         return;
      }

      public BigDecimal getChartTotal() {
         return shopperDataProvider.find(new Shopper()).getCartTotal();
      }

      public BigDecimal getChartTotalDiscount() {
         return shopperDataProvider.find(new Shopper()).getCartTotalDiscount();
      }

      public BigDecimal getShippingCostTotal() {
         return shopperDataProvider.find(new Shopper()).getShippingCostTotal();
      }

      @Override
      public Iterator<? extends OfferRecord> iterator(long first, long count) {
         final List<OfferRecord> offerRecordIteratorList = new ArrayList<OfferRecord>();

         for (int index = (int) first; index < first + count; index++) {
            offerRecordIteratorList.add(shopperDataProvider.find(new Shopper()).getCart().getRecords().get(index));
         }

         return offerRecordIteratorList.iterator();
      }

      @Override
      public IModel<OfferRecord> model(OfferRecord object) {
         return Model.of(object);
      }

      @Override
      public long size() {
         return shopperDataProvider.find(new Shopper()).getCart().getRecords().size();
      }
   }

   class OfferRecordDataView extends DataView<OfferRecord> {

      private static final long serialVersionUID = -8885578770770605991L;

      private int selectedIndex = 0;

      protected OfferRecordDataView() {
         super("offerRecordDataview", offerRecordDataProvider);
      }

      @Override
      protected Item<OfferRecord> newItem(String id, int index, IModel<OfferRecord> model) {
         final Item<OfferRecord> item = super.newItem(id, index, model);

         if (index == selectedIndex) {
            item.add(new AttributeModifier("class", "info"));
         }

         return item;
      }

      @Override
      protected void populateItem(Item<OfferRecord> item) {
         final BigDecimal amount = item.getModelObject().getProduct().getAmount();
         final BigDecimal tax = item.getModelObject().getProduct().getTax();
         final BigDecimal discount = item.getModelObject().getProduct().getDiscount();
         final BigDecimal quantity = BigDecimal.valueOf(item.getModelObject().getQuantity().intValue());

         item.setModel(new CompoundPropertyModel<OfferRecord>(item.getModelObject()));
         item.add(new Label("name"));
         item.add(new Label("option"));
         item.add(new Label("quantity"));
         item.add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(amount.add(tax).subtract(discount).multiply(quantity)))));
         item.add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(amount.add(tax).multiply(quantity)))));
         item.add(new AjaxEventBehavior("click") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onEvent(AjaxRequestTarget target) {
               selectedIndex = item.getIndex();

               offerRecordProductDataProvider.offerRecords.clear();
               offerRecordProductDataProvider.offerRecords.add(item.getModelObject());
               target.add(offerRecordProductDataViewContainer);
               target.add(offerRecordDataviewContainer);
               target.add(offerRecordTotalDataviewContainer);
            }
         });
      }
   }

   class OfferRecordProductDataProvider implements IDataProvider<OfferRecord> {

      private static final long serialVersionUID = 9170940545796805775L;

      private final List<OfferRecord> offerRecords = new ArrayList<OfferRecord>();

      @Override
      public void detach() {
         return;
      }

      @Override
      public Iterator<? extends OfferRecord> iterator(long first, long count) {
         final List<OfferRecord> offerRecordProductIteratorList = new ArrayList<OfferRecord>();

         for (int index = (int) first; index < first + count; index++) {
            offerRecordProductIteratorList.add(offerRecords.get(index));
         }

         return offerRecordProductIteratorList.iterator();
      }

      @Override
      public IModel<OfferRecord> model(OfferRecord object) {
         return Model.of(object);
      }

      @Override
      public long size() {
         return offerRecords.size();
      }
   }

   class OfferRecordProductDataView extends DataView<OfferRecord> {

      private static final long serialVersionUID = -3333902779955513421L;

      private static final int ITEMS_PER_PAGE = 5;

      protected OfferRecordProductDataView() {
         super("offerRecordProductDataView", offerRecordProductDataProvider, ITEMS_PER_PAGE);
      }

      @Override
      protected void populateItem(Item<OfferRecord> item) {
         final List<ICarouselImage> carouselImages = new ArrayList<ICarouselImage>();

         for (final Content content : item.getModelObject().getProduct().getContents()) {
            if (MediaType.HTML_UTF_8.is(MediaType.parse(content.getFormat()))) {
               carouselImages.add(new CarouselImage(new String(content.getContent())));
            }
         }

         item.setModel(new CompoundPropertyModel<OfferRecord>(item.getModelObject()));
         item.add(new ProductCarousel("productCarousel", carouselImages)
               .add(new PopoverBehavior(Model.of(getString("descriptionMessage")), Model.of(item.getModelObject().getProduct().getDescription()), new PopoverConfig().withHoverTrigger().withPlacement(Placement.left))));
         item.add(new Label("name"));
         item.add(new Label("product.stock.quantity"));
         item.add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(item.getModelObject().getProduct().getAmount().subtract(item.getModelObject().getProduct().getDiscount())))));
         item.add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(item.getModelObject().getProduct().getAmount()))));
         item.add(new Loop("rating", ITEMS_PER_PAGE) {

            private static final long serialVersionUID = -443304621920358169L;

            @Override
            protected void populateItem(LoopItem loopItem) {
               loopItem.add(new IconBehavior(loopItem.getIndex() < item.getModelObject().getProduct().getRating() ? GlyphIconType.star : GlyphIconType.starempty));
            }
         });
      }
   }

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
         add(new LoadingBehavior(Model.of(SpecificationViewPanel.this.getString("directingToPaymentProviderMessage"))));
      }

      @Override
      public String getAjaxIndicatorMarkupId() {
         return VEIL_CART_LOADING;
      }

      @Override
      protected void onError(AjaxRequestTarget target, Form<?> form) {
         form.add(new TooltipValidation());
         target.add(form);
         target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(SpecificationViewPanel.this.getString("directingToPaymentProviderMessage")))));
      }

      @Override
      protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
         saveContract((Contract) form.getDefaultModelObject());
         saveOrderAndDoCheckout();
      }

      private void saveContract(Contract contract) {
         final Shopper shopper = shopperDataProvider.find(new Shopper());

         if (contract.getId() == 0) {
            contract = contractDataProvider.persist(contract);
         } else {
            contract = contractDataProvider.merge(contract);
         }

         contract = contractDataProvider.findById(contract);

         shopper.setContract(contract);
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
         order.getShipment().getAddress().setCountry("BR");
         order.getShipment().getAddress().setCityName(shopper.getContract().getCustomer().getAddress().getCityName());
         order.getShipment().getAddress().setPostalCode(shopper.getContract().getCustomer().getAddress().getPostalCode());
         order.getShipment().getAddress().setStateOrProvince(shopper.getContract().getCustomer().getAddress().getStateOrProvince());
         order.getShipment().getAddress().setPhone(shopper.getContract().getCustomer().getAddress().getPhone());
         order.getInvoice().getAddress().setStreet1(shopper.getContract().getCustomer().getAddress().getStreet1());
         order.getInvoice().getAddress().setStreet2(shopper.getContract().getCustomer().getAddress().getStreet2());
         order.getInvoice().getAddress().setCountry("BR");
         order.getInvoice().getAddress().setCityName(shopper.getContract().getCustomer().getAddress().getCityName());
         order.getInvoice().getAddress().setPostalCode(shopper.getContract().getCustomer().getAddress().getPostalCode());
         order.getInvoice().getAddress().setStateOrProvince(shopper.getContract().getCustomer().getAddress().getStateOrProvince());
         order.getInvoice().getAddress().setPhone(shopper.getContract().getCustomer().getAddress().getPhone());

         for (final OfferRecord offerRecord : shopper.getCart().getRecords()) {
            final OrderRecord orderRecord = new OrderRecord();
            orderRecord.setProduct(offerRecord.getProduct());
            orderRecord.setQuantity(offerRecord.getQuantity());
            orderRecord.setOption(offerRecord.getOption());
            order.getRecords().add(orderRecord);
         }

         order = orderDataProvider.persist(order);
         order.setContract(shopper.getContract());
         order = orderDataProvider.merge(order);
         order = orderDataProvider.doCheckout(orderDataProvider.findById(order));
         order = orderDataProvider.findById(order);

         shopper.setContract(order.getContract());
         return order;
      }

      private void saveOrderAndDoCheckout() {
         final Shopper shopper = shopperDataProvider.find(new Shopper());
         final Order order = saveOrder(shopper);
         shopper.getCart().getRecords().clear();
         shopper.setOrderId(order.getOrderId());
         shopperDataProvider.merge(shopper);

         switch (orderDataProvider.getCheckOut()) {
         case PAGSEGURO:
            throw new RedirectToUrlException(System.getProperty(PAGSEGURO_CHECKOUT_URL_PROPERTY, PAGSEGURO_CHECKOUT_URL_PROPERTY_VALUE) + order.getToken());
         case PAY_PAL:
            throw new RedirectToUrlException(System.getProperty(PAYPAL_CHECKOUT_URL_PROPERTY, PAYPAL_CHECKOUT_URL_PROPERTY_VALUE) + order.getToken());
         }
      }
   }

   class SpecificationViewFragement extends Fragment {

      class State implements IClusterable {

         private static final long serialVersionUID = 537050311855603864L;

         private final String code;
         private final String name;

         public State(String code, String name) {
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

      private static final long serialVersionUID = 9159244637681177882L;

      public SpecificationViewFragement() {
         super("specificationCustomerViewFragement", "specificationViewFragement", SpecificationViewPanel.this, SpecificationViewPanel.this.getDefaultModel());
      }

      public List<State> getStatesOfBrazil() {
         final ArrayList<State> states = new ArrayList<>();
         final String[][] statesOfBrazil = new String[][] { { "AC", "Acre" }, { "AL", "Alagoas" }, { "AP", "Amapá" }, { "AM", "Amazonas" }, { "BA", "Bahia" }, { "CE", "Ceará" }, { "ES", "Espírito Santo" }, { "GO", "Goiás" }, { "MA", "Maranhão" },
            { "MT", "Mato Grosso" }, { "MS", "Mato Grosso do Sul" }, { "MG", "Minas Gerais" }, { "PA", "Pará" }, { "PB", "Paraíba" }, { "PR", "Paraná" }, { "PE", "Pernambuco" }, { "PI", "Piauí" }, { "RJ", "Rio de Janeiro" },
            { "RN", "Rio Grande do Norte" }, { "RS", "Rio Grande do Sul" }, { "RO", "Rondônia" }, { "RR", "Roraima" }, { "SC", "Santa Catarina" }, { "SP", "São Paulo" }, { "SE", "Sergipe" }, { "TO", "Tocantins" } };

         for (final String[] state : statesOfBrazil) {
            states.add(new State(state[0], state[1]));
         }

         return states;
      }

      @Override
      protected void onInitialize() {
         final BootstrapForm<Contract> customerEditForm = new BootstrapForm<Contract>("customerEditForm");

         customerEditForm.setModel(new CompoundPropertyModel<Contract>(Model.of(shopperDataProvider.find(new Shopper()).getContract())));
         customerEditForm.add(new RequiredTextField<String>("customer.buyerEmail").setLabel(Model.of(getString("buyerEmailMessage"))).add(EmailAddressValidator.getInstance()).add(StringValidator.maximumLength(60)));
         customerEditForm.add(new RequiredTextField<String>("customer.firstName").setLabel(Model.of(getString("firstNameMessage"))).add(StringValidator.maximumLength(40)));
         customerEditForm.add(new RequiredTextField<String>("customer.lastName").setLabel(Model.of(getString("lastNameMessage"))).add(StringValidator.maximumLength(40)));
         customerEditForm.add(new RequiredTextField<String>("customer.address.street1").setLabel(Model.of(getString("street1Message"))).add(StringValidator.maximumLength(40)));
         customerEditForm.add(new TextField<String>("customer.address.street2").add(StringValidator.maximumLength(40)));
         customerEditForm.add(new TextField<String>("customer.address.country", Model.of("Brasil")).setLabel(Model.of(getString("countryNameMessage"))).setEnabled(false));
         customerEditForm.add(new RequiredTextField<String>("customer.address.cityName").setLabel(Model.of(getString("cityNameMessage"))).add(StringValidator.maximumLength(40)));
         customerEditForm.add(new RequiredTextField<String>("customer.address.postalCode").setLabel(Model.of(getString("postalCodeMessage"))).add(StringValidator.maximumLength(15)));
         customerEditForm.add(new DropDownChoice<State>("customer.address.stateOrProvince", getStatesOfBrazil(), new ChoiceRenderer<State>("name", "")).setRequired(true).setLabel(Model.of(getString("stateOrProvinceMessage"))));

         final RadioGroup<CheckOut> paymentOptions = new RadioGroup<CheckOut>("paymentOption", new Model<CheckOut>(orderDataProvider.getCheckOut()));
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

         add(customerEditForm.setOutputMarkupId(true));
         add(offerRecordProductDataViewContainer.setOutputMarkupId(true));
         add(offerRecordDataviewContainer.setOutputMarkupId(true));
         add(offerRecordTotalDataviewContainer.add(new SaveAjaxButton(customerEditForm).setOutputMarkupId(true)).setOutputMarkupId(true));
         super.onInitialize();
      }
   }

   private static final long serialVersionUID = 293941244262646336L;

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   @SpringBean(name = "ContractDataProvider", required = true)
   private GenericTypeDataProvider<Contract> contractDataProvider;

   @SpringBean(name = "OrderDataProvider", required = true)
   private GenericOrderCheckoutDataProvider<Order> orderDataProvider;

   private final OfferRecordProductDataProvider offerRecordProductDataProvider = new OfferRecordProductDataProvider();

   protected WebMarkupContainer offerRecordProductDataViewContainer = new WebMarkupContainer("offerRecordProductDataViewContainer") {

      private static final long serialVersionUID = -497527332092449028L;

      @Override
      protected void onInitialize() {
         final int index = shopperDataProvider.find(new Shopper()).getCart().getRecords().size();

         if (index > 0) {
            offerRecordProductDataProvider.offerRecords.add(shopperDataProvider.find(new Shopper()).getCart().getRecords().get(0));
         }

         add(offerRecordProductDataView.setOutputMarkupId(true));
         super.onInitialize();
      }
   };

   protected WebMarkupContainer offerRecordTotalDataviewContainer = new WebMarkupContainer("offerRecordTotalDataviewContainer") {

      private static final long serialVersionUID = 1L;

      @Override
      protected void onInitialize() {
         add(totalDiscountLabel.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
         add(totalLabel.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
         add(totalShippingCost.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
         super.onInitialize();
      }
   };

   private final OfferRecordProductDataView offerRecordProductDataView = new OfferRecordProductDataView();

   private final OfferRecordDataProvider offerRecordDataProvider = new OfferRecordDataProvider();

   private final Label totalDiscountLabel = new Label("totalDiscount", Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getChartTotalDiscount())));

   private final Label totalLabel = new Label("total", Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getChartTotal())));

   private final Label totalShippingCost = new Label("totalShippingCost", Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getShippingCostTotal())));

   private final OfferRecordDataView offerRecordDataview = new OfferRecordDataView();

   protected WebMarkupContainer offerRecordDataviewContainer = new WebMarkupContainer("offerRecordDataviewContainer") {

      private static final long serialVersionUID = 1L;

      @Override
      protected void onInitialize() {
         add(offerRecordDataview.setOutputMarkupId(true));
         super.onInitialize();
      }
   };

   public SpecificationViewPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
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
      super.onInitialize();
   }
}
