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
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.springframework.beans.BeanUtils;

import com.google.common.net.MediaType;
import com.netbrasoft.gnuob.api.Address;
import com.netbrasoft.gnuob.api.Content;
import com.netbrasoft.gnuob.api.Contract;
import com.netbrasoft.gnuob.api.Invoice;
import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.OfferRecord;
import com.netbrasoft.gnuob.api.Option;
import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.OrderRecord;
import com.netbrasoft.gnuob.api.Shipment;
import com.netbrasoft.gnuob.api.SubOption;
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

@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class SpecificationViewPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class SpecificationViewFragement extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
    class OfferRecordContainer extends WebMarkupContainer {

      private static final int FIVE_STARS_RATING = 5;

      private static final long serialVersionUID = -1556817303531395170L;

      public OfferRecordContainer(IModel<OfferRecord> model) {
        super("offerRecordContainer", model);
      }

      @Override
      protected void onInitialize() {
        removeAll();

        final OfferRecord offerRecord = ((OfferRecord) getDefaultModelObject());
        final List<ICarouselImage> carouselImages = new ArrayList<ICarouselImage>();

        for (final Content content : offerRecord.getProduct().getContents()) {
          if (MediaType.HTML_UTF_8.is(MediaType.parse(content.getFormat()))) {
            carouselImages.add(new CarouselImage(new String(content.getContent())));
          }
        }

        add(new ProductCarousel("productCarousel", carouselImages).setOutputMarkupId(true).add(new PopoverBehavior(Model.of(getString("descriptionMessage")),
            Model.of(offerRecord.getProduct().getDescription()), new PopoverConfig().withHoverTrigger().withPlacement(Placement.left))));
        add(new Label("name"));
        add(new Label("product.stock.quantity"));
        add(new Label("amountWithDiscount",
            Model.of(NumberFormat.getCurrencyInstance().format(offerRecord.getProduct().getAmount().subtract(offerRecord.getProduct().getDiscount())))));
        add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(offerRecord.getProduct().getAmount()))).setOutputMarkupId(true));
        add(new Loop("rating", FIVE_STARS_RATING) {

          private static final long serialVersionUID = -443304621920358169L;

          @Override
          protected void populateItem(LoopItem loopItem) {
            loopItem.add(new IconBehavior(loopItem.getIndex() < offerRecord.getProduct().getRating() ? GlyphIconType.star : GlyphIconType.starempty));
          }
        });

        super.onInitialize();
      }
    }

    @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
    class OfferRecordDataviewContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
      class OfferRecordDataView extends DataView<OfferRecord> {

        private static final long serialVersionUID = -8885578770770605991L;

        private Item<OfferRecord> selectedItem;

        protected OfferRecordDataView() {
          super("offerRecordDataview", offerRecordListDataProvider);
        }

        @Override
        protected Item<OfferRecord> newItem(String id, int index, IModel<OfferRecord> model) {
          final Item<OfferRecord> item = super.newItem(id, index, model);

          if (selectedItem == null && index == 0) {
            selectedItem = item;
          }

          if (selectedItem.getIndex() == index) {
            item.add(new AttributeModifier("class", "info"));
          }

          return item;
        }

        @Override
        protected void populateItem(Item<OfferRecord> item) {
          final StringBuffer stringBuffer = new StringBuffer();
          final BigDecimal amount = item.getModelObject().getProduct().getAmount();
          final BigDecimal tax = item.getModelObject().getProduct().getTax();
          final BigDecimal discount = item.getModelObject().getProduct().getDiscount();
          final BigDecimal quantity = BigDecimal.valueOf(item.getModelObject().getQuantity().intValue());

          for (final Option option : item.getModelObject().getOptions()) {
            stringBuffer.append(option.getValue()).append(": ").append(option.getSubOptions().iterator().next().getValue()).append(" ");
          }

          item.setModel(new CompoundPropertyModel<OfferRecord>(item.getModelObject()));
          item.add(new Label("name").setOutputMarkupId(true));
          item.add(new Label("options", Model.of(stringBuffer.toString())).setOutputMarkupId(true));
          item.add(new Label("quantity").setOutputMarkupId(true));
          item.add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(amount.add(tax).subtract(discount).multiply(quantity))))
              .setOutputMarkupId(true));
          item.add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(amount.add(tax).multiply(quantity)))).setOutputMarkupId(true));
          item.add(new AjaxEventBehavior("click") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onEvent(AjaxRequestTarget target) {
              selectedItem = item;

              offerRecordContainer.setDefaultModel(item.getDefaultModel());
              offerRecordContainer.onInitialize();
              offerRecordDataviewContainer.onInitialize();

              target.add(offerRecordContainer.setOutputMarkupId(true));
              target.add(offerRecordDataviewContainer.setOutputMarkupId(true));
            }
          });
        }
      }

      class OfferRecordListDataProvider extends ListDataProvider<OfferRecord> {

        private static final long serialVersionUID = 3755475588885853693L;

        @Override
        public Iterator<OfferRecord> iterator(long first, long count) {
          final List<OfferRecord> offerRecordIteratorList = new ArrayList<OfferRecord>();

          for (int index = (int) first; index < first + count; index++) {
            offerRecordIteratorList.add(shopperDataProvider.find((Shopper) SpecificationViewPanel.this.getDefaultModelObject()).getCart().getRecords().get(index));
          }

          return offerRecordIteratorList.iterator();
        }

        @Override
        public long size() {
          return shopperDataProvider.find((Shopper) SpecificationViewPanel.this.getDefaultModelObject()).getCart().getRecords().size();
        }
      }

      private static final long serialVersionUID = 1843462579421164639L;

      private final OfferRecordListDataProvider offerRecordListDataProvider;

      private final OfferRecordDataView offerRecordDataview;

      public OfferRecordDataviewContainer() {
        super("offerRecordDataviewContainer");

        offerRecordListDataProvider = new OfferRecordListDataProvider();
        offerRecordDataview = new OfferRecordDataView();
      }

      @Override
      protected void onInitialize() {
        removeAll();

        add(offerRecordDataview.setOutputMarkupId(true));

        super.onInitialize();
      }
    }

    @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
    class OfferRecordTotalContainer extends WebMarkupContainer {

      private static final long serialVersionUID = 6545779558348445960L;

      public OfferRecordTotalContainer() {
        super("offerRecordTotalContainer");
      }

      @Override
      protected void onInitialize() {
        final BigDecimal totalDiscount = shopperDataProvider.find((Shopper) SpecificationViewPanel.this.getDefaultModelObject()).getCartTotalDiscount();
        final BigDecimal total = shopperDataProvider.find((Shopper) SpecificationViewPanel.this.getDefaultModelObject()).getCartTotal();
        final BigDecimal totalShippingCost = shopperDataProvider.find((Shopper) SpecificationViewPanel.this.getDefaultModelObject()).getShippingCostTotal();

        removeAll();

        add(new Label("totalDiscount", Model.of(NumberFormat.getCurrencyInstance().format(totalDiscount))).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
        add(new Label("total", Model.of(NumberFormat.getCurrencyInstance().format(total))).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
        add(new Label("totalShippingCost", Model.of(NumberFormat.getCurrencyInstance().format(totalShippingCost))).setOutputMarkupId(true)
            .setOutputMarkupPlaceholderTag(true));

        super.onInitialize();
      }
    }

    @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
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

      private void doCheckout(Shopper shopper) {
        if (CheckOut.PAGSEGURO.equals(orderDataProvider.getCheckOut())) {
          throw new RedirectToUrlException(System.getProperty(PAGSEGURO_CHECKOUT_URL_PROPERTY, PAGSEGURO_CHECKOUT_URL_PROPERTY_VALUE) + shopper.getCheckout().getToken());
        } else { // PayPal
          throw new RedirectToUrlException(System.getProperty(PAYPAL_CHECKOUT_URL_PROPERTY, PAYPAL_CHECKOUT_URL_PROPERTY_VALUE) + shopper.getCheckout().getToken());
        }
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
        saveContract((Contract) form.getDefaultModelObject(), shopperDataProvider.find((Shopper) SpecificationViewPanel.this.getDefaultModelObject()));
        saveOrder(shopperDataProvider.find((Shopper) SpecificationViewPanel.this.getDefaultModelObject()));
        doCheckout(shopperDataProvider.find((Shopper) SpecificationViewPanel.this.getDefaultModelObject()));
      }

      private void saveContract(Contract contract, Shopper shopper) {
        // FIXME; get this information from form and use geonames.
        contract.getCustomer().getAddress().setCountry("BR");

        if (contract.getId() == 0) {
          shopper.setContract(contractDataProvider.findById(contractDataProvider.persist(contract)));
        } else {
          shopper.setContract(contractDataProvider.findById(contractDataProvider.merge(contract)));
        }

        shopperDataProvider.merge(shopper);
      }

      private void saveOrder(Shopper shopper) {
        shopper.setCheckout(new Order());
        shopper.getCheckout().setActive(true);
        shopper.getCheckout().setCheckout(orderDataProvider.getCheckOut().toString());
        shopper.getCheckout().setInsuranceTotal(BigDecimal.ZERO);
        shopper.getCheckout().setHandlingTotal(BigDecimal.ZERO);
        shopper.getCheckout().setShippingDiscount(BigDecimal.ZERO);
        shopper.getCheckout().setInvoice(new Invoice());
        shopper.getCheckout().setShipment(new Shipment());
        shopper.getCheckout().getShipment().setAddress(new Address());
        shopper.getCheckout().getInvoice().setAddress(new Address());
        shopper.getCheckout().getShipment().getAddress().setStreet1(shopper.getContract().getCustomer().getAddress().getStreet1());
        shopper.getCheckout().getShipment().getAddress().setStreet2(shopper.getContract().getCustomer().getAddress().getStreet2());
        shopper.getCheckout().getShipment().getAddress().setCountry("BR");
        shopper.getCheckout().getShipment().getAddress().setCityName(shopper.getContract().getCustomer().getAddress().getCityName());
        shopper.getCheckout().getShipment().getAddress().setPostalCode(shopper.getContract().getCustomer().getAddress().getPostalCode());
        shopper.getCheckout().getShipment().getAddress().setStateOrProvince(shopper.getContract().getCustomer().getAddress().getStateOrProvince());
        shopper.getCheckout().getShipment().getAddress().setPhone(shopper.getContract().getCustomer().getAddress().getPhone());
        shopper.getCheckout().getInvoice().getAddress().setStreet1(shopper.getContract().getCustomer().getAddress().getStreet1());
        shopper.getCheckout().getInvoice().getAddress().setStreet2(shopper.getContract().getCustomer().getAddress().getStreet2());
        shopper.getCheckout().getInvoice().getAddress().setCountry("BR");
        shopper.getCheckout().getInvoice().getAddress().setCityName(shopper.getContract().getCustomer().getAddress().getCityName());
        shopper.getCheckout().getInvoice().getAddress().setPostalCode(shopper.getContract().getCustomer().getAddress().getPostalCode());
        shopper.getCheckout().getInvoice().getAddress().setStateOrProvince(shopper.getContract().getCustomer().getAddress().getStateOrProvince());
        shopper.getCheckout().getInvoice().getAddress().setPhone(shopper.getContract().getCustomer().getAddress().getPhone());

        for (final OfferRecord offerRecord : shopper.getCart().getRecords()) {
          final OrderRecord orderRecord = new OrderRecord();

          BeanUtils.copyProperties(offerRecord, orderRecord, "id", "version");

          for (final Option rootOption : offerRecord.getOptions()) {
            final Option orderRecordRootOption = new Option();

            BeanUtils.copyProperties(rootOption, orderRecordRootOption, "id", "version");

            for (final SubOption childSubOption : rootOption.getSubOptions()) {
              final SubOption orderRecordChildSubOption = new SubOption();

              BeanUtils.copyProperties(childSubOption, orderRecordChildSubOption, "id", "version");
              orderRecordRootOption.getSubOptions().add(orderRecordChildSubOption);
            }

            orderRecord.getOptions().add(orderRecordRootOption);
          }

          shopper.getCheckout().getRecords().add(orderRecord);
        }

        shopper.setCheckout(orderDataProvider.persist(shopper.getCheckout()));
        shopper.getCheckout().setContract(shopper.getContract());
        shopper.setCheckout(orderDataProvider.merge(shopper.getCheckout()));
        shopper.setCheckout(orderDataProvider.doCheckout(orderDataProvider.findById(shopper.getCheckout())));
        shopper.setCheckout(orderDataProvider.findById(shopper.getCheckout()));
        shopper.setContract(shopper.getCheckout().getContract());
        shopper.setCart(new Offer());

        shopperDataProvider.merge(shopper);
      }
    }

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

    private final OfferRecordContainer offerRecordContainer;

    private final OfferRecordDataviewContainer offerRecordDataviewContainer;

    private final OfferRecordTotalContainer offerRecordTotalContainer;

    public SpecificationViewFragement() {
      super("specificationCustomerViewFragement", "specificationViewFragement", SpecificationViewPanel.this, SpecificationViewPanel.this.getDefaultModel());

      offerRecordContainer = new OfferRecordContainer(new CompoundPropertyModel<OfferRecord>(
          Model.of(shopperDataProvider.find((Shopper) SpecificationViewPanel.this.getDefaultModelObject()).getCart().getRecords().get(0))));
      offerRecordDataviewContainer = new OfferRecordDataviewContainer();
      offerRecordTotalContainer = new OfferRecordTotalContainer();
    }

    public List<State> getStatesOfBrazil() {
      final List<State> states = new ArrayList<>();
      final String[][] statesOfBrazil = new String[][] {{"AC", "Acre"}, {"AL", "Alagoas"}, {"AP", "Amapá"}, {"AM", "Amazonas"}, {"BA", "Bahia"}, {"CE", "Ceará"},
          {"ES", "Espírito Santo"}, {"GO", "Goiás"}, {"MA", "Maranhão"}, {"MT", "Mato Grosso"}, {"MS", "Mato Grosso do Sul"}, {"MG", "Minas Gerais"}, {"PA", "Pará"},
          {"PB", "Paraíba"}, {"PR", "Paraná"}, {"PE", "Pernambuco"}, {"PI", "Piauí"}, {"RJ", "Rio de Janeiro"}, {"RN", "Rio Grande do Norte"}, {"RS", "Rio Grande do Sul"},
          {"RO", "Rondônia"}, {"RR", "Roraima"}, {"SC", "Santa Catarina"}, {"SP", "São Paulo"}, {"SE", "Sergipe"}, {"TO", "Tocantins"}};

      for (final String[] state : statesOfBrazil) {
        states.add(new State(state[0], state[1]));
      }

      return states;
    }

    @Override
    protected void onInitialize() {
      final BootstrapForm<Contract> customerEditForm = new BootstrapForm<Contract>("customerEditForm");

      customerEditForm.setModel(new CompoundPropertyModel<Contract>(Model.of(shopperDataProvider.find(new Shopper()).getContract())));
      customerEditForm.add(new RequiredTextField<String>("customer.buyerEmail").setLabel(Model.of(getString("buyerEmailMessage"))).add(EmailAddressValidator.getInstance())
          .add(StringValidator.maximumLength(60)));
      customerEditForm.add(new RequiredTextField<String>("customer.firstName").setLabel(Model.of(getString("firstNameMessage"))).add(StringValidator.maximumLength(40)));
      customerEditForm.add(new RequiredTextField<String>("customer.lastName").setLabel(Model.of(getString("lastNameMessage"))).add(StringValidator.maximumLength(40)));
      customerEditForm.add(new RequiredTextField<String>("customer.address.street1").setLabel(Model.of(getString("street1Message"))).add(StringValidator.maximumLength(40)));
      customerEditForm.add(new TextField<String>("customer.address.street2").add(StringValidator.maximumLength(40)));
      customerEditForm.add(new TextField<String>("customer.address.country", Model.of("Brasil")).setLabel(Model.of(getString("countryNameMessage"))).setEnabled(false));
      customerEditForm.add(new RequiredTextField<String>("customer.address.cityName").setLabel(Model.of(getString("cityNameMessage"))).add(StringValidator.maximumLength(40)));
      customerEditForm.add(new RequiredTextField<String>("customer.address.postalCode").setLabel(Model.of(getString("postalCodeMessage")))
          .add(new PatternValidator("([0-9]){5}([-])([0-9]){3}")));
      customerEditForm.add(new DropDownChoice<State>("customer.address.stateOrProvince", getStatesOfBrazil(), new ChoiceRenderer<State>("name", "")).setRequired(true)
          .setLabel(Model.of(getString("stateOrProvinceMessage"))));

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
      add(offerRecordContainer.setOutputMarkupId(true));
      add(offerRecordDataviewContainer.setOutputMarkupId(true));
      add(offerRecordTotalContainer.setOutputMarkupId(true));
      add(new SaveAjaxButton(customerEditForm).setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = 293941244262646336L;

  @SpringBean(name = "ShopperDataProvider", required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  @SpringBean(name = "ContractDataProvider", required = true)
  private GenericTypeDataProvider<Contract> contractDataProvider;

  @SpringBean(name = "OrderDataProvider", required = true)
  private GenericOrderCheckoutDataProvider<Order> orderDataProvider;

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
