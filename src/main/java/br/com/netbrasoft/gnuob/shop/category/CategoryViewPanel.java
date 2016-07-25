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

package br.com.netbrasoft.gnuob.shop.category;

import static br.com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.PRODUCT_DATA_PROVIDER_NAME;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.AMOUNT_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.AMOUNT_WITH_DISCOUNT_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CLICK_EVENT;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CONTENT_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.DESCRIPTION_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.LINK_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.NAME_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.PRODUCT_CAROUSEL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.PRODUCT_DATAVIEW_CONTAINER_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.PRODUCT_DATAVIEW_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.PRODUCT_PAGING_NAVIGATOR_MARKUP_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.PRODUCT_VIEW_FRAGMENT_MARKUP_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.PURCHASE_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.PURCHASE_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.RATING_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SHOPPER_DATA_PROVIDER_NAME;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.STOCK_QUANTITY_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SUB_CATEGORY_BOOTSTRAP_LIST_VIEW_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SUB_CATEGORY_DATAVIEW_CONTAINER_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SUB_CATEGORY_DATAVIEW_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SUB_CATEGORY_MENU_BOOTSTRAP_LIST_VIEW_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SUB_CATEGORY_PRODUCT_VIEW_FRAGMENT_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SUB_CATEGORY_VIEW_FRAGMENT_MARKUP_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.UNCHECKED;
import static br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession.getPassword;
import static br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession.getSite;
import static br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession.getUserName;
import static br.com.netbrasoft.gnuob.shop.security.ShopRoles.GUEST;
import static com.google.common.collect.Lists.newArrayList;
import static de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Size.Small;
import static de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type.Link;
import static de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type.Primary;
import static de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig.Placement.bottom;
import static de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType.star;
import static de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType.starempty;
import static java.lang.Integer.MAX_VALUE;
import static java.util.stream.Collectors.toList;
import static org.apache.wicket.model.Model.of;
import static org.apache.wicket.model.Model.ofList;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.convert.IConverter;

import com.google.common.net.MediaType;

import br.com.netbrasoft.gnuob.api.Category;
import br.com.netbrasoft.gnuob.api.Content;
import br.com.netbrasoft.gnuob.api.Product;
import br.com.netbrasoft.gnuob.api.SubCategory;
import br.com.netbrasoft.gnuob.api.generic.IGenericTypeDataProvider;
import br.com.netbrasoft.gnuob.api.generic.converter.CurrencyConverter;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.page.CartPage;
import br.com.netbrasoft.gnuob.shop.product.ProductCarousel;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.CarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.ICarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverConfig;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.list.BootstrapListView;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator;

@SuppressWarnings(UNCHECKED)
@AuthorizeAction(action = Action.RENDER, roles = {GUEST})
public class CategoryViewPanel extends BreadCrumbPanel {

  @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
  class ProductViewFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
    class ProductDataviewContainer extends WebMarkupContainer {

      class ProductDataView extends DataView<Product> {

        @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
        class RatingLoop extends Loop {

          private static final long serialVersionUID = 5495135025461796293L;

          private final IModel<Integer> minModel;

          public RatingLoop(final String id, final IModel<Integer> minModel, final IModel<Integer> maxModel) {
            super(id, maxModel);
            this.minModel = minModel;
          }

          @Override
          protected void populateItem(final LoopItem item) {
            item.add(getIconBehavior(item));
          }

          private IconBehavior getIconBehavior(final LoopItem item) {
            return new IconBehavior(item.getIndex() < minModel.getObject() ? star : starempty);
          }
        }

        private static final long serialVersionUID = -8926501865477098913L;
        private static final int FIVE_STARS_RATING = 5;

        private ProductDataView(final String id, final IDataProvider<Product> dataProvider, final long itemsPerPage) {
          super(id, dataProvider, itemsPerPage);
        }


        @Override
        protected void populateItem(final Item<Product> item) {
          item.setModel(new CompoundPropertyModel<Product>(item.getModelObject()));
          item.add(getNameLabelComponent()).add(getStockQuantityLabelCompontent())
              .add(getAmountWithDiscountLabelComponent(item)).add(getAmountLabelCompontent())
              .add(getProductCarouselCompontent(item)).add(getRatingLoopComponent(item))
              .add(getPurchasesBootstrapAjaxLinkComponent(item));
        }

        private Component getNameLabelComponent() {
          return getNameLabel().setOutputMarkupId(true);
        }

        private Label getNameLabel() {
          return new Label(NAME_ID);
        }

