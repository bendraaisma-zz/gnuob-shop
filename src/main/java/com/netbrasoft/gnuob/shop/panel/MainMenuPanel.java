package com.netbrasoft.gnuob.shop.panel;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.page.tab.CategoryTab;
import com.netbrasoft.gnuob.shop.page.tab.HomeTab;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

public class MainMenuPanel extends Panel {

   class MainMenuTabbedPanel extends BootstrapTabbedPanel<ITab> {

      private static final long serialVersionUID = 6838221105862530322L;

      public MainMenuTabbedPanel() {
         super("mainMenuTabbedPanel", new ArrayList<ITab>());
      }

      @Override
      public String getTabContainerCssClass() {
         return "nav nav-pills nav-justified";
      }
   }

   private static final long serialVersionUID = 6083651059402628915L;

   private final ITab homeTab = new HomeTab(new Model<String>(getString("homeMessage", new Model<String>(), "INICIO")));

   private final ITab contactTab = new HomeTab(new Model<String>(getString("contactMessage", new Model<String>(), "CONTATO")));

   private final MainMenuTabbedPanel mainMenuTabbedPanel = new MainMenuTabbedPanel();

   @SpringBean(name = "CategoryDataProvider", required = true)
   private GenericTypeDataProvider<Category> categoryDataProvider;

   public MainMenuPanel(final String id, final IModel<Category> model) {
      super(id, model);
      model.getObject().setActive(true);
   }

   @Override
   protected void onInitialize() {
      categoryDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
      categoryDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
      categoryDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
      categoryDataProvider.setType((Category) getDefaultModelObject());
      categoryDataProvider.setOrderBy(OrderBy.POSITION_A_Z);

      mainMenuTabbedPanel.getTabs().add(homeTab);

      Iterator<? extends Category> iterator = categoryDataProvider.iterator(0, 5);

      while (iterator.hasNext()) {
         Category category = iterator.next();
         mainMenuTabbedPanel.getTabs().add(new CategoryTab(new Model<String>(category.getName().toUpperCase())));
      }

      mainMenuTabbedPanel.getTabs().add(contactTab);

      add(mainMenuTabbedPanel);

      super.onInitialize();
   }
}
