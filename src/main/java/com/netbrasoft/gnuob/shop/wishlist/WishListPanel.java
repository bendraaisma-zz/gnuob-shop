package com.netbrasoft.gnuob.shop.wishlist;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.api.offer.OfferDataProvider;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;
import com.netbrasoft.gnuob.shop.shopper.ShopperDataProvider;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class WishListPanel extends Panel {

  private static final String WISH_LIST_EMPTY_OR_EDIT_PANEL_ID = "wishListEmptyOrEditPanel";

  private static final long serialVersionUID = 2034566325989232879L;

  private final WishListEmptyOrEditPanel wishListEmptyOrEditPanel;

  @SpringBean(name = ShopperDataProvider.SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  @SpringBean(name = OfferDataProvider.OFFER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeDataProvider<Offer> offerDataProvider;

  public WishListPanel(final String id, final IModel<Offer> model) {
    super(id, model);
    wishListEmptyOrEditPanel = new WishListEmptyOrEditPanel(WISH_LIST_EMPTY_OR_EDIT_PANEL_ID, (IModel<Offer>) WishListPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {

    offerDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    offerDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    offerDataProvider.setType(new Offer());
    offerDataProvider.getType().setActive(true);
    offerDataProvider.setOrderBy(OrderBy.CREATION_Z_A);
    offerDataProvider.getType().setContract(shopperDataProvider.find(new Shopper()).getContract());
    if (offerDataProvider.size() > 0) {
      add(wishListEmptyOrEditPanel.add(wishListEmptyOrEditPanel.new WishtListEditFragment()).setOutputMarkupId(true));
    } else {
      add(wishListEmptyOrEditPanel.add(wishListEmptyOrEditPanel.new WishtListEmptyFragment()).setOutputMarkupId(true));
    }
    super.onInitialize();
  }
}