        private Component getStockQuantityLabelCompontent() {
          return getStockQuantityLabel().setOutputMarkupId(true);
        }

        private Label getStockQuantityLabel() {
          return new Label(STOCK_QUANTITY_ID);
        }

        private Component getAmountWithDiscountLabelComponent(final Item<Product> item) {
          return getAmountWithDiscountLabel(item).setOutputMarkupId(true);
        }

        private Label getAmountWithDiscountLabel(final Item<Product> item) {
          return new Label(AMOUNT_WITH_DISCOUNT_ID,
              of(item.getModelObject().getAmount().subtract(item.getModelObject().getDiscount()))) {

            private static final long serialVersionUID = 2992356937203130959L;

            @Override
            public <C> IConverter<C> getConverter(final Class<C> type) {
              return (IConverter<C>) new CurrencyConverter();
            }
          };
        }

        private Component getAmountLabelCompontent() {
          return getAmountLabel().setOutputMarkupId(true);
        }

        private Label getAmountLabel() {
          return new Label(AMOUNT_ID) {

            private static final long serialVersionUID = -2395718656821800152L;

            @Override
            public <C> IConverter<C> getConverter(final Class<C> type) {
              return (IConverter<C>) new CurrencyConverter();
            }
          };
        }

        private Component getProductCarouselCompontent(final Item<Product> item) {
          return getProductCarousel(item).setOutputMarkupId(true).add(getPopoverBehavior(item));
        }

        private ProductCarousel getProductCarousel(final Item<Product> item) {
          return new ProductCarousel(PRODUCT_CAROUSEL_ID,
              ofList(convertContentsToCarouselImages(item.getModelObject().getContents())));
        }

        private List<ICarouselImage> convertContentsToCarouselImages(final List<Content> contents) {
          return contents.stream().filter(e -> MediaType.HTML_UTF_8.is(MediaType.parse(e.getFormat())))
              .map(Content::getContent).map(e -> new CarouselImage(new String(e))).collect(toList());
        }

        private PopoverBehavior getPopoverBehavior(final Item<Product> item) {
          return new PopoverBehavior(of(CategoryViewPanel.this.getString(DESCRIPTION_MESSAGE_KEY)),
              of(item.getModelObject().getDescription()), getPopoverConfig());
        }

        private PopoverConfig getPopoverConfig() {
          return new PopoverConfig().withHoverTrigger().withPlacement(bottom);
        }

        private Component getRatingLoopComponent(final Item<Product> item) {
          return getRatingLoop(item).setOutputMarkupId(true);
        }

        private RatingLoop getRatingLoop(final Item<Product> item) {
          return new RatingLoop(RATING_ID, of(((Product) item.getDefaultModelObject()).getRating()),
              of(FIVE_STARS_RATING));
        }

        private Component getPurchasesBootstrapAjaxLinkComponent(final Item<Product> item) {
          return getPurchaseBootstrapAjaxLink(item).setOutputMarkupId(true);
        }

        private PurchaseBootstrapAjaxLink getPurchaseBootstrapAjaxLink(final Item<Product> item) {
          return new PurchaseBootstrapAjaxLink(PURCHASE_ID, item.getModel(), Primary,
              of(CategoryViewPanel.this.getString(PURCHASE_MESSAGE_KEY)));
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
      class PurchaseBootstrapAjaxLink extends BootstrapAjaxLink<Product> {

        private static final long serialVersionUID = 1393894351888380103L;

        public PurchaseBootstrapAjaxLink(final String id, final IModel<Product> model, final Type type,
            final IModel<String> labelModel) {
          super(id, model, type, labelModel);
        }

        @Override
        protected void onInitialize() {
          setSize(Small);
          super.onInitialize();
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          shopperDataProvider.find(Shopper.getInstance()).addToCart(PurchaseBootstrapAjaxLink.this.getModelObject());
          setResponsePage(getCartPage());
        }

        private CartPage getCartPage() {
          return new CartPage();
        }
      }

      private static final long serialVersionUID = 6364502301735024811L;
      private static final int ITEMS_PER_PAGE = 5;

      public ProductDataviewContainer(final String id, final IModel<Category> model) {
        super(id, model);
      }

      @Override
      protected void onInitialize() {
        add(getProductDataViewComponent());
        super.onInitialize();
      }

      private Component getProductDataViewComponent() {
        return getProductDataView().setOutputMarkupId(true);
      }

      private ProductDataView getProductDataView() {
        return new ProductDataView(PRODUCT_DATAVIEW_ID, productDataProvider, ITEMS_PER_PAGE);
      }
    }

