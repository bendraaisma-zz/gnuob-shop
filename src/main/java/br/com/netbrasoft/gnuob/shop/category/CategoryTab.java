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

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import br.com.netbrasoft.gnuob.api.Category;

public class CategoryTab extends AbstractTab {

  private static final long serialVersionUID = 4835579949680085443L;

  private final IModel<Category> model;

  public CategoryTab(final IModel<String> title, final IModel<Category> model) {
    super(title);
    this.model = model;
  }

  public IModel<Category> getModel() {
    return model;
  }

  public Category getModelObject() {
    return model.getObject();
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    return new CategoryPanel(panelId, model);
  }
}
