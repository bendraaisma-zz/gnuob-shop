package com.netbrasoft.gnuob.shop.cart;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.common.net.MediaType;
import com.netbrasoft.gnuob.api.Content;
import com.netbrasoft.gnuob.api.OfferRecord;
import com.netbrasoft.gnuob.api.Product;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.product.ProductCarousel;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.CarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.ICarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconBehavior;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class CartViewPanel extends Panel {

   class OfferRecordDataProvider implements IDataProvider<OfferRecord> {

      private static final long serialVersionUID = 3755475588885853693L;

      @Override
      public void detach() {
         return;
      }

      @Override
      public Iterator<? extends OfferRecord> iterator(long first, long count) {
         List<OfferRecord> offerRecordIteratorList = new ArrayList<OfferRecord>();

         for (int index = (int) first; index < first + count; index++) {
            offerRecordIteratorList.add(shopperDataProvider.find(new Shopper()).getCart().get(index));
         }

         return offerRecordIteratorList.iterator();
      }

      @Override
      public IModel<OfferRecord> model(OfferRecord object) {
         return Model.of(object);
      }

      @Override
      public long size() {
         return shopperDataProvider.find(new Shopper()).getCart().size();
      }
   }

   class OfferRecordDataView extends DataView<OfferRecord> {

      private static final long serialVersionUID = -8885578770770605991L;

      private int selectedIndex = 0;

      protected OfferRecordDataView() {
         super("offerRecordDataview", offerRecordDataProvider);
      }

      @Override
      protected Item<OfferRecord> newItem(String id, int index, IModel<OfferRecord> model) {
         Item<OfferRecord> item = super.newItem(id, index, model);

         if (index == selectedIndex) {
            item.add(new AttributeModifier("class", "info"));
         }

         return item;
      }

      @Override
      protected void populateItem(Item<OfferRecord> item) {
         IModel<OfferRecord> compound = new CompoundPropertyModel<OfferRecord>(item.getModelObject());
         item.setModel(compound);
         item.add(new Label("product.name"));
         item.add(new Label("quantity"));
         item.add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(item.getModelObject().getProduct().getAmount().subtract(item.getModelObject().getProduct().getDiscount())))));
         item.add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(item.getModelObject().getProduct().getAmount()))));
         item.add(new AjaxEventBehavior("click") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onEvent(AjaxRequestTarget target) {
               selectedIndex = item.getIndex();

               offerRecordProductDataProvider.products.clear();
               offerRecordProductDataProvider.getProducts().add(item.getModelObject().getProduct());
               target.add(offerRecordProductDataViewContainer);
               target.add(offerRecordDataviewContainer);
            }
         });
      }
   }

   class OfferRecordProductDataProvider implements IDataProvider<Product> {

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

   class OfferRecordProductDataView extends DataView<Product> {

      private static final long serialVersionUID = -3333902779955513421L;

      private static final int ITEMS_PER_PAGE = 5;

      protected OfferRecordProductDataView() {
         super("offerRecordProductDataView", offerRecordProductDataProvider, ITEMS_PER_PAGE);
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

   class OfferRecordViewFragement extends Fragment {

      private static final long serialVersionUID = -5518685687286043845L;

      public OfferRecordViewFragement() {
         super("cartOfferProductViewFragement", "offerRecordViewFragement", CartViewPanel.this, CartViewPanel.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
         add(offerRecordProductDataViewContainer.setOutputMarkupId(true));
         add(offerRecordDataviewContainer.setOutputMarkupId(true));
         add(new Label("totalDiscount", Model.of(NumberFormat.getCurrencyInstance().format(shopperDataProvider.find(new Shopper()).getChartTotalDiscount()))));
         add(new Label("total", Model.of(NumberFormat.getCurrencyInstance().format(shopperDataProvider.find(new Shopper()).getChartTotal()))));
         super.onInitialize();
      }
   }

   private static final long serialVersionUID = 6183635879900747064L;

   protected WebMarkupContainer offerRecordProductDataViewContainer = new WebMarkupContainer("offerRecordProductDataViewContainer") {

      private static final long serialVersionUID = -497527332092449028L;

      @Override
      protected void onInitialize() {
         int index = shopperDataProvider.find(new Shopper()).getCart().size();

         if (index > 0) {
            offerRecordProductDataProvider.products.add(shopperDataProvider.find(new Shopper()).getCart().get(0).getProduct());
         }

         add(offerRecordProductDataView);
         super.onInitialize();
      }
   };

   protected WebMarkupContainer offerRecordDataviewContainer = new WebMarkupContainer("offerRecordDataviewContainer") {

      private static final long serialVersionUID = 1L;

      @Override
      protected void onInitialize() {
         add(offerRecordDataview);
         super.onInitialize();
      }
   };

   private OfferRecordDataProvider offerRecordDataProvider = new OfferRecordDataProvider();

   private OfferRecordProductDataProvider offerRecordProductDataProvider = new OfferRecordProductDataProvider();

   private OfferRecordDataView offerRecordDataview = new OfferRecordDataView();

   private OfferRecordProductDataView offerRecordProductDataView = new OfferRecordProductDataView();

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   public CartViewPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }
}