    @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
    class SubCategoryDataviewContainer extends WebMarkupContainer {

      class SubCategoryDataView extends DataView<SubCategory> {

        private static final long serialVersionUID = 2776123630121635305L;

        private SubCategoryDataView(final String id, final IDataProvider<SubCategory> dataProvider,
            final long itemsPerPage) {
          super(id, dataProvider, itemsPerPage);
        }

        @Override
        protected void populateItem(final Item<SubCategory> item) {
          item.setModel(new CompoundPropertyModel<SubCategory>(item.getModelObject()));
          item.add(getContentLabelComponent(item)).add(geCategoryBreadCrumbAjaxEventBehavior(item));
        }

        private Component getContentLabelComponent(final Item<SubCategory> item) {
          return getContentLabel(item).setEscapeModelStrings(false);
        }

        private CategoryBreadCrumbAjaxEventBehavior geCategoryBreadCrumbAjaxEventBehavior(
            final Item<SubCategory> item) {
          return new CategoryBreadCrumbAjaxEventBehavior(CLICK_EVENT, CategoryViewPanel.this, item.getModel());
        }

        private Label getContentLabel(final Item<SubCategory> item) {
          return new Label(CONTENT_ID, new AbstractReadOnlyModel<String>() {

            private static final long serialVersionUID = 4751535250171413561L;

            @Override
            public String getObject() {
              final StringBuilder stringBuilder = new StringBuilder();
              item.getModelObject().getContents().stream()
                  .forEach(e -> stringBuilder.append(new String(e.getContent())));
              return stringBuilder.toString();
            }
          });
        }
      }

      private static final long serialVersionUID = 6098269569797773482L;

      public SubCategoryDataviewContainer(final String id, final IModel<Category> model) {
        super(id, model);
      }

      @Override
      protected void onInitialize() {
        add(getSubCategoryDataViewComponent());
        super.onInitialize();
      }

      private Component getSubCategoryDataViewComponent() {
        return getSubCategoryDataView().setOutputMarkupId(true);
      }

      private SubCategoryDataView getSubCategoryDataView() {
        return new SubCategoryDataView(SUB_CATEGORY_DATAVIEW_ID, getListDataProvider(), MAX_VALUE);
      }

      private ListDataProvider<SubCategory> getListDataProvider() {
        return new ListDataProvider<SubCategory>() {

          private static final long serialVersionUID = 1L;

          @Override
          protected List<SubCategory> getData() {
            return createFlatSubCategoryList(selectedModelList.getObject());
          }
        };
      }
    }

    private static final long serialVersionUID = -1722501866439698640L;

    public ProductViewFragment() {
      super(SUB_CATEGORY_PRODUCT_VIEW_FRAGMENT_ID, PRODUCT_VIEW_FRAGMENT_MARKUP_ID, CategoryViewPanel.this,
          CategoryViewPanel.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      final ProductDataviewContainer productDataviewContainer = getProductDataViewContainer();
      add(getProductDataViewContainerComponent(productDataviewContainer));
      add(getBootstrapPagingNavigatorComponent(productDataviewContainer));
      add(getSubCategoryMenuBootstrapListViewComponent());
      add(getSubCategoryDataViewContainerComponent());
      super.onInitialize();
    }

    private ProductDataviewContainer getProductDataViewContainer() {
      return new ProductDataviewContainer(PRODUCT_DATAVIEW_CONTAINER_ID,
          (IModel<Category>) ProductViewFragment.this.getDefaultModel());
    }

    private Component getProductDataViewContainerComponent(final ProductDataviewContainer productDataviewContainer) {
      return productDataviewContainer.setOutputMarkupId(true);
    }

    private Component getBootstrapPagingNavigatorComponent(final ProductDataviewContainer productDataviewContainer) {
      return getBootstrapPagingNavigator(productDataviewContainer).setOutputMarkupId(true);
    }

    private BootstrapPagingNavigator getBootstrapPagingNavigator(
        final ProductDataviewContainer productDataviewContainer) {
      return new BootstrapPagingNavigator(PRODUCT_PAGING_NAVIGATOR_MARKUP_ID,
          productDataviewContainer.getProductDataView());
    }

    private Component getSubCategoryMenuBootstrapListViewComponent() {
      return getSubCategoryMenuBoostrapListView().setOutputMarkupId(true);
    }

    private SubCategoryMenuBootstrapListView getSubCategoryMenuBoostrapListView() {
      return new SubCategoryMenuBootstrapListView(SUB_CATEGORY_MENU_BOOTSTRAP_LIST_VIEW_ID,
          ofList(newArrayList(selectedModel.getObject())));
    }

    private Component getSubCategoryDataViewContainerComponent() {
      return getSubCategoryDataViewContainer().setOutputMarkupId(true);
    }

    private SubCategoryDataviewContainer getSubCategoryDataViewContainer() {
      return new SubCategoryDataviewContainer(SUB_CATEGORY_DATAVIEW_CONTAINER_ID,
          (IModel<Category>) ProductViewFragment.this.getDefaultModel());
    }
  }

