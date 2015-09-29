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
import org.apache.wicket.markup.repeater.data.ListDataProvider;
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

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class EmptyOfferRecordViewFragement extends Fragment {

      private static final long serialVersionUID = 5058607382122871571L;

      public EmptyOfferRecordViewFragement() {
         super("cartOfferProductViewFragement", "emptyOfferRecordViewFragement", CartViewPanel.this, CartViewPanel.this.getDefaultModel());
      }
   }

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class OfferRecordViewFragement extends Fragment {

      @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
      class OfferRecordContainer extends WebMarkupContainer {

         @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
         class OptionDataView extends DataView<Option> {

            private static final long serialVersionUID = -1449287225314761342L;

            protected OptionDataView() {
               super("optionDataview", optionListDataProvider);
            }

            @Override
            protected void populateItem(Item<Option> item) {

               final OfferRecord offerRecord = ((OfferRecord) offerRecordContainer.getDefaultModel().getObject());

               Option optionModel = new Option();

               for(final Option rootOptionOriginal : offerRecord.getOptions()) {
                  if (rootOptionOriginal.getValue().equals(((Option) item.getDefaultModelObject()).getValue())) {
                     optionModel = rootOptionOriginal.getOptions().get(0);
                  }
               }

               final DropDownChoice<Option> optionDropDownChoice = new DropDownChoice<Option>("option", Model.of(optionModel), ((Option) item.getDefaultModelObject()).getOptions(), new ChoiceRenderer<Option>("description", "value")) {

                  private static final long serialVersionUID = -1948250807329668681L;

                  @Override
                  protected boolean isDisabled(Option object, int index, String selected) {
                     return object.isDisabled();
                  }
               };

               item.add(optionDropDownChoice.setOutputMarkupId(true).add(new OnChangeAjaxBehavior() {

                  private static final long serialVersionUID = -8850274264336474334L;

                  @Override
                  protected void onUpdate(AjaxRequestTarget target) {

                     final Option childOptionModel = (Option) optionDropDownChoice.getDefaultModelObject();

                     for(final Option rootOptionOriginal : offerRecord.getOptions()) {
                        if (rootOptionOriginal.getValue().equals(((Option) item.getDefaultModelObject()).getValue())) {
                           rootOptionOriginal.getOptions().get(0).setValue(childOptionModel.getValue());
                           rootOptionOriginal.getOptions().get(0).setDescription(childOptionModel.getDescription());
                           rootOptionOriginal.getOptions().get(0).setDisabled(childOptionModel.isDisabled());
                           break;
                        }
                     }

                     offerRecordContainer.onInitialize();
                     offerRecordDataviewContainer.onInitialize();
                     offerRecordTotalContainer.onInitialize();

                     target.add(offerRecordContainer.setOutputMarkupId(true));
                     target.add(offerRecordDataviewContainer.setOutputMarkupId(true));
                     target.add(offerRecordTotalContainer.setOutputMarkupId(true));
                  }
               }));
            }
         }

         @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
         class OptionDataviewContainer extends WebMarkupContainer {

            private static final long serialVersionUID = -7025936127900626268L;

            public OptionDataviewContainer() {
               super("optionDataviewContainer");
            }

            @Override
            protected void onInitialize() {
               add(optionDataView.setOutputMarkupId(true));
               super.onInitialize();
            }
         }

         class OptionListDataProvider extends ListDataProvider<Option> {

            private static final long serialVersionUID = 5552708924888205452L;

            public OptionListDataProvider(List<Option> options) {
               super(options);
            }

            @Override
            protected List<Option> getData() {
               return super.getData();
            }
         }

         private static final int FIVE_STARS_RATING = 5;

         private static final long serialVersionUID = -1556817303531395170L;

         private final OptionDataviewContainer optionDataviewContainer;

         private final OptionListDataProvider optionListDataProvider;

         private final OptionDataView optionDataView;

         public OfferRecordContainer(IModel<OfferRecord> model) {
            super("offerRecordContainer", model);

            optionListDataProvider = new OptionListDataProvider(model.getObject().getProduct().getOptions());
            optionDataView = new OptionDataView();
            optionDataviewContainer = new OptionDataviewContainer();
         }

         @Override
         protected void onInitialize() {
            removeAll();

            final OfferRecord offerRecord = ((OfferRecord) getDefaultModelObject());
            final List<ICarouselImage> carouselImages = new ArrayList<ICarouselImage>();

            for (final Content content : offerRecord.getProduct().getContents()) {
               if (MediaType.HTML_UTF_8.is(MediaType.parse(content.getFormat()))) {
                  carouselImages.add(new CarouselImage(new String(content.getContent())));
               }
            }

            add(new ProductCarousel("productCarousel", carouselImages).setOutputMarkupId(true).add(new PopoverBehavior(Model.of(getString("descriptionMessage")), Model.of(offerRecord.getProduct().getDescription()), new PopoverConfig().withHoverTrigger().withPlacement(Placement.left))));
            add(new Label("name"));
            add(new Label("product.stock.quantity"));
            add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(offerRecord.getProduct().getAmount().subtract(offerRecord.getProduct().getDiscount())))));
            add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(offerRecord.getProduct().getAmount()))).setOutputMarkupId(true));
            add(new Loop("rating", FIVE_STARS_RATING) {

               private static final long serialVersionUID = -443304621920358169L;

               @Override
               protected void populateItem(LoopItem loopItem) {
                  loopItem.add(new IconBehavior(loopItem.getIndex() < offerRecord.getProduct().getRating() ? GlyphIconType.star : GlyphIconType.starempty));
               }
            });
            final DropDownChoice<Integer> quantityDropDownChoice = new DropDownChoice<Integer>("quantity", Model.of(offerRecord.getQuantity().intValue()), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20));
            add(quantityDropDownChoice.add(new OnChangeAjaxBehavior() {

               private static final long serialVersionUID = -8850274264336474334L;

               @Override
               protected void onUpdate(AjaxRequestTarget target) {

                  final Integer model = (Integer) quantityDropDownChoice.getDefaultModelObject();

                  final Shopper shopper = shopperDataProvider.find((Shopper) CartViewPanel.this.getDefaultModelObject());
                  shopper.getCart().getRecords().get(offerRecordDataviewContainer.offerRecordDataview.selectedItem.getIndex()).setQuantity(BigInteger.valueOf(model));
                  offerRecordContainer.setDefaultModelObject(shopper.getCart().getRecords().get(offerRecordDataviewContainer.offerRecordDataview.selectedItem.getIndex()));
                  shopperDataProvider.merge(shopper);

                  offerRecordContainer.onInitialize();
                  offerRecordDataviewContainer.onInitialize();
                  offerRecordTotalContainer.onInitialize();

                  target.add(offerRecordContainer.setOutputMarkupId(true));
                  target.add(offerRecordDataviewContainer.setOutputMarkupId(true));
                  target.add(offerRecordTotalContainer.setOutputMarkupId(true));
               }
            }));
            add(optionDataviewContainer.setOutputMarkupId(true));

            super.onInitialize();
         }
      }

      @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
      class OfferRecordDataviewContainer extends WebMarkupContainer {

         @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
         class OfferRecordDataView extends DataView<OfferRecord> {

            private static final long serialVersionUID = -8885578770770605991L;

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
               final BigDecimal amount = item.getModelObject().getProduct().getAmount();
               final BigDecimal tax = item.getModelObject().getProduct().getTax();
               final BigDecimal discount = item.getModelObject().getProduct().getDiscount();
               final BigDecimal quantity = BigDecimal.valueOf(item.getModelObject().getQuantity().intValue());

               for(final Option option : item.getModelObject().getOptions()) {
                  stringBuffer.append(option.getValue()).append(": ").append(option.getOptions().iterator().next().getValue()).append(" ");
               }

               item.setModel(new CompoundPropertyModel<OfferRecord>(item.getModelObject()));
               item.add(new RemoveAjaxButton(item.getModel()));
               item.add(new Label("name").setOutputMarkupId(true));
               item.add(new Label("options", Model.of(stringBuffer.toString())).setOutputMarkupId(true));
               item.add(new Label("quantity").setOutputMarkupId(true));
               item.add(new Label("amountWithDiscount", Model.of(NumberFormat.getCurrencyInstance().format(amount.add(tax).subtract(discount).multiply(quantity)))).setOutputMarkupId(true));
               item.add(new Label("amount", Model.of(NumberFormat.getCurrencyInstance().format(amount.add(tax).multiply(quantity)))).setOutputMarkupId(true));
               item.add(new AjaxEventBehavior("click") {

                  private static final long serialVersionUID = 1L;

                  @Override
                  public void onEvent(AjaxRequestTarget target) {
                     selectedItem = item;

                     offerRecordContainer.setDefaultModel(item.getDefaultModel());
                     offerRecordContainer.onInitialize();
                     offerRecordDataviewContainer.onInitialize();
                     offerRecordTotalContainer.onInitialize();

                     target.add(offerRecordContainer.setOutputMarkupId(true));
                     target.add(offerRecordDataviewContainer.setOutputMarkupId(true));
                     target.add(offerRecordTotalContainer.setOutputMarkupId(true));
                  }
               });
            }
         }

         class OfferRecordListDataProvider extends ListDataProvider<OfferRecord> {

            private static final long serialVersionUID = 3755475588885853693L;

            @Override
            public Iterator<OfferRecord> iterator(long first, long count) {
               final List<OfferRecord> offerRecordIteratorList = new ArrayList<OfferRecord>();

               for (int index = (int) first; index < first + count; index++) {
                  offerRecordIteratorList.add(shopperDataProvider.find((Shopper) CartViewPanel.this.getDefaultModelObject()).getCart().getRecords().get(index));
               }

               return offerRecordIteratorList.iterator();
            }

            @Override
            public long size() {
               return shopperDataProvider.find((Shopper) CartViewPanel.this.getDefaultModelObject()).getCart().getRecords().size();
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
               final Shopper shopper = shopperDataProvider.find((Shopper) CartViewPanel.this.getDefaultModelObject());
               shopper.getCart().getRecords().remove(getModelObject());

               if (!shopper.getCart().getRecords().isEmpty()) {
                  offerRecordContainer.setDefaultModel(new CompoundPropertyModel<OfferRecord>(Model.of(shopper.getCart().getRecords().get(0))));
                  offerRecordDataview.selectedItem = null;
                  shopperDataProvider.merge(shopper);

                  offerRecordContainer.onInitialize();
                  offerRecordDataviewContainer.onInitialize();
                  offerRecordTotalContainer.onInitialize();

                  target.add(offerRecordContainer.setOutputMarkupId(true));
                  target.add(offerRecordDataviewContainer.setOutputMarkupId(true));
                  target.add(offerRecordTotalContainer.setOutputMarkupId(true));
               } else {
                  CartViewPanel.this.removeAll();
                  CartViewPanel.this.add(new EmptyOfferRecordViewFragement().setOutputMarkupId(true));
                  target.add(CartViewPanel.this);
               }
            }
         }

         private static final long serialVersionUID = 1843462579421164639L;

         private final OfferRecordListDataProvider offerRecordListDataProvider;

         private final OfferRecordDataView offerRecordDataview;

         public OfferRecordDataviewContainer() {
            super("offerRecordDataviewContainer");

            offerRecordListDataProvider = new OfferRecordListDataProvider();
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
               Offer offer = shopper.getCart();

               offer.setActive(true);
               offer.setInsuranceTotal(BigDecimal.ZERO);
               offer.setHandlingTotal(BigDecimal.ZERO);
               offer.setShippingDiscount(BigDecimal.ZERO);

               offer = offerDataProvider.findById(offerDataProvider.persist(offer));
               offer.setContract(shopper.getContract());
               offer = offerDataProvider.findById(offerDataProvider.merge(offer));

               shopper.setContract(offer.getContract());
            }

            private void saveOfferAndGotoWishList() {
               saveOffer(shopperDataProvider.find((Shopper) CartViewPanel.this.getDefaultModelObject()));
               throw new RedirectToUrlException("wishlist.html");
            }
         }

         private static final long serialVersionUID = 6545779558348445960L;

         public OfferRecordTotalContainer() {
            super("offerRecordTotalContainer");
         }

         @Override
         protected void onInitialize() {
            final BigDecimal totalDiscount = shopperDataProvider.find((Shopper) CartViewPanel.this.getDefaultModelObject()).getCartTotalDiscount();
            final BigDecimal total = shopperDataProvider.find((Shopper) CartViewPanel.this.getDefaultModelObject()).getCartTotal();
            final BigDecimal totalShippingCost = shopperDataProvider.find((Shopper) CartViewPanel.this.getDefaultModelObject()).getShippingCostTotal();

            removeAll();

            add(new Label("totalDiscount", Model.of(NumberFormat.getCurrencyInstance().format(totalDiscount))).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
            add(new Label("total", Model.of(NumberFormat.getCurrencyInstance().format(total))).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
            add(new Label("totalShippingCost", Model.of(NumberFormat.getCurrencyInstance().format(totalShippingCost))).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
            add(new SaveAjaxButton().setVisible(shopperDataProvider.find((Shopper) CartViewPanel.this.getDefaultModelObject()).isLoggedIn()).setOutputMarkupId(true));

            super.onInitialize();
         }
      }

      private static final long serialVersionUID = -5518685687286043845L;

      private final OfferRecordContainer offerRecordContainer;

      private final OfferRecordDataviewContainer offerRecordDataviewContainer;

      private final OfferRecordTotalContainer offerRecordTotalContainer;

      public OfferRecordViewFragement() {
         super("cartOfferProductViewFragement", "offerRecordViewFragement", CartViewPanel.this, CartViewPanel.this.getDefaultModel());

         offerRecordContainer = new OfferRecordContainer(new CompoundPropertyModel<OfferRecord>(Model.of(shopperDataProvider.find((Shopper) CartViewPanel.this.getDefaultModelObject()).getCart().getRecords().get(0))));
         offerRecordDataviewContainer = new OfferRecordDataviewContainer();
         offerRecordTotalContainer = new OfferRecordTotalContainer();
      }

      @Override
      protected void onInitialize() {
         add(offerRecordContainer.setOutputMarkupId(true));
         add(offerRecordDataviewContainer.setOutputMarkupId(true));
         add(offerRecordTotalContainer.setOutputMarkupId(true));
         super.onInitialize();
      }
   }

   private static final long serialVersionUID = 6183635879900747064L;

   @SpringBean(name = "ShopperDataProvider", required = true)
   private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

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
