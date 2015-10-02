package com.netbrasoft.gnuob.shop.wishlist;

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
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.BeanUtils;

import com.google.common.net.MediaType;
import com.netbrasoft.gnuob.api.Content;
import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.OfferRecord;
import com.netbrasoft.gnuob.api.Option;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.Product;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.product.ProductCarousel;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.CarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.ICarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverConfig;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig.Placement;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconBehavior;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class WishListViewPanel extends Panel {

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class EmptyWishtListViewFragment extends Fragment {

      private static final long serialVersionUID = 5058607382122871571L;

      public EmptyWishtListViewFragment() {
         super("wishListOffersViewFragement", "emptyWishListViewFragement", WishListViewPanel.this, WishListViewPanel.this.getDefaultModel());
      }
   }

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class WishtListViewFragment extends Fragment {

      @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
      class OfferContainer extends WebMarkupContainer {

         @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
         class OfferRecordContainer extends WebMarkupContainer {

            private static final long serialVersionUID = 6255198372733049968L;

            private static final int FIVE_STARS_RATING = 5;

            public OfferRecordContainer(IModel<OfferRecord> model) {
               super("offerRecordContainer", model);
            }

            @Override
            protected void onInitialize() {
               removeAll();

               final OfferRecord offerRecord = ((OfferRecord) getDefaultModelObject());
               final List<ICarouselImage> carouselImages = new ArrayList<ICarouselImage>();

               productDataProvider.getType().setNumber(offerRecord.getProductNumber());

               if (productDataProvider.size() > 0) {
                  offerRecord.setProduct(productDataProvider.iterator(0, 1).next());
               } else {
                  offerRecord.setProduct(new Product());
               }

               for (final Content content : offerRecord.getProduct().getContents()) {
                  if (MediaType.HTML_UTF_8.is(MediaType.parse(content.getFormat()))) {
                     carouselImages.add(new CarouselImage(new String(content.getContent())));
                  }
               }

               add(new ProductCarousel("productCarousel", carouselImages).setOutputMarkupId(true)
                     .add(new PopoverBehavior(Model.of(getString("descriptionMessage")), Model.of(offerRecord.getProduct().getDescription()), new PopoverConfig().withHoverTrigger().withPlacement(Placement.left))));
               add(new Label("name"));
               add(new Label("product.stock.quantity"));
               add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(offerRecord.getProduct().getAmount().subtract(offerRecord.getProduct().getDiscount())))));
               add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(offerRecord.getProduct().getAmount()))).setOutputMarkupId(true));
               add(new Loop("rating", FIVE_STARS_RATING) {

                  private static final long serialVersionUID = -443304621920358169L;

                  @Override
                  protected void populateItem(LoopItem loopItem) {
                     loopItem.add(new IconBehavior(loopItem.getIndex() < (offerRecord.getProduct().getRating() != null ? offerRecord.getProduct().getRating() : 0) ? GlyphIconType.star : GlyphIconType.starempty));
                  }
               });
               super.onInitialize();
            }
         }

         @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
         class OfferRecordDataviewContainer extends WebMarkupContainer {

            @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
            class OfferRecordDataView extends DataView<OfferRecord> {

               private static final long serialVersionUID = 4070664509842584692L;

               private Item<OfferRecord> selectedItem;

               protected OfferRecordDataView() {
                  super("offerRecordDataview", offerRecordListDataProvider);
               }

               @Override
               protected Item<OfferRecord> newItem(String id, int index, IModel<OfferRecord> model) {
                  final Item<OfferRecord> item = super.newItem(id, index, model);

                  if (selectedItem == null && index == 0) {
                     selectedItem = item;
                  }

                  if (selectedItem.getIndex() == index) {
                     item.add(new AttributeModifier("class", "info"));
                  }

                  return item;
               }

               @Override
               protected void populateItem(Item<OfferRecord> item) {
                  final StringBuffer stringBuffer = new StringBuffer();
                  final BigDecimal amount = item.getModelObject().getAmount();
                  final BigDecimal tax = item.getModelObject().getTax();
                  final BigDecimal discount = item.getModelObject().getDiscount();
                  final BigDecimal quantity = BigDecimal.valueOf(item.getModelObject().getQuantity().intValue());

                  for (final Option option : item.getModelObject().getOptions()) {
                     stringBuffer.append(option.getValue()).append(": ").append(option.getOptions().iterator().next().getValue()).append(" ");
                  }

                  item.setModel(new CompoundPropertyModel<OfferRecord>(item.getModelObject()));
                  item.add(new Label("name").setOutputMarkupId(true));
                  item.add(new Label("options", Model.of(stringBuffer.toString())).setOutputMarkupId(true));
                  item.add(new Label("quantity").setOutputMarkupId(true));
                  item.add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(amount.add(tax).add(discount).multiply(quantity)))).setOutputMarkupId(true));
                  item.add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(amount.add(tax).multiply(quantity)))).setOutputMarkupId(true));
                  item.add(new AjaxEventBehavior("click") {

                     private static final long serialVersionUID = 1L;

                     @Override
                     public void onEvent(AjaxRequestTarget target) {
                        selectedItem = item;

                        offerRecordContainer.setDefaultModel(item.getDefaultModel());
                        offerRecordContainer.onInitialize();
                        offerRecordDataviewContainer.onInitialize();
                        offerRecordTotalContainer.onInitialize();

                        target.add(offerRecordContainer);
                        target.add(offerRecordDataviewContainer);
                        target.add(offerRecordTotalContainer);
                     }
                  });
               }
            }

            private static final long serialVersionUID = -5431812993098976129L;

            private final OfferRecordDataView offerRecordDataview;

            private final ListDataProvider<OfferRecord> offerRecordListDataProvider;

            public OfferRecordDataviewContainer(final IModel<Offer> model) {
               super("offerRecordDataviewContainer", model);

               offerRecordListDataProvider = new ListDataProvider<OfferRecord>(((Offer) getDefaultModelObject()).getRecords());
               offerRecordDataview = new OfferRecordDataView();
            }

            @Override
            protected void onInitialize() {
               removeAll();

               add(offerRecordDataview.setOutputMarkupId(true));
               super.onInitialize();
            }
         }

         @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
         class OfferRecordTotalContainer extends WebMarkupContainer {

            @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
            class RemoveAjaxButton extends BootstrapAjaxLink<String> {

               private static final long serialVersionUID = 1090211687798345558L;

               public RemoveAjaxButton() {
                  super("remove", Model.of(WishListViewPanel.this.getString("removeFromWishListMessage")), Buttons.Type.Default, Model.of(WishListViewPanel.this.getString("removeFromWishListMessage")));
               }

               @Override
               public void onClick(AjaxRequestTarget target) {
                  final Shopper shopper = shopperDataProvider.find(new Shopper());

                  Offer offer = (Offer) OfferRecordTotalContainer.this.getDefaultModelObject();
                  offer.setActive(false);
                  offer = offerDataProvider.merge(offer);

                  shopper.setContract(offerDataProvider.findById(offer).getContract());

                  shopperDataProvider.merge(shopper);

                  target.add(offerRecordContainer);
                  target.add(offerRecordDataviewContainer);
                  target.add(offerRecordTotalContainer);
                  target.add(getPage());
               }
            }

            @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
            class SaveAjaxButton extends BootstrapAjaxLink<String> {

               private static final long serialVersionUID = 8687168415274819114L;

               public SaveAjaxButton() {
                  super("save", Model.of(WishListViewPanel.this.getString("addToCartMessage")), Buttons.Type.Primary, Model.of(WishListViewPanel.this.getString("addToCartMessage")));
               }

               @Override
               public void onClick(AjaxRequestTarget target) {
                  final Shopper shopper = shopperDataProvider.find(new Shopper());

                  final Offer offer = (Offer) OfferRecordTotalContainer.this.getDefaultModelObject();

                  shopper.getCart().getRecords().clear();

                  for (final OfferRecord sourceOfferRecord : offer.getRecords()) {
                     final OfferRecord targetOfferRecord = new OfferRecord();

                     if (sourceOfferRecord.getProduct() == null) {
                        productDataProvider.getType().setNumber(sourceOfferRecord.getProductNumber());

                        @SuppressWarnings("unchecked")
                        final Iterator<Product> iterator = (Iterator<Product>) productDataProvider.iterator(0, 1);

                        sourceOfferRecord.setProduct(iterator.next());
                     }

                     BeanUtils.copyProperties(sourceOfferRecord, targetOfferRecord);
                     targetOfferRecord.setId(0);
                     targetOfferRecord.setVersion(0);

                     shopper.getCart().getRecords().add(targetOfferRecord);
                  }

                  shopper.setContract(offerDataProvider.findById(offer).getContract());
                  shopperDataProvider.merge(shopper);

                  throw new RedirectToUrlException("cart.html");
               }
            }

            private static final long serialVersionUID = 8368158598130032268L;

            public OfferRecordTotalContainer(final IModel<Offer> model) {
               super("offerRecordTotalContainer", model);
            }

            @Override
            protected void onInitialize() {
               final BigDecimal totalDiscount = ((Offer) getDefaultModelObject()).getDiscountTotal();
               final BigDecimal total = ((Offer) getDefaultModelObject()).getOfferTotal();
               final BigDecimal totalShippingCost = ((Offer) getDefaultModelObject()).getShippingTotal();

               removeAll();

               add(new Label("totalDiscount", Model.of(NumberFormat.getCurrencyInstance().format(totalDiscount))).setOutputMarkupId(true));
               add(new Label("total", Model.of(NumberFormat.getCurrencyInstance().format(total))).setOutputMarkupId(true));
               add(new Label("totalShippingCost", Model.of(NumberFormat.getCurrencyInstance().format(totalShippingCost))).setOutputMarkupId(true));
               add(new RemoveAjaxButton().setOutputMarkupId(true));
               add(new SaveAjaxButton().setOutputMarkupId(true));
               super.onInitialize();
            }
         }

         private static final long serialVersionUID = -42423237162544279L;

         private final OfferRecordContainer offerRecordContainer;

         private final OfferRecordDataviewContainer offerRecordDataviewContainer;

         private final OfferRecordTotalContainer offerRecordTotalContainer;

         public OfferContainer(final IModel<Offer> model) {
            super("offerContainer", model);

            offerRecordContainer = new OfferRecordContainer(new CompoundPropertyModel<OfferRecord>(model.getObject().getRecords().get(0)));
            offerRecordDataviewContainer = new OfferRecordDataviewContainer(model);
            offerRecordTotalContainer = new OfferRecordTotalContainer(model);
         }

         @Override
         protected void onInitialize() {
            add(offerRecordContainer.setOutputMarkupId(true));
            add(offerRecordDataviewContainer.setOutputMarkupId(true));
            add(offerRecordTotalContainer.setOutputMarkupId(true));

            super.onInitialize();
         }
      }

      class OfferDataview extends DataView<Offer> {

         private static final long serialVersionUID = -708080269042062417L;

         protected OfferDataview() {
            super("offerDataview", offerDataProvider);
         }

         @Override
         protected void populateItem(Item<Offer> item) {
            item.setModel(new CompoundPropertyModel<Offer>(item.getModelObject()));
            item.add(new Label("offerId"));
            item.add(new OfferContainer(item.getModel()));
         }
      }

      @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
      class OfferDataviewContainer extends WebMarkupContainer {

         private static final long serialVersionUID = -7103703229307873022L;

         public OfferDataviewContainer() {
            super("offerDataviewContainer");
         }

         @Override
         protected void onInitialize() {
            add(offerDataview.setOutputMarkupId(true));
            super.onInitialize();
         }
      }

      private static final long serialVersionUID = -6168511667223170398L;

      private final OfferDataviewContainer offerDataviewContainer;

      private final OfferDataview offerDataview;

      public WishtListViewFragment() {
         super("wishListOffersViewFragement", "wishListViewFragement", WishListViewPanel.this, WishListViewPanel.this.getDefaultModel());

         offerDataview = new OfferDataview();
         offerDataviewContainer = new OfferDataviewContainer();
      }

      @Override
      protected void onInitialize() {
         add(offerDataviewContainer.setOutputMarkupId(true));
         super.onInitialize();
      }
   }

   private static final long serialVersionUID = -4406441947235524118L;

   @SpringBean(name = "ShopperDataProvider", required = true)
   private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   @SpringBean(name = "OfferDataProvider", required = true)
   private GenericTypeDataProvider<Offer> offerDataProvider;

   @SpringBean(name = "ProductDataProvider", required = true)
   private GenericTypeDataProvider<Product> productDataProvider;

   public WishListViewPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
      offerDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
      offerDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
      offerDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
      offerDataProvider.setType(new Offer());
      offerDataProvider.getType().setActive(true);
      offerDataProvider.setOrderBy(OrderBy.NONE);
      offerDataProvider.getType().setContract(shopperDataProvider.find((Shopper) getDefaultModelObject()).getContract());

      productDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
      productDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
      productDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
      productDataProvider.setType(new Product());
      productDataProvider.getType().setActive(true);
      productDataProvider.setOrderBy(OrderBy.NONE);

      super.onInitialize();
   }
}