  class SubCategoryMenuBootstrapListView extends BootstrapListView<SubCategory> {

    class SubCategoryBootstrapListView extends BootstrapListView<SubCategory> {

      private static final long serialVersionUID = 2148940232228759419L;

      private SubCategoryBootstrapListView(final String id, final IModel<? extends List<SubCategory>> model) {
        super(id, model);
      }

      @Override
      protected void populateItem(final ListItem<SubCategory> item) {
        item.add(getCategoryBreadCrumbBoostrapAjaxLinkComponent(item));
      }

      private Component getCategoryBreadCrumbBoostrapAjaxLinkComponent(final ListItem<SubCategory> item) {
        return getCategoryBreadCrumbBootstrapAjaxLink(item).setOutputMarkupId(true);
      }

      private CategoryBreadCrumbBootstrapAjaxLink getCategoryBreadCrumbBootstrapAjaxLink(
          final ListItem<SubCategory> item) {
        return new CategoryBreadCrumbBootstrapAjaxLink(LINK_ID, CategoryViewPanel.this, item.getModel(), Link,
            of(item.getModel().getObject().getName()));
      }
    }

    private static final long serialVersionUID = 2148940232228759419L;

    private SubCategoryMenuBootstrapListView(final String id, final IModel<? extends List<SubCategory>> model) {
      super(id, model);
    }

    @Override
    protected void populateItem(final ListItem<SubCategory> item) {
      item.setModel(new CompoundPropertyModel<SubCategory>(item.getModelObject()));
      item.add(getNameLabelComponent()).add(getSubCategoryBootstrapListViewComponent(item));
    }

    private Component getNameLabelComponent() {
      return getNameLabel().setOutputMarkupId(true);
    }

    private Label getNameLabel() {
      return new Label(NAME_ID);
    }

    private Component getSubCategoryBootstrapListViewComponent(final ListItem<SubCategory> item) {
      return getSubCategoryBootstrapListView(item).setOutputMarkupId(true);
    }

