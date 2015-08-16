package com.netbrasoft.gnuob.shop.wishlist;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class WishListViewPanel extends Panel {

   class OfferDataview extends DataView<Offer> {

      private static final long serialVersionUID = -708080269042062417L;

      protected OfferDataview() {
         super("offerDataview", offerDataProvider);
      }

      @Override
      protected void populateItem(Item<Offer> item) {
         item.setModel(new CompoundPropertyModel<Offer>(item.getModelObject()));
         item.add(new Label("offerId"));
         item.add(new WishListOfferViewPanel("offerViewPanel", item.getModel()));
      }
   }

   class WishtListViewFragment extends Fragment {

      private static final long serialVersionUID = -6168511667223170398L;

      public WishtListViewFragment() {
         super("wishListOffersViewFragement", "wishListViewFragement", WishListViewPanel.this, WishListViewPanel.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
         add(offerDataviewContainer.setOutputMarkupId(true));
         super.onInitialize();
      }
   }

   private static final long serialVersionUID = -4406441947235524118L;

   private final WebMarkupContainer offerDataviewContainer = new WebMarkupContainer("offerDataviewContainer") {

      private static final long serialVersionUID = 5642254953273644428L;

      @Override
      protected void onInitialize() {
         add(offerDataview.setOutputMarkupId(true));
         super.onInitialize();
      }
   };

   private final OfferDataview offerDataview = new OfferDataview();

   @SpringBean(name = "ShopperDataProvider", required = true)
   private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   @SpringBean(name = "OfferDataProvider", required = true)
   private GenericTypeDataProvider<Offer> offerDataProvider;

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
      offerDataProvider.getType().setContract(shopperDataProvider.find(new Shopper()).getContract());

      super.onInitialize();
   }
}
