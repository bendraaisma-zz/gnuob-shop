package com.netbrasoft.gnuob.shop.cart;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
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
import com.netbrasoft.gnuob.api.Option;
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

      public BigDecimal getChartTotal() {
         return shopperDataProvider.find(new Shopper()).getChartTotal();
      }

      public BigDecimal getChartTotalDiscount() {
         return shopperDataProvider.find(new Shopper()).getChartTotalDiscount();
      }

      public BigDecimal getShippingCostTotal() {
         return shopperDataProvider.find(new Shopper()).getShippingCostTotal();
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
         BigDecimal amount = item.getModelObject().getProduct().getAmount();
         BigDecimal tax = item.getModelObject().getProduct().getTax();
         BigDecimal discount = item.getModelObject().getProduct().getDiscount();
         BigDecimal quantity = BigDecimal.valueOf(item.getModelObject().getQuantity().intValue());

         IModel<OfferRecord> compound = new CompoundPropertyModel<OfferRecord>(item.getModelObject());
         item.setModel(compound);
         item.add(new Label("name"));
         item.add(new Label("option"));
         item.add(new Label("quantity"));
         item.add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(amount.add(tax).subtract(discount).multiply(quantity)))));
         item.add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(amount.add(tax).multiply(quantity)))));
         item.add(new AjaxEventBehavior("click") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onEvent(AjaxRequestTarget target) {
               selectedIndex = item.getIndex();

               offerRecordProductDataProvider.offerRecord.clear();
               offerRecordProductDataProvider.getProductOfferRecord().add(item.getModelObject());
               target.add(offerRecordProductDataViewContainer);
               target.add(offerRecordDataviewContainer);
            }
         });
      }
   }

   class OfferRecordProductDataProvider implements IDataProvider<OfferRecord> {

      private static final long serialVersionUID = 9170940545796805775L;

      private List<OfferRecord> offerRecord = new ArrayList<OfferRecord>();

      @Override
      public void detach() {
         return;
      }

      public List<OfferRecord> getProductOfferRecord() {
         return offerRecord;
      }

      @Override
      public Iterator<? extends OfferRecord> iterator(long first, long count) {
         List<OfferRecord> offerRecordProductIteratorList = new ArrayList<OfferRecord>();

         for (int index = (int) first; index < first + count; index++) {
            offerRecordProductIteratorList.add(offerRecord.get(index));
         }

         return offerRecordProductIteratorList.iterator();
      }

      @Override
      public IModel<OfferRecord> model(OfferRecord object) {
         return Model.of(object);
      }

      public void setProducts(List<OfferRecord> offerRecord) {
         this.offerRecord = offerRecord;
      }

      @Override
      public long size() {
         return offerRecord.size();
      }
   }

   class OfferRecordProductDataView extends DataView<OfferRecord> {

      private static final long serialVersionUID = -3333902779955513421L;

      private static final int ITEMS_PER_PAGE = 5;

      protected OfferRecordProductDataView() {
         super("offerRecordProductDataView", offerRecordProductDataProvider, ITEMS_PER_PAGE);
      }

      @Override
      protected void populateItem(Item<OfferRecord> item) {
         List<ICarouselImage> carouselImages = new ArrayList<ICarouselImage>();

         Option option = new Option();
         option.setValue(item.getModelObject().getOption());

         DropDownChoice<Integer> dropDownChoiceQuantity = new DropDownChoice<Integer>("quantity", Model.of(item.getModelObject().getQuantity().intValue()), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
         DropDownChoice<Option> dropDownChoiceOption = new DropDownChoice<Option>("option", Model.of(option), item.getModelObject().getProduct().getOptions(), new ChoiceRenderer<Option>("description", "value")) {

            private static final long serialVersionUID = -1948250807329668681L;

            @Override
            protected boolean isDisabled(Option object, int index, String selected) {
               return object.isDisabled();
            };
         };

         for (Content content : item.getModelObject().getProduct().getContents()) {
            if (MediaType.HTML_UTF_8.is(MediaType.parse(content.getFormat()))) {
               carouselImages.add(new CarouselImage(new String(content.getContent())));
            }
         }

         item.setModel(new CompoundPropertyModel<OfferRecord>(item.getModelObject()));
         item.add(new ProductCarousel("productCarousel", carouselImages));
         item.add(new Label("name"));
         item.add(new Label("product.stock.quantity"));
         item.add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(item.getModelObject().getProduct().getAmount().subtract(item.getModelObject().getProduct().getDiscount())))));
         item.add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(item.getModelObject().getProduct().getAmount()))));
         item.add(new Loop("rating", ITEMS_PER_PAGE) {

            private static final long serialVersionUID = -443304621920358169L;

            @Override
            protected void populateItem(LoopItem loopItem) {
               loopItem.add(new IconBehavior(loopItem.getIndex() < item.getModelObject().getProduct().getRating() ? GlyphIconType.star : GlyphIconType.starempty));
            }
         });
         item.add(dropDownChoiceQuantity.add(new OnChangeAjaxBehavior() {

            private static final long serialVersionUID = -8850274264336474334L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
               item.getModelObject().setQuantity(BigInteger.valueOf(dropDownChoiceQuantity.getModelObject()));

               totalDiscountLabel.setDefaultModel(Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getChartTotalDiscount())));
               totalShippingCost.setDefaultModel(Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getShippingCostTotal())));
               totalLabel.setDefaultModel(Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getChartTotal())));

               target.add(offerRecordProductDataViewContainer);
               target.add(offerRecordDataviewContainer);
               target.add(offerRecordTotalDataviewContainer);
            }
         }));
         item.add(dropDownChoiceOption.add(new OnChangeAjaxBehavior() {

            private static final long serialVersionUID = -8850274264336474334L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
               item.getModelObject().setOption(dropDownChoiceOption.getModelObject().getValue());

               totalDiscountLabel.setDefaultModel(Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getChartTotalDiscount())));
               totalShippingCost.setDefaultModel(Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getShippingCostTotal())));
               totalLabel.setDefaultModel(Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getChartTotal())));

               target.add(offerRecordProductDataViewContainer);
               target.add(offerRecordDataviewContainer);
               target.add(offerRecordTotalDataviewContainer);
            }
         }));
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
         add(offerRecordTotalDataviewContainer.setOutputMarkupId(true));
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
            offerRecordProductDataProvider.offerRecord.add(shopperDataProvider.find(new Shopper()).getCart().get(0));
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

   protected WebMarkupContainer offerRecordTotalDataviewContainer = new WebMarkupContainer("offerRecordTotalDataviewContainer") {

      private static final long serialVersionUID = 1L;

      @Override
      protected void onInitialize() {
         add(totalDiscountLabel.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
         add(totalLabel.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
         add(totalShippingCost.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
         super.onInitialize();
      }
   };

   private OfferRecordDataProvider offerRecordDataProvider = new OfferRecordDataProvider();

   private OfferRecordProductDataProvider offerRecordProductDataProvider = new OfferRecordProductDataProvider();

   private OfferRecordDataView offerRecordDataview = new OfferRecordDataView();

   private OfferRecordProductDataView offerRecordProductDataView = new OfferRecordProductDataView();

   private Label totalDiscountLabel = new Label("totalDiscount", Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getChartTotalDiscount())));

   private Label totalLabel = new Label("total", Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getChartTotal())));

   private Label totalShippingCost = new Label("totalShippingCost", Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getShippingCostTotal())));

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   public CartViewPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }
}
