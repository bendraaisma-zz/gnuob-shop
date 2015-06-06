package com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.Model;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.shop.border.ContentBorder;
import com.netbrasoft.gnuob.shop.checkout.CheckoutMainMenuPanel;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@MountPath("checkout.html")
@AuthorizeInstantiation({ ShopRoles.GUEST })
public class CheckoutPage extends BasePage {

   private static final long serialVersionUID = 4051343927877779621L;

   private CheckoutMainMenuPanel mainMenuPanel = new CheckoutMainMenuPanel("mainMenuPanel", Model.of(new Shopper()));

   private ContentBorder contentBorder = new ContentBorder("contentBorder");

   @Override
   protected void onInitialize() {
      contentBorder.add(mainMenuPanel);
      add(contentBorder);

      super.onInitialize();
   }
}
