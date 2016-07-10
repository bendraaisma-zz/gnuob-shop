
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

import static br.com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.CATEGORY_DATA_PROVIDER_NAME;

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

import br.com.netbrasoft.gnuob.api.Category;
import br.com.netbrasoft.gnuob.api.OrderBy;
import br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants;
import br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import br.com.netbrasoft.gnuob.shop.page.tab.ContactTab;
import br.com.netbrasoft.gnuob.shop.security.ShopRoles;

import br.com.netbrasoft.gnuob.api.generic.IGenericTypeDataProvider;
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

  @SpringBean(name = CATEGORY_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Category> categoryDataProvider;

  public CategoryMainMenuPanel(final String id, final IModel<Category> model) {
    super(id, model);
    mainMenuTabbedPanel = new MainMenuTabbedPanel(MAIN_MENU_TABBED_PANEL_ID, new ArrayList<ITab>(), null);
  }

  @Override
  protected void onInitialize() {
    final CategoryHomeTab categoryHomeTab =
        new CategoryHomeTab(Model.of(CategoryMainMenuPanel.this.getString(NetbrasoftShopConstants.HOME_MESSAGE_KEY)));
    final ContactTab contactTab =
        new ContactTab(Model.of(CategoryMainMenuPanel.this.getString(NetbrasoftShopConstants.CONTACT_MESSAGE_KEY)));

    categoryDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    categoryDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    categoryDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    categoryDataProvider.setType(new Category());
    categoryDataProvider.getType().setActive(true);
    categoryDataProvider.setOrderBy(OrderBy.POSITION_A_Z);

    mainMenuTabbedPanel.getTabs().add(categoryHomeTab);

    final long count = categoryDataProvider.size();
    if (count > 0) {
      for (final Iterator<? extends Category> iterator = categoryDataProvider.iterator(0, count); iterator.hasNext();) {
        final Category category = iterator.next();
        final CategoryTab categoryTab = new CategoryTab(Model.of(category.getName()), Model.of(category));
        mainMenuTabbedPanel.getTabs().add(categoryTab);
      }
    }
    mainMenuTabbedPanel.getTabs().add(contactTab);
    add(mainMenuTabbedPanel.setOutputMarkupId(true));

    super.onInitialize();
  }
}
