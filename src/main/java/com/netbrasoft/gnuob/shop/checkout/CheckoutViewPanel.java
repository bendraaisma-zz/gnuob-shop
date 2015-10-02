package com.netbrasoft.gnuob.shop.checkout;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
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
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.common.net.MediaType;
import com.netbrasoft.gnuob.api.Content;
import com.netbrasoft.gnuob.api.Option;
import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.OrderRecord;
import com.netbrasoft.gnuob.api.Product;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.product.ProductCarousel;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.CarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.ICarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverConfig;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig.Placement;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconBehavior;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class CheckoutViewPanel extends Panel {

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class CheckoutViewFragment extends Fragment {

      @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
      class OrderContainer extends WebMarkupContainer {

         @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
         class OrderRecordContainer extends WebMarkupContainer {

            private static final long serialVersionUID = 6255198372733049968L;

            private static final int FIVE_STARS_RATING = 5;

            public OrderRecordContainer(IModel<OrderRecord> model) {
               super("orderRecordContainer", model);
            }

            @Override
            protected void onInitialize() {
               removeAll();

               final OrderRecord orderRecord = ((OrderRecord) getDefaultModelObject());
               final List<ICarouselImage> carouselImages = new ArrayList<ICarouselImage>();

               productDataProvider.getType().setNumber(orderRecord.getProductNumber());

               if (productDataProvider.size() > 0) {
                  orderRecord.setProduct(productDataProvider.iterator(0, 1).next());
               } else {
                  orderRecord.setProduct(new Product());
               }

               if (orderRecord.getProduct() != null) {
                  for (final Content content : orderRecord.getProduct().getContents()) {
                     if (MediaType.HTML_UTF_8.is(MediaType.parse(content.getFormat()))) {
                        carouselImages.add(new CarouselImage(new String(content.getContent())));
                     }
                  }
               }

               add(new ProductCarousel("productCarousel", carouselImages).setOutputMarkupId(true)
                     .add(new PopoverBehavior(Model.of(getString("descriptionMessage")), Model.of(orderRecord.getProduct().getDescription()), new PopoverConfig().withHoverTrigger().withPlacement(Placement.left))));
               add(new Label("name"));
               add(new Label("product.stock.quantity"));
               add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(orderRecord.getProduct().getAmount().subtract(orderRecord.getProduct().getDiscount())))));
               add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(orderRecord.getProduct().getAmount()))).setOutputMarkupId(true));
               add(new Loop("rating", FIVE_STARS_RATING) {

                  private static final long serialVersionUID = -443304621920358169L;

                  @Override
                  protected void populateItem(LoopItem loopItem) {
                     loopItem.add(new IconBehavior(loopItem.getIndex() < (orderRecord.getProduct().getRating() != null ? orderRecord.getProduct().getRating() : 0) ? GlyphIconType.star : GlyphIconType.starempty));
                  }
               });
               super.onInitialize();
            }
         }

         @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
         class OrderRecordDataviewContainer extends WebMarkupContainer {

            @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
            class OrderRecordDataView extends DataView<OrderRecord> {

               private static final long serialVersionUID = 4070664509842584692L;

               private Item<OrderRecord> selectedItem;

               protected OrderRecordDataView() {
                  super("orderRecordDataview", orderRecordListDataProvider);
               }

               @Override
               protected Item<OrderRecord> newItem(String id, int index, IModel<OrderRecord> model) {
                  final Item<OrderRecord> item = super.newItem(id, index, model);

                  if (selectedItem == null && index == 0) {
                     selectedItem = item;
                  }

                  if (selectedItem.getIndex() == index) {
                     item.add(new AttributeModifier("class", "info"));
                  }

                  return item;
               }

               @Override
               protected void populateItem(Item<OrderRecord> item) {
                  final StringBuffer stringBuffer = new StringBuffer();
                  final BigDecimal amount = item.getModelObject().getAmount();
                  final BigDecimal tax = item.getModelObject().getTax();
                  final BigDecimal quantity = BigDecimal.valueOf(item.getModelObject().getQuantity().intValue());

                  for (final Option option : item.getModelObject().getOptions()) {
                     stringBuffer.append(option.getValue()).append(": ").append(option.getOptions().iterator().next().getValue()).append(" ");
                  }

                  item.setModel(new CompoundPropertyModel<OrderRecord>(item.getModelObject()));
                  item.add(new Label("name").setOutputMarkupId(true));
                  item.add(new Label("options", Model.of(stringBuffer.toString())).setOutputMarkupId(true));
                  item.add(new Label("quantity").setOutputMarkupId(true));
                  item.add(new Label("deliveryDate").setOutputMarkupId(true));
                  item.add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(amount.add(tax).multiply(quantity)))).setOutputMarkupId(true));
                  item.add(new AjaxEventBehavior("click") {

                     private static final long serialVersionUID = 1L;

                     @Override
                     public void onEvent(AjaxRequestTarget target) {
                        selectedItem = item;

                        orderRecordContainer.setDefaultModel(item.getDefaultModel());
                        orderRecordContainer.onInitialize();
                        orderRecordDataviewContainer.onInitialize();
                        orderRecordTotalContainer.onInitialize();

                        target.add(orderRecordContainer);
                        target.add(orderRecordDataviewContainer);
                        target.add(orderRecordTotalContainer);
                     }
                  });
               }
            }

            private static final long serialVersionUID = -5431812993098976129L;

            private final OrderRecordDataView orderRecordDataview;

            private final ListDataProvider<OrderRecord> orderRecordListDataProvider;

            public OrderRecordDataviewContainer(final IModel<Order> model) {
               super("orderRecordDataviewContainer", model);

               orderRecordListDataProvider = new ListDataProvider<OrderRecord>(((Order) getDefaultModelObject()).getRecords());
               orderRecordDataview = new OrderRecordDataView();
            }

            @Override
            protected void onInitialize() {
               removeAll();

               add(orderRecordDataview.setOutputMarkupId(true));
               super.onInitialize();
            }
         }

         @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
         class OrderRecordTotalContainer extends WebMarkupContainer {

            private static final long serialVersionUID = 8368158598130032268L;

            public OrderRecordTotalContainer(final IModel<Order> model) {
               super("orderRecordTotalContainer", model);
            }

            @Override
            protected void onInitialize() {
               final BigDecimal total = ((Order) getDefaultModelObject()).getOrderTotal();
               final BigDecimal totalShippingCost = ((Order) getDefaultModelObject()).getShippingTotal();

               removeAll();

               add(new Label("total", Model.of(NumberFormat.getCurrencyInstance().format(total))).setOutputMarkupId(true));
               add(new Label("totalShippingCost", Model.of(NumberFormat.getCurrencyInstance().format(totalShippingCost))).setOutputMarkupId(true));
               super.onInitialize();
            }
         }

         private static final long serialVersionUID = -42423237162544279L;

         private final OrderRecordContainer orderRecordContainer;

         private final OrderRecordDataviewContainer orderRecordDataviewContainer;

         private final OrderRecordTotalContainer orderRecordTotalContainer;

         public OrderContainer(final IModel<Order> model) {
            super("orderContainer", model);

            orderRecordContainer = new OrderRecordContainer(new CompoundPropertyModel<OrderRecord>(model.getObject().getRecords().get(0)));
            orderRecordDataviewContainer = new OrderRecordDataviewContainer(model);
            orderRecordTotalContainer = new OrderRecordTotalContainer(model);
         }

         @Override
         protected void onInitialize() {
            add(orderRecordContainer.setOutputMarkupId(true));
            add(orderRecordDataviewContainer.setOutputMarkupId(true));
            add(orderRecordTotalContainer.setOutputMarkupId(true));

            super.onInitialize();
         }
      }

      class OrderDataview extends DataView<Order> {

         private static final long serialVersionUID = -708080269042062417L;

         protected OrderDataview() {
            super("orderDataview", orderDataProvider);
         }

         @Override
         protected void populateItem(Item<Order> item) {
            item.setModel(new CompoundPropertyModel<Order>(item.getModelObject()));
            item.add(new Label("orderId"));
            item.add(new OrderContainer(item.getModel()));
         }
      }

      @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
      class OrderDataviewContainer extends WebMarkupContainer {

         private static final long serialVersionUID = -7103703229307873022L;

         public OrderDataviewContainer() {
            super("orderDataviewContainer");
         }

         @Override
         protected void onInitialize() {
            add(orderDataview.setOutputMarkupId(true));
            super.onInitialize();
         }
      }

      private static final long serialVersionUID = -6168511667223170398L;

      private final OrderDataviewContainer orderDataviewContainer;

      private final OrderDataview orderDataview;

      public CheckoutViewFragment() {
         super("checkoutOrdersViewFragement", "checkoutViewFragement", CheckoutViewPanel.this, CheckoutViewPanel.this.getDefaultModel());

         orderDataview = new OrderDataview();
         orderDataviewContainer = new OrderDataviewContainer();
      }

      @Override
      protected void onInitialize() {
         add(orderDataviewContainer.setOutputMarkupId(true));
         super.onInitialize();
      }
   }

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class EmptyCheckoutViewFragment extends Fragment {

      private static final long serialVersionUID = 5058607382122871571L;

      public EmptyCheckoutViewFragment() {
         super("checkoutOrdersViewFragement", "emptyCheckoutViewFragement", CheckoutViewPanel.this, CheckoutViewPanel.this.getDefaultModel());
      }
   }

   private static final long serialVersionUID = -4406441947235524118L;

   @SpringBean(name = "ShopperDataProvider", required = true)
   private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   @SpringBean(name = "OrderDataProvider", required = true)
   private GenericTypeDataProvider<Order> orderDataProvider;

   @SpringBean(name = "ProductDataProvider", required = true)
   private GenericTypeDataProvider<Product> productDataProvider;

   public CheckoutViewPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
      orderDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
      orderDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
      orderDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
      orderDataProvider.setType(new Order());
      orderDataProvider.getType().setActive(true);
      orderDataProvider.setOrderBy(OrderBy.NONE);
      orderDataProvider.getType().setContract(shopperDataProvider.find((Shopper) getDefaultModelObject()).getContract());

      productDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
      productDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
      productDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
      productDataProvider.setType(new Product());
      productDataProvider.getType().setActive(true);
      productDataProvider.setOrderBy(OrderBy.NONE);

      super.onInitialize();
   }
}
