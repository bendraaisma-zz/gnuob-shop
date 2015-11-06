package com.netbrasoft.gnuob.shop.cart;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.convert.IConverter;

import com.google.common.net.MediaType;
import com.netbrasoft.gnuob.api.Content;
import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.OfferRecord;
import com.netbrasoft.gnuob.api.Option;
import com.netbrasoft.gnuob.api.Product;
import com.netbrasoft.gnuob.api.SubOption;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.api.generic.converter.CurrencyConverter;
import com.netbrasoft.gnuob.api.product.ProductDataProvider;
import com.netbrasoft.gnuob.shop.NetbrasoftShopMessageKeyConstants;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.product.ProductCarousel;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;
import com.netbrasoft.gnuob.shop.shopper.ShopperDataProvider;

import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.CarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.ICarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverConfig;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig.Placement;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;

/**
 * Panel for viewing, selecting and editing {@link OfferRecord} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CartViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class CartOfferRecordEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
    class CartOfferRecordEditContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
      class OptionDataviewContainer extends WebMarkupContainer {

        class OptionDataView extends DataView<Option> {

          @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
          class SubOptionSelect extends BootstrapSelect<SubOption> {

            class SubOptionSelectOnChangeAjaxBehavior extends OnChangeAjaxBehavior {
              private static final long serialVersionUID = -8850274264336474334L;

              @Override
              protected void onUpdate(final AjaxRequestTarget target) {
                final SubOption childSubOptionModel = (SubOption) SubOptionSelect.this.getDefaultModelObject();
                final OfferRecord offerRecord = CartViewOrEditPanel.this.selectedModel.getObject();

                for (final Option rootOptionOriginal : offerRecord.getOptions()) {
                  if (rootOptionOriginal.getValue().equals(parentModel.getObject().getValue())) {
                    rootOptionOriginal.getSubOptions().get(0).setValue(childSubOptionModel.getValue());
                    rootOptionOriginal.getSubOptions().get(0).setDescription(childSubOptionModel.getDescription());
                    rootOptionOriginal.getSubOptions().get(0).setDisabled(childSubOptionModel.isDisabled());
                    break;
                  }
                }
                shopperDataProvider.find(new Shopper()).calculateCart();
                target.appendJavaScript(LOCATION_RELOAD_JAVA_SCRIPT);
              }
            }

            private static final String LOCATION_RELOAD_JAVA_SCRIPT = "location.reload();";

            private static final long serialVersionUID = -8325682783277545937L;

            private final IModel<Option> parentModel;

            public SubOptionSelect(final String id, final IModel<Option> parentModel, final IModel<SubOption> model, final IModel<? extends List<? extends SubOption>> choices,
                final IChoiceRenderer<? super SubOption> renderer) {
              super(id, model, choices, renderer);
              this.parentModel = parentModel;
            }

            @Override
            protected void onInitialize() {
              add(new SubOptionSelectOnChangeAjaxBehavior());
              super.onInitialize();
            }
          }

          private static final String OPTION_ID = "option";

          private static final String VALUE_ID_EXPRESSION = "value";

          private static final String DESCRIPTION_DISPLAY_EXPRESSION = "description";

          private static final long serialVersionUID = -1449287225314761342L;

          protected OptionDataView(final String id, final IDataProvider<Option> dataProvider, final long itemsPerPage) {
            super(id, dataProvider, itemsPerPage);
          }

          @Override
          protected void populateItem(final Item<Option> item) {
            SubOption subOptionModel = new SubOption();
            final OfferRecord offerRecord = CartViewOrEditPanel.this.selectedModel.getObject();

            for (final Option rootOptionOriginal : offerRecord.getOptions()) {
              if (rootOptionOriginal.getValue().equals(((Option) item.getDefaultModelObject()).getValue())) {
                subOptionModel = rootOptionOriginal.getSubOptions().get(0);
              }
            }

            final SubOptionSelect optionDropDownChoice = new SubOptionSelect(OPTION_ID, item.getModel(), Model.of(subOptionModel),
                Model.ofList(((Option) item.getDefaultModelObject()).getSubOptions()), new ChoiceRenderer<SubOption>(DESCRIPTION_DISPLAY_EXPRESSION, VALUE_ID_EXPRESSION));
            item.add(optionDropDownChoice.setOutputMarkupId(true));
          }
        }

        private static final String OPTION_DATAVIEW_ID = "optionDataview";

        private static final long serialVersionUID = -7025936127900626268L;

        private final OptionDataView optionDataView;

        private final ListDataProvider<Option> optionListDataProvider;

        public OptionDataviewContainer(final String id, final IModel<Offer> model) {
          super(id, model);
          optionListDataProvider = new ListDataProvider<Option>() {

            private static final long serialVersionUID = -3261859241046697057L;

            @Override
            protected List<Option> getData() {
              final OfferRecord offerRecord = CartViewOrEditPanel.this.selectedModel.getObject();
              return offerRecord.getProduct().getOptions();
            }
          };
          optionDataView = new OptionDataView(OPTION_DATAVIEW_ID, optionListDataProvider, Integer.MAX_VALUE);
        }

        @Override
        protected void onInitialize() {
          optionDataView.add(new FormBehavior(FormType.Horizontal));
          add(optionDataView.setOutputMarkupId(true));
          super.onInitialize();
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
      class QuantitySelect extends BootstrapSelect<BigInteger> {

        private static final String LOCATION_RELOAD_JAVA_SCRIPT = "location.reload();";

        private static final long serialVersionUID = 5079756014198001992L;

        public QuantitySelect(final String id, final IModel<BigInteger> model, final IModel<? extends List<? extends BigInteger>> choices) {
          super(id, model, choices);
        }

        @Override
        protected void onInitialize() {
          final OnChangeAjaxBehavior onChangeAjaxBehavior = new OnChangeAjaxBehavior() {

            private static final long serialVersionUID = -8850274264336474334L;

            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
              CartViewOrEditPanel.this.selectedModel.getObject().setQuantity((BigInteger) QuantitySelect.this.getDefaultModelObject());
              shopperDataProvider.find(new Shopper()).calculateCart();
              target.appendJavaScript(LOCATION_RELOAD_JAVA_SCRIPT);
            }
          };
          add(onChangeAjaxBehavior);
          super.onInitialize();
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
      class RatingLoop extends Loop {

        private static final long serialVersionUID = 5495135025461796293L;

        private final IModel<Integer> minModel;

        public RatingLoop(final String id, final IModel<Integer> minModel, final IModel<Integer> maxModel) {
          super(id, maxModel);
          this.minModel = minModel;
        }

        @Override
        protected void populateItem(final LoopItem item) {
          final IconBehavior iconBehavior = new IconBehavior(item.getIndex() < minModel.getObject() ? GlyphIconType.star : GlyphIconType.starempty);
          item.add(iconBehavior);
        }
      }

      private static final String CART_OFFER_RECORD_EDIT_FORM_COMPONENT_ID = "cartOfferRecordEditForm";

      private static final String OPTION_DATAVIEW_CONTAINER_ID = "optionDataviewContainer";

      private static final String QUANTITY_ID = "quantity";

      private static final String PRODUCT_CAROUSEL_ID = "productCarousel";

      private static final String PRODUCT_STOCK_QUANTITY_ID = "product.stock.quantity";

      private static final String AMOUNT_ID = "amount";

      private static final String PRODUCT_AMOUNT_ID = "product.amount";

      private static final String RATING_ID = "rating";

      private static final String NAME_ID = "name";

      private static final int FIVE_STARS_RATING = 5;

      private static final long serialVersionUID = -1556817303531395170L;

      //@formatter:off
      private final List<BigInteger> quantityChoices = Arrays.asList(
          BigInteger.valueOf(1), BigInteger.valueOf(2), BigInteger.valueOf(3), BigInteger.valueOf(4),
          BigInteger.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(7), BigInteger.valueOf(8),
          BigInteger.valueOf(9), BigInteger.valueOf(10), BigInteger.valueOf(15), BigInteger.valueOf(20),
          BigInteger.valueOf(25), BigInteger.valueOf(50));
      //@formatter:on

      private final OptionDataviewContainer optionDataviewContainer;

      private final BootstrapForm<OfferRecord> cartOfferRecordEditForm;

      private final Label nameLabel;

      private final Label productStockQuantityLabel;

      private final Label amountLabel;

      private final Label productAmountLabel;

      private final RatingLoop ratingLoop;

      private final ProductCarousel productCarousel;

      private final QuantitySelect quantitySelect;

      public CartOfferRecordEditContainer(final String id, final IModel<Offer> model) {
        super(id, model);
        cartOfferRecordEditForm = new BootstrapForm<OfferRecord>(CART_OFFER_RECORD_EDIT_FORM_COMPONENT_ID,
            new CompoundPropertyModel<OfferRecord>(Model.of(CartViewOrEditPanel.this.selectedModel.getObject())));
        nameLabel = new Label(NAME_ID);
        productStockQuantityLabel = new Label(PRODUCT_STOCK_QUANTITY_ID);
        amountLabel = new Label(AMOUNT_ID) {

          private static final long serialVersionUID = -4143367505737220689L;

          @Override
          public <C> IConverter<C> getConverter(final Class<C> type) {
            return (IConverter<C>) new CurrencyConverter();
          }
        };
        productAmountLabel = new Label(PRODUCT_AMOUNT_ID) {

          private static final long serialVersionUID = -4143367505737220689L;

          @Override
          public <C> IConverter<C> getConverter(final Class<C> type) {
            return (IConverter<C>) new CurrencyConverter();
          }
        };
        productCarousel =
            new ProductCarousel(PRODUCT_CAROUSEL_ID, Model.ofList(convertContentsToCarouselImages(CartViewOrEditPanel.this.selectedModel.getObject().getProduct().getContents())));
        ratingLoop = new RatingLoop(RATING_ID, Model.of(CartViewOrEditPanel.this.selectedModel.getObject().getProduct().getRating()), Model.of(FIVE_STARS_RATING));
        quantitySelect = new QuantitySelect(QUANTITY_ID, null, Model.ofList(quantityChoices));
        optionDataviewContainer = new OptionDataviewContainer(OPTION_DATAVIEW_CONTAINER_ID, (IModel<Offer>) CartOfferRecordEditContainer.this.getDefaultModel());
      }

      private List<ICarouselImage> convertContentsToCarouselImages(final List<Content> contents) {
        final List<ICarouselImage> carouselImages = new ArrayList<ICarouselImage>();
        for (final Content content : contents) {
          if (MediaType.HTML_UTF_8.is(MediaType.parse(content.getFormat()))) {
            carouselImages.add(new CarouselImage(new String(content.getContent())));
          }
        }
        return carouselImages;
      }

      @Override
      protected void onInitialize() {
        final PopoverConfig popoverConfig = new PopoverConfig();
        final PopoverBehavior popoverBehavior = new PopoverBehavior(Model.of(CartViewOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.DESCRIPTION_MESSAGE_KEY)),
            Model.of(CartViewOrEditPanel.this.selectedModel.getObject().getProduct().getDescription()), popoverConfig);
        popoverConfig.withHoverTrigger();
        popoverConfig.withPlacement(Placement.left);
        productCarousel.add(popoverBehavior);
        cartOfferRecordEditForm.add(productCarousel.setOutputMarkupId(true));
        cartOfferRecordEditForm.add(nameLabel.setOutputMarkupId(true));
        cartOfferRecordEditForm.add(productStockQuantityLabel.setOutputMarkupId(true));
        cartOfferRecordEditForm.add(amountLabel.setOutputMarkupId(true));
        cartOfferRecordEditForm.add(productAmountLabel.setOutputMarkupId(true));
        cartOfferRecordEditForm.add(ratingLoop.setOutputMarkupId(true));
        cartOfferRecordEditForm.add(quantitySelect.setOutputMarkupId(true));
        cartOfferRecordEditForm.add(optionDataviewContainer.setOutputMarkupId(true));
        cartOfferRecordEditForm.add(new FormBehavior(FormType.Horizontal));
        add(cartOfferRecordEditForm.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String CART_OFFER_RECORD_EDIT_CONTAINER_ID = "cartOfferRecordEditContainer";

    private static final String CART_OFFER_RECORD_EDIT_FRAGMENT_MARKUP_ID = "cartOfferRecordEditFragment";

    private static final String CART_OFFER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID = "cartOfferRecordViewOrEditFragment";

    private static final long serialVersionUID = -4468533938564225326L;

    private final CartOfferRecordEditContainer cartOfferRecordEditContainer;

    public CartOfferRecordEditFragment() {
      super(CART_OFFER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID, CART_OFFER_RECORD_EDIT_FRAGMENT_MARKUP_ID, CartViewOrEditPanel.this, CartViewOrEditPanel.this.getDefaultModel());
      cartOfferRecordEditContainer = new CartOfferRecordEditContainer(CART_OFFER_RECORD_EDIT_CONTAINER_ID, (IModel<Offer>) CartOfferRecordEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(cartOfferRecordEditContainer.setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = 2429301997818659198L;

  @SpringBean(name = ShopperDataProvider.SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  @SpringBean(name = ProductDataProvider.PRODUCT_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeDataProvider<Product> productDataProvider;

  private IModel<OfferRecord> selectedModel;

  public CartViewOrEditPanel(final String id, final IModel<Offer> model) {
    super(id, model);
    final OfferRecord offerRecord = new OfferRecord();
    final Product product = new Product();
    offerRecord.setProduct(product);
    product.setAmount(BigDecimal.ZERO);
    product.setDiscount(BigDecimal.ZERO);
    product.setRating(Integer.valueOf(0));
    selectedModel = Model.of(offerRecord);
  }

  @Override
  protected void onInitialize() {
    productDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    productDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    productDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    productDataProvider.setType(new Product());
    productDataProvider.getType().setActive(true);
    shopperDataProvider.find(new Shopper()).calculateCart();
    super.onInitialize();
  }

  public void setSelectedModel(final IModel<OfferRecord> selectedModel) {
    this.selectedModel = selectedModel;
    if (this.selectedModel.getObject().getProduct() == null) {
      productDataProvider.getType().setNumber(selectedModel.getObject().getProductNumber());
      if (productDataProvider.size() > 0) {
        this.selectedModel.getObject().setProduct(productDataProvider.iterator(0, 1).next());
      } else {
        this.selectedModel.getObject().setProduct(new Product());
      }
    }
  }
}
