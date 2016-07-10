package br.com.netbrasoft.gnuob.shop.checkout;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants;
import br.com.netbrasoft.gnuob.shop.page.tab.AccountTab;
import br.com.netbrasoft.gnuob.shop.page.tab.HomeTab;
import br.com.netbrasoft.gnuob.shop.page.tab.WishListTab;
import br.com.netbrasoft.gnuob.shop.security.ShopRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CheckoutMainMenuPanel extends Panel {

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

  private static final int SELECTED_TAB = 3;

  private static final long serialVersionUID = 4037036072135523233L;

  private final MainMenuTabbedPanel mainMenuTabbedPanel;

  public CheckoutMainMenuPanel(final String id) {
    super(id);
    mainMenuTabbedPanel = new MainMenuTabbedPanel(MAIN_MENU_TABBED_PANEL_ID, new ArrayList<ITab>(), null);
  }

  @Override
  protected void onInitialize() {
    final HomeTab homeTab = new HomeTab(Model.of(CheckoutMainMenuPanel.this.getString(NetbrasoftShopConstants.HOME_MESSAGE_KEY)));
    final AccountTab accountTab = new AccountTab(Model.of(CheckoutMainMenuPanel.this.getString(NetbrasoftShopConstants.ACCOUNT_MESSAGE_KEY)));
    final WishListTab wishListTab = new WishListTab(Model.of(CheckoutMainMenuPanel.this.getString(NetbrasoftShopConstants.WISH_LIST_MESSAGE_KEY)));
    final CheckoutTab checkoutTab = new CheckoutTab(Model.of(CheckoutMainMenuPanel.this.getString(NetbrasoftShopConstants.CHECKOUT_MESSAGE_KEY)));
    mainMenuTabbedPanel.getTabs().add(homeTab);
    mainMenuTabbedPanel.getTabs().add(accountTab);
    mainMenuTabbedPanel.getTabs().add(wishListTab);
    mainMenuTabbedPanel.getTabs().add(checkoutTab);
    mainMenuTabbedPanel.setSelectedTab(SELECTED_TAB);
    add(mainMenuTabbedPanel.setOutputMarkupId(true));
    super.onInitialize();
  }
}
