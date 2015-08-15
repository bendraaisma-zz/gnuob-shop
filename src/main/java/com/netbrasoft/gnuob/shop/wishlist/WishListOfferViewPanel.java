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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
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
public class WishListOfferViewPanel extends Panel {

   class OfferRecordDataProvider implements IDataProvider<OfferRecord> {

      private static final long serialVersionUID = -7558540790400984298L;

      private List<OfferRecord> offerRecords = new ArrayList<OfferRecord>();

      @Override
      public void detach() {
         return;
      }

      public List<OfferRecord> getOfferRecords() {
         return offerRecords;
      }

      @Override
      public Iterator<? extends OfferRecord> iterator(long first, long count) {
         final List<OfferRecord> offerRecordProductIteratorList = new ArrayList<OfferRecord>();

         for (int index = (int) first; index < first + count; index++) {
            offerRecordProductIteratorList.add(offerRecords.get(index));
         }

         return offerRecordProductIteratorList.iterator();
      }

      @Override
      public IModel<OfferRecord> model(OfferRecord object) {
         return Model.of(object);
      }

      public void setOfferRecords(List<OfferRecord> offerRecords) {
         this.offerRecords = offerRecords;
      }

      @Override
      public long size() {
         return offerRecords.size();
      }
   }

   class OfferRecordDataView extends DataView<OfferRecord> {

      private static final long serialVersionUID = 4070664509842584692L;

      private int selectedIndex = 0;

      protected OfferRecordDataView() {
         super("offerRecordDataview", offerRecordDataProvider);
      }

      @Override
      protected Item<OfferRecord> newItem(String id, int index, IModel<OfferRecord> model) {
         final Item<OfferRecord> item = super.newItem(id, index, model);

         if (index == selectedIndex) {
            item.add(new AttributeModifier("class", "info"));
         }

         return item;
      }

