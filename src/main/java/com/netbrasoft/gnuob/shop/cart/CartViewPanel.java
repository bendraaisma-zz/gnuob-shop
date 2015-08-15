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
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.common.net.MediaType;
import com.netbrasoft.gnuob.api.Content;
import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.OfferRecord;
import com.netbrasoft.gnuob.api.Option;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.product.ProductCarousel;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.LoadingBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.CarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.ICarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverConfig;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig.Placement;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconBehavior;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class CartViewPanel extends Panel {

   class EmptyOfferRecordViewFragement extends Fragment {

      private static final long serialVersionUID = 5058607382122871571L;

      public EmptyOfferRecordViewFragement() {
         super("cartOfferProductViewFragement", "emptyOfferRecordViewFragement", CartViewPanel.this, CartViewPanel.this.getDefaultModel());
      }
   }

   class OfferRecordDataProvider implements IDataProvider<OfferRecord> {

      private static final long serialVersionUID = 3755475588885853693L;

      @Override
      public void detach() {
         return;
      }

      public BigDecimal getChartTotal() {
         return shopperDataProvider.find(new Shopper()).getCartTotal();
      }

      public BigDecimal getChartTotalDiscount() {
         return shopperDataProvider.find(new Shopper()).getCartTotalDiscount();
      }

      public BigDecimal getShippingCostTotal() {
         return shopperDataProvider.find(new Shopper()).getShippingCostTotal();
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

   class OfferRecordDataView extends DataView<OfferRecord> {

      private static final long serialVersionUID = -8885578770770605991L;

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
         final BigDecimal amount = item.getModelObject().getProduct().getAmount();
         final BigDecimal tax = item.getModelObject().getProduct().getTax();
         final BigDecimal discount = item.getModelObject().getProduct().getDiscount();
         final BigDecimal quantity = BigDecimal.valueOf(item.getModelObject().getQuantity().intValue());

         item.setModel(new CompoundPropertyModel<OfferRecord>(item.getModelObject()));
         item.add(new RemoveAjaxButton(item.getModel()));
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

               offerRecordProductDataProvider.offerRecords.clear();
               offerRecordProductDataProvider.offerRecords.add(item.getModelObject());
               target.add(offerRecordProductDataViewContainer);
               target.add(offerRecordDataviewContainer);
               target.add(offerRecordTotalDataviewContainer);
            }
         });
      }
   }

   class OfferRecordProductDataProvider implements IDataProvider<OfferRecord> {

      private static final long serialVersionUID = 9170940545796805775L;

      private final List<OfferRecord> offerRecords = new ArrayList<OfferRecord>();

      @Override
      public void detach() {
         return;
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

      @Override
      public long size() {
         return offerRecords.size();
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
         final List<ICarouselImage> carouselImages = new ArrayList<ICarouselImage>();

         final Option option = new Option();
         option.setValue(item.getModelObject().getOption());

         final DropDownChoice<Integer> dropDownChoiceQuantity = new DropDownChoice<Integer>("quantity", Model.of(item.getModelObject().getQuantity().intValue()), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
         final DropDownChoice<Option> dropDownChoiceOption = new DropDownChoice<Option>("option", Model.of(option), item.getModelObject().getProduct().getOptions(), new ChoiceRenderer<Option>("description", "value")) {

            private static final long serialVersionUID = -1948250807329668681L;

            @Override
            protected boolean isDisabled(Option object, int index, String selected) {
               return object.isDisabled();
            };
         };

         for (final Content content : item.getModelObject().getProduct().getContents()) {
            if (MediaType.HTML_UTF_8.is(MediaType.parse(content.getFormat()))) {
               carouselImages.add(new CarouselImage(new String(content.getContent())));
            }
         }

         item.setModel(new CompoundPropertyModel<OfferRecord>(item.getModelObject()));
         item.add(new ProductCarousel("productCarousel", carouselImages).add(new PopoverBehavior(Model.of(getString("descriptionMessage")), Model.of(item.getModelObject().getProduct().getDescription()), new PopoverConfig().withHoverTrigger().withPlacement(Placement.left))));
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

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class RemoveAjaxButton extends BootstrapAjaxLink<OfferRecord> {

      private static final long serialVersionUID = 1090211687798345558L;

      public RemoveAjaxButton(final IModel<OfferRecord> model) {
         super("remove", model, Buttons.Type.Link);
         setSize(Buttons.Size.Mini);
         setLabel(Model.of("X"));
      }

      @Override
      public void onClick(AjaxRequestTarget target) {
         final Shopper shopper = shopperDataProvider.find(new Shopper());
         shopper.getCart().getRecords().remove(getModelObject());

         offerRecordProductDataProvider.offerRecords.clear();

         if (!shopper.getCart().getRecords().isEmpty()) {
            offerRecordProductDataProvider.offerRecords.add(shopper.getCart().getRecords().get(0));
            offerRecordDataview.selectedIndex = 0;
            shopperDataProvider.merge(shopper);

            totalDiscountLabel.setDefaultModel(Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getChartTotalDiscount())));
            totalShippingCost.setDefaultModel(Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getShippingCostTotal())));
            totalLabel.setDefaultModel(Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getChartTotal())));

            target.add(offerRecordProductDataViewContainer);
            target.add(offerRecordDataviewContainer);
            target.add(offerRecordTotalDataviewContainer);
         } else {
            CartViewPanel.this.removeAll();
            CartViewPanel.this.add(new EmptyOfferRecordViewFragement().setOutputMarkupId(true));
            target.add(CartViewPanel.this);
         }
      }
   }

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class SaveAjaxButton extends BootstrapAjaxLink<String> {

      private static final long serialVersionUID = 6184459006667863564L;

      public SaveAjaxButton() {
         super("saveToWishList", Model.of(CartViewPanel.this.getString("addToWishListMessage")), Buttons.Type.Default, Model.of(CartViewPanel.this.getString("addToWishListMessage")));
         setSize(Buttons.Size.Small);
         add(new LoadingBehavior(Model.of(CartViewPanel.this.getString("savingToWishListMessage"))));
      }

      @Override
      public void onClick(AjaxRequestTarget target) {
         saveOfferAndGotoWishList();
         target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(CartViewPanel.this.getString("savingToWishListMessage")))));

      }

      private void saveOffer(Shopper shopper) {
         Offer offer = new Offer();

         offer.setActive(true);
         offer.setInsuranceTotal(BigDecimal.ZERO);
         offer.setHandlingTotal(BigDecimal.ZERO);
         offer.setShippingDiscount(BigDecimal.ZERO);
         offer.getRecords().addAll(shopper.getCart().getRecords());

         offer = offerDataProvider.persist(offer);
         offer.setContract(shopper.getContract());
         offer = offerDataProvider.merge(offer);
         offer = offerDataProvider.findById(offer);

         shopper.setContract(offer.getContract());
      }

      private void saveOfferAndGotoWishList() {
         final Shopper shopper = shopperDataProvider.find(new Shopper());
         saveOffer(shopper);
         shopper.getCart().getRecords().clear();
         shopperDataProvider.merge(shopper);
         throw new RedirectToUrlException("wishlist.html");
      }
   }

   private static final long serialVersionUID = 6183635879900747064L;

   protected WebMarkupContainer offerRecordProductDataViewContainer = new WebMarkupContainer("offerRecordProductDataViewContainer") {

      private static final long serialVersionUID = -497527332092449028L;

      @Override
      protected void onInitialize() {
         final int index = shopperDataProvider.find(new Shopper()).getCart().getRecords().size();

         if (index > 0) {
            offerRecordProductDataProvider.offerRecords.add(shopperDataProvider.find(new Shopper()).getCart().getRecords().get(0));
         }

         add(offerRecordProductDataView.setOutputMarkupId(true));
         super.onInitialize();
      }
   };

   protected WebMarkupContainer offerRecordDataviewContainer = new WebMarkupContainer("offerRecordDataviewContainer") {

      private static final long serialVersionUID = 1L;

      @Override
      protected void onInitialize() {
         add(offerRecordDataview.setOutputMarkupId(true));
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
         add(new SaveAjaxButton().setVisible(shopperDataProvider.find(new Shopper()).isLoggedIn()).setOutputMarkupId(true));
         super.onInitialize();
      }
   };

   private final OfferRecordDataProvider offerRecordDataProvider = new OfferRecordDataProvider();

   private final OfferRecordProductDataProvider offerRecordProductDataProvider = new OfferRecordProductDataProvider();

   private final OfferRecordDataView offerRecordDataview = new OfferRecordDataView();

   private final OfferRecordProductDataView offerRecordProductDataView = new OfferRecordProductDataView();

   private final Label totalDiscountLabel = new Label("totalDiscount", Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getChartTotalDiscount())));

   private final Label totalLabel = new Label("total", Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getChartTotal())));

   private final Label totalShippingCost = new Label("totalShippingCost", Model.of(NumberFormat.getCurrencyInstance().format(offerRecordDataProvider.getShippingCostTotal())));

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   @SpringBean(name = "OfferDataProvider", required = true)
   private GenericTypeDataProvider<Offer> offerDataProvider;

   public CartViewPanel(final String id, final IModel<Shopper> model) {
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

      super.onInitialize();
   }
}
