package com.netbrasoft.gnuob.shop.category;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.Content;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.Product;
import com.netbrasoft.gnuob.api.SubCategory;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.security.ShopRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Size;
import de.agilecoders.wicket.core.markup.html.bootstrap.list.BootstrapListView;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class SubCategoryProductViewPanel extends Panel {

   class SubCategoryViewFragement extends Fragment {

      private static final long serialVersionUID = -3028153699938016168L;

      public SubCategoryViewFragement() {
         super("subCategoryProductViewFragement", "subCategoryViewFragement", SubCategoryProductViewPanel.this, SubCategoryProductViewPanel.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
         add(subCategoryMenuBootstrapListView);
         add(subCategoryDataview);
         super.onInitialize();
      }
   }

   class SubCategoryDataProvider implements IDataProvider<SubCategory> {

      private static final long serialVersionUID = -2600778565688301137L;
      private List<SubCategory> subCategories = new ArrayList<SubCategory>();

      private SubCategoryDataProvider() {
         for (SubCategory subCategory : ((Category) SubCategoryProductViewPanel.this.getDefaultModelObject()).getSubCategories()) {
            subCategories.add(subCategory);
         }
      }

      @Override
      public void detach() {
         return;
      }

      @Override
      public Iterator<? extends SubCategory> iterator(long first, long count) {
         List<SubCategory> subCategoryIteratorList = new ArrayList<SubCategory>();

         for (int index = (int) first; index < first + count; index++) {
            subCategoryIteratorList.add(subCategories.get(index));
         }

         return subCategoryIteratorList.iterator();
      }

      @Override
      public IModel<SubCategory> model(SubCategory object) {
         return new Model<SubCategory>(object);
      }

      @Override
      public long size() {
         return subCategories.size();
      }
   }

   class SubCategoryDataview extends DataView<SubCategory> {

      private static final long serialVersionUID = 2776123630121635305L;

      private static final int ITEMS_PER_PAGE = 5;

      protected SubCategoryDataview() {
         super("subCategoryDataview", subCategoryDataProvider, ITEMS_PER_PAGE);
      }

      @Override
      protected void populateItem(Item<SubCategory> item) {
         item.setModel(new CompoundPropertyModel<SubCategory>(item.getModelObject()));
         item.add(new Label("content", new AbstractReadOnlyModel<String>() {

            private static final long serialVersionUID = 4751535250171413561L;

            @Override
            public String getObject() {
               StringBuilder contentStringBuilder = new StringBuilder();

               for (SubCategory subCategory : item.getModelObject().getSubCategories()) {
                  for (Content content : subCategory.getContents()) {
                     contentStringBuilder.append(new String(content.getContent()));
                  }
               }

               return contentStringBuilder.toString();
            }
         }).setEscapeModelStrings(false));
         item.add(new AjaxEventBehavior("click") {

            private static final long serialVersionUID = 3898435649434303190L;

            @Override
            protected void onEvent(AjaxRequestTarget target) {
               // TODO Auto-generated method stub
            }
         });
      }
   }

   class SubCategoryBootstrapListView extends BootstrapListView<SubCategory> {

      private static final long serialVersionUID = 2148940232228759419L;

      private SubCategoryBootstrapListView(final List<? extends SubCategory> listData) {
         super("subCategoryBootstrapListView", listData);
      }

      @Override
      protected void populateItem(ListItem<SubCategory> item) {
         item.setModel(new CompoundPropertyModel<SubCategory>(item.getModelObject()));
         item.add(new BootstrapAjaxLink<String>("subCategoryLink", new Model<String>(item.getModel().getObject().getName()), Buttons.Type.Link) {

            private static final long serialVersionUID = -1216788078532675590L;

            @Override
            public void onClick(AjaxRequestTarget target) {
               // TODO Auto-generated method stub
            }

         }.setSize(Size.Small).setOutputMarkupId(true));
      }
   }

   class SubCategoryMenuBootstrapListView extends BootstrapListView<SubCategory> {

      private static final long serialVersionUID = 2148940232228759419L;

      private SubCategoryMenuBootstrapListView() {
         super("subCategoryMenuBootstrapListView", ((Category) SubCategoryProductViewPanel.this.getDefaultModelObject()).getSubCategories());
      }

      @Override
      protected void populateItem(ListItem<SubCategory> item) {
         item.setModel(new CompoundPropertyModel<SubCategory>(item.getModelObject()));
         item.add(new Label("name"));
         item.add(new SubCategoryBootstrapListView(item.getModelObject().getSubCategories()));
      }
   }

   private static final long serialVersionUID = -9083340164646887954L;

   private SubCategoryDataProvider subCategoryDataProvider = new SubCategoryDataProvider();

   private SubCategoryDataview subCategoryDataview = new SubCategoryDataview();

   private SubCategoryMenuBootstrapListView subCategoryMenuBootstrapListView = new SubCategoryMenuBootstrapListView();

   public SubCategoryProductViewPanel(String id, IModel<Category> model) {
      super(id, model);
   }

   @SpringBean(name = "ProductDataProvider", required = true)
   private GenericTypeDataProvider<Product> productDataProvider;

   @Override
   protected void onInitialize() {
      productDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
      productDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
      productDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
      productDataProvider.setType(new Product());
      productDataProvider.getType().setActive(true);
      productDataProvider.setOrderBy(OrderBy.POSITION_A_Z);

      super.onInitialize();
   }

}
