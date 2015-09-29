package com.netbrasoft.gnuob.shop.cart;

import java.util.ArrayList;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.shop.page.tab.ConfirmationTab;
import com.netbrasoft.gnuob.shop.page.tab.HomeTab;
import com.netbrasoft.gnuob.shop.page.tab.SpecificationTab;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class CartMainMenuPanel extends Panel {

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

   private static final long serialVersionUID = -4776222984181317489L;

   private final MainMenuTabbedPanel mainMenuTabbedPanel;

   public CartMainMenuPanel(final String id, final IModel<Shopper> model) {
      super(id, model);

      mainMenuTabbedPanel = new MainMenuTabbedPanel();
   }

   @Override
   protected void onInitialize() {
      mainMenuTabbedPanel.getTabs().add(new HomeTab(Model.of(getString("homeMessage", new Model<String>(), "HOME").toUpperCase())));
      mainMenuTabbedPanel.getTabs().add(new CartTab(Model.of(getString("cartMessage", new Model<String>(), "CART").toUpperCase())));
      mainMenuTabbedPanel.getTabs().add(new SpecificationTab(Model.of(getString("specificationMessage", new Model<String>(), "SPECIFICATIONS").toUpperCase())));
      mainMenuTabbedPanel.getTabs().add(new ConfirmationTab(Model.of(getString("confirmationMessage", new Model<String>(), "CONFIRMATION").toUpperCase())));
      mainMenuTabbedPanel.setSelectedTab(1);

      add(mainMenuTabbedPanel.setOutputMarkupId(true));
      super.onInitialize();
   }
}