      @Override
      protected void populateItem(Item<OfferRecord> item) {
         final BigDecimal amount = item.getModelObject().getAmount();
         final BigDecimal tax = item.getModelObject().getTax();
         final BigDecimal discount = item.getModelObject().getDiscount();
         final BigDecimal quantity = BigDecimal.valueOf(item.getModelObject().getQuantity().intValue());

         item.setModel(new CompoundPropertyModel<OfferRecord>(item.getModelObject()));
         item.add(new Label("name"));
         item.add(new Label("option"));
         item.add(new Label("quantity"));
         item.add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(amount.add(tax).add(discount).multiply(quantity)))));
         item.add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(amount.add(tax).multiply(quantity)))));
         item.add(new AjaxEventBehavior("click") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onEvent(AjaxRequestTarget target) {
               selectedIndex = item.getIndex();

               offerRecordProductDataProvider.offerRecords.clear();
               offerRecordProductDataProvider.offerRecords.add(item.getModelObject());
               target.add(offerRecordProductDataViewContainer);
               target.add(offerRecordDataviewContainer);
               target.add(offerRecordTotalDataviewContainer);
            }
         });
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

         productDataProvider.getType().setNumber(item.getModelObject().getProductNumber());

         @SuppressWarnings("unchecked")
         final Iterator<Product> iterator = (Iterator<Product>) productDataProvider.iterator(0, 1);

         if (iterator.hasNext()) {
            item.getModelObject().setProduct(iterator.next());

            final List<ICarouselImage> carouselImages = new ArrayList<ICarouselImage>();

            for (final Content content : item.getModelObject().getProduct().getContents()) {
               if (MediaType.HTML_UTF_8.is(MediaType.parse(content.getFormat()))) {
                  carouselImages.add(new CarouselImage(new String(content.getContent())));
               }
            }

            item.setModel(new CompoundPropertyModel<OfferRecord>(item.getModelObject()));
            item.add(new ProductCarousel("productCarousel", carouselImages).add(new PopoverBehavior(Model.of(getString("descriptionMessage")), Model.of(item.getModelObject().getDescription()), new PopoverConfig().withHoverTrigger().withPlacement(Placement.left))));
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

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class RemoveAjaxButton extends BootstrapAjaxLink<String> {

      private static final long serialVersionUID = 1090211687798345558L;

      public RemoveAjaxButton() {
         super("remove", Model.of(WishListOfferViewPanel.this.getString("removeFromWishListMessage")), Buttons.Type.Default,  Model.of(WishListOfferViewPanel.this.getString("removeFromWishListMessage")));
      }

      @Override
      public void onClick(AjaxRequestTarget target) {
         final Shopper shopper = shopperDataProvider.find(new Shopper());

         Offer offer = (Offer) WishListOfferViewPanel.this.getDefaultModelObject();
         offer.setActive(false);
         offer = offerDataProvider.merge(offer);

         shopper.setContract(offerDataProvider.findById(offer).getContract());

         shopperDataProvider.merge(shopper);

         target.add(offerRecordProductDataViewContainer);
         target.add(offerRecordDataviewContainer);
         target.add(offerRecordTotalDataviewContainer);
         target.add(getPage());
      }
   }

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class SaveAjaxButton extends BootstrapAjaxLink<String>  {

      private static final long serialVersionUID = 8687168415274819114L;

      public SaveAjaxButton() {
         super("save", Model.of(WishListOfferViewPanel.this.getString("addToCartMessage")), Buttons.Type.Primary, Model.of(WishListOfferViewPanel.this.getString("addToCartMessage")));
      }

      @Override
      public void onClick(AjaxRequestTarget target) {
         final Shopper shopper = shopperDataProvider.find(new Shopper());

         final Offer offer = (Offer) WishListOfferViewPanel.this.getDefaultModelObject();

         shopper.getCart().getRecords().clear();

         for(final OfferRecord sourceOfferRecord : offer.getRecords()) {
            final OfferRecord targetOfferRecord = new OfferRecord();

            if(sourceOfferRecord.getProduct() == null) {
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

   private static final long serialVersionUID = 7944947444790944275L;

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   @SpringBean(name = "ProductDataProvider", required = true)
   private GenericTypeDataProvider<Product> productDataProvider;

   private final OfferRecordDataProvider offerRecordProductDataProvider = new OfferRecordDataProvider();

   private final OfferRecordDataProvider offerRecordDataProvider = new OfferRecordDataProvider();

   private final OfferRecordProductDataView offerRecordProductDataView = new OfferRecordProductDataView();

   private final OfferRecordDataView offerRecordDataView = new OfferRecordDataView();

   private final WebMarkupContainer offerRecordProductDataViewContainer = new WebMarkupContainer("offerRecordProductDataViewContainer") {

      private static final long serialVersionUID = -2854403993766433450L;

      @Override
      protected void onInitialize() {
         final long index = offerRecordDataProvider.size();

         if (index > 0) {
            offerRecordProductDataProvider.offerRecords.add(offerRecordDataProvider.iterator(0, 1).next());
         }

         add(offerRecordProductDataView.setOutputMarkupId(true));
         super.onInitialize();
      }
   };

   private final WebMarkupContainer offerRecordDataviewContainer = new WebMarkupContainer("offerRecordDataviewContainer") {

      private static final long serialVersionUID = -9218076119837972841L;

      @Override
      protected void onInitialize() {
         add(offerRecordDataView.setOutputMarkupId(true));
         super.onInitialize();
      }
   };

   private final WebMarkupContainer offerRecordTotalDataviewContainer = new WebMarkupContainer("offerRecordTotalDataviewContainer") {

      private static final long serialVersionUID = -7304590918079257112L;

      @Override
      protected void onInitialize() {
         add(totalDiscountLabel.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
         add(totalLabel.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
         add(totalShippingCost.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
         add(new RemoveAjaxButton().setOutputMarkupId(true));
         add(new SaveAjaxButton().setOutputMarkupId(true));
         super.onInitialize();
      }
   };

   private final Label totalDiscountLabel = new Label("totalDiscount", Model.of(NumberFormat.getCurrencyInstance().format(((Offer) getDefaultModelObject()).getDiscountTotal())));

   private final Label totalLabel = new Label("total", Model.of(NumberFormat.getCurrencyInstance().format(((Offer) getDefaultModelObject()).getOfferTotal())));

   private final Label totalShippingCost = new Label("totalShippingCost", Model.of(NumberFormat.getCurrencyInstance().format(((Offer) getDefaultModelObject()).getShippingTotal())));

   @SpringBean(name = "OfferDataProvider", required = true)
   private GenericTypeDataProvider<Offer> offerDataProvider;

   public WishListOfferViewPanel(final String id, final IModel<Offer> model) {
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

      offerDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
      offerDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
      offerDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
      offerDataProvider.setType(new Offer());
      offerDataProvider.getType().setActive(true);
      offerDataProvider.setOrderBy(OrderBy.NONE);
      offerDataProvider.getType().setContract(shopperDataProvider.find(new Shopper()).getContract());

      offerRecordDataProvider.offerRecords.clear();
      offerRecordDataProvider.offerRecords.addAll(((Offer) getDefaultModelObject()).getRecords());

      add(offerRecordProductDataViewContainer.setOutputMarkupId(true));
      add(offerRecordDataviewContainer.setOutputMarkupId(true));
      add(offerRecordTotalDataviewContainer.setOutputMarkupId(true));

      super.onInitialize();
   }
}
