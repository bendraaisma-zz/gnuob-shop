package br.com.netbrasoft.gnuob.shop.specification;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants;
import br.com.netbrasoft.gnuob.shop.page.tab.CartTab;
import br.com.netbrasoft.gnuob.shop.page.tab.ConfirmationTab;
import br.com.netbrasoft.gnuob.shop.page.tab.HomeTab;
import br.com.netbrasoft.gnuob.shop.security.ShopRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class SpecificationMainMenuPanel extends Panel {

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

  private static final int SELECTED_TAB = 2;

  private static final long serialVersionUID = -4776222984181317489L;

  private final MainMenuTabbedPanel mainMenuTabbedPanel;

  public SpecificationMainMenuPanel(final String id) {
    super(id);
    mainMenuTabbedPanel = new MainMenuTabbedPanel(MAIN_MENU_TABBED_PANEL_ID, new ArrayList<ITab>(), null);
  }

  @Override
  protected void onInitialize() {
    final HomeTab homeTab = new HomeTab(Model.of(SpecificationMainMenuPanel.this.getString(NetbrasoftShopConstants.HOME_MESSAGE_KEY)));
    final CartTab cartTab = new CartTab(Model.of(SpecificationMainMenuPanel.this.getString(NetbrasoftShopConstants.CART_MESSAGE_KEY)));
    final SpecificationTab specificationTab = new SpecificationTab(Model.of(SpecificationMainMenuPanel.this.getString(NetbrasoftShopConstants.SPECIFICATION_MESSAGE_KEY)));
    final ConfirmationTab confirmationTab = new ConfirmationTab(Model.of(SpecificationMainMenuPanel.this.getString(NetbrasoftShopConstants.CONFIRMATION_MESSAGE_KEY)));

    mainMenuTabbedPanel.getTabs().add(homeTab);
    mainMenuTabbedPanel.getTabs().add(cartTab);
    mainMenuTabbedPanel.getTabs().add(specificationTab);
    mainMenuTabbedPanel.getTabs().add(confirmationTab);
    mainMenuTabbedPanel.setSelectedTab(SELECTED_TAB);
    add(mainMenuTabbedPanel.setOutputMarkupId(true));

    super.onInitialize();
  }
}
