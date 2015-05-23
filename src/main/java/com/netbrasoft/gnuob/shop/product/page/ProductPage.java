package com.netbrasoft.gnuob.shop.product.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.Model;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.shop.border.ContentBorder;
import com.netbrasoft.gnuob.shop.page.BasePage;
import com.netbrasoft.gnuob.shop.panel.MainMenuPanel;
import com.netbrasoft.gnuob.shop.security.ShopRoles;

@MountPath("product.html")
@AuthorizeInstantiation({ ShopRoles.GUEST })
public class ProductPage extends BasePage {

   private static final long serialVersionUID = -7854507374209656133L;

   private MainMenuPanel mainMenuPanel = new MainMenuPanel("mainMenuPanel", new Model<Category>(new Category()));

   private ContentBorder contentBorder = new ContentBorder("contentBorder");

   @Override
   protected void onInitialize() {
      contentBorder.add(mainMenuPanel);
      add(contentBorder);

      super.onInitialize();
   }
}
