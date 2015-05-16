package com.netbrasoft.gnuob.shop.page.tab;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.shop.category.CategoryPanel;

public class CategoryTab extends AbstractTab {

   private static final long serialVersionUID = 4835579949680085443L;
   IModel<Category> model;

   public CategoryTab(final IModel<String> title, final IModel<Category> model) {
      super(title);
      this.model = model;
   }

   @Override
   public WebMarkupContainer getPanel(final String panelId) {
      return new CategoryPanel(panelId, model);
   }

   public Category getModelObject() {
      return model.getObject();
   }

   public IModel<Category> getModel() {
      return model;
   }
}
