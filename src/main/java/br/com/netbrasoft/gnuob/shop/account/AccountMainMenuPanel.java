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

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNT_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CHECKOUT_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.HOME_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.MAIN_MENU_TABBED_PANEL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.NAV_NAV_PILLS_NAV_JUSTIFIED_CSS_CLASS;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.WISH_LIST_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.security.ShopRoles.GUEST;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.wicket.model.Model.of;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.com.netbrasoft.gnuob.shop.page.tab.CheckoutTab;
import br.com.netbrasoft.gnuob.shop.page.tab.HomeTab;
import br.com.netbrasoft.gnuob.shop.page.tab.WishListTab;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

@AuthorizeAction(action = Action.RENDER, roles = {GUEST})
public class AccountMainMenuPanel extends Panel {

  @AuthorizeAction(action = Action.ENABLE, roles = {GUEST})
  class MainMenuTabbedPanel extends BootstrapTabbedPanel<ITab> {

    private static final long serialVersionUID = 6838221105862530322L;

    public MainMenuTabbedPanel(final String id, final List<ITab> tabs, final IModel<Integer> model) {
      super(id, tabs);
    }

    @Override
    public String getTabContainerCssClass() {
      return NAV_NAV_PILLS_NAV_JUSTIFIED_CSS_CLASS;
    }
  }

  private static final long serialVersionUID = 4037036072135523233L;
  private static final int SELECTED_TAB = 1;

  public AccountMainMenuPanel(final String id) {
    super(id);
  }

  @Override
  protected void onInitialize() {
    add(getMainMenuTabbedPanelComponent());
    super.onInitialize();
  }

  private Component getMainMenuTabbedPanelComponent() {
    final MainMenuTabbedPanel mainMenuTabbedPanel = getMainMenuTabbedPanel();
    mainMenuTabbedPanel.getTabs().addAll(getTabs());
    mainMenuTabbedPanel.setSelectedTab(SELECTED_TAB);
    return mainMenuTabbedPanel.setOutputMarkupId(true);
  }

  private MainMenuTabbedPanel getMainMenuTabbedPanel() {
    return new MainMenuTabbedPanel(MAIN_MENU_TABBED_PANEL_ID, new ArrayList<ITab>(), null);
  }

  private List<AbstractTab> getTabs() {
    return newArrayList(getHomeTab(), getAccountTab(), getWishListTab(), getCheckoutTab());
  }

  private HomeTab getHomeTab() {
    return new HomeTab(of(AccountMainMenuPanel.this.getString(HOME_MESSAGE_KEY)));
  }

  private AccountTab getAccountTab() {
    return new AccountTab(of(AccountMainMenuPanel.this.getString(ACCOUNT_MESSAGE_KEY)));
  }

  private WishListTab getWishListTab() {
    return new WishListTab(of(AccountMainMenuPanel.this.getString(WISH_LIST_MESSAGE_KEY)));
  }

  private CheckoutTab getCheckoutTab() {
    return new CheckoutTab(of(AccountMainMenuPanel.this.getString(CHECKOUT_MESSAGE_KEY)));
  }
}
