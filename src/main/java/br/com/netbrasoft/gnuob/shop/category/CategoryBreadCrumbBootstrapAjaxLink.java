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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.netbrasoft.gnuob.api.Category;
import br.com.netbrasoft.gnuob.api.SubCategory;
import br.com.netbrasoft.gnuob.shop.category.CategoryViewPanel.ProductViewFragment;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;

@SuppressWarnings(UNCHECKED)
public class CategoryBreadCrumbBootstrapAjaxLink extends BootstrapAjaxLink<SubCategory> {
    
    private static final long serialVersionUID = 4784885003823614976L;
    private final IModel<Category> categoryModel;
    private final IBreadCrumbModel breadCrumbModel;
    
    public CategoryBreadCrumbBootstrapAjaxLink(final String id, final BreadCrumbPanel caller,
            final IModel<SubCategory> model, final Buttons.Type type, final IModel<String> labelModel) {
        super(id, model, type, labelModel);
        categoryModel = (IModel<Category>) caller.getDefaultModel();
        breadCrumbModel = caller.getBreadCrumbModel();
        breadCrumbModel.setActive(caller);
    }
    
    @Override
    public void onClick(final AjaxRequestTarget target) {
        breadCrumbModel.setActive(getBreadCrumbParticipant(getComponentID()));
        target.add(target.getPage());
    }
    
    protected IBreadCrumbParticipant getBreadCrumbParticipant(final String componentId) {
        final CategoryViewPanel categoryViewPanel = getCategoryViewPanel(componentId);
        return (IBreadCrumbParticipant) categoryViewPanel.add(getProductViewFragmentComponent(categoryViewPanel));
    }
    
    private CategoryViewPanel getCategoryViewPanel(final String componentId) {
        return new CategoryViewPanel(componentId, breadCrumbModel, categoryModel,
                Model.of(CategoryBreadCrumbBootstrapAjaxLink.this.getModelObject()),
                Model.ofList(CategoryBreadCrumbBootstrapAjaxLink.this.getModelObject().getSubCategories()));
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
