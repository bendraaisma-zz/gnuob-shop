/*
 * Copyright 2016 Netbrasoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package br.com.netbrasoft.gnuob.shop.cart;

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CART_EMPTY_OR_EDIT_PANEL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SHOPPER_DATA_PROVIDER_NAME;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.UNCHECKED;
import static br.com.netbrasoft.gnuob.shop.security.ShopRoles.GUEST;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.netbrasoft.gnuob.api.Offer;
import br.com.netbrasoft.gnuob.shop.cart.CartEmptyOrEditPanel.CartEditFragment;
import br.com.netbrasoft.gnuob.shop.cart.CartEmptyOrEditPanel.CartEmptyFragment;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;

@SuppressWarnings(UNCHECKED)
@AuthorizeAction(action = Action.RENDER, roles = {GUEST})
public class CartPanel extends Panel {

  private static final long serialVersionUID = 2034566325989232879L;


  @SpringBean(name = SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  public CartPanel(final String id, final IModel<Offer> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    add(getCartEmptyOrEditPanelComponent());
    super.onInitialize();
  }

  private Component getCartEmptyOrEditPanelComponent() {
    final CartEmptyOrEditPanel cartEmptyOrEditPanel = getCartEmptyOrEditPanel();
    return cartEmptyOrEditPanel
        .add(hasCartRecords() ? getCartEditFragement(cartEmptyOrEditPanel) : getCartEmptyFragment(cartEmptyOrEditPanel))
        .setDefaultModelObject(getContractModelObject()).setOutputMarkupId(true);
  }

  private CartEmptyOrEditPanel getCartEmptyOrEditPanel() {
    return new CartEmptyOrEditPanel(CART_EMPTY_OR_EDIT_PANEL_ID, (IModel<Offer>) CartPanel.this.getDefaultModel());
  }

  private boolean hasCartRecords() {
    return !getContractModelObject().getRecords().isEmpty();
  }

  private Offer getContractModelObject() {
    return shopperDataProvider.find(Shopper.getInstance()).getCart();
  }

  private CartEditFragment getCartEditFragement(CartEmptyOrEditPanel cartEmptyOrEditPanel) {
    return cartEmptyOrEditPanel.new CartEditFragment();
  }

  private CartEmptyFragment getCartEmptyFragment(CartEmptyOrEditPanel cartEmptyOrEditPanel) {
    return cartEmptyOrEditPanel.new CartEmptyFragment();
  }
}
