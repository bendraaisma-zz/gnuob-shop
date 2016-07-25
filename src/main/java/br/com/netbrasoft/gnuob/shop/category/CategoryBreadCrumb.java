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

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.model.IModel;

import br.com.netbrasoft.gnuob.api.Category;
import br.com.netbrasoft.gnuob.api.SubCategory;
import br.com.netbrasoft.gnuob.shop.category.CategoryViewPanel.ProductViewFragment;

public class CategoryBreadCrumb {
  private final IModel<Category> categoryModel;
  private final IModel<SubCategory> subCategoryModel;
  private final IModel<List<SubCategory>> subCategoryModelList;
  private final IBreadCrumbModel breadCrumbModel;

  public CategoryBreadCrumb(final BreadCrumbPanel caller, final IModel<Category> categoryModel,
      final IModel<SubCategory> subCategoryModel, final IModel<List<SubCategory>> subCategoryModelList) {
    this.categoryModel = categoryModel;
    this.subCategoryModel = subCategoryModel;
    this.subCategoryModelList = subCategoryModelList;
    breadCrumbModel = caller.getBreadCrumbModel();
    breadCrumbModel.setActive(caller);
  }

  public void setActive() {
    breadCrumbModel.setActive(getBreadCrumbParticipant(getComponentID()));
  }

  protected IBreadCrumbParticipant getBreadCrumbParticipant(final String componentId) {
    final CategoryViewPanel categoryViewPanel = getCategoryViewPanel(componentId);
    return (IBreadCrumbParticipant) categoryViewPanel.add(getProductViewFragmentComponent(categoryViewPanel));
  }

  private CategoryViewPanel getCategoryViewPanel(final String componentId) {
    return new CategoryViewPanel(componentId, breadCrumbModel, categoryModel, subCategoryModel, subCategoryModelList);
  }

  private String getComponentID() {
    return breadCrumbModel.getActive().getComponent().getId();
  }

  private Component getProductViewFragmentComponent(final CategoryViewPanel categoryViewPanel) {
    return getProductViewFragment(categoryViewPanel).setOutputMarkupId(true);
  }

  private ProductViewFragment getProductViewFragment(final CategoryViewPanel categoryViewPanel) {
    return categoryViewPanel.new ProductViewFragment();
  }
}
