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
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CATEGORY_DATA_VIEW_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CLICK_EVENT;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CONTENT_BORDER_CONTENT_BORDER_BODY_MAIN_MENU_PANEL_MAIN_MENU_TABBED_PANEL;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CONTENT_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.UNCHECKED;
import static br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession.getPassword;
import static br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession.getSite;
import static br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession.getUserName;
import static br.com.netbrasoft.gnuob.shop.security.ShopRoles.GUEST;
import static java.lang.Integer.MAX_VALUE;
import static java.util.stream.Collectors.toList;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.netbrasoft.gnuob.api.Category;
import br.com.netbrasoft.gnuob.api.generic.IGenericTypeDataProvider;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

@SuppressWarnings(UNCHECKED)
@AuthorizeAction(action = Action.RENDER, roles = {GUEST})
public class CategoryHomePanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
  class CategoryDataview extends DataView<Category> {

    private static final long serialVersionUID = 5098665993468197838L;

    private CategoryDataview(final String id, final IDataProvider<Category> dataProvider, final long itemsPerPage) {
      super(id, dataProvider, itemsPerPage);
    }

    @Override
    protected void populateItem(final Item<Category> item) {
      item.setModel(getCompoundPropertyModel(item));
      item.add(getContentLabelComponent(getReadOnlyModel(item)));
      item.add(getAjaxEventBehavior(item));
    }

    private CompoundPropertyModel<Category> getCompoundPropertyModel(final Item<Category> item) {
      return new CompoundPropertyModel<>(item.getModelObject());
    }

    private Component getContentLabelComponent(final AbstractReadOnlyModel<String> readOnlyModel) {
      return getContentLabel(readOnlyModel).setEscapeModelStrings(false);
    }

    private AbstractReadOnlyModel<String> getReadOnlyModel(final Item<Category> item) {
      return new AbstractReadOnlyModel<String>() {

        private static final long serialVersionUID = 4751535250171413561L;

        @Override
        public String getObject() {
          final StringBuilder stringBuilder = new StringBuilder();
          item.getModelObject().getContents().stream().forEach(e -> stringBuilder.append(e.getContent()));
          return stringBuilder.toString();
        }
      };
    }

    private Label getContentLabel(final AbstractReadOnlyModel<String> readOnlyModel) {
      return new Label(CONTENT_ID, readOnlyModel);
    }

    private AjaxEventBehavior getAjaxEventBehavior(final Item<Category> item) {
      return new AjaxEventBehavior(CLICK_EVENT) {

        private static final long serialVersionUID = 175594289469817897L;

        @Override
        public void onEvent(final AjaxRequestTarget target) {
          final BootstrapTabbedPanel<ITab> bootstrapTabbedPanel = (BootstrapTabbedPanel<ITab>) getPage()
              .get(CONTENT_BORDER_CONTENT_BORDER_BODY_MAIN_MENU_PANEL_MAIN_MENU_TABBED_PANEL);
          bootstrapTabbedPanel.setSelectedTab(getSelectedTabIndex(item, bootstrapTabbedPanel));
          target.add(target.getPage());
        }

        private int getSelectedTabIndex(final Item<Category> item,
            final BootstrapTabbedPanel<ITab> bootstrapTabbedPanel) {
          return bootstrapTabbedPanel.getTabs().stream()
              .filter(e -> e.getClass().equals(CategoryTab.class)
                  && ((CategoryTab) e).getModelObject().getId() == ((Category) item.getDefaultModelObject()).getId())
              .collect(toList()).size();
        }
      };
    }
  }

  private static final long serialVersionUID = 5858682402634442147L;

  @SpringBean(name = CATEGORY_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Category> categoryDataProvider;

  public CategoryHomePanel(final String id, final IModel<Category> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    initializeCategoryDataProvider();
    add(getCategoryDataviewComponent());
    super.onInitialize();
  }

  private void initializeCategoryDataProvider() {
    categoryDataProvider.setUser(getUserName());
    categoryDataProvider.setPassword(getPassword());
    categoryDataProvider.setSite(getSite());
    categoryDataProvider.setType(new Category());
    categoryDataProvider.getType().setActive(true);
    categoryDataProvider.setOrderBy(POSITION_A_Z);
  }

  private Component getCategoryDataviewComponent() {
    return getCategoryDataview().setOutputMarkupId(true);
  }

  private CategoryDataview getCategoryDataview() {
    return new CategoryDataview(CATEGORY_DATA_VIEW_ID, categoryDataProvider, MAX_VALUE);
  }
}
