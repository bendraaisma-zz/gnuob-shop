package com.netbrasoft.gnuob.shop.category;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
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
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.page.CartPage;
import com.netbrasoft.gnuob.shop.product.ProductCarousel;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Size;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.CarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.ICarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverConfig;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig.Placement;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.list.BootstrapListView;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator;

@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CategoryViewPanel extends Panel {

  class OfferRecordDataProvider implements IDataProvider<OfferRecord> {

    private static final long serialVersionUID = 3755475588885853693L;

    @Override
    public void detach() {
      return;
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

  class OfferRecordProductDataProvider implements IDataProvider<Product> {

    private static final long serialVersionUID = 9170940545796805775L;

    private transient List<Product> products = new ArrayList<Product>();

    @Override
    public void detach() {
      return;
    }

    public List<Product> getProducts() {
      return products;
    }

    @Override
    public Iterator<? extends Product> iterator(long first, long count) {
      final List<Product> productIteratorList = new ArrayList<Product>();

      for (int index = (int) first; index < first + count; index++) {
        productIteratorList.add(products.get(index));
      }

      return productIteratorList.iterator();
    }

    @Override
    public IModel<Product> model(Product object) {
      return Model.of(object);
    }

    public void setProducts(List<Product> products) {
      this.products = products;
    }

    @Override
    public long size() {
      return products.size();
    }
  }

  class ProductDataView extends DataView<Product> {

    private static final long serialVersionUID = -8926501865477098913L;

    private static final int ITEMS_PER_PAGE = 5;

    private ProductDataView() {
      super("productDataview", productDataProvider, ITEMS_PER_PAGE);
    }

    @Override
    protected void populateItem(Item<Product> item) {
      final List<ICarouselImage> carouselImages = new ArrayList<ICarouselImage>();

      for (final Content content : item.getModelObject().getContents()) {
        if (MediaType.HTML_UTF_8.is(MediaType.parse(content.getFormat()))) {
          carouselImages.add(new CarouselImage(new String(content.getContent())));
        }
      }

      item.setModel(new CompoundPropertyModel<Product>(item.getModelObject()));
      item.add(new Label("name"));
      item.add(new Label("stock.quantity"));
      item.add(new Label("amountWithDiscount",
          Model.of(NumberFormat.getCurrencyInstance().format(item.getModelObject().getAmount().subtract(item.getModelObject().getDiscount())))));
      item.add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(item.getModelObject().getAmount()))));
      item.add(new ProductCarousel("productCarousel", carouselImages).setInterval(Duration.NONE).add(new PopoverBehavior(Model.of(getString("descriptionMessage")),
          Model.of(item.getModelObject().getDescription()), new PopoverConfig().withHoverTrigger().withPlacement(Placement.bottom))));
      item.add(new Loop("rating", ITEMS_PER_PAGE) {

        private static final long serialVersionUID = -443304621920358169L;

        @Override
        protected void populateItem(LoopItem loopItem) {
          loopItem.add(new IconBehavior(loopItem.getIndex() < item.getModelObject().getRating() ? GlyphIconType.star : GlyphIconType.starempty));
        }
      });
      item.add(new BootstrapAjaxLink<String>("purchase", Model.of(getString("purchaseMessage")), Buttons.Type.Primary, Model.of(getString("purchaseMessage"))) {

        private static final long serialVersionUID = -2845735209719008615L;

        @Override
        public void onClick(AjaxRequestTarget target) {
          final OfferRecord offerRecord = new OfferRecord();
          offerRecord.setProduct(item.getModelObject());
          offerRecord.setName(item.getModelObject().getName());
          offerRecord.setDescription(item.getModelObject().getDescription());
          offerRecord.setDiscount(item.getModelObject().getDiscount());
          offerRecord.setShippingCost(item.getModelObject().getShippingCost());
          offerRecord.setTax(item.getModelObject().getTax());
          offerRecord.setQuantity(BigInteger.ONE);
          offerRecord.setProductNumber(item.getModelObject().getNumber());
          offerRecord.setAmount(item.getModelObject().getAmount().subtract(item.getModelObject().getDiscount()));
          for (final Option rootOption : item.getModelObject().getOptions()) {
            if (!rootOption.isDisabled()) {

              final Option offerRecordRootOption = new Option();
              BeanUtils.copyProperties(rootOption, offerRecordRootOption, "id", "version");

              for (final SubOption childSubOption : rootOption.getSubOptions()) {
                if (!childSubOption.isDisabled()) {

                  final SubOption offerRecordChildSubOption = new SubOption();
                  BeanUtils.copyProperties(childSubOption, offerRecordChildSubOption, "id", "version");

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
      }.setSize(Buttons.Size.Small).setOutputMarkupId(true));
    }
  }

  class ProductViewFragement extends Fragment {

    private static final long serialVersionUID = -1722501866439698640L;

    public ProductViewFragement() {
      super("subCategoryProductViewFragement", "productViewFragement", CategoryViewPanel.this, CategoryViewPanel.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(subCategoryMenuBootstrapListView.setOutputMarkupId(true));
      add(subCategoryDataviewContainer.setOutputMarkupId(true));
      add(productDataView.setOutputMarkupId(true));
      add(productPagingNavigator);
      super.onInitialize();
    }
  }

  class SubCategoryBootstrapListView extends BootstrapListView<SubCategory> {

    private static final long serialVersionUID = 2148940232228759419L;

    private SubCategoryBootstrapListView(final List<SubCategory> listData) {
      super("subCategoryBootstrapListView", listData);
    }

    @Override
    protected void populateItem(ListItem<SubCategory> item) {
      item.setModel(new CompoundPropertyModel<SubCategory>(item.getModelObject()));
      item.add(new BootstrapAjaxLink<String>("link", Model.of(item.getModel().getObject().getName()), Buttons.Type.Link, Model.of(item.getModel().getObject().getName())) {

        private static final long serialVersionUID = -1216788078532675590L;

        @Override
        public void onClick(AjaxRequestTarget target) {
          final List<SubCategory> subCategories = new ArrayList<SubCategory>();
          subCategories.add((SubCategory) item.getDefaultModelObject());

          subCategoryDataProvider.setSubCategories(subCategories.get(0).getSubCategories());
          subCategoryMenuBootstrapListView.setModelObject(subCategories);
          productDataProvider.setType(new Product());
          productDataProvider.getType().setActive(true);
          productDataProvider.getType().getSubCategories().addAll(subCategories);

          CategoryViewPanel.this.removeAll();
          CategoryViewPanel.this.add(new ProductViewFragement().setOutputMarkupId(true));
          target.add(CategoryViewPanel.this);
        }

      }.setSize(Size.Small).setOutputMarkupId(true));
    }
  }

  class SubCategoryDataProvider implements IDataProvider<SubCategory> {

    private static final long serialVersionUID = -2600778565688301137L;

    private transient List<SubCategory> subCategories = new ArrayList<SubCategory>();

    @Override
    public void detach() {
      return;
    }

    public List<SubCategory> getSubCategories() {
      return subCategories;
    }

    @Override
    public Iterator<? extends SubCategory> iterator(long first, long count) {
      final List<SubCategory> subCategoryIteratorList = new ArrayList<SubCategory>();

      for (int index = (int) first; index < first + count; index++) {
        subCategoryIteratorList.add(subCategories.get(index));
      }

      return subCategoryIteratorList.iterator();
    }

    @Override
    public IModel<SubCategory> model(SubCategory object) {
      return Model.of(object);
    }

    public void setSubCategories(List<SubCategory> subCategories) {
      this.subCategories = subCategories;
    }

    @Override
    public long size() {
      return subCategories.size();
    }
  }

  class SubCategoryDataview extends DataView<SubCategory> {

    private static final long serialVersionUID = 2776123630121635305L;

    private SubCategoryDataview() {
      super("subCategoryDataview", subCategoryDataProvider);
    }

    @Override
    protected void populateItem(Item<SubCategory> item) {
      item.setModel(new CompoundPropertyModel<SubCategory>(item.getModelObject()));
      item.add(new Label("content", new AbstractReadOnlyModel<String>() {

        private static final long serialVersionUID = 4751535250171413561L;

        @Override
        public String getObject() {
          final StringBuilder contentStringBuilder = new StringBuilder();

          for (final Content content : item.getModelObject().getContents()) {
            contentStringBuilder.append(new String(content.getContent()));
          }

          return contentStringBuilder.toString();
        }
      }).setEscapeModelStrings(false));
      item.add(new AjaxEventBehavior("click") {

        private static final long serialVersionUID = 3898435649434303190L;

        @Override
        protected void onEvent(AjaxRequestTarget target) {
          final List<SubCategory> subCategories = new ArrayList<SubCategory>();
          subCategories.add((SubCategory) item.getDefaultModelObject());

          subCategoryDataProvider.setSubCategories(subCategories.get(0).getSubCategories());
          subCategoryMenuBootstrapListView.setModelObject(subCategories);
          productDataProvider.setType(new Product());
          productDataProvider.getType().setActive(true);
          productDataProvider.getType().getSubCategories().addAll(subCategories);

          CategoryViewPanel.this.removeAll();
          CategoryViewPanel.this.add(new ProductViewFragement().setOutputMarkupId(true));
          target.add(CategoryViewPanel.this);
        }
      });
    }
  }

  class SubCategoryMenuBootstrapListView extends BootstrapListView<SubCategory> {

    private static final long serialVersionUID = 2148940232228759419L;

    private SubCategoryMenuBootstrapListView() {
      super("subCategoryMenuBootstrapListView", ((Category) CategoryViewPanel.this.getDefaultModelObject()).getSubCategories());
    }

    @Override
    protected void populateItem(ListItem<SubCategory> item) {
      item.setModel(new CompoundPropertyModel<SubCategory>(item.getModelObject()));
      item.add(new Label("name"));
      item.add(new SubCategoryBootstrapListView(item.getModelObject().getSubCategories()));
    }
  }

  class SubCategoryViewFragement extends Fragment {

    private static final long serialVersionUID = -3028153699938016168L;

    public SubCategoryViewFragement() {
      super("subCategoryProductViewFragement", "subCategoryViewFragement", CategoryViewPanel.this, CategoryViewPanel.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(subCategoryMenuBootstrapListView.setOutputMarkupId(true));
      add(subCategoryDataviewContainer.setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = -9083340164646887954L;

  private final WebMarkupContainer subCategoryDataviewContainer = new WebMarkupContainer("subCategoryDataviewContainer") {

    private static final long serialVersionUID = -497527332092449028L;

    @Override
    protected void onInitialize() {
      add(subCategoryDataview);
      super.onInitialize();
    }
  };

  private final SubCategoryDataProvider subCategoryDataProvider = new SubCategoryDataProvider();

  private final SubCategoryMenuBootstrapListView subCategoryMenuBootstrapListView = new SubCategoryMenuBootstrapListView();

  private final SubCategoryDataview subCategoryDataview = new SubCategoryDataview();

  private final ProductDataView productDataView = new ProductDataView();

  private final BootstrapPagingNavigator productPagingNavigator = new BootstrapPagingNavigator("productPagingNavigator", productDataView);

  @SpringBean(name = "ProductDataProvider", required = true)
  private GenericTypeDataProvider<Product> productDataProvider;

  @SpringBean(name = "ShopperDataProvider", required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  public CategoryViewPanel(final String id, final IModel<Category> model) {
    super(id, model);
  }

  private List<SubCategory> createFlatSubCategoryList(List<SubCategory> subCategories) {
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

    subCategoryDataProvider.setSubCategories(createFlatSubCategoryList(((Category) CategoryViewPanel.this.getDefaultModelObject()).getSubCategories()));

    super.onInitialize();
  }
}