    private SubCategoryBootstrapListView getSubCategoryBootstrapListView(final ListItem<SubCategory> item) {
      return new SubCategoryBootstrapListView(SUB_CATEGORY_BOOTSTRAP_LIST_VIEW_ID,
          ofList(item.getModelObject().getSubCategories()));
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
  class SubCategoryViewFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
    class SubCategoryDataviewContainer extends WebMarkupContainer {

      class SubCategoryDataView extends DataView<SubCategory> {

        private static final long serialVersionUID = 2776123630121635305L;

        private SubCategoryDataView(final String id, final IDataProvider<SubCategory> dataProvider,
            final long itemsPerPage) {
          super(id, dataProvider, itemsPerPage);
        }

        @Override
        protected void populateItem(final Item<SubCategory> item) {
          item.setModel(new CompoundPropertyModel<SubCategory>(item.getModelObject()));
          item.add(getContentLabelComponent(item)).add(getCategoryBreadCrumbAjaxEventBehavior(item));
        }

        private Component getContentLabelComponent(final Item<SubCategory> item) {
          return getContentLabel(item).setEscapeModelStrings(false);
        }

        private Label getContentLabel(final Item<SubCategory> item) {
          return new Label(CONTENT_ID, new AbstractReadOnlyModel<String>() {

            private static final long serialVersionUID = 4751535250171413561L;

            @Override
            public String getObject() {
              final StringBuilder stringBuilder = new StringBuilder();
              item.getModelObject().getContents().stream()
                  .forEach(e -> stringBuilder.append(new String(e.getContent())));
              return stringBuilder.toString();
            }
          });
        }

        private CategoryBreadCrumbAjaxEventBehavior getCategoryBreadCrumbAjaxEventBehavior(
            final Item<SubCategory> item) {
          return new CategoryBreadCrumbAjaxEventBehavior(CLICK_EVENT, CategoryViewPanel.this, item.getModel());
        }
      }

      private static final long serialVersionUID = 6098269569797773482L;

      public SubCategoryDataviewContainer(final String id, final IModel<Category> model) {
        super(id, model);
      }

      @Override
      protected void onInitialize() {
        add(getSubCategoryDataViewComponent());
        super.onInitialize();
      }

      private Component getSubCategoryDataViewComponent() {
        return getSubCategoryDataView(getListDataProvider()).setOutputMarkupId(true);
      }

      private ListDataProvider<SubCategory> getListDataProvider() {
        return new ListDataProvider<SubCategory>() {

          private static final long serialVersionUID = 1L;

          @Override
          protected List<SubCategory> getData() {
            return createFlatSubCategoryList(selectedModelList.getObject());
          }
        };
      }

      private SubCategoryDataView getSubCategoryDataView(final IDataProvider<SubCategory> subCategoryDataProvider) {
        return new SubCategoryDataView(SUB_CATEGORY_DATAVIEW_ID, subCategoryDataProvider, MAX_VALUE);
      }
    }

    private static final long serialVersionUID = -3028153699938016168L;

    public SubCategoryViewFragment() {
      super(SUB_CATEGORY_PRODUCT_VIEW_FRAGMENT_ID, SUB_CATEGORY_VIEW_FRAGMENT_MARKUP_ID, CategoryViewPanel.this,
          CategoryViewPanel.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(getSubCategoryMenuBootstrapListViewComponent());
      add(getSubCategoryDataViewContainerComponent());
      super.onInitialize();
    }

    private Component getSubCategoryMenuBootstrapListViewComponent() {
      return getSubCategoryMenuBootstrapListView().setOutputMarkupId(true);
    }

    private SubCategoryDataviewContainer getSubCategoryDataviewContainer() {
      return new SubCategoryDataviewContainer(SUB_CATEGORY_DATAVIEW_CONTAINER_ID,
          (IModel<Category>) SubCategoryViewFragment.this.getDefaultModel());
    }

    private Component getSubCategoryDataViewContainerComponent() {
      return getSubCategoryDataviewContainer().setOutputMarkupId(true);
    }

    private SubCategoryMenuBootstrapListView getSubCategoryMenuBootstrapListView() {
      return new SubCategoryMenuBootstrapListView(SUB_CATEGORY_MENU_BOOTSTRAP_LIST_VIEW_ID,
          ofList(selectedModelList.getObject()));
    }
  }

  private static final long serialVersionUID = -9083340164646887954L;

  @SpringBean(name = PRODUCT_DATA_PROVIDER_NAME, required = true)
  private IGenericTypeDataProvider<Product> productDataProvider;

  @SpringBean(name = SHOPPER_DATA_PROVIDER_NAME, required = true)
  private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  private final IModel<SubCategory> selectedModel;
  private final IModel<List<SubCategory>> selectedModelList;

  public CategoryViewPanel(final String id, final IBreadCrumbModel breadCrumbModel, final IModel<Category> model) {
    this(id, breadCrumbModel, model, null, ofList(model.getObject().getSubCategories()));
  }

  public CategoryViewPanel(final String id, final IBreadCrumbModel breadCrumbModel, final IModel<Category> model,
      final IModel<SubCategory> subCategoryModel, final IModel<List<SubCategory>> subCategoryModelList) {
    super(id, breadCrumbModel, model);
    selectedModel = subCategoryModel;
    selectedModelList = subCategoryModelList;
    breadCrumbModel.setActive(CategoryViewPanel.this);
  }

  private List<SubCategory> createFlatSubCategoryList(final List<SubCategory> subCategories) {
    final List<SubCategory> flatSubCategoryList = newArrayList();
    for (final SubCategory subCategory : subCategories) {
      flatSubCategoryList.addAll(subCategory.getSubCategories());
    }
    return flatSubCategoryList;
  }

  @Override
  public IModel<String> getTitle() {
    return selectedModel == null
        ? of(((IModel<Category>) CategoryViewPanel.this.getDefaultModel()).getObject().getName())
        : of(selectedModel.getObject().getName());
  }

  @Override
  protected void onConfigure() {
    configureProductDataProvider();
    super.onConfigure();
  }

  private void configureProductDataProvider() {
    productDataProvider.setType(new Product());
    productDataProvider.getType().setActive(true);
    if (selectedModel != null) {
      productDataProvider.getType().getSubCategories().add(selectedModel.getObject());
    }
  }

  @Override
  protected void onInitialize() {
    initializeProductProvider();
    super.onInitialize();
  }

  private void initializeProductProvider() {
    productDataProvider.setUser(getUserName());
    productDataProvider.setPassword(getPassword());
    productDataProvider.setSite(getSite());
  }
}
