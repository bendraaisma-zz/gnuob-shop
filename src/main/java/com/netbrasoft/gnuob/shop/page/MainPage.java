package com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.Model;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.shop.border.ContentBorder;
import com.netbrasoft.gnuob.shop.panel.MainMenuPanel;
import com.netbrasoft.gnuob.shop.security.ShopRoles;

@MountPath("shop.html")
@AuthorizeInstantiation({ ShopRoles.GUEST })
public class MainPage extends BasePage {

   private static final long serialVersionUID = 7583829533111693200L;

   private MainMenuPanel mainMenuPanel = new MainMenuPanel("mainMenuPanel", new Model<Category>(new Category()));

   private ContentBorder contentBorder = new ContentBorder("contentBorder");

   @Override
   protected void onInitialize() {
      contentBorder.add(mainMenuPanel);
      add(contentBorder);

      super.onInitialize();
   }
}
