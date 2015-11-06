package com.netbrasoft.gnuob.shop.category;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.category.CategoryDataProvider;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.NetbrasoftShopMessageKeyConstants;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.page.tab.ContactTab;
import com.netbrasoft.gnuob.shop.security.ShopRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CategoryMainMenuPanel extends Panel {

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

  private static final long serialVersionUID = 6083651059402628915L;

  private final MainMenuTabbedPanel mainMenuTabbedPanel;

  @SpringBean(name = CategoryDataProvider.CATEGORY_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeDataProvider<Category> categoryDataProvider;

  public CategoryMainMenuPanel(final String id, final IModel<Category> model) {
    super(id, model);
    mainMenuTabbedPanel = new MainMenuTabbedPanel(MAIN_MENU_TABBED_PANEL_ID, new ArrayList<ITab>(), null);
  }

  @Override
  protected void onInitialize() {
    final CategoryHomeTab categoryHomeTab = new CategoryHomeTab(Model.of(CategoryMainMenuPanel.this.getString(NetbrasoftShopMessageKeyConstants.HOME_MESSAGE_KEY)));
    final ContactTab contactTab = new ContactTab(Model.of(CategoryMainMenuPanel.this.getString(NetbrasoftShopMessageKeyConstants.CONTACT_MESSAGE_KEY)));

    categoryDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    categoryDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    categoryDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    categoryDataProvider.setType(new Category());
    categoryDataProvider.getType().setActive(true);
    categoryDataProvider.setOrderBy(OrderBy.POSITION_A_Z);

    mainMenuTabbedPanel.getTabs().add(categoryHomeTab);
    for (final Iterator<? extends Category> iterator = categoryDataProvider.iterator(0, 5); iterator.hasNext();) {
      final Category category = iterator.next();
      final CategoryTab categoryTab = new CategoryTab(Model.of(category.getName()), Model.of(category));
      mainMenuTabbedPanel.getTabs().add(categoryTab);
    }
    mainMenuTabbedPanel.getTabs().add(contactTab);
    add(mainMenuTabbedPanel.setOutputMarkupId(true));

    super.onInitialize();
  }
}
