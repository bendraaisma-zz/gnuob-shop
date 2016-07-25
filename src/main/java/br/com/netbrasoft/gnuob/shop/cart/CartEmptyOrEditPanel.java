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

package br.com.netbrasoft.gnuob.shop.cart;

import static br.com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.OFFER_DATA_PROVIDER_NAME;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ADD_TO_WISH_LIST_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.AMOUNT_TOTAL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CANCEL_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CART_DATAVIEW_CONTAINER_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CART_DATAVIEW_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CART_EDIT_CONTAINER_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CART_EDIT_FORM_COMPONENT_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CART_EDIT_FRAGMENT_MARKUP_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CART_EDIT_TABLE;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CART_EMPTY_FRAGMENT_MARKUP_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CART_EMPTY_OR_EDIT_FRAGMENT_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CART_VIEW_OR_EDIT_PANEL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CHECKOUT_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CLASS_ATTRIBUTE;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CLICK_EVENT;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CONFIRMATION_FUNCTION_NAME;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CONFIRMATION_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CONFIRM_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.DISCOUNT_TOTAL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.FEEDBACK_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.INFO_VALUE;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ITEM_TOTAL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.NAME_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.OFFER_TOTAL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.OPTIONS_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.QUANTITY_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.REMOVE_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SAVE_AS_OFFER_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SAVE_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SHIPPING_TOTAL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SHOPPER_DATA_PROVIDER_NAME;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.UNCHECKED;
import static br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession.getPassword;
import static br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession.getSite;
import static br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession.getUserName;
import static br.com.netbrasoft.gnuob.shop.security.ShopRoles.GUEST;
import static de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Size.Mini;
import static de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Size.Small;
import static de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type.Default;
import static de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type.Primary;
import static de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType.Horizontal;
import static de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType.plus;
import static de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType.remove;
import static de.agilecoders.wicket.jquery.JQuery.$;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.slf4j.LoggerFactory.getLogger;

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

import br.com.netbrasoft.gnuob.api.Offer;
import br.com.netbrasoft.gnuob.api.OfferRecord;
import br.com.netbrasoft.gnuob.api.Option;
import br.com.netbrasoft.gnuob.api.generic.IGenericTypeDataProvider;
import br.com.netbrasoft.gnuob.api.generic.converter.CurrencyConverter;
import br.com.netbrasoft.gnuob.shop.cart.CartViewOrEditPanel.CartOfferRecordEditFragment;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.page.SpecificationPage;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationConfig;

