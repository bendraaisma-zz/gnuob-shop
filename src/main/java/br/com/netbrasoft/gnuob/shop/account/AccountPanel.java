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

package br.com.netbrasoft.gnuob.shop.account;

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNT_LOGIN_OR_EDIT_PANEL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SHOPPER_DATA_PROVIDER_NAME;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.UNCHECKED;
import static br.com.netbrasoft.gnuob.shop.security.ShopRoles.GUEST;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.netbrasoft.gnuob.api.Contract;
import br.com.netbrasoft.gnuob.shop.account.AccountLoginOrEditPanel.AccountEditFragment;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;

@SuppressWarnings(UNCHECKED)
@AuthorizeAction(action = Action.RENDER, roles = {GUEST})
public class AccountPanel extends Panel {

  private static final long serialVersionUID = 2034566325989232879L;

  @SpringBean(name = SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  public AccountPanel(final String id, final IModel<Contract> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    add(isShopperLoggedIn() ? getAccountEditComponent() : getAccountLoginComponent());
    super.onInitialize();
  }

  private boolean isShopperLoggedIn() {
    return shopperDataProvider.find(Shopper.getInstance()).isLoggedIn();
  }

  private Component getAccountEditComponent() {
    return getAccountEditFragmentComponent(
        (AccountLoginOrEditPanel) getAccountLoginOrEditPanelComponent(getAccountLoginOrEditPanel()));
  }

  private Component getAccountEditFragmentComponent(final AccountLoginOrEditPanel accountLoginOrEditPanel) {
    return accountLoginOrEditPanel.add(getAccountEditFragment(accountLoginOrEditPanel)).setOutputMarkupId(true);
  }

  private AccountEditFragment getAccountEditFragment(final AccountLoginOrEditPanel accountLoginOrEditPanel) {
    return accountLoginOrEditPanel.new AccountEditFragment();
  }

  private Component getAccountLoginOrEditPanelComponent(final AccountLoginOrEditPanel accountLoginOrEditPanel) {
    return accountLoginOrEditPanel.setDefaultModelObject(getContractModel());
  }

  private Contract getContractModel() {
    return shopperDataProvider.find(Shopper.getInstance()).getContract();
  }

  private AccountLoginOrEditPanel getAccountLoginOrEditPanel() {
    return new AccountLoginOrEditPanel(ACCOUNT_LOGIN_OR_EDIT_PANEL_ID,
        (IModel<Contract>) AccountPanel.this.getDefaultModel());
  }

  private Component getAccountLoginComponent() {
    return getAccountLoginFragmentComponent(
        (AccountLoginOrEditPanel) getAccountLoginOrEditPanelComponent(getAccountLoginOrEditPanel()));
  }

  private Component getAccountLoginFragmentComponent(final AccountLoginOrEditPanel accountLoginOrEditPanel) {
    return accountLoginOrEditPanel.add(accountLoginOrEditPanel.new AccountLoginFragment()).setOutputMarkupId(true);
  }
}
