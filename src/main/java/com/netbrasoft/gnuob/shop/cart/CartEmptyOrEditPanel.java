package com.netbrasoft.gnuob.shop.cart;

import static de.agilecoders.wicket.jquery.JQuery.$;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.head.IHeaderResponse;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.OfferRecord;
import com.netbrasoft.gnuob.api.Option;
import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.OrderRecord;
import com.netbrasoft.gnuob.api.SubOption;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.api.generic.converter.CurrencyConverter;
import com.netbrasoft.gnuob.api.offer.OfferDataProvider;
import com.netbrasoft.gnuob.shop.NetbrasoftShopMessageKeyConstants;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.page.SpecificationPage;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;
import com.netbrasoft.gnuob.shop.shopper.ShopperDataProvider;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationConfig;

/**
 * Wicket panel for viewing, selecting and editing {@link Offer} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CartEmptyOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class CartEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
    class CartEditContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
      class CartEditTable extends WebMarkupContainer {

        @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
        class CartDataviewContainer extends WebMarkupContainer {

          class CartDataView extends DataView<OfferRecord> {

            @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
            class RemoveAjaxButton extends BootstrapAjaxLink<OfferRecord> {

              private static final long serialVersionUID = 1090211687798345558L;

              private static final String CONFIRMATION_FUNCTION_NAME = "confirmation";

              public RemoveAjaxButton(final String id, final IModel<OfferRecord> model, final Buttons.Type type, final IModel<String> labelModel) {
                super(id, model, type, labelModel);
                setIconType(GlyphIconType.remove);
                setSize(Buttons.Size.Mini);
              }

              @Override
              public void onClick(final AjaxRequestTarget target) {
                ((Offer) CartDataviewContainer.this.getDefaultModelObject()).getRecords().remove(RemoveAjaxButton.this.getDefaultModelObject());
                shopperDataProvider.find(new Shopper()).calculateCart();
                index = index > 0 ? --index : index;

                if (!((Offer) CartDataviewContainer.this.getDefaultModelObject()).getRecords().isEmpty()) {
                  cartViewOrEditPanel.removeAll();
                  target.add(cartEditTable.setOutputMarkupId(true));
                  target.add(cartViewOrEditPanel.add(cartViewOrEditPanel.new CartOfferRecordEditFragment().setOutputMarkupId(true)).setOutputMarkupId(true));
                } else {
                  CartEmptyOrEditPanel.this.removeAll();
                  target.add(CartEmptyOrEditPanel.this.add(CartEmptyOrEditPanel.this.new CartEmptyFragment()));
                }
              }

              @Override
              protected void onInitialize() {
                final ConfirmationBehavior confirmationBehavior = new ConfirmationBehavior() {

                  private static final long serialVersionUID = 7744720444161839031L;

                  @Override
                  public void renderHead(final Component component, final IHeaderResponse response) {
                    response.render($(component).chain(CONFIRMATION_FUNCTION_NAME,
                        new ConfirmationConfig().withTitle(getString(NetbrasoftShopMessageKeyConstants.CONFIRMATION_TITLE_MESSAGE_KEY)).withSingleton(true).withPopout(true)
                            .withBtnOkLabel(getString(NetbrasoftShopMessageKeyConstants.CONFIRM_MESSAGE_KEY))
                            .withBtnCancelLabel(getString(NetbrasoftShopMessageKeyConstants.CANCEL_MESSAGE_KEY)))
                        .asDomReadyScript());
                  }
                };
                add(confirmationBehavior);
                super.onInitialize();
              }
            }

            private static final String AMOUNT_TOTAL_ID = "amountTotal";

            private static final String ITEM_TOTAL_ID = "itemTotal";

            private static final String QUANTITY_ID = "quantity";

            private static final String OPTIONS_ID = "options";

            private static final String NAME_ID = "name";

            private static final long serialVersionUID = -8885578770770605991L;

            private static final String REMOVE_ID = "remove";

            private static final String CLICK_EVENT = "click";

            private static final String INFO_VALUE = "info";

            private static final String CLASS_ATTRIBUTE = "class";

            private int index = 0;

            protected CartDataView(final String id, final IDataProvider<OfferRecord> dataProvider, final long itemsPerPage) {
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
              final IModel<Offer> model = (IModel<Offer>) CartDataviewContainer.this.getDefaultModel();
              if (!model.getObject().getRecords().isEmpty()) {
                cartViewOrEditPanel.removeAll();
                cartViewOrEditPanel.setSelectedModel(Model.of(model.getObject().getRecords().get(index)));
                cartViewOrEditPanel.add(cartViewOrEditPanel.new CartOfferRecordEditFragment()).setOutputMarkupId(true);
              }
              super.onConfigure();
            }

            @Override
            protected void populateItem(final Item<OfferRecord> item) {
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
                  cartViewOrEditPanel.setSelectedModel(item.getModel());
                  cartViewOrEditPanel.removeAll();
                  target.add(cartDataviewContainer.setOutputMarkupId(true));
                  target.add(cartViewOrEditPanel.add(cartViewOrEditPanel.new CartOfferRecordEditFragment()).setOutputMarkupId(true));
                }
              };
              final RemoveAjaxButton removeAjaxButton = new RemoveAjaxButton(REMOVE_ID, item.getModel(), Buttons.Type.Default, Model.of(""));
              item.setModel(new CompoundPropertyModel<OfferRecord>(item.getModelObject()));
              item.add(nameLabel.setOutputMarkupId(true));
              item.add(optionsLabel.setOutputMarkupId(true));
              item.add(quantityLabel.setOutputMarkupId(true));
              item.add(itemTotalLabel.setOutputMarkupId(true));
              item.add(amountLabel.setOutputMarkupId(true));
              item.add(removeAjaxButton.setOutputMarkupId(true));
              item.add(ajaxEventBehavior);
            }
          }

          private static final String CART_DATAVIEW_ID = "cartDataview";

          private static final long serialVersionUID = 1843462579421164639L;

          private final ListDataProvider<OfferRecord> cartListDataProvider;

          private final CartDataView cartDataview;

          public CartDataviewContainer(final String id, final IModel<Offer> model) {
            super(id, model);
            cartListDataProvider = new ListDataProvider<OfferRecord>() {

              private static final long serialVersionUID = -3261859241046697057L;

              @Override
              protected List<OfferRecord> getData() {
                return ((Offer) CartDataviewContainer.this.getDefaultModelObject()).getRecords();
              }
            };
            cartDataview = new CartDataView(CART_DATAVIEW_ID, cartListDataProvider, Integer.MAX_VALUE);
          }

          @Override
          protected void onInitialize() {
            add(cartDataview.setOutputMarkupId(true));
            super.onInitialize();
          }
        }

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

            BeanUtils.copyProperties(sourceOffer, targetOrder, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
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
        class SaveAsOfferAjaxLink extends BootstrapAjaxLink<Offer> {

          private static final long serialVersionUID = 6184459006667863564L;

          public SaveAsOfferAjaxLink(final String id, final IModel<Offer> model, final Type type, final IModel<String> labelModel) {
            super(id, model, type, labelModel);
            setIconType(GlyphIconType.plus);
            setSize(Buttons.Size.Small);
          }

          @Override
          public void onClick(final AjaxRequestTarget target) {
            try {
              if (((Offer) SaveAsOfferAjaxLink.this.getDefaultModelObject()).getId() == 0) {
                CartEditTable.this.setDefaultModelObject(offerDataProvider.findById(offerDataProvider.persist((Offer) SaveAsOfferAjaxLink.this.getDefaultModelObject())));
              } else {
                CartEditTable.this.setDefaultModelObject(offerDataProvider.findById(offerDataProvider.merge((Offer) SaveAsOfferAjaxLink.this.getDefaultModelObject())));
              }
              CartEmptyOrEditPanel.this.removeAll();
              target.add(CartEmptyOrEditPanel.this.add(CartEmptyOrEditPanel.this.new CartEditFragment()).setOutputMarkupId(true));
            } catch (final RuntimeException e) {
              LOGGER.warn(e.getMessage(), e);
              feedbackPanel.warn(e.getLocalizedMessage());
              target.add(feedbackPanel.setOutputMarkupId(true));
            }
          }
        }

        private static final String SAVE_ID = "save";

        private static final String SHIPPING_TOTAL_ID = "shippingTotal";

        private static final String OFFER_TOTAL_ID = "offerTotal";

        private static final String DISCOUNT_TOTAL_ID = "discountTotal";

        private static final String FEEDBACK_ID = "feedback";

        private static final String SAVE_AS_OFFER_ID = "saveAsOffer";

        private static final String CART_DATAVIEW_CONTAINER_ID = "cartDataviewContainer";

        private static final long serialVersionUID = -4127747804312130801L;

        private final NotificationPanel feedbackPanel;

        private final Label discountTotalLabel;

        private final Label offerTotalLabel;

        private final Label shippingTotalLabel;

        private final SaveAsOfferAjaxLink saveAsOfferAjaxButton;

        private final SaveAjaxButton saveAjaxButton;

        private final CartDataviewContainer cartDataviewContainer;

        public CartEditTable(final String id, final IModel<Offer> model) {
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
          saveAsOfferAjaxButton = new SaveAsOfferAjaxLink(SAVE_AS_OFFER_ID, (IModel<Offer>) CartEditTable.this.getDefaultModel(), Buttons.Type.Default,
              Model.of(CartEmptyOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.ADD_TO_WISH_LIST_MESSAGE_KEY)));
          saveAjaxButton =
              new SaveAjaxButton(SAVE_ID, Model.of(CartEmptyOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.CHECKOUT_MESSAGE_KEY)), cartEditForm, Type.Primary);
          feedbackPanel = new NotificationPanel(FEEDBACK_ID);
          cartDataviewContainer = new CartDataviewContainer(CART_DATAVIEW_CONTAINER_ID, (IModel<Offer>) CartEditTable.this.getDefaultModel());
        }

        @Override
        protected void onInitialize() {
          final Shopper shopper = shopperDataProvider.find(new Shopper());
          add(discountTotalLabel.setOutputMarkupId(true));
          add(offerTotalLabel.setOutputMarkupId(true));
          add(shippingTotalLabel.setOutputMarkupId(true));
          add(saveAsOfferAjaxButton.setVisible(shopper.isLoggedIn()).setOutputMarkupId(true));
          add(saveAjaxButton.setOutputMarkupId(true));
          add(feedbackPanel.setOutputMarkupId(true));
          add(cartDataviewContainer.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      private static final String CART_EDIT_FORM_COMPONENT_ID = "cartEditForm";

      private static final long serialVersionUID = 3702189627576121189L;

      private static final String CART_EDIT_TABLE = "cartEditTable";

      private final CartViewOrEditPanel cartViewOrEditPanel;

      private final CartEditTable cartEditTable;

      private final BootstrapForm<Offer> cartEditForm;

      public CartEditContainer(final String id, final IModel<Offer> model) {
        super(id, model);
        cartViewOrEditPanel = new CartViewOrEditPanel(CART_VIEW_OR_EDIT_PANEL_ID, (IModel<Offer>) CartEditContainer.this.getDefaultModel());
        cartEditTable = new CartEditTable(CART_EDIT_TABLE, (IModel<Offer>) CartEditContainer.this.getDefaultModel());
        cartEditForm = new BootstrapForm<Offer>(CART_EDIT_FORM_COMPONENT_ID, (IModel<Offer>) CartEditContainer.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
        cartEditForm.add(cartViewOrEditPanel.add(cartViewOrEditPanel.new CartOfferRecordEditFragment().setOutputMarkupId(true)).setOutputMarkupId(true));
        cartEditForm.add(cartEditTable.add(new TableBehavior()).setOutputMarkupId(true));
        cartEditForm.add(new FormBehavior(FormType.Horizontal));
        add(cartEditForm.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String CART_VIEW_OR_EDIT_PANEL_ID = "cartViewOrEditPanel";

    private static final String CART_EMPTY_OR_EDIT_FRAGMENT_ID = "cartEmptyOrEditFragment";

    private static final String CART_EDIT_FRAGMENT_MARKUP_ID = "cartEditFragment";

    private static final long serialVersionUID = -5518685687286043845L;

    private final CartEditContainer cartEditContainer;

    public CartEditFragment() {
      super(CART_EMPTY_OR_EDIT_FRAGMENT_ID, CART_EDIT_FRAGMENT_MARKUP_ID, CartEmptyOrEditPanel.this, CartEmptyOrEditPanel.this.getDefaultModel());
      cartEditContainer = new CartEditContainer("cartEditContainer", (IModel<Offer>) CartEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(cartEditContainer.setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class CartEmptyFragment extends Fragment {

    private static final String CART_EMPTY_OR_EDIT_FRAGMENT_ID = "cartEmptyOrEditFragment";

    private static final String CART_EMPTY_FRAGMENT_MARKUP_ID = "cartEmptyFragment";

    private static final long serialVersionUID = 5058607382122871571L;

    public CartEmptyFragment() {
      super(CART_EMPTY_OR_EDIT_FRAGMENT_ID, CART_EMPTY_FRAGMENT_MARKUP_ID, CartEmptyOrEditPanel.this, CartEmptyOrEditPanel.this.getDefaultModel());
    }
  }

  private static final long serialVersionUID = 6183635879900747064L;

  private static final Logger LOGGER = LoggerFactory.getLogger(CartEmptyOrEditPanel.class);

  @SpringBean(name = ShopperDataProvider.SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  @SpringBean(name = OfferDataProvider.OFFER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeDataProvider<Offer> offerDataProvider;

  public CartEmptyOrEditPanel(final String id, final IModel<Offer> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    offerDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    offerDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    offerDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    offerDataProvider.setType(new Offer());
    offerDataProvider.getType().setActive(true);
    super.onInitialize();
  }
}
