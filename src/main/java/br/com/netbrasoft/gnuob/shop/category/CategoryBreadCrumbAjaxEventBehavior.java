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

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.UNCHECKED;
import static org.apache.wicket.model.Model.ofList;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.model.IModel;

import br.com.netbrasoft.gnuob.api.Category;
import br.com.netbrasoft.gnuob.api.SubCategory;
import br.com.netbrasoft.gnuob.shop.category.CategoryViewPanel.ProductViewFragment;

@SuppressWarnings(UNCHECKED)
public class CategoryBreadCrumbAjaxEventBehavior extends AjaxEventBehavior {

  private static final long serialVersionUID = 6736376015262634150L;
  private final IModel<Category> categoryModel;
  private final IBreadCrumbModel breadCrumbModel;
  private final IModel<SubCategory> model;

  public CategoryBreadCrumbAjaxEventBehavior(final String event, final BreadCrumbPanel caller,
      final IModel<SubCategory> model) {
    super(event);
    this.model = model;
    this.categoryModel = (IModel<Category>) caller.getDefaultModel();
    this.breadCrumbModel = caller.getBreadCrumbModel();
    this.breadCrumbModel.setActive(caller);
  }

  @Override
  protected void onEvent(final AjaxRequestTarget target) {
    breadCrumbModel.setActive(getBreadCrumbParticipant(getComponentId()));
    target.add(target.getPage());
  }

  protected IBreadCrumbParticipant getBreadCrumbParticipant(final String componentId) {
    final CategoryViewPanel categoryViewPanel = getCategoryViewPanel(componentId);
    return (IBreadCrumbParticipant) categoryViewPanel.add(getProductViewFragmentComponent(categoryViewPanel));
  }

  private String getComponentId() {
    return breadCrumbModel.getActive().getComponent().getId();
  }

  private CategoryViewPanel getCategoryViewPanel(final String componentId) {
    return new CategoryViewPanel(componentId, this.breadCrumbModel, this.categoryModel, model,
        ofList(model.getObject().getSubCategories()));
  }

  private Component getProductViewFragmentComponent(final CategoryViewPanel categoryViewPanel) {
    return getProductViewFragment(categoryViewPanel).setOutputMarkupId(true);
  }

  private ProductViewFragment getProductViewFragment(final CategoryViewPanel categoryViewPanel) {
    return categoryViewPanel.new ProductViewFragment();
  }
}
