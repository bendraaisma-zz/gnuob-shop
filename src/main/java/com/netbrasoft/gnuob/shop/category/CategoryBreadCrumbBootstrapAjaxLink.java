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

package com.netbrasoft.gnuob.shop.category;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbBar;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.SubCategory;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;

/**
 * A link that when clicked will set the the active {@link IBreadCrumbParticipant bread crumb
 * participant} to the one that is returned by {@link #getParticipant(String)}. It is used
 * internally by {@link BreadCrumbBar the the bread crumb bar component}, and you can use it for
 * rendering links e.g. with {@link BreadCrumbPanel bread crumb panel components}.
 *
 * @author "Bernard Arjan Draaisma"
 */
@SuppressWarnings("unchecked")
public class CategoryBreadCrumbBootstrapAjaxLink extends BootstrapAjaxLink<SubCategory> {

  private static final long serialVersionUID = 4784885003823614976L;

  private final IModel<Category> categoryModel;

  private final IBreadCrumbModel breadCrumbModel;

  public CategoryBreadCrumbBootstrapAjaxLink(final String id, final BreadCrumbPanel caller, final IModel<SubCategory> model, final Buttons.Type type,
      final IModel<String> labelModel) {
    super(id, model, type, labelModel);
    this.categoryModel = (IModel<Category>) caller.getDefaultModel();
    this.breadCrumbModel = caller.getBreadCrumbModel();
    this.breadCrumbModel.setActive(caller);
  }

  /**
   * Gets the {@link IBreadCrumbParticipant bread crumb participant} to be set active when the link
   * is clicked.
   *
   * @param componentId When the participant creates it's own view, it typically should use this
   *        component id for the component that is returned by
   *        {@link IBreadCrumbParticipant#getComponent()}.
   * @return The bread crumb participant
   */
  protected IBreadCrumbParticipant getParticipant(final String componentId) {
    final CategoryViewPanel categoryViewPanel = new CategoryViewPanel(componentId, this.breadCrumbModel, this.categoryModel,
        Model.of(CategoryBreadCrumbBootstrapAjaxLink.this.getModelObject()), Model.ofList(CategoryBreadCrumbBootstrapAjaxLink.this.getModelObject().getSubCategories()));
    return (IBreadCrumbParticipant) categoryViewPanel.add(categoryViewPanel.new ProductViewFragment().setOutputMarkupId(true));
  }

  @Override
  public void onClick(final AjaxRequestTarget target) {
    // get the currently active particpant
    final IBreadCrumbParticipant active = breadCrumbModel.getActive();
    if (active == null) {
      throw new IllegalStateException("The model has no active bread crumb. Before using " + this + ", you have to have at least one bread crumb in the model");
    }

    // get the participant to set as active
    final IBreadCrumbParticipant participant = getParticipant(active.getComponent().getId());

    // set the next participant as the active one
    breadCrumbModel.setActive(participant);
    target.add(target.getPage());
  }
}
