package com.netbrasoft.gnuob.shop.category;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
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

import com.google.common.net.MediaType;
import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.Content;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.OrderRecord;
import com.netbrasoft.gnuob.api.Product;
import com.netbrasoft.gnuob.api.SubCategory;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.security.ShopRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Size;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.Carousel;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.CarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.ICarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverConfig;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig.Placement;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.list.BootstrapListView;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class SubCategoryProductViewPanel extends Panel {

   class OrderRecordDataProvider implements IDataProvider<OrderRecord> {

      private static final long serialVersionUID = 3755475588885853693L;

      private List<OrderRecord> orderRecords = new ArrayList<OrderRecord>();

      @Override
      public void detach() {
         return;
      }

      public List<OrderRecord> getOrderRecords() {
         return orderRecords;
      }

      @Override
      public Iterator<? extends OrderRecord> iterator(long first, long count) {
         List<OrderRecord> orderRecordIteratorList = new ArrayList<OrderRecord>();

         for (int index = (int) first; index < first + count; index++) {
            orderRecordIteratorList.add(orderRecords.get(index));
         }

         return orderRecordIteratorList.iterator();
      }

      @Override
      public IModel<OrderRecord> model(OrderRecord object) {
         return Model.of(object);
      }

      public void setOrderRecords(List<OrderRecord> orderRecords) {
         this.orderRecords = orderRecords;
      }

      @Override
      public long size() {
         return orderRecords.size();
      }
   }

   class OrderRecordDataView extends DataView<OrderRecord> {

      private static final long serialVersionUID = -8885578770770605991L;

      protected OrderRecordDataView() {
         super("orderRecordDataview", orderRecordDataProvider);
      }

      @Override
      protected void populateItem(Item<OrderRecord> item) {
         IModel<OrderRecord> compound = new CompoundPropertyModel<OrderRecord>(item.getModelObject());
         item.setModel(compound);
         item.add(new Label("product.name"));
         item.add(new Label("quantity"));
         item.add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(item.getModelObject().getProduct().getAmount().subtract(item.getModelObject().getProduct().getDiscount())))));
         item.add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(item.getModelObject().getProduct().getAmount()))));
      }
   }

   class OrderRecordProductDataProvider implements IDataProvider<Product> {

      private static final long serialVersionUID = 9170940545796805775L;

      private List<Product> products = new ArrayList<Product>();

      @Override
      public void detach() {
         return;
      }

      public List<Product> getProducts() {
         return products;
      }

      @Override
      public Iterator<? extends Product> iterator(long first, long count) {
         List<Product> productIteratorList = new ArrayList<Product>();

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

   class OrderRecordProductDataView extends DataView<Product> {

      private static final long serialVersionUID = -3333902779955513421L;

      private static final int ITEMS_PER_PAGE = 5;

      protected OrderRecordProductDataView() {
         super("orderRecordProductDataView", orderRecordProductDataProvider, ITEMS_PER_PAGE);
      }

      @Override
      protected void populateItem(Item<Product> item) {
         List<ICarouselImage> carouselImages = new ArrayList<ICarouselImage>();

         for (Content content : item.getModelObject().getContents()) {
            if (MediaType.HTML_UTF_8.is(MediaType.parse(content.getFormat()))) {
               carouselImages.add(new CarouselImage(new String(content.getContent())));
            }
         }

         item.setModel(new CompoundPropertyModel<Product>(item.getModelObject()));
         item.add(new ProductCarousel("productCarousel", carouselImages));
         item.add(new Loop("rating", ITEMS_PER_PAGE) {

            private static final long serialVersionUID = -443304621920358169L;

            @Override
            protected void populateItem(LoopItem loopItem) {
               loopItem.add(new IconBehavior(loopItem.getIndex() < item.getModelObject().getRating() ? GlyphIconType.star : GlyphIconType.starempty));
            }
         });
         item.add(new Label("name"));
         item.add(new Label("stock.quantity"));
         item.add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(item.getModelObject().getAmount().subtract(item.getModelObject().getDiscount())))));
         item.add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(item.getModelObject().getAmount()))));
      }
   }

   class OrderRecordViewFragement extends Fragment {

      private static final long serialVersionUID = -5518685687286043845L;

      public OrderRecordViewFragement() {
         super("subCategoryProductViewFragement", "orderRecordViewFragement", SubCategoryProductViewPanel.this, SubCategoryProductViewPanel.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
         add(orderRecordProductDataView.setOutputMarkupId(true));
         add(orderRecordDataviewContainer.setOutputMarkupId(true));
         super.onInitialize();
      }
   }

   class ProductCarousel extends Carousel {

      private static final long serialVersionUID = -8356867197970835590L;

      private ProductCarousel(final String markupId, final List<ICarouselImage> images) {
         super(markupId, images);
      }

      @Override
      protected Component newImage(String markupId, ICarouselImage image) {
         final Label html = new Label(markupId, new AbstractReadOnlyModel<String>() {

            private static final long serialVersionUID = -7501719023515852494L;

            @Override
            public String getObject() {
               return image.url();
            }
         });
         return html.setEscapeModelStrings(false);
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
         List<ICarouselImage> carouselImages = new ArrayList<ICarouselImage>();

         for (Content content : item.getModelObject().getContents()) {
            if (MediaType.HTML_UTF_8.is(MediaType.parse(content.getFormat()))) {
               carouselImages.add(new CarouselImage(new String(content.getContent())));
            }
         }

         item.setModel(new CompoundPropertyModel<Product>(item.getModelObject()));
         item.add(new ProductCarousel("productCarousel", carouselImages).setInterval(Duration.NONE)
               .add(new PopoverBehavior(Model.of(getString("descriptionMessage")), Model.of(item.getModelObject().getDescription()), new PopoverConfig().withHoverTrigger().withPlacement(Placement.bottom))));
         item.add(new Loop("rating", ITEMS_PER_PAGE) {

            private static final long serialVersionUID = -443304621920358169L;

            @Override
            protected void populateItem(LoopItem loopItem) {
               loopItem.add(new IconBehavior(loopItem.getIndex() < item.getModelObject().getRating() ? GlyphIconType.star : GlyphIconType.starempty));
            }
         });
         item.add(new Label("name"));
         item.add(new Label("stock.quantity"));
         item.add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(item.getModelObject().getAmount().subtract(item.getModelObject().getDiscount())))));
         item.add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(item.getModelObject().getAmount()))));
         item.add(new BootstrapAjaxLink<String>("purchase", Model.of(getString("purchaseMessage")), Buttons.Type.Primary) {

            private static final long serialVersionUID = -2845735209719008615L;

            @Override
            public void onClick(AjaxRequestTarget target) {
               OrderRecord orderRecord = new OrderRecord();
               orderRecord.setProduct(item.getModelObject());

               orderRecordProductDataProvider.getProducts().clear();
               orderRecordProductDataProvider.getProducts().add(orderRecord.getProduct());
               orderRecordDataProvider.getOrderRecords().add(orderRecord);

               SubCategoryProductViewPanel.this.removeAll();
               SubCategoryProductViewPanel.this.add(new OrderRecordViewFragement().setOutputMarkupId(true));

               target.add(SubCategoryProductViewPanel.this);
            }
         }.setSize(Buttons.Size.Small).setOutputMarkupId(true));
      }
   }

   class ProductViewFragement extends Fragment {

      private static final long serialVersionUID = -1722501866439698640L;

      public ProductViewFragement() {
         super("subCategoryProductViewFragement", "productViewFragement", SubCategoryProductViewPanel.this, SubCategoryProductViewPanel.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
         add(subCategoryMenuBootstrapListView.setOutputMarkupId(true));
         add(subCategoryDataview.setOutputMarkupId(true));
         add(productDataView.setOutputMarkupId(true));
         add(productPagingNavigator);
         super.onInitialize();
      }
   }

   class SubCategoryBootstrapListView extends BootstrapListView<SubCategory> {

      private static final long serialVersionUID = 2148940232228759419L;

      private SubCategoryBootstrapListView(final List<? extends SubCategory> listData) {
         super("subCategoryBootstrapListView", listData);
      }

      @Override
      protected void populateItem(ListItem<SubCategory> item) {
         item.setModel(new CompoundPropertyModel<SubCategory>(item.getModelObject()));
         item.add(new BootstrapAjaxLink<String>("link", Model.of(item.getModel().getObject().getName()), Buttons.Type.Link) {

            private static final long serialVersionUID = -1216788078532675590L;

            @Override
            public void onClick(AjaxRequestTarget target) {
               List<SubCategory> subCategories = new ArrayList<SubCategory>();
               subCategories.add(((SubCategory) item.getDefaultModelObject()));

               subCategoryDataProvider.setSubCategories(subCategories.get(0).getSubCategories());
               subCategoryMenuBootstrapListView.setModelObject(subCategories);
               productDataProvider.setType(new Product());
               productDataProvider.getType().setActive(true);
               productDataProvider.getType().getSubCategories().addAll(subCategories);

               SubCategoryProductViewPanel.this.removeAll();
               SubCategoryProductViewPanel.this.add(new ProductViewFragement().setOutputMarkupId(true));
               target.add(SubCategoryProductViewPanel.this);
            }

         }.setSize(Size.Small).setOutputMarkupId(true));
      }
   }

   class SubCategoryDataProvider implements IDataProvider<SubCategory> {

      private static final long serialVersionUID = -2600778565688301137L;

      private List<SubCategory> subCategories = new ArrayList<SubCategory>();

      @Override
      public void detach() {
         return;
      }

      public List<SubCategory> getSubCategories() {
         return subCategories;
      }

      @Override
      public Iterator<? extends SubCategory> iterator(long first, long count) {
         List<SubCategory> subCategoryIteratorList = new ArrayList<SubCategory>();

         for (int index = (int) first; index < first + count; index++) {
            subCategoryIteratorList.add(subCategories.get(index));
         }

         return subCategoryIteratorList.iterator();
      }

      @Override
      public IModel<SubCategory> model(SubCategory object) {
         return new Model<SubCategory>(object);
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
               StringBuilder contentStringBuilder = new StringBuilder();

               for (Content content : item.getModelObject().getContents()) {
                  contentStringBuilder.append(new String(content.getContent()));
               }

               return contentStringBuilder.toString();
            }
         }).setEscapeModelStrings(false));
         item.add(new AjaxEventBehavior("click") {

            private static final long serialVersionUID = 3898435649434303190L;

            @Override
            protected void onEvent(AjaxRequestTarget target) {
               List<SubCategory> subCategories = new ArrayList<SubCategory>();
               subCategories.add(((SubCategory) item.getDefaultModelObject()));

               subCategoryDataProvider.setSubCategories(subCategories.get(0).getSubCategories());
               subCategoryMenuBootstrapListView.setModelObject(subCategories);
               productDataProvider.setType(new Product());
               productDataProvider.getType().setActive(true);
               productDataProvider.getType().getSubCategories().addAll(subCategories);

               SubCategoryProductViewPanel.this.removeAll();
               SubCategoryProductViewPanel.this.add(new ProductViewFragement().setOutputMarkupId(true));
               target.add(SubCategoryProductViewPanel.this);
            }
         });
      }
   }

   class SubCategoryMenuBootstrapListView extends BootstrapListView<SubCategory> {

      private static final long serialVersionUID = 2148940232228759419L;

      private SubCategoryMenuBootstrapListView() {
         super("subCategoryMenuBootstrapListView", ((Category) SubCategoryProductViewPanel.this.getDefaultModelObject()).getSubCategories());
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
         super("subCategoryProductViewFragement", "subCategoryViewFragement", SubCategoryProductViewPanel.this, SubCategoryProductViewPanel.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
         add(subCategoryMenuBootstrapListView.setOutputMarkupId(true));
         add(subCategoryDataview.setOutputMarkupId(true));
         super.onInitialize();
      }
   }

   private static final long serialVersionUID = -9083340164646887954L;

   private WebMarkupContainer orderRecordDataviewContainer = new WebMarkupContainer("orderRecordDataviewContainer") {

      private static final long serialVersionUID = 1L;

      @Override
      protected void onInitialize() {
         add(orderRecordDataview);
         super.onInitialize();
      }
   };

   private SubCategoryDataProvider subCategoryDataProvider = new SubCategoryDataProvider();

   private OrderRecordDataProvider orderRecordDataProvider = new OrderRecordDataProvider();

   private OrderRecordProductDataProvider orderRecordProductDataProvider = new OrderRecordProductDataProvider();

   private SubCategoryMenuBootstrapListView subCategoryMenuBootstrapListView = new SubCategoryMenuBootstrapListView();

   private SubCategoryDataview subCategoryDataview = new SubCategoryDataview();

   private OrderRecordProductDataView orderRecordProductDataView = new OrderRecordProductDataView();

   private OrderRecordDataView orderRecordDataview = new OrderRecordDataView();

   private ProductDataView productDataView = new ProductDataView();

   private BootstrapPagingNavigator productPagingNavigator = new BootstrapPagingNavigator("productPagingNavigator", productDataView);

   @SpringBean(name = "ProductDataProvider", required = true)
   private GenericTypeDataProvider<Product> productDataProvider;

   public SubCategoryProductViewPanel(final String id, final IModel<Category> model) {
      super(id, model);
   }

   private List<SubCategory> createFlatSubCategoryList(List<SubCategory> subCategories) {
      List<SubCategory> flatSubCategoryList = new ArrayList<SubCategory>();

      for (SubCategory subCategory : subCategories) {
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
      productDataProvider.setOrderBy(OrderBy.NONE);

      subCategoryDataProvider.setSubCategories(createFlatSubCategoryList(((Category) SubCategoryProductViewPanel.this.getDefaultModelObject()).getSubCategories()));

      super.onInitialize();
   }
}
