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

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CART_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CONFIRMATION_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.HOME_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.MAIN_MENU_TABBED_PANEL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.NAV_NAV_PILLS_NAV_JUSTIFIED_CSS_CLASS;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SPECIFICATION_MESSAGE_KEY;
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

import br.com.netbrasoft.gnuob.shop.page.tab.ConfirmationTab;
import br.com.netbrasoft.gnuob.shop.page.tab.HomeTab;
import br.com.netbrasoft.gnuob.shop.page.tab.SpecificationTab;
import br.com.netbrasoft.gnuob.shop.security.ShopRoles;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CartMainMenuPanel extends Panel {


  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class MainMenuTabbedPanel extends BootstrapTabbedPanel<ITab> {

    private static final long serialVersionUID = 6838221105862530322L;

    public MainMenuTabbedPanel(final String id, final List<ITab> tabs, final IModel<Integer> model) {
      super(id, tabs, model);
    }

    @Override
    public String getTabContainerCssClass() {
      return NAV_NAV_PILLS_NAV_JUSTIFIED_CSS_CLASS;
    }
  }

  private static final long serialVersionUID = -4776222984181317489L;
  private static final int SELECTED_TAB = 1;


  public CartMainMenuPanel(final String id) {
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

  private ArrayList<AbstractTab> getTabs() {
    return newArrayList(getHomeTab(), getCartTab(), getSpecificationTab(), getConfirmationTab());
  }

  private HomeTab getHomeTab() {
    return new HomeTab(of(CartMainMenuPanel.this.getString(HOME_MESSAGE_KEY)));
  }

  private CartTab getCartTab() {
    return new CartTab(of(CartMainMenuPanel.this.getString(CART_MESSAGE_KEY)));
  }

  private SpecificationTab getSpecificationTab() {
    return new SpecificationTab(of(CartMainMenuPanel.this.getString(SPECIFICATION_MESSAGE_KEY)));
  }

  private ConfirmationTab getConfirmationTab() {
    return new ConfirmationTab(of(CartMainMenuPanel.this.getString(CONFIRMATION_MESSAGE_KEY)));
  }
}
