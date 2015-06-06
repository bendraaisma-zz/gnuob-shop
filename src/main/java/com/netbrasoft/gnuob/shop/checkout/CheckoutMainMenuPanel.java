package com.netbrasoft.gnuob.shop.checkout;

import java.util.ArrayList;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.shop.page.tab.AccountTab;
import com.netbrasoft.gnuob.shop.page.tab.HomeTab;
import com.netbrasoft.gnuob.shop.page.tab.WishListTab;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

public class CheckoutMainMenuPanel extends Panel {

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

   private static final long serialVersionUID = 4037036072135523233L;

   private final ITab homeTab = new HomeTab(Model.of(getString("homeMessage", new Model<String>(), "HOME").toUpperCase()));

   private final ITab accountTab = new AccountTab(Model.of(getString("accountMessage", new Model<String>(), "ACCOUNT").toUpperCase()));

   private final ITab wishtListTab = new WishListTab(Model.of(getString("wishListMessage", new Model<String>(), "OFFER").toUpperCase()));

   private final ITab checkoutTab = new CheckoutTab(Model.of(getString("checkoutMessage", new Model<String>(), "ORDERS").toUpperCase()));

   private final MainMenuTabbedPanel mainMenuTabbedPanel = new MainMenuTabbedPanel();

   public CheckoutMainMenuPanel(final String id, final IModel<?> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
      mainMenuTabbedPanel.getTabs().add(homeTab);
      mainMenuTabbedPanel.getTabs().add(accountTab);
      mainMenuTabbedPanel.getTabs().add(wishtListTab);
      mainMenuTabbedPanel.getTabs().add(checkoutTab);
      mainMenuTabbedPanel.setSelectedTab(mainMenuTabbedPanel.getTabs().indexOf(checkoutTab));

      add(mainMenuTabbedPanel.setOutputMarkupId(true));
      super.onInitialize();
   }

}
