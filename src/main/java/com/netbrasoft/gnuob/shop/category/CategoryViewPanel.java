package com.netbrasoft.gnuob.shop.category;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.convert.IConverter;
import org.springframework.beans.BeanUtils;

import com.google.common.net.MediaType;
import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.Content;
import com.netbrasoft.gnuob.api.OfferRecord;
import com.netbrasoft.gnuob.api.Option;
import com.netbrasoft.gnuob.api.Product;
import com.netbrasoft.gnuob.api.SubCategory;
import com.netbrasoft.gnuob.api.SubOption;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.api.generic.converter.CurrencyConverter;
import com.netbrasoft.gnuob.api.product.ProductDataProvider;
import com.netbrasoft.gnuob.shop.NetbrasoftShopMessageKeyConstants;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.page.CartPage;
import com.netbrasoft.gnuob.shop.product.ProductCarousel;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;
import com.netbrasoft.gnuob.shop.shopper.ShopperDataProvider;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Size;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.CarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.ICarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverConfig;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig.Placement;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.list.BootstrapListView;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CategoryViewPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class ProductViewFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
    class ProductDataviewContainer extends WebMarkupContainer {

      class ProductDataView extends DataView<Product> {

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

        private static final String NAME_ID = "name";

        private static final String STOCK_QUANTITY_ID = "stock.quantity";

        private static final String PRODUCT_CAROUSEL_ID = "productCarousel";

        private static final String RATING_ID = "rating";

        private static final String AMOUNT_WITH_DISCOUNT_ID = "amountWithDiscount";

        private static final String AMOUNT_ID = "amount";

        private static final String PURCHASE_ID = "purchase";

        private static final long serialVersionUID = -8926501865477098913L;

        private static final int FIVE_STARS_RATING = 5;

        private ProductDataView(final String id, final IDataProvider<Product> dataProvider, final long itemsPerPage) {
          super(id, dataProvider, itemsPerPage);
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
        protected void populateItem(final Item<Product> item) {
          final Label nameLabel = new Label(NAME_ID);
          final Label stockQuantityLabel = new Label(STOCK_QUANTITY_ID);
          final ProductCarousel productCarousel = new ProductCarousel(PRODUCT_CAROUSEL_ID, Model.ofList(convertContentsToCarouselImages(item.getModelObject().getContents())));
          final RatingLoop ratingLoop = new RatingLoop(RATING_ID, Model.of(((Product) item.getDefaultModelObject()).getRating()), Model.of(FIVE_STARS_RATING));
          final Label amountWithDiscountLabel = new Label(AMOUNT_WITH_DISCOUNT_ID, Model.of(item.getModelObject().getAmount().subtract(item.getModelObject().getDiscount()))) {

            private static final long serialVersionUID = 2992356937203130959L;

            @Override
            public <C> IConverter<C> getConverter(final Class<C> type) {
              return (IConverter<C>) new CurrencyConverter();
            }
          };
          final Label amountLabel = new Label(AMOUNT_ID) {

            private static final long serialVersionUID = -2395718656821800152L;

            @Override
            public <C> IConverter<C> getConverter(final Class<C> type) {
              return (IConverter<C>) new CurrencyConverter();
            }
          };
          final PurchaseBootstrapAjaxLink purchaseBootstrapAjaxLink =
              new PurchaseBootstrapAjaxLink(PURCHASE_ID, item.getModel(), Type.Primary, Model.of(CategoryViewPanel.this.getString(NetbrasoftShopMessageKeyConstants.PURCHASE_MESSAGE_KEY)));
          final PopoverConfig popoverConfig = new PopoverConfig();
          final PopoverBehavior popoverBehavior = new PopoverBehavior(Model.of(CategoryViewPanel.this.getString(NetbrasoftShopMessageKeyConstants.DESCRIPTION_MESSAGE_KEY)),
              Model.of(item.getModelObject().getDescription()), popoverConfig);
          popoverConfig.withHoverTrigger();
          popoverConfig.withPlacement(Placement.bottom);
          productCarousel.add(popoverBehavior);
          item.setModel(new CompoundPropertyModel<Product>(item.getModelObject()));
          item.add(nameLabel.setOutputMarkupId(true));
          item.add(stockQuantityLabel).setOutputMarkupId(true);
          item.add(amountWithDiscountLabel.setOutputMarkupId(true));
          item.add(amountLabel.setOutputMarkupId(true));
          item.add(productCarousel.setOutputMarkupId(true));
          item.add(ratingLoop.setOutputMarkupId(true));
          item.add(purchaseBootstrapAjaxLink.setOutputMarkupId(true));
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
      class PurchaseBootstrapAjaxLink extends BootstrapAjaxLink<Product> {

        private static final String VERSION_IGNORE_PROPERTIES = "version";

        private static final String ID_IGNORE_PROPERTIES = "id";

        private static final long serialVersionUID = 1393894351888380103L;

        public PurchaseBootstrapAjaxLink(final String id, final IModel<Product> model, final Type type, final IModel<String> labelModel) {
          super(id, model, type, labelModel);
          setSize(Buttons.Size.Small);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          final OfferRecord offerRecord = new OfferRecord();
          final Product product = PurchaseBootstrapAjaxLink.this.getModelObject();
          BeanUtils.copyProperties(product, offerRecord, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
          offerRecord.setProduct(product);
          offerRecord.setProductNumber(product.getNumber());
          offerRecord.setAmount(product.getAmount().subtract(product.getDiscount()));
          offerRecord.setQuantity(BigInteger.ONE);
          for (final Option rootOption : product.getOptions()) {
            if (!rootOption.isDisabled()) {
              final Option offerRecordRootOption = new Option();
              BeanUtils.copyProperties(rootOption, offerRecordRootOption, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
              for (final SubOption childSubOption : rootOption.getSubOptions()) {
                if (!childSubOption.isDisabled()) {
                  final SubOption offerRecordChildSubOption = new SubOption();
                  BeanUtils.copyProperties(childSubOption, offerRecordChildSubOption, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
                  offerRecordRootOption.getSubOptions().add(offerRecordChildSubOption);
                  break;
                }
              }
              offerRecord.getOptions().add(offerRecordRootOption);
            }
          }
          shopperDataProvider.find(new Shopper()).getCart().getRecords().add(0, offerRecord);
          setResponsePage(new CartPage());
        }
      }

      private static final String PRODUCT_DATAVIEW_ID = "productDataview";

      private static final int ITEMS_PER_PAGE = 5;

      private static final long serialVersionUID = 6364502301735024811L;

      private final ProductDataView productDataView;

      public ProductDataviewContainer(final String id, final IModel<Category> model) {
        super(id, model);
        productDataView = new ProductDataView(PRODUCT_DATAVIEW_ID, productDataProvider, ITEMS_PER_PAGE);
      }

      @Override
      protected void onInitialize() {
        add(productDataView.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
    class SubCategoryDataviewContainer extends WebMarkupContainer {

      class SubCategoryDataview extends DataView<SubCategory> {

        private static final String CLICK_EVENT = "click";

        private static final String CONTENT_ID = "content";

        private static final long serialVersionUID = 2776123630121635305L;

        private SubCategoryDataview(final String id, final IDataProvider<SubCategory> dataProvider, final long itemsPerPage) {
          super(id, dataProvider, itemsPerPage);
        }

        @Override
        protected void populateItem(final Item<SubCategory> item) {
          item.setModel(new CompoundPropertyModel<SubCategory>(item.getModelObject()));
          item.add(new Label(CONTENT_ID, new AbstractReadOnlyModel<String>() {

            private static final long serialVersionUID = 4751535250171413561L;

            @Override
            public String getObject() {
              final StringBuilder stringBuilder = new StringBuilder();
              for (final Content content : item.getModelObject().getContents()) {
                stringBuilder.append(new String(content.getContent()));
              }
              return stringBuilder.toString();
            }
          }).setEscapeModelStrings(false));
          item.add(new AjaxEventBehavior(CLICK_EVENT) {

            private static final long serialVersionUID = 3898435649434303190L;

            @Override
            protected void onEvent(final AjaxRequestTarget target) {
              selectedModel = (IModel<SubCategory>) item.getDefaultModel();
              selectedModelList = Model.ofList(((SubCategory) item.getDefaultModelObject()).getSubCategories());
              subCategoryMenuBootstrapListView.setDefaultModel(Model.ofList(((SubCategory) item.getDefaultModelObject()).getSubCategories()));
              productDataProvider.setType(new Product());
              productDataProvider.getType().setActive(true);
              productDataProvider.getType().getSubCategories().add((SubCategory) item.getDefaultModelObject());
              CategoryViewPanel.this.removeAll();
              CategoryViewPanel.this.add(new ProductViewFragment().setOutputMarkupId(true));
              target.add(CategoryViewPanel.this);
            }
          });
        }
      }

      private static final String SUB_CATEGORY_DATAVIEW_ID = "subCategoryDataview";

      private static final long serialVersionUID = 6098269569797773482L;

      private final SubCategoryDataview subCategoryDataview;

      public SubCategoryDataviewContainer(final String id, final IModel<Category> model) {
        super(id, model);
        final IDataProvider<SubCategory> subCategoryDataProvider = new ListDataProvider<SubCategory>() {

          private static final long serialVersionUID = 1L;

          @Override
          protected List<SubCategory> getData() {
            return createFlatSubCategoryList(selectedModelList.getObject());
          }
        };
        subCategoryDataview = new SubCategoryDataview(SUB_CATEGORY_DATAVIEW_ID, subCategoryDataProvider, Integer.MAX_VALUE);
      }

      @Override
      protected void onInitialize() {
        add(subCategoryDataview.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String PRODUCT_VIEW_FRAGMENT_MARKUP_ID = "productViewFragment";

    private static final String SUB_CATEGORY_PRODUCT_VIEW_FRAGMENT_ID = "subCategoryProductViewFragment";

    private static final String PRODUCT_DATAVIEW_CONTAINER_ID = "productDataviewContainer";

    private static final String PRODUCT_PAGING_NAVIGATOR_MARKUP_ID = "productPagingNavigator";

    private static final long serialVersionUID = -1722501866439698640L;

    private static final String SUB_CATEGORY_MENU_BOOTSTRAP_LIST_VIEW_ID = "subCategoryMenuBootstrapListView";

    private static final String SUB_CATEGORY_DATAVIEW_CONTAINER_ID = "subCategoryDataviewContainer";

    private final SubCategoryMenuBootstrapListView subCategoryMenuBootstrapListView;

    private final SubCategoryDataviewContainer subCategoryDataviewContainer;

    private final ProductDataviewContainer productDataviewContainer;

    private final BootstrapPagingNavigator productPagingNavigator;

    public ProductViewFragment() {
      super(SUB_CATEGORY_PRODUCT_VIEW_FRAGMENT_ID, PRODUCT_VIEW_FRAGMENT_MARKUP_ID, CategoryViewPanel.this, CategoryViewPanel.this.getDefaultModel());
      final ArrayList<SubCategory> subCategoryList = new ArrayList<SubCategory>();
      subCategoryList.add(selectedModel.getObject());
      subCategoryMenuBootstrapListView = new SubCategoryMenuBootstrapListView(SUB_CATEGORY_MENU_BOOTSTRAP_LIST_VIEW_ID, Model.ofList(subCategoryList));
      subCategoryDataviewContainer = new SubCategoryDataviewContainer(SUB_CATEGORY_DATAVIEW_CONTAINER_ID, (IModel<Category>) ProductViewFragment.this.getDefaultModel());
      productDataviewContainer = new ProductDataviewContainer(PRODUCT_DATAVIEW_CONTAINER_ID, (IModel<Category>) ProductViewFragment.this.getDefaultModel());
      productPagingNavigator = new BootstrapPagingNavigator(PRODUCT_PAGING_NAVIGATOR_MARKUP_ID, productDataviewContainer.productDataView);
    }

    @Override
    protected void onInitialize() {
      add(subCategoryMenuBootstrapListView.setOutputMarkupId(true));
      add(subCategoryDataviewContainer.setOutputMarkupId(true));
      add(productDataviewContainer.setOutputMarkupId(true));
      add(productPagingNavigator.setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  class SubCategoryMenuBootstrapListView extends BootstrapListView<SubCategory> {

    class SubCategoryBootstrapListView extends BootstrapListView<SubCategory> {

      private static final String LINK_ID = "link";

      private static final long serialVersionUID = 2148940232228759419L;

      private SubCategoryBootstrapListView(final String id, final IModel<? extends List<SubCategory>> model) {
        super(id, model);
      }

      @Override
      protected void populateItem(final ListItem<SubCategory> item) {
        final BootstrapAjaxLink<String> bootstrapAjaxLink =
            new BootstrapAjaxLink<String>(LINK_ID, Model.of(item.getModel().getObject().getName()), Buttons.Type.Link, Model.of(item.getModel().getObject().getName())) {

              private static final long serialVersionUID = -1216788078532675590L;

              @Override
              public void onClick(final AjaxRequestTarget target) {
                selectedModel = (IModel<SubCategory>) item.getDefaultModel();
                selectedModelList = Model.ofList(((SubCategory) item.getDefaultModelObject()).getSubCategories());
                SubCategoryMenuBootstrapListView.this.setDefaultModel(Model.ofList(((SubCategory) item.getDefaultModelObject()).getSubCategories()));
                productDataProvider.setType(new Product());
                productDataProvider.getType().setActive(true);
                productDataProvider.getType().getSubCategories().add((SubCategory) item.getDefaultModelObject());
                CategoryViewPanel.this.removeAll();
                CategoryViewPanel.this.add(new ProductViewFragment().setOutputMarkupId(true));
                target.add(CategoryViewPanel.this);
              }
            };
        item.setModel(new CompoundPropertyModel<SubCategory>(item.getModelObject()));
        item.add(bootstrapAjaxLink.setSize(Size.Small).setOutputMarkupId(true));
      }
    }

    private static final String SUB_CATEGORY_BOOTSTRAP_LIST_VIEW_ID = "subCategoryBootstrapListView";

    private static final String NAME_ID = "name";

    private static final long serialVersionUID = 2148940232228759419L;

    private SubCategoryMenuBootstrapListView(final String id, final IModel<? extends List<SubCategory>> model) {
      super(id, model);
    }

    @Override
    protected void populateItem(final ListItem<SubCategory> item) {
      final Label nameLabel = new Label(NAME_ID);
      final SubCategoryBootstrapListView subCategoryBootstrapListView =
          new SubCategoryBootstrapListView(SUB_CATEGORY_BOOTSTRAP_LIST_VIEW_ID, Model.ofList(item.getModelObject().getSubCategories()));
      item.setModel(new CompoundPropertyModel<SubCategory>(item.getModelObject()));
      item.add(nameLabel);
      item.add(subCategoryBootstrapListView);
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class SubCategoryViewFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
    class SubCategoryDataviewContainer extends WebMarkupContainer {

      class SubCategoryDataview extends DataView<SubCategory> {

        private static final String CLICK_EVENT = "click";

        private static final String CONTENT_ID = "content";

        private static final long serialVersionUID = 2776123630121635305L;

        private SubCategoryDataview(final String id, final IDataProvider<SubCategory> dataProvider, final long itemsPerPage) {
          super(id, dataProvider, itemsPerPage);
        }

        @Override
        protected void populateItem(final Item<SubCategory> item) {
          item.setModel(new CompoundPropertyModel<SubCategory>(item.getModelObject()));
          item.add(new Label(CONTENT_ID, new AbstractReadOnlyModel<String>() {

            private static final long serialVersionUID = 4751535250171413561L;

            @Override
            public String getObject() {
              final StringBuilder stringBuilder = new StringBuilder();
              for (final Content content : item.getModelObject().getContents()) {
                stringBuilder.append(new String(content.getContent()));
              }
              return stringBuilder.toString();
            }
          }).setEscapeModelStrings(false));
          item.add(new AjaxEventBehavior(CLICK_EVENT) {

            private static final long serialVersionUID = 3898435649434303190L;

            @Override
            protected void onEvent(final AjaxRequestTarget target) {
              selectedModel = (IModel<SubCategory>) item.getDefaultModel();
              selectedModelList = Model.ofList(((SubCategory) item.getDefaultModelObject()).getSubCategories());
              subCategoryMenuBootstrapListView.setDefaultModel(Model.ofList(((SubCategory) item.getDefaultModelObject()).getSubCategories()));
              productDataProvider.setType(new Product());
              productDataProvider.getType().setActive(true);
              productDataProvider.getType().getSubCategories().add((SubCategory) item.getDefaultModelObject());
              CategoryViewPanel.this.removeAll();
              CategoryViewPanel.this.add(new ProductViewFragment().setOutputMarkupId(true));
              target.add(CategoryViewPanel.this);
            }
          });
        }
      }

      private static final String SUB_CATEGORY_DATAVIEW_ID = "subCategoryDataview";

      private static final long serialVersionUID = 6098269569797773482L;

      private final SubCategoryDataview subCategoryDataview;

      public SubCategoryDataviewContainer(final String id, final IModel<Category> model) {
        super(id, model);
        final IDataProvider<SubCategory> subCategoryDataProvider = new ListDataProvider<SubCategory>() {

          private static final long serialVersionUID = 1L;

          @Override
          protected List<SubCategory> getData() {
            return createFlatSubCategoryList(selectedModelList.getObject());
          }
        };
        subCategoryDataview = new SubCategoryDataview(SUB_CATEGORY_DATAVIEW_ID, subCategoryDataProvider, Integer.MAX_VALUE);
      }

      @Override
      protected void onInitialize() {
        add(subCategoryDataview.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String SUB_CATEGORY_DATAVIEW_CONTAINER_ID = "subCategoryDataviewContainer";

    private static final String SUB_CATEGORY_MENU_BOOTSTRAP_LIST_VIEW_ID = "subCategoryMenuBootstrapListView";

    private static final String SUB_CATEGORY_VIEW_FRAGMENT_MARKUP_ID = "subCategoryViewFragment";

    private static final String SUB_CATEGORY_PRODUCT_VIEW_FRAGMENT_ID = "subCategoryProductViewFragment";

    private static final long serialVersionUID = -3028153699938016168L;

    private final SubCategoryMenuBootstrapListView subCategoryMenuBootstrapListView;

    private final SubCategoryDataviewContainer subCategoryDataviewContainer;

    public SubCategoryViewFragment() {
      super(SUB_CATEGORY_PRODUCT_VIEW_FRAGMENT_ID, SUB_CATEGORY_VIEW_FRAGMENT_MARKUP_ID, CategoryViewPanel.this, CategoryViewPanel.this.getDefaultModel());
      subCategoryMenuBootstrapListView = new SubCategoryMenuBootstrapListView(SUB_CATEGORY_MENU_BOOTSTRAP_LIST_VIEW_ID, Model.ofList(selectedModelList.getObject()));
      subCategoryDataviewContainer = new SubCategoryDataviewContainer(SUB_CATEGORY_DATAVIEW_CONTAINER_ID, (IModel<Category>) SubCategoryViewFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(subCategoryMenuBootstrapListView.setOutputMarkupId(true));
      add(subCategoryDataviewContainer.setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = -9083340164646887954L;

  @SpringBean(name = ProductDataProvider.PRODUCT_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeDataProvider<Product> productDataProvider;

  @SpringBean(name = ShopperDataProvider.SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  private IModel<SubCategory> selectedModel;

  private IModel<List<SubCategory>> selectedModelList;

  public CategoryViewPanel(final String id, final IModel<Category> model) {
    super(id, model);
    selectedModel = Model.of(((Category) CategoryViewPanel.this.getDefaultModelObject()).getSubCategories().iterator().next());
    selectedModelList = Model.ofList(((Category) CategoryViewPanel.this.getDefaultModelObject()).getSubCategories());
  }

  private List<SubCategory> createFlatSubCategoryList(final List<SubCategory> subCategories) {
    final List<SubCategory> flatSubCategoryList = new ArrayList<SubCategory>();
    for (final SubCategory subCategory : subCategories) {
      flatSubCategoryList.addAll(subCategory.getSubCategories());
    }
    return flatSubCategoryList;
  }

  @Override
  protected void onInitialize() {
    productDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    productDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    productDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    productDataProvider.setType(new Product());
    productDataProvider.getType().setActive(true);
    super.onInitialize();
  }
}