@SuppressWarnings(UNCHECKED)
@AuthorizeAction(action = Action.RENDER, roles = {GUEST})
public class CartEmptyOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
  class CartEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
    class CartEditContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
      class CartEditTable extends WebMarkupContainer {

        @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
        class CartDataviewContainer extends WebMarkupContainer {

          class CartDataView extends DataView<OfferRecord> {

            @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
            class RemoveAjaxButton extends BootstrapAjaxLink<OfferRecord> {

              private static final long serialVersionUID = 1090211687798345558L;

              public RemoveAjaxButton(final String id, final IModel<OfferRecord> model, final Buttons.Type type,
                  final IModel<String> labelModel) {
                super(id, model, type, labelModel);
              }

              @Override
              public void onClick(final AjaxRequestTarget target) {
                ((Offer) CartDataviewContainer.this.getDefaultModelObject()).getRecords()
                    .remove(RemoveAjaxButton.this.getDefaultModelObject());
                shopperDataProvider.find(Shopper.getInstance()).calculateCart();
                if (!((Offer) CartDataviewContainer.this.getDefaultModelObject()).getRecords().isEmpty()) {
                  target.add(CartEmptyOrEditPanel.this);
                } else {
                  CartEmptyOrEditPanel.this.removeAll();
                  target.add(CartEmptyOrEditPanel.this.add(CartEmptyOrEditPanel.this.new CartEmptyFragment()));
                }
              }

              @Override
              protected void onInitialize() {
                setIconType(remove);
                setSize(Mini);
                add(getConfirmationBehavior());
                super.onInitialize();
              }

              private ConfirmationBehavior getConfirmationBehavior() {
                return new ConfirmationBehavior() {

                  private static final long serialVersionUID = 7744720444161839031L;

                  @Override
                  public void renderHead(final Component component, final IHeaderResponse response) {
                    response.render($(component).chain(CONFIRMATION_FUNCTION_NAME,
                        new ConfirmationConfig().withTitle(getString(CONFIRMATION_MESSAGE_KEY)).withSingleton(true)
                            .withPopout(true).withBtnOkLabel(getString(CONFIRM_MESSAGE_KEY))
                            .withBtnCancelLabel(getString(CANCEL_MESSAGE_KEY)))
                        .asDomReadyScript());
                  }
                };
              }
            }

            private static final long serialVersionUID = -8885578770770605991L;

            private int index = 0;

            protected CartDataView(final String id, final IDataProvider<OfferRecord> dataProvider,
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
                // cartViewOrEditPanel.removeAll();
                // cartViewOrEditPanel.setSelectedModel(Model.of(model.getObject().getRecords().get(index)));
                // cartViewOrEditPanel.add(cartViewOrEditPanel.new
                // CartOfferRecordEditFragment()).setOutputMarkupId(true);
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
                  // cartViewOrEditPanel.setSelectedModel(item.getModel());
                  // cartViewOrEditPanel.removeAll();
                  // target.add(cartDataviewContainer.setOutputMarkupId(true));
                  // target.add(cartViewOrEditPanel.add(cartViewOrEditPanel.new
                  // CartOfferRecordEditFragment())
                  // .setOutputMarkupId(true));
                }
              };
              item.setModel(new CompoundPropertyModel<OfferRecord>(item.getModelObject()));
              item.add(nameLabel.setOutputMarkupId(true));
              item.add(optionsLabel.setOutputMarkupId(true));
              item.add(quantityLabel.setOutputMarkupId(true));
              item.add(itemTotalLabel.setOutputMarkupId(true));
              item.add(amountLabel.setOutputMarkupId(true));
              item.add(getRemoveAjaxButtonComponent(item));
              item.add(ajaxEventBehavior);
            }

            private Component getRemoveAjaxButtonComponent(final Item<OfferRecord> item) {
              return getRemoveAjaxButton(item).setOutputMarkupId(true);
            }

            private RemoveAjaxButton getRemoveAjaxButton(final Item<OfferRecord> item) {
              return new RemoveAjaxButton(REMOVE_ID, item.getModel(), Buttons.Type.Default, Model.of(EMPTY));
            }
          }

          private static final long serialVersionUID = 1843462579421164639L;

          public CartDataviewContainer(final String id, final IModel<Offer> model) {
            super(id, model);
          }

          @Override
          protected void onInitialize() {
            add(getCartDataViewComponent());
            super.onInitialize();
          }

          private Component getCartDataViewComponent() {
            return getCartDataView().setOutputMarkupId(true);
          }

          private CartDataView getCartDataView() {
            return new CartDataView(CART_DATAVIEW_ID, getListDataProvider(), Integer.MAX_VALUE);
          }

          private ListDataProvider<OfferRecord> getListDataProvider() {
            return new ListDataProvider<OfferRecord>() {

              private static final long serialVersionUID = -3261859241046697057L;

              @Override
              protected List<OfferRecord> getData() {
                return ((Offer) CartDataviewContainer.this.getDefaultModelObject()).getRecords();
              }
            };
          }
        }

        @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
        class SaveAjaxButton extends BootstrapAjaxButton {

          private static final long serialVersionUID = -3090506205170780941L;

          public SaveAjaxButton(final String id, final IModel<String> model, final Form<Offer> form, final Type type) {
            super(id, model, form, type);
            setSize(Small);
          }

          @Override
          protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
            shopperDataProvider.find(Shopper.getInstance()).setCheckOutByOffer((Offer) form.getDefaultModelObject());
            super.onSubmit(target, form);
            throw new RedirectToUrlException(SpecificationPage.SPECIFICATION_HTML_VALUE);
          }
        }

        @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
        class SaveAsOfferAjaxLink extends BootstrapAjaxLink<Offer> {

          private static final long serialVersionUID = 6184459006667863564L;

          public SaveAsOfferAjaxLink(final String id, final IModel<Offer> model, final Type type,
              final IModel<String> labelModel) {
            super(id, model, type, labelModel);
          }

          @Override
          protected void onInitialize() {
            setSize(Small);
            setIconType(plus);
            super.onInitialize();
          }

          @Override
          public void onClick(final AjaxRequestTarget target) {
            try {
              CartEditTable.this.setDefaultModelObject(
                  offerDataProvider.findById(((Offer) SaveAsOfferAjaxLink.this.getDefaultModelObject()).getId() == 0
                      ? offerDataProvider.persist((Offer) SaveAsOfferAjaxLink.this.getDefaultModelObject())
                      : offerDataProvider.merge((Offer) SaveAsOfferAjaxLink.this.getDefaultModelObject())));
              CartEmptyOrEditPanel.this.removeAll();
              target.add(getCartEmptyOrEditPanelComponent());
            } catch (final RuntimeException e) {
              LOGGER.warn(e.getMessage(), e);
              warn(e.getLocalizedMessage());
            }
          }

          private Component getCartEmptyOrEditPanelComponent() {
            return CartEmptyOrEditPanel.this.add(CartEmptyOrEditPanel.this.new CartEditFragment())
                .setOutputMarkupId(true);
          }
        }

        private static final long serialVersionUID = -4127747804312130801L;

        public CartEditTable(final String id, final IModel<Offer> model) {
          super(id, new CompoundPropertyModel<Offer>(model));
        }

        private SaveAjaxButton getSaveAjaxButton() {
          return new SaveAjaxButton(SAVE_ID, Model.of(CartEmptyOrEditPanel.this.getString(CHECKOUT_MESSAGE_KEY)),
              cartEditForm, Primary);
        }

        private Label getShippingTotalLabel() {
          return new Label(SHIPPING_TOTAL_ID) {

            private static final long serialVersionUID = -4143367505737220689L;

            @Override
            public <C> IConverter<C> getConverter(final Class<C> type) {
              return (IConverter<C>) CurrencyConverter.getInstance();
            }
          };
        }

        private Label getOfferTotalLabel() {
          return new Label(OFFER_TOTAL_ID) {

            private static final long serialVersionUID = -4143367505737220689L;

            @Override
            public <C> IConverter<C> getConverter(final Class<C> type) {
              return (IConverter<C>) CurrencyConverter.getInstance();
            }
          };
        }

        private Label getDiscountTotalLabel() {
          return new Label(DISCOUNT_TOTAL_ID) {

            private static final long serialVersionUID = -4143367505737220689L;

            @Override
            public <C> IConverter<C> getConverter(final Class<C> type) {
              return (IConverter<C>) CurrencyConverter.getInstance();
            }
          };
        }

        private SaveAsOfferAjaxLink getSaveAsOfferAjaxLink() {
          return new SaveAsOfferAjaxLink(SAVE_AS_OFFER_ID, (IModel<Offer>) CartEditTable.this.getDefaultModel(),
              Default, Model.of(CartEmptyOrEditPanel.this.getString(ADD_TO_WISH_LIST_MESSAGE_KEY)));
        }

        @Override
        protected void onInitialize() {
          add(getDiscountTotalLabelComponent());
          add(getOfferTotalLabelComponent());
          add(getShippingTotalLabelComponent());
          add(getSaveAsOfferAjaxLinkComponent());
          add(getSaveAjaxButtonComponent());
          add(getNotificationPanelComponent());
          add(getCartDataviewContainerComponent());
          super.onInitialize();
        }

        private Component getDiscountTotalLabelComponent() {
          return getDiscountTotalLabel().setOutputMarkupId(true);
        }

        private Component getOfferTotalLabelComponent() {
          return getOfferTotalLabel().setOutputMarkupId(true);
        }

        private Component getShippingTotalLabelComponent() {
          return getShippingTotalLabel().setOutputMarkupId(true);
        }

        private Component getSaveAjaxButtonComponent() {
          return getSaveAjaxButton().setOutputMarkupId(true);
        }

        private Component getSaveAsOfferAjaxLinkComponent() {
          return getSaveAsOfferAjaxLink().setVisible(isShopperLoggedIn()).setOutputMarkupId(true);
        }

        private boolean isShopperLoggedIn() {
          return shopperDataProvider.find(Shopper.getInstance()).isLoggedIn();
        }

        private Component getNotificationPanelComponent() {
          return getNotificationPanel().setOutputMarkupId(true);
        }

        private NotificationPanel getNotificationPanel() {
          return new NotificationPanel(FEEDBACK_ID);
        }

        private Component getCartDataviewContainerComponent() {
          return getCartDataviewContainer().setOutputMarkupId(true);
        }

        private CartDataviewContainer getCartDataviewContainer() {
          return new CartDataviewContainer(CART_DATAVIEW_CONTAINER_ID,
              (IModel<Offer>) CartEditTable.this.getDefaultModel());
        }
      }

      private static final long serialVersionUID = 3702189627576121189L;

      private final Form<Offer> cartEditForm;

      public CartEditContainer(final String id, final IModel<Offer> model) {
        super(id, model);
        cartEditForm = getCartEditForm();
      }

      @Override
      protected void onInitialize() {
        add(getCartEditFormComponent());
        super.onInitialize();
      }

      private Component getCartEditFormComponent() {
        return cartEditForm.add(getCartViewOrEditPanelComponent(getCartViewOrEditPanel()))
            .add(getCartEditTableComponent()).add(getHorizontalFormBehavior()).setOutputMarkupId(true);
      }

      private BootstrapForm<Offer> getCartEditForm() {
        return new BootstrapForm<>(CART_EDIT_FORM_COMPONENT_ID,
            (IModel<Offer>) CartEditContainer.this.getDefaultModel());
      }

      private Component getCartViewOrEditPanelComponent(CartViewOrEditPanel cartViewOrEditPanel) {
        return cartViewOrEditPanel.add(getCartOfferRecordEditFragmentComponent(cartViewOrEditPanel))
            .setOutputMarkupId(true);
      }

      private Component getCartOfferRecordEditFragmentComponent(CartViewOrEditPanel cartViewOrEditPanel) {
        return getCartOfferRecordEditFragment(cartViewOrEditPanel).setOutputMarkupId(true);
      }

      private CartOfferRecordEditFragment getCartOfferRecordEditFragment(CartViewOrEditPanel cartViewOrEditPanel) {
        return cartViewOrEditPanel.new CartOfferRecordEditFragment();
      }

      private CartViewOrEditPanel getCartViewOrEditPanel() {
        return new CartViewOrEditPanel(CART_VIEW_OR_EDIT_PANEL_ID,
            (IModel<Offer>) CartEditContainer.this.getDefaultModel());
      }

      private Component getCartEditTableComponent() {
        return getCartEditTable().add(getTableBehavior()).setOutputMarkupId(true);
      }

      private CartEditTable getCartEditTable() {
        return new CartEditTable(CART_EDIT_TABLE, (IModel<Offer>) CartEditContainer.this.getDefaultModel());
      }

      private TableBehavior getTableBehavior() {
        return new TableBehavior();
      }

      private FormBehavior getHorizontalFormBehavior() {
        return new FormBehavior(Horizontal);
      }
    }

    private static final long serialVersionUID = -5518685687286043845L;

    public CartEditFragment() {
      super(CART_EMPTY_OR_EDIT_FRAGMENT_ID, CART_EDIT_FRAGMENT_MARKUP_ID, CartEmptyOrEditPanel.this,
          CartEmptyOrEditPanel.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(getCartEditContainerComponent());
      super.onInitialize();
    }

    private Component getCartEditContainerComponent() {
      return getCartEditContainer().setOutputMarkupId(true);
    }

    private CartEditContainer getCartEditContainer() {
      return new CartEditContainer(CART_EDIT_CONTAINER_ID, (IModel<Offer>) CartEditFragment.this.getDefaultModel());
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
  class CartEmptyFragment extends Fragment {

    private static final long serialVersionUID = 5058607382122871571L;

    public CartEmptyFragment() {
      super(CART_EMPTY_OR_EDIT_FRAGMENT_ID, CART_EMPTY_FRAGMENT_MARKUP_ID, CartEmptyOrEditPanel.this,
          CartEmptyOrEditPanel.this.getDefaultModel());
    }
  }

  private static final long serialVersionUID = 6183635879900747064L;

  private static final Logger LOGGER = getLogger(CartEmptyOrEditPanel.class);

  @SpringBean(name = SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  @SpringBean(name = OFFER_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Offer> offerDataProvider;

  public CartEmptyOrEditPanel(final String id, final IModel<Offer> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    initializeOfferDataProvider();
    super.onInitialize();
  }

  private void initializeOfferDataProvider() {
    LOGGER.debug("Setting up the offer data provider using the next values: user=[{}] site=[{}]", getUserName(),
        getSite());
    offerDataProvider.setUser(getUserName());
    offerDataProvider.setPassword(getPassword());
    offerDataProvider.setSite(getSite());
    offerDataProvider.setType(new Offer());
    offerDataProvider.getType().setActive(true);
  }
}
