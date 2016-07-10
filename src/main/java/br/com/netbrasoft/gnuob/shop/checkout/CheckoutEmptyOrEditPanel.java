package br.com.netbrasoft.gnuob.shop.checkout;

import static br.com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.ORDER_DATA_PROVIDER_NAME;
import static br.com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.PRODUCT_DATA_PROVIDER_NAME;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
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

import br.com.netbrasoft.gnuob.api.Option;
import br.com.netbrasoft.gnuob.api.Order;
import br.com.netbrasoft.gnuob.api.OrderBy;
import br.com.netbrasoft.gnuob.api.OrderRecord;
import br.com.netbrasoft.gnuob.api.Product;
import br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants;
import br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.page.SpecificationPage;
import br.com.netbrasoft.gnuob.shop.security.ShopRoles;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;
import br.com.netbrasoft.gnuob.shop.shopper.ShopperDataProvider;

import br.com.netbrasoft.gnuob.api.generic.IGenericTypeDataProvider;
import br.com.netbrasoft.gnuob.api.generic.converter.CurrencyConverter;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CheckoutEmptyOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class CheckoutEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
    class CheckoutsDataviewContainer extends WebMarkupContainer {

      class CheckoutsDataview extends DataView<Order> {

        @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
        class CheckoutEditContainer extends WebMarkupContainer {

          @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
          class CheckoutEditTable extends WebMarkupContainer {

            @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
            class CheckoutDataviewContainer extends WebMarkupContainer {

              class CheckoutDataview extends DataView<OrderRecord> {

                private static final long serialVersionUID = 4734944728359967090L;

                private static final String AMOUNT_TOTAL_ID = "amountTotal";

                private static final String ITEM_TOTAL_ID = "itemTotal";

                private static final String QUANTITY_ID = "quantity";

                private static final String OPTIONS_ID = "options";

                private static final String NAME_ID = "name";

                private static final String CLICK_EVENT = "click";

                private static final String INFO_VALUE = "info";

                private static final String CLASS_ATTRIBUTE = "class";

                private int index = 0;

                protected CheckoutDataview(final String id, final IDataProvider<OrderRecord> dataProvider,
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
                  final IModel<Order> model = (IModel<Order>) CheckoutDataviewContainer.this.getDefaultModel();
                  if (!model.getObject().getRecords().isEmpty()) {
                    checkoutViewOrEditPanel.removeAll();
                    checkoutViewOrEditPanel.setSelectedModel(Model.of(model.getObject().getRecords().get(index)));
                    checkoutViewOrEditPanel.add(checkoutViewOrEditPanel.new CheckoutOrderRecordEditFragment())
                        .setOutputMarkupId(true);
                  }
                  super.onConfigure();
                }

                @Override
                protected void populateItem(final Item<OrderRecord> item) {
                  if (item.getModelObject().getProduct() == null) {
                    productDataProvider.getType().setNumber(item.getModelObject().getProductNumber());
                    if (productDataProvider.size() > 0) {
                      item.getModelObject().setProduct(productDataProvider.iterator(0, 1).next());
                    }
                  }
                  final BigDecimal productAmount = item.getModelObject().getProduct().getAmount();
                  final BigDecimal productTax = item.getModelObject().getProduct().getTax();
                  final BigDecimal productDiscount = item.getModelObject().getProduct().getDiscount();
                  final BigDecimal quantity = BigDecimal.valueOf(item.getModelObject().getQuantity().intValue());
                  final BigDecimal itemTotal =
                      productAmount.add(productTax).subtract(productDiscount).multiply(quantity);
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
                      checkoutViewOrEditPanel.setSelectedModel(item.getModel());
                      checkoutViewOrEditPanel.removeAll();
                      target.add(checkoutDataviewContainer.setOutputMarkupId(true));
                      target.add(checkoutViewOrEditPanel
                          .add(checkoutViewOrEditPanel.new CheckoutOrderRecordEditFragment()).setOutputMarkupId(true));
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

              private static final String CHECKOUT_DATAVIEW_ID = "checkoutDataview";

              private static final long serialVersionUID = -24892733113063092L;

              private final CheckoutDataview checkoutDataview;

              private final ListDataProvider<OrderRecord> orderRecordDataProvider;

              public CheckoutDataviewContainer(final String id, final IModel<Order> model) {
                super(id, model);
                orderRecordDataProvider = new ListDataProvider<OrderRecord>() {

                  private static final long serialVersionUID = -2868883399981331022L;

                  @Override
                  protected List<OrderRecord> getData() {
                    return ((Order) CheckoutDataviewContainer.this.getDefaultModelObject()).getRecords();
                  }
                };
                checkoutDataview =
                    new CheckoutDataview(CHECKOUT_DATAVIEW_ID, orderRecordDataProvider, Integer.MAX_VALUE);
              }

              @Override
              protected void onInitialize() {
                add(checkoutDataview.setOutputMarkupId(true));
                super.onInitialize();
              }
            }

            @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
            class SaveAjaxButton extends BootstrapAjaxButton {

              private static final long serialVersionUID = -3090506205170780941L;

              public SaveAjaxButton(final String id, final IModel<String> model, final Form<Order> form,
                  final Type type) {
                super(id, model, form, type);
                setSize(Buttons.Size.Small);
              }

              @Override
              protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                shopperDataProvider.find(new Shopper()).setCheckout((Order) form.getDefaultModelObject());
                super.onSubmit(target, form);
                throw new RedirectToUrlException(SpecificationPage.SPECIFICATION_HTML_VALUE);
              }
            }

            private static final String CHECKOUT_DATAVIEW_CONTAINER_ID = "checkoutDataviewContainer";

            private static final String SHIPPING_TOTAL_ID = "shippingTotal";

            private static final String OFFER_TOTAL_ID = "orderTotal";

            private static final String DISCOUNT_TOTAL_ID = "discountTotal";

            private static final String FEEDBACK_ID = "feedback";

            private static final long serialVersionUID = 4709875628737072256L;

            private static final String SAVE_ID = "save";

            private final NotificationPanel feedbackPanel;

            private final Label discountTotalLabel;

            private final Label orderTotalLabel;

            private final Label shippingTotalLabel;

            private final SaveAjaxButton saveAjaxButton;

            private final CheckoutDataviewContainer checkoutDataviewContainer;

            public CheckoutEditTable(final String id, final IModel<Order> model) {
              super(id, new CompoundPropertyModel<Order>(model));
              discountTotalLabel = new Label(DISCOUNT_TOTAL_ID) {

                private static final long serialVersionUID = -4143367505737220689L;

                @Override
                public <C> IConverter<C> getConverter(final Class<C> type) {
                  return (IConverter<C>) new CurrencyConverter();
                }
              };
              orderTotalLabel = new Label(OFFER_TOTAL_ID) {

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
                  Model.of(CheckoutEmptyOrEditPanel.this.getString(NetbrasoftShopConstants.CHECKOUT_MESSAGE_KEY)),
                  checkoutEditForm, Type.Primary);
              feedbackPanel = new NotificationPanel(FEEDBACK_ID);
              checkoutDataviewContainer = new CheckoutDataviewContainer(CHECKOUT_DATAVIEW_CONTAINER_ID,
                  (IModel<Order>) CheckoutEditTable.this.getDefaultModel());
            }

            @Override
            protected void onInitialize() {
              add(discountTotalLabel.setOutputMarkupId(true));
              add(orderTotalLabel.setOutputMarkupId(true));
              add(shippingTotalLabel.setOutputMarkupId(true));
              add(saveAjaxButton.setOutputMarkupId(true));
              add(feedbackPanel.setOutputMarkupId(true));
              add(checkoutDataviewContainer.setOutputMarkupId(true));
              super.onInitialize();
            }
          }

          private static final String CHECKOUT_VIEW_OR_EDIT_PANEL_ID = "checkoutViewOrEditPanel";

          private static final String OFFER_ID_ID = "orderId";

          private static final String CHECKOUT_EDIT_FORM_ID = "checkoutEditForm";

          private static final String CHECKOUT_EDIT_TABLE_ID = "checkoutEditTable";

          private static final long serialVersionUID = -5562628434664496089L;

          private final BootstrapForm<Order> checkoutEditForm;

          private final CheckoutEditTable checkoutEditTable;

          private final CheckoutViewOrEditPanel checkoutViewOrEditPanel;

          private final Label orderIdLabel;

          public CheckoutEditContainer(final String id, final IModel<Order> model) {
            super(id, model);
            checkoutEditForm = new BootstrapForm<Order>(CHECKOUT_EDIT_FORM_ID,
                new CompoundPropertyModel<Order>((IModel<Order>) CheckoutEditContainer.this.getDefaultModel()));
            checkoutViewOrEditPanel = new CheckoutViewOrEditPanel(CHECKOUT_VIEW_OR_EDIT_PANEL_ID,
                (IModel<Order>) CheckoutEditContainer.this.getDefaultModel());
            checkoutEditTable = new CheckoutEditTable(CHECKOUT_EDIT_TABLE_ID,
                (IModel<Order>) CheckoutEditContainer.this.getDefaultModel());
            orderIdLabel = new Label(OFFER_ID_ID);
          }

          @Override
          protected void onInitialize() {
            checkoutEditForm.add(orderIdLabel.setOutputMarkupId(true));
            checkoutEditForm.add(checkoutEditTable.add(new TableBehavior()).setOutputMarkupId(true));
            checkoutEditForm.add(checkoutViewOrEditPanel.setOutputMarkupId(true));
            checkoutEditForm.add(new FormBehavior(FormType.Horizontal));
            add(checkoutEditForm.setOutputMarkupId(true));
            super.onInitialize();
          }
        }

        private static final String CHECKOUT_EDIT_CONTAINER_ID = "checkoutEditContainer";

        private static final long serialVersionUID = -3442697472532305469L;

        protected CheckoutsDataview(final String id, final IDataProvider<Order> dataProvider, final long itemsPerPage) {
          super(id, dataProvider, itemsPerPage);
        }

        @Override
        protected void populateItem(final Item<Order> item) {
          final CheckoutEditContainer checkoutEditContainer =
              new CheckoutEditContainer(CHECKOUT_EDIT_CONTAINER_ID, item.getModel());
          item.add(checkoutEditContainer.setOutputMarkupId(true));
        }
      }

      private static final String CHECKOUTS_DATAVIEW_ID = "checkoutsDataview";

      private static final long serialVersionUID = -1964097272300182331L;

      private final CheckoutsDataview checkoutsDataview;

      public CheckoutsDataviewContainer(final String id, final IModel<Order> model) {
        super(id, model);
        checkoutsDataview = new CheckoutsDataview(CHECKOUTS_DATAVIEW_ID, orderDataProvider, Integer.MAX_VALUE);
      }

      @Override
      protected void onInitialize() {
        add(checkoutsDataview.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String CHECKOUTS_DATAVIEW_CONTAINER_ID = "checkoutsDataviewContainer";

    private static final String CHECKOUT_EDIT_FRAGMENT_MARKUP_ID = "checkoutEditFragment";

    private static final String CHECKOUT_EMPTY_OR_EDIT_FRAGMENT_ID = "checkoutEmptyOrEditFragment";

    private static final long serialVersionUID = -6168511667223170398L;

    private final CheckoutsDataviewContainer checkoutsDataviewContainer;

    public CheckoutEditFragment() {
      super(CHECKOUT_EMPTY_OR_EDIT_FRAGMENT_ID, CHECKOUT_EDIT_FRAGMENT_MARKUP_ID, CheckoutEmptyOrEditPanel.this,
          CheckoutEmptyOrEditPanel.this.getDefaultModel());
      checkoutsDataviewContainer = new CheckoutsDataviewContainer(CHECKOUTS_DATAVIEW_CONTAINER_ID,
          (IModel<Order>) CheckoutEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(checkoutsDataviewContainer.setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class CheckoutEmptyFragment extends Fragment {

    private static final String CHECKOUT_EMPTY_FRAGMENT_MARKUP_ID = "checkoutEmptyFragment";

    private static final String CHECKOUT_EMPTY_OR_EDIT_FRAGMENT_ID = "checkoutEmptyOrEditFragment";

    private static final long serialVersionUID = 5058607382122871571L;

    public CheckoutEmptyFragment() {
      super(CHECKOUT_EMPTY_OR_EDIT_FRAGMENT_ID, CHECKOUT_EMPTY_FRAGMENT_MARKUP_ID, CheckoutEmptyOrEditPanel.this,
          CheckoutEmptyOrEditPanel.this.getDefaultModel());
    }
  }

  private static final long serialVersionUID = -4406441947235524118L;

  @SpringBean(name = ShopperDataProvider.SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  @SpringBean(name = ORDER_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Order> orderDataProvider;

  @SpringBean(name = PRODUCT_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Product> productDataProvider;

  public CheckoutEmptyOrEditPanel(final String id, final IModel<Order> model) {
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
    orderDataProvider.getType().setContract(shopperDataProvider.find(new Shopper()).getContract());
    productDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    productDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    productDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    productDataProvider.setType(new Product());
    productDataProvider.getType().setActive(true);
    productDataProvider.setOrderBy(OrderBy.NONE);
    super.onInitialize();
  }
}
