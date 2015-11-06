package com.netbrasoft.gnuob.shop.wishlist;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.convert.IConverter;

import com.google.common.net.MediaType;
import com.netbrasoft.gnuob.api.Content;
import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.OfferRecord;
import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.Product;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.api.generic.converter.CurrencyConverter;
import com.netbrasoft.gnuob.api.product.ProductDataProvider;
import com.netbrasoft.gnuob.shop.NetbrasoftShopMessageKeyConstants;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.product.ProductCarousel;
import com.netbrasoft.gnuob.shop.security.ShopRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.CarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.ICarouselImage;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverConfig;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig.Placement;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconBehavior;

/**
 * Panel for viewing, selecting and editing {@link OfferRecord} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class WishListViewOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class WishListOfferRecordEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
    class WishListOfferRecordEditContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
      class RatingLoop extends Loop {

        private static final long serialVersionUID = 5495135025461796293L;

        private final IModel<Integer> minModel;

        public RatingLoop(final String id, final IModel<Integer> minModel, final IModel<Integer> maxModel) {
          super(id, maxModel);
          this.minModel = minModel;
        }

        @Override
        protected void populateItem(final LoopItem item) {
          final IconBehavior iconBehavior = new IconBehavior(item.getIndex() < minModel.getObject() ? GlyphIconType.star : GlyphIconType.starempty);
          item.add(iconBehavior);
        }
      }

      private static final String WISH_LIST_OFFER_RECORD_EDIT_FORM_COMPONENT_ID = "wishListOfferRecordEditForm";

      private static final String PRODUCT_CAROUSEL_ID = "productCarousel";

      private static final String PRODUCT_STOCK_QUANTITY_ID = "product.stock.quantity";

      private static final String AMOUNT_ID = "amount";

      private static final String PRODUCT_AMOUNT_ID = "product.amount";

      private static final String RATING_ID = "rating";

      private static final String NAME_ID = "name";

      private static final long serialVersionUID = 6333331915599680021L;

      private static final int FIVE_STARS_RATING = 5;

      private BootstrapForm<OfferRecord> wishListOfferRecordEditForm;

      private final Label nameLabel;

      private final Label productStockQuantityLabel;

      private final Label amountLabel;

      private final Label productAmountLabel;

      private final RatingLoop ratingLoop;

      private final ProductCarousel productCarousel;

      public WishListOfferRecordEditContainer(final String id, final IModel<Order> model) {
        super(id, model);
        wishListOfferRecordEditForm = new BootstrapForm<OfferRecord>(WISH_LIST_OFFER_RECORD_EDIT_FORM_COMPONENT_ID,
            new CompoundPropertyModel<OfferRecord>(Model.of(WishListViewOrEditPanel.this.selectedModel.getObject())));
        nameLabel = new Label(NAME_ID);
        productStockQuantityLabel = new Label(PRODUCT_STOCK_QUANTITY_ID);
        amountLabel = new Label(AMOUNT_ID) {

          private static final long serialVersionUID = -4143367505737220689L;

          @Override
          public <C> IConverter<C> getConverter(final Class<C> type) {
            return (IConverter<C>) new CurrencyConverter();
          }
        };
        productAmountLabel = new Label(PRODUCT_AMOUNT_ID) {

          private static final long serialVersionUID = -4143367505737220689L;

          @Override
          public <C> IConverter<C> getConverter(final Class<C> type) {
            return (IConverter<C>) new CurrencyConverter();
          }
        };
        productCarousel = new ProductCarousel(PRODUCT_CAROUSEL_ID,
            Model.ofList(convertContentsToCarouselImages(WishListViewOrEditPanel.this.selectedModel.getObject().getProduct().getContents())));
        ratingLoop = new RatingLoop(RATING_ID, Model.of(WishListViewOrEditPanel.this.selectedModel.getObject().getProduct().getRating()), Model.of(FIVE_STARS_RATING));

      }

      private List<ICarouselImage> convertContentsToCarouselImages(final List<Content> contents) {
        final List<ICarouselImage> carouselImages = new ArrayList<ICarouselImage>();
        for (final Content content : contents) {
          if (MediaType.HTML_UTF_8.is(MediaType.parse(content.getFormat()))) {
            carouselImages.add(new CarouselImage(new String(content.getContent())));
          }
        }
        return carouselImages;
      }

      @Override
      protected void onInitialize() {
        final PopoverConfig popoverConfig = new PopoverConfig();
        final PopoverBehavior popoverBehavior = new PopoverBehavior(Model.of(WishListViewOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.DESCRIPTION_MESSAGE_KEY)),
            Model.of(WishListViewOrEditPanel.this.selectedModel.getObject().getProduct().getDescription()), popoverConfig);
        popoverConfig.withHoverTrigger();
        popoverConfig.withPlacement(Placement.left);
        productCarousel.add(popoverBehavior);
        wishListOfferRecordEditForm.add(productCarousel.setOutputMarkupId(true));
        wishListOfferRecordEditForm.add(nameLabel.setOutputMarkupId(true));
        wishListOfferRecordEditForm.add(productStockQuantityLabel.setOutputMarkupId(true));
        wishListOfferRecordEditForm.add(amountLabel.setOutputMarkupId(true));
        wishListOfferRecordEditForm.add(productAmountLabel.setOutputMarkupId(true));
        wishListOfferRecordEditForm.add(ratingLoop.setOutputMarkupId(true));
        wishListOfferRecordEditForm.add(new FormBehavior(FormType.Horizontal));
        add(wishListOfferRecordEditForm.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String WISH_LIST_OFFER_RECORD_EDIT_CONTAINER_ID = "wishListOfferRecordEditContainer";

    private static final String WISH_LIST_OFFER_RECORD_EDIT_FRAGMENT_MARKUP_ID = "wishListOfferRecordEditFragment";

    private static final String WISH_LIST_OFFER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID = "wishListOfferRecordViewOrEditFragment";

    private static final long serialVersionUID = 2090636400657143172L;

    private final WishListOfferRecordEditContainer wishListOfferRecordEditContainer;

    public WishListOfferRecordEditFragment() {
      super(WISH_LIST_OFFER_RECORD_VIEW_OR_EDIT_FRAGMENT_ID, WISH_LIST_OFFER_RECORD_EDIT_FRAGMENT_MARKUP_ID, WishListViewOrEditPanel.this,
          WishListViewOrEditPanel.this.getDefaultModel());
      wishListOfferRecordEditContainer =
          new WishListOfferRecordEditContainer(WISH_LIST_OFFER_RECORD_EDIT_CONTAINER_ID, (IModel<Order>) WishListOfferRecordEditFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(wishListOfferRecordEditContainer.setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final long serialVersionUID = 559425501078519811L;

  @SpringBean(name = ProductDataProvider.PRODUCT_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeDataProvider<Product> productDataProvider;

  private IModel<OfferRecord> selectedModel;

  public WishListViewOrEditPanel(final String id, final IModel<Offer> model) {
    super(id, model);
    final OfferRecord offerRecord = new OfferRecord();
    final Product product = new Product();
    offerRecord.setProduct(product);
    product.setAmount(BigDecimal.ZERO);
    product.setDiscount(BigDecimal.ZERO);
    product.setRating(Integer.valueOf(0));
    selectedModel = Model.of(offerRecord);
  }

  @Override
  protected void onInitialize() {
    productDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    productDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    productDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    productDataProvider.setType(new Product());
    productDataProvider.getType().setActive(true);
    super.onInitialize();
  }

  public void setSelectedModel(final IModel<OfferRecord> selectedModel) {
    this.selectedModel = selectedModel;
    if (this.selectedModel.getObject().getProduct() == null) {
      productDataProvider.getType().setNumber(selectedModel.getObject().getProductNumber());
      if (productDataProvider.size() > 0) {
        this.selectedModel.getObject().setProduct(productDataProvider.iterator(0, 1).next());
      } else {
        this.selectedModel.getObject().setProduct(new Product());
      }
    }
  }
}
