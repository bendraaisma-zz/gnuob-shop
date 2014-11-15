package com.netbrasoft.gnuob.shop.panel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;

public class MainMenuPanel extends Panel {

   private static class CategoryDataView extends DataView<Category> {

      private static final long serialVersionUID = -6631295517827664521L;

      private static final int ITEMS_PER_PAGE = 10;

      protected CategoryDataView(String id, IDataProvider<Category> dataProvider) {
         super(id, dataProvider);
      }

      @Override
      public long getItemsPerPage() {
         return ITEMS_PER_PAGE;
      }

      @Override
      protected void populateItem(Item<Category> item) {
         Category category = item.getModelObject();

         item.add(new Label("name", category.getName().toUpperCase()));
         item.setOutputMarkupId(true);
      }
   }

   private static final long serialVersionUID = 6083651059402628915L;

   @SpringBean(name = "CategoryDataProvider", required = true)
   private GenericTypeDataProvider<Category> genericTypeDataProvider;

   public MainMenuPanel(String id) {
      super(id);
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();

      genericTypeDataProvider.setUser(System.getProperty("site.user", "administrator"));
      genericTypeDataProvider.setPassword(System.getProperty("site.password", "administrator"));
      genericTypeDataProvider.setSite(System.getProperty("site.name", "www.netbrasoft.com"));

      Category category = new Category();
      category.setActive(true);

      genericTypeDataProvider.setType(category);

      CategoryDataView categoryDataView = new CategoryDataView("categoryListItems", genericTypeDataProvider);

      add(categoryDataView);
   }
}
