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

package br.com.netbrasoft.gnuob.shop.category;

import static br.com.netbrasoft.gnuob.api.OrderBy.POSITION_A_Z;
import static br.com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.CATEGORY_DATA_PROVIDER_NAME;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CONTACT_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.HOME_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.MAIN_MENU_TABBED_PANEL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.NAV_NAV_PILLS_NAV_JUSTIFIED_CSS_CLASS;
import static br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession.getPassword;
import static br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession.getSite;
import static br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession.getUserName;
import static br.com.netbrasoft.gnuob.shop.security.ShopRoles.GUEST;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.wicket.model.Model.of;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.netbrasoft.gnuob.api.Category;
import br.com.netbrasoft.gnuob.api.generic.IGenericTypeDataProvider;
import br.com.netbrasoft.gnuob.shop.page.tab.ContactTab;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

@AuthorizeAction(action = Action.RENDER, roles = {GUEST})
public class CategoryMainMenuPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
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

  private static final long serialVersionUID = 6083651059402628915L;

  @SpringBean(name = CATEGORY_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Category> categoryDataProvider;

  public CategoryMainMenuPanel(final String id, final IModel<Category> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    initializeCategoryProvider();
    add(getMainMenuTabbedPanelComponent());
    super.onInitialize();
  }

  private void initializeCategoryProvider() {
    categoryDataProvider.setUser(getUserName());
    categoryDataProvider.setPassword(getPassword());
    categoryDataProvider.setSite(getSite());
    categoryDataProvider.setType(new Category());
    categoryDataProvider.getType().setActive(true);
    categoryDataProvider.setOrderBy(POSITION_A_Z);
  }

  private Component getMainMenuTabbedPanelComponent() {
    final MainMenuTabbedPanel mainMenuTabbedPanel = getMainMenuTabbedPanel();
    mainMenuTabbedPanel.getTabs().add(getCategoryHomeTab());
    mainMenuTabbedPanel.getTabs().addAll(getCategoryTabs());
    mainMenuTabbedPanel.getTabs().add(getContactTab());
    return mainMenuTabbedPanel.setOutputMarkupId(true);
  }

  private MainMenuTabbedPanel getMainMenuTabbedPanel() {
    return new MainMenuTabbedPanel(MAIN_MENU_TABBED_PANEL_ID, new ArrayList<ITab>(), null);
  }

  private CategoryHomeTab getCategoryHomeTab() {
    return new CategoryHomeTab(of(CategoryMainMenuPanel.this.getString(HOME_MESSAGE_KEY)));
  }

  private List<CategoryTab> getCategoryTabs() {
    final List<CategoryTab> categoryTabs = newArrayList();
    if (categoryDataProvider.size() > 0) {
      for (final Iterator<? extends Category> iterator = categoryDataProvider.iterator(-1, -1); iterator.hasNext();) {
        final Category category = iterator.next();
        categoryTabs.add(new CategoryTab(of(category.getName()), of(category)));
      }
    }
    return categoryTabs;
  }

  private ContactTab getContactTab() {
    return new ContactTab(of(CategoryMainMenuPanel.this.getString(CONTACT_MESSAGE_KEY)));
  }
}
