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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants;
import br.com.netbrasoft.gnuob.shop.page.tab.CheckoutTab;
import br.com.netbrasoft.gnuob.shop.page.tab.HomeTab;
import br.com.netbrasoft.gnuob.shop.page.tab.WishListTab;
import br.com.netbrasoft.gnuob.shop.security.ShopRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class AccountMainMenuPanel extends Panel {

  @AuthorizeAction(action = Action.ENABLE, roles = {ShopRoles.GUEST})
  class MainMenuTabbedPanel extends BootstrapTabbedPanel<ITab> {

    private static final String NAV_NAV_PILLS_NAV_JUSTIFIED_CSS_CLASS = "nav nav-pills nav-justified";

    private static final long serialVersionUID = 6838221105862530322L;

    public MainMenuTabbedPanel(final String id, final List<ITab> tabs, final IModel<Integer> model) {
      super(id, tabs);
    }

    @Override
    public String getTabContainerCssClass() {
      return NAV_NAV_PILLS_NAV_JUSTIFIED_CSS_CLASS;
    }
  }

  private static final int SELECTED_TAB = 1;

  private static final String MAIN_MENU_TABBED_PANEL_ID = "mainMenuTabbedPanel";

  private static final long serialVersionUID = 4037036072135523233L;

  private final MainMenuTabbedPanel mainMenuTabbedPanel;

  public AccountMainMenuPanel(final String id) {
    super(id);
    mainMenuTabbedPanel = new MainMenuTabbedPanel(MAIN_MENU_TABBED_PANEL_ID, new ArrayList<ITab>(), null);
  }

  @Override
  protected void onInitialize() {
    final HomeTab homeTab =
        new HomeTab(Model.of(AccountMainMenuPanel.this.getString(NetbrasoftShopConstants.HOME_MESSAGE_KEY)));
    final AccountTab accountTab =
        new AccountTab(Model.of(AccountMainMenuPanel.this.getString(NetbrasoftShopConstants.ACCOUNT_MESSAGE_KEY)));
    final WishListTab wishListTab =
        new WishListTab(Model.of(AccountMainMenuPanel.this.getString(NetbrasoftShopConstants.WISH_LIST_MESSAGE_KEY)));
    final CheckoutTab checkoutTab =
        new CheckoutTab(Model.of(AccountMainMenuPanel.this.getString(NetbrasoftShopConstants.CHECKOUT_MESSAGE_KEY)));
    mainMenuTabbedPanel.getTabs().add(homeTab);
    mainMenuTabbedPanel.getTabs().add(accountTab);
    mainMenuTabbedPanel.getTabs().add(wishListTab);
    mainMenuTabbedPanel.getTabs().add(checkoutTab);
    mainMenuTabbedPanel.setSelectedTab(SELECTED_TAB);
    add(mainMenuTabbedPanel.setOutputMarkupId(true));
    super.onInitialize();
  }
}
