package com.netbrasoft.gnuob.shop.wishlist;

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
import org.springframework.beans.BeanUtils;

import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.OfferRecord;
import com.netbrasoft.gnuob.api.Option;
import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.OrderRecord;
import com.netbrasoft.gnuob.api.Product;
import com.netbrasoft.gnuob.api.SubOption;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.api.generic.converter.CurrencyConverter;
import com.netbrasoft.gnuob.api.offer.OfferDataProvider;
import com.netbrasoft.gnuob.api.product.ProductDataProvider;
import com.netbrasoft.gnuob.shop.NetbrasoftShopMessageKeyConstants;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.page.SpecificationPage;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;
import com.netbrasoft.gnuob.shop.shopper.ShopperDataProvider;

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
public class WishListEmptyOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class WishtListEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
    class WishListsDataviewContainer extends WebMarkupContainer {

      class WishListsDataview extends DataView<Offer> {

        @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
        class WishListEditContainer extends WebMarkupContainer {

          @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
          class WishListEditTable extends WebMarkupContainer {

            @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
            class SaveAjaxButton extends BootstrapAjaxButton {

              private static final String VERSION_IGNORE_PROPERTIES = "version";

              private static final String ID_IGNORE_PROPERTIES = "id";

              private static final long serialVersionUID = -3090506205170780941L;

              public SaveAjaxButton(final String id, final IModel<String> model, final Form<Offer> form, final Type type) {
                super(id, model, form, type);
                setSize(Buttons.Size.Small);
              }

              @Override
              protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                final Offer sourceOffer = (Offer) form.getDefaultModelObject();
                final Order targetOrder = new Order();

                BeanUtils.copyProperties(sourceOffer, targetOrder, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES, "permission");
                for (final OfferRecord sourceOfferRecord : sourceOffer.getRecords()) {
                  final OrderRecord targetOrderRecord = new OrderRecord();
                  BeanUtils.copyProperties(sourceOfferRecord, targetOrderRecord);
                  for (final Option sourceOption : sourceOfferRecord.getOptions()) {
                    final Option targetOption = new Option();
                    BeanUtils.copyProperties(sourceOption, targetOption);
                    for (final SubOption sourceSubOption : sourceOption.getSubOptions()) {
                      final SubOption targetSubOption = new SubOption();
                      BeanUtils.copyProperties(sourceSubOption, targetSubOption);
                      targetOption.getSubOptions().add(targetSubOption);
                    }
                    targetOrderRecord.getOptions().add(targetOption);
                  }
                  targetOrder.getRecords().add(targetOrderRecord);
                }
                targetOrder.setOrderTotal(BigDecimal.valueOf(sourceOffer.getOfferTotal().doubleValue()));
                targetOrder.setOrderDescription(sourceOffer.getOfferDescription());
                shopperDataProvider.find(new Shopper()).setCheckout(targetOrder);
                super.onSubmit(target, form);
                throw new RedirectToUrlException(SpecificationPage.SPECIFICATION_HTML_VALUE);
              }
            }

            @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
            class WishListDataviewContainer extends WebMarkupContainer {

              class WishListDataview extends DataView<OfferRecord> {

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

                protected WishListDataview(final String id, final IDataProvider<OfferRecord> dataProvider, final long itemsPerPage) {
                  super(id, dataProvider, itemsPerPage);
                }

                private String getOptions(final List<Option> options) {
                  final StringBuilder optionStringBuilder = new StringBuilder();

                  for (final Option option : options) {
                    optionStringBuilder.append(option.getValue()).append(": ").append(option.getSubOptions().iterator().next().getValue()).append(" ");
                  }
                  return optionStringBuilder.toString();
                }

                @Override
                protected Item<OfferRecord> newItem(final String id, final int index, final IModel<OfferRecord> model) {
                  final Item<OfferRecord> item = super.newItem(id, index, model);
                  if (this.index == index) {
                    item.add(new AttributeModifier(CLASS_ATTRIBUTE, INFO_VALUE));
                  }
                  return item;
                }

                @Override
                protected void onConfigure() {
                  final IModel<Offer> model = (IModel<Offer>) WishListDataviewContainer.this.getDefaultModel();
                  if (!model.getObject().getRecords().isEmpty()) {
                    wishListViewOrEditPanel.removeAll();
                    wishListViewOrEditPanel.setSelectedModel(Model.of(model.getObject().getRecords().get(index)));
                    wishListViewOrEditPanel.add(wishListViewOrEditPanel.new WishListOfferRecordEditFragment()).setOutputMarkupId(true);
                  }
                  super.onConfigure();
                }

                @Override
                protected void populateItem(final Item<OfferRecord> item) {
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
                  final BigDecimal itemTotal = productAmount.add(productTax).subtract(productDiscount).multiply(quantity);
                  final BigDecimal amountTotal = productAmount.add(productTax).multiply(quantity);
                  final Label nameLabel = new Label(NAME_ID);
                  final Label optionsLabel = new Label(OPTIONS_ID, Model.of(getOptions(item.getModelObject().getOptions())));
                  final Label quantityLabel = new Label(QUANTITY_ID);
                  final Label itemTotalLabel = new Label(ITEM_TOTAL_ID, Model.of(NumberFormat.getCurrencyInstance().format(itemTotal)));
                  final Label amountLabel = new Label(AMOUNT_TOTAL_ID, Model.of(NumberFormat.getCurrencyInstance().format(amountTotal)));
                  final AjaxEventBehavior ajaxEventBehavior = new AjaxEventBehavior(CLICK_EVENT) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onEvent(final AjaxRequestTarget target) {
                      index = item.getIndex();
                      wishListViewOrEditPanel.setSelectedModel(item.getModel());
                      wishListViewOrEditPanel.removeAll();
                      target.add(wishListDataviewContainer.setOutputMarkupId(true));
                      target.add(wishListViewOrEditPanel.add(wishListViewOrEditPanel.new WishListOfferRecordEditFragment()).setOutputMarkupId(true));
                    }
                  };
                  item.setModel(new CompoundPropertyModel<OfferRecord>(item.getModelObject()));
                  item.add(nameLabel.setOutputMarkupId(true));
                  item.add(optionsLabel.setOutputMarkupId(true));
                  item.add(quantityLabel.setOutputMarkupId(true));
                  item.add(itemTotalLabel.setOutputMarkupId(true));
                  item.add(amountLabel.setOutputMarkupId(true));
                  item.add(ajaxEventBehavior);
                }
              }

              private static final String WISH_LIST_DATAVIEW_ID = "wishListDataview";

              private static final long serialVersionUID = -24892733113063092L;

              private final WishListDataview wishListDataview;

              private final ListDataProvider<OfferRecord> offerRecordDataProvider;

              public WishListDataviewContainer(final String id, final IModel<Offer> model) {
                super(id, model);
                offerRecordDataProvider = new ListDataProvider<OfferRecord>() {

                  private static final long serialVersionUID = -2868883399981331022L;

                  @Override
                  protected List<OfferRecord> getData() {
                    return ((Offer) WishListDataviewContainer.this.getDefaultModelObject()).getRecords();
                  }
                };
                wishListDataview = new WishListDataview(WISH_LIST_DATAVIEW_ID, offerRecordDataProvider, Integer.MAX_VALUE);
              }

              @Override
              protected void onInitialize() {
                add(wishListDataview.setOutputMarkupId(true));
                super.onInitialize();
              }
            }

            private static final String WISH_LIST_DATAVIEW_CONTAINER_ID = "wishListDataviewContainer";

            private static final String SHIPPING_TOTAL_ID = "shippingTotal";

            private static final String OFFER_TOTAL_ID = "offerTotal";

            private static final String DISCOUNT_TOTAL_ID = "discountTotal";

            private static final String FEEDBACK_ID = "feedback";

            private static final long serialVersionUID = 4709875628737072256L;

            private static final String SAVE_ID = "save";

            private final NotificationPanel feedbackPanel;

            private final Label discountTotalLabel;

            private final Label offerTotalLabel;

            private final Label shippingTotalLabel;

            private final SaveAjaxButton saveAjaxButton;

            private final WishListDataviewContainer wishListDataviewContainer;

            public WishListEditTable(final String id, final IModel<Offer> model) {
              super(id, new CompoundPropertyModel<Offer>(model));
              discountTotalLabel = new Label(DISCOUNT_TOTAL_ID) {

                private static final long serialVersionUID = -4143367505737220689L;

                @Override
                public <C> IConverter<C> getConverter(final Class<C> type) {
                  return (IConverter<C>) new CurrencyConverter();
                }
              };
              offerTotalLabel = new Label(OFFER_TOTAL_ID) {

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
              saveAjaxButton =
                  new SaveAjaxButton(SAVE_ID, Model.of(WishListEmptyOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.CHECKOUT_MESSAGE_KEY)), wishListEditForm, Type.Primary);
              feedbackPanel = new NotificationPanel(FEEDBACK_ID);
              wishListDataviewContainer = new WishListDataviewContainer(WISH_LIST_DATAVIEW_CONTAINER_ID, (IModel<Offer>) WishListEditTable.this.getDefaultModel());
            }

            @Override
            protected void onInitialize() {
              add(discountTotalLabel.setOutputMarkupId(true));
              add(offerTotalLabel.setOutputMarkupId(true));
              add(shippingTotalLabel.setOutputMarkupId(true));
              add(saveAjaxButton.setOutputMarkupId(true));
              add(feedbackPanel.setOutputMarkupId(true));
              add(wishListDataviewContainer.setOutputMarkupId(true));
              super.onInitialize();
            }
          }

          private static final String WISH_LIST_VIEW_OR_EDIT_PANEL_ID = "wishListViewOrEditPanel";

          private static final String OFFER_ID_ID = "offerId";

          private static final String WISH_LIST_EDIT_FORM_ID = "wishListEditForm";

          private static final String WISH_LIST_EDIT_TABLE_ID = "wishListEditTable";

          private static final long serialVersionUID = -5562628434664496089L;

          private final BootstrapForm<Offer> wishListEditForm;

          private final WishListEditTable wishListEditTable;

          private final WishListViewOrEditPanel wishListViewOrEditPanel;

          private final Label offerIdLabel;

          public WishListEditContainer(final String id, final IModel<Offer> model) {
            super(id, model);
            wishListEditForm = new BootstrapForm<Offer>(WISH_LIST_EDIT_FORM_ID, new CompoundPropertyModel<Offer>((IModel<Offer>) WishListEditContainer.this.getDefaultModel()));
            wishListViewOrEditPanel = new WishListViewOrEditPanel(WISH_LIST_VIEW_OR_EDIT_PANEL_ID, (IModel<Offer>) WishListEditContainer.this.getDefaultModel());
            wishListEditTable = new WishListEditTable(WISH_LIST_EDIT_TABLE_ID, (IModel<Offer>) WishListEditContainer.this.getDefaultModel());
            offerIdLabel = new Label(OFFER_ID_ID);
          }

          @Override
          protected void onInitialize() {
            wishListEditForm.add(offerIdLabel.setOutputMarkupId(true));
            wishListEditForm.add(wishListEditTable.add(new TableBehavior()).setOutputMarkupId(true));
            wishListEditForm.add(wishListViewOrEditPanel.setOutputMarkupId(true));
            wishListEditForm.add(new FormBehavior(FormType.Horizontal));
            add(wishListEditForm.setOutputMarkupId(true));
            super.onInitialize();
          }
        }

        private static final String WISH_LIST_EDIT_CONTAINER_ID = "wishListEditContainer";

        private static final long serialVersionUID = -3442697472532305469L;

        protected WishListsDataview(final String id, final IDataProvider<Offer> dataProvider, final long itemsPerPage) {
          super(id, dataProvider, itemsPerPage);
        }

        @Override
        protected void populateItem(final Item<Offer> item) {
          final WishListEditContainer wishListEditContainer = new WishListEditContainer(WISH_LIST_EDIT_CONTAINER_ID, item.getModel());
          item.add(wishListEditContainer.setOutputMarkupId(true));
        }
      }

      private static final String WISH_LISTS_DATAVIEW_ID = "wishListsDataview";

      private static final long serialVersionUID = -1964097272300182331L;

      private final WishListsDataview wishListsDataview;

      public WishListsDataviewContainer(final String id, final IModel<Offer> model) {
        super(id, model);
        wishListsDataview = new WishListsDataview(WISH_LISTS_DATAVIEW_ID, offerDataProvider, Integer.MAX_VALUE);
      }

      @Override
      protected void onInitialize() {
        add(wishListsDataview.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String WISH_LISTS_DATAVIEW_CONTAINER_ID = "wishListsDataviewContainer";

    private static final String WISH_LIST_EDIT_FRAGMENT_MARKUP_ID = "wishListEditFragment";

    private static final String WISH_LIST_EMPTY_OR_EDIT_FRAGMENT_ID = "wishListEmptyOrEditFragment";

    private static final long serialVersionUID = -6168511667223170398L;

    private final WishListsDataviewContainer wishListsDataviewContainer;

    public WishtListEditFragment() {
      super(WISH_LIST_EMPTY_OR_EDIT_FRAGMENT_ID, WISH_LIST_EDIT_FRAGMENT_MARKUP_ID, WishListEmptyOrEditPanel.this, WishListEmptyOrEditPanel.this.getDefaultModel());
      wishListsDataviewContainer = new WishListsDataviewContainer(WISH_LISTS_DATAVIEW_CONTAINER_ID, (IModel<Offer>) WishtListEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(wishListsDataviewContainer.setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class WishtListEmptyFragment extends Fragment {

    private static final String WISH_LIST_EMPTY_FRAGMENT_MARKUP_ID = "wishListEmptyFragment";

    private static final String WISH_LIST_EMPTY_OR_EDIT_FRAGMENT_ID = "wishListEmptyOrEditFragment";

    private static final long serialVersionUID = 5058607382122871571L;

    public WishtListEmptyFragment() {
      super(WISH_LIST_EMPTY_OR_EDIT_FRAGMENT_ID, WISH_LIST_EMPTY_FRAGMENT_MARKUP_ID, WishListEmptyOrEditPanel.this, WishListEmptyOrEditPanel.this.getDefaultModel());
    }
  }

  private static final long serialVersionUID = -4406441947235524118L;

  @SpringBean(name = ShopperDataProvider.SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  @SpringBean(name = OfferDataProvider.OFFER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeDataProvider<Offer> offerDataProvider;

  @SpringBean(name = ProductDataProvider.PRODUCT_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeDataProvider<Product> productDataProvider;

  public WishListEmptyOrEditPanel(final String id, final IModel<Offer> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    offerDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    offerDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    offerDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    offerDataProvider.setType(new Offer());
    offerDataProvider.getType().setActive(true);
    offerDataProvider.setOrderBy(OrderBy.NONE);
    offerDataProvider.getType().setContract(shopperDataProvider.find(new Shopper()).getContract());
    productDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    productDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    productDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    productDataProvider.setType(new Product());
    productDataProvider.getType().setActive(true);
    productDataProvider.setOrderBy(OrderBy.NONE);
    super.onInitialize();
  }
}
