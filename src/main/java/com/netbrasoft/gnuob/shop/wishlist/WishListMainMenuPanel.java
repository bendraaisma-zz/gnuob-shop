package com.netbrasoft.gnuob.shop.wishlist;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.shop.NetbrasoftShopMessageKeyConstants;
import com.netbrasoft.gnuob.shop.page.tab.AccountTab;
import com.netbrasoft.gnuob.shop.page.tab.CheckoutTab;
import com.netbrasoft.gnuob.shop.page.tab.HomeTab;
import com.netbrasoft.gnuob.shop.security.ShopRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class WishListMainMenuPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class MainMenuTabbedPanel extends BootstrapTabbedPanel<ITab> {

    private static final String NAV_NAV_PILLS_NAV_JUSTIFIED_CSS_CLASS = "nav nav-pills nav-justified";

    private static final long serialVersionUID = 6838221105862530322L;

    public MainMenuTabbedPanel(final String id, final List<ITab> tabs, final IModel<Integer> model) {
      super(id, tabs, model);
    }

    @Override
    public String getTabContainerCssClass() {
      return NAV_NAV_PILLS_NAV_JUSTIFIED_CSS_CLASS;
    }
  }

  private static final String MAIN_MENU_TABBED_PANEL_ID = "mainMenuTabbedPanel";

  private static final long serialVersionUID = 4037036072135523233L;

  private static final int SELECTED_TAB = 2;

  private final MainMenuTabbedPanel mainMenuTabbedPanel;

  public WishListMainMenuPanel(final String id) {
    super(id);
    mainMenuTabbedPanel = new MainMenuTabbedPanel(MAIN_MENU_TABBED_PANEL_ID, new ArrayList<ITab>(), null);
  }

  @Override
  protected void onInitialize() {
    final HomeTab homeTab = new HomeTab(Model.of(WishListMainMenuPanel.this.getString(NetbrasoftShopMessageKeyConstants.HOME_MESSAGE_KEY)));
    final AccountTab accountTab = new AccountTab(Model.of(WishListMainMenuPanel.this.getString(NetbrasoftShopMessageKeyConstants.ACCOUNT_MESSAGE_KEY)));
    final WishListTab wishListTab = new WishListTab(Model.of(WishListMainMenuPanel.this.getString(NetbrasoftShopMessageKeyConstants.WISH_LIST_MESSAGE_KEY)));
    final CheckoutTab checkoutTab = new CheckoutTab(Model.of(WishListMainMenuPanel.this.getString(NetbrasoftShopMessageKeyConstants.CHECKOUT_MESSAGE_KEY)));

    mainMenuTabbedPanel.getTabs().add(homeTab);
    mainMenuTabbedPanel.getTabs().add(accountTab);
    mainMenuTabbedPanel.getTabs().add(wishListTab);
    mainMenuTabbedPanel.getTabs().add(checkoutTab);
    mainMenuTabbedPanel.setSelectedTab(SELECTED_TAB);
    add(mainMenuTabbedPanel.setOutputMarkupId(true));
    super.onInitialize();
  }
}
