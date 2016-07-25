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

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CATEGORY_BREAD_CRUMB_BAR_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CATEGORY_VIEW_PANEL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.UNCHECKED;
import static br.com.netbrasoft.gnuob.shop.security.ShopRoles.GUEST;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.com.netbrasoft.gnuob.api.Category;
import br.com.netbrasoft.gnuob.shop.category.CategoryViewPanel.SubCategoryViewFragment;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;

@SuppressWarnings(UNCHECKED)
@AuthorizeAction(action = Action.RENDER, roles = {GUEST})
public class CategoryPanel extends Panel {

  private static final long serialVersionUID = 1970275605681803223L;

  public CategoryPanel(final String id, final IModel<Category> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    add(getBreadcrumbComponent());
    add(getCategoryViewPanelComponent(getCategoryViewPanel()));
    super.onInitialize();
  }

  private Component getBreadcrumbComponent() {
    return getBreadcrumb().setOutputMarkupId(true);
  }

  private Breadcrumb getBreadcrumb() {
    return new Breadcrumb(CATEGORY_BREAD_CRUMB_BAR_ID);
  }

  private Component getCategoryViewPanelComponent(CategoryViewPanel categoryViewPanel) {
    return categoryViewPanel.add(getSubCategoryViewFragment(categoryViewPanel)).setOutputMarkupId(true);
  }

  private CategoryViewPanel getCategoryViewPanel() {
    return new CategoryViewPanel(CATEGORY_VIEW_PANEL_ID, getBreadcrumb(),
        (IModel<Category>) CategoryPanel.this.getDefaultModel());
  }

  private SubCategoryViewFragment getSubCategoryViewFragment(CategoryViewPanel categoryViewPanel) {
    return categoryViewPanel.new SubCategoryViewFragment();
  }
}
