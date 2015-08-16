package com.netbrasoft.gnuob.shop.checkout;

import java.math.BigDecimal;
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
import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.OrderRecord;
import com.netbrasoft.gnuob.api.Product;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.product.ProductCarousel;
import com.netbrasoft.gnuob.shop.security.ShopRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.CarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.ICarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverConfig;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig.Placement;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconBehavior;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class CheckoutOrderViewPanel extends Panel {

   class OrderRecordDataProvider implements IDataProvider<OrderRecord> {

      private static final long serialVersionUID = -7558540790400984298L;

      private transient List<OrderRecord> orderRecords = new ArrayList<OrderRecord>();

      @Override
      public void detach() {
         return;
      }

      @Override
      public Iterator<? extends OrderRecord> iterator(long first, long count) {
         final List<OrderRecord> orderRecordProductIteratorList = new ArrayList<OrderRecord>();

         for (int index = (int) first; index < first + count; index++) {
            orderRecordProductIteratorList.add(orderRecords.get(index));
         }

         return orderRecordProductIteratorList.iterator();
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

      private static final long serialVersionUID = 4070664509842584692L;

      private int selectedIndex = 0;

      protected OrderRecordDataView() {
         super("orderRecordDataview", orderRecordDataProvider);
      }

      @Override
      protected Item<OrderRecord> newItem(String id, int index, IModel<OrderRecord> model) {
         final Item<OrderRecord> item = super.newItem(id, index, model);

         if (index == selectedIndex) {
            item.add(new AttributeModifier("class", "info"));
         }

         return item;
      }

      @Override
      protected void populateItem(Item<OrderRecord> item) {
         final BigDecimal amount = item.getModelObject().getAmount();
         final BigDecimal tax = item.getModelObject().getTax();
         final BigDecimal quantity = BigDecimal.valueOf(item.getModelObject().getQuantity().intValue());

         item.setModel(new CompoundPropertyModel<OrderRecord>(item.getModelObject()));
         item.add(new Label("name"));
         item.add(new Label("deliveryDate"));
         item.add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(amount.add(tax).multiply(quantity)))));
         item.add(new AjaxEventBehavior("click") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onEvent(AjaxRequestTarget target) {
               selectedIndex = item.getIndex();

               orderRecordProductDataProvider.orderRecords.clear();
               orderRecordProductDataProvider.orderRecords.add(item.getModelObject());
               target.add(orderRecordProductDataViewContainer);
               target.add(orderRecordDataviewContainer);
            }
         });
      }
   }

   class OrderRecordProductDataView extends DataView<OrderRecord> {

      private static final long serialVersionUID = -3333902779955513421L;

      private static final int ITEMS_PER_PAGE = 5;

      protected OrderRecordProductDataView() {
         super("orderRecordProductDataView", orderRecordProductDataProvider, ITEMS_PER_PAGE);
      }

      @Override
      protected void populateItem(Item<OrderRecord> item) {

         productDataProvider.getType().setNumber(item.getModelObject().getProductNumber());

         @SuppressWarnings("unchecked")
         final
         Iterator<Product> iterator = (Iterator<Product>) productDataProvider.iterator(0, 1);

         if (iterator.hasNext()) {
            item.getModelObject().setProduct(iterator.next());

            final List<ICarouselImage> carouselImages = new ArrayList<ICarouselImage>();

            for (final Content content : item.getModelObject().getProduct().getContents()) {
               if (MediaType.HTML_UTF_8.is(MediaType.parse(content.getFormat()))) {
                  carouselImages.add(new CarouselImage(new String(content.getContent())));
               }
            }

            item.setModel(new CompoundPropertyModel<OrderRecord>(item.getModelObject()));
            item.add(new ProductCarousel("productCarousel", carouselImages)
                  .add(new PopoverBehavior(Model.of(getString("descriptionMessage")), Model.of(item.getModelObject().getDescription()), new PopoverConfig().withHoverTrigger().withPlacement(Placement.left))));
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
         }
      }
   }

   private static final long serialVersionUID = 7944947444790944275L;

   @SpringBean(name = "ProductDataProvider", required = true)
   private GenericTypeDataProvider<Product> productDataProvider;

   private final OrderRecordDataProvider orderRecordProductDataProvider = new OrderRecordDataProvider();

   private final OrderRecordDataProvider orderRecordDataProvider = new OrderRecordDataProvider();

   private final OrderRecordProductDataView orderRecordProductDataView = new OrderRecordProductDataView();

   private final OrderRecordDataView orderRecordDataView = new OrderRecordDataView();

   private final WebMarkupContainer orderRecordProductDataViewContainer = new WebMarkupContainer("orderRecordProductDataViewContainer") {

      private static final long serialVersionUID = -2854403993766433450L;

      @Override
      protected void onInitialize() {
         final long index = orderRecordDataProvider.size();

         if (index > 0) {
            orderRecordProductDataProvider.orderRecords.add(orderRecordDataProvider.iterator(0, 1).next());
         }

         add(orderRecordProductDataView.setOutputMarkupId(true));
         super.onInitialize();
      }
   };

   private final WebMarkupContainer orderRecordDataviewContainer = new WebMarkupContainer("orderRecordDataviewContainer") {

      private static final long serialVersionUID = -9218076119837972841L;

      @Override
      protected void onInitialize() {
         add(orderRecordDataView.setOutputMarkupId(true));
         super.onInitialize();
      }
   };

   public CheckoutOrderViewPanel(final String id, final IModel<Order> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
      productDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
      productDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
      productDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
      productDataProvider.setType(new Product());
      productDataProvider.getType().setActive(true);
      productDataProvider.setOrderBy(OrderBy.NONE);

      orderRecordDataProvider.orderRecords.clear();
      orderRecordDataProvider.orderRecords.addAll(((Order) getDefaultModelObject()).getRecords());

      add(orderRecordProductDataViewContainer.setOutputMarkupId(true));
      add(orderRecordDataviewContainer.setOutputMarkupId(true));

      super.onInitialize();
   }
}
