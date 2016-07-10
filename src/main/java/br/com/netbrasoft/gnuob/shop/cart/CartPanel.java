package br.com.netbrasoft.gnuob.shop.cart;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.netbrasoft.gnuob.api.Offer;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.security.ShopRoles;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;
import br.com.netbrasoft.gnuob.shop.shopper.ShopperDataProvider;

/**
 * Panel for viewing, selecting and editing {@link Offer} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CartPanel extends Panel {

  private static final String CART_EMPTY_OR_EDIT_PANEL_ID = "cartEmptyOrEditPanel";

  private static final long serialVersionUID = 2034566325989232879L;

  private final CartEmptyOrEditPanel cartEmptyOrEditPanel;

  @SpringBean(name = ShopperDataProvider.SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  public CartPanel(final String id, final IModel<Offer> model) {
    super(id, model);
    cartEmptyOrEditPanel = new CartEmptyOrEditPanel(CART_EMPTY_OR_EDIT_PANEL_ID, (IModel<Offer>) CartPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    cartEmptyOrEditPanel.setDefaultModelObject(shopperDataProvider.find(new Shopper()).getCart());
    if (!shopperDataProvider.find(new Shopper()).getCart().getRecords().isEmpty()) {
      cartEmptyOrEditPanel.add(cartEmptyOrEditPanel.new CartEditFragment());
      add(cartEmptyOrEditPanel.setOutputMarkupId(true));
    } else {
      cartEmptyOrEditPanel.add(cartEmptyOrEditPanel.new CartEmptyFragment());
      add(cartEmptyOrEditPanel.setOutputMarkupId(true));
    }
    super.onInitialize();
  }
}
