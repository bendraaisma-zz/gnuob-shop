package com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.shop.border.ContentBorder;
import com.netbrasoft.gnuob.shop.checkout.CheckoutMainMenuPanel;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@MountPath("checkout.html")
@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class CheckoutPage extends BasePage {

   private static final long serialVersionUID = 4051343927877779621L;

   private CheckoutMainMenuPanel mainMenuPanel = new CheckoutMainMenuPanel("mainMenuPanel", Model.of(new Shopper()));

   private ContentBorder contentBorder = new ContentBorder("contentBorder");

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   @Override
   protected void onInitialize() {
      if (!shopperDataProvider.find(new Shopper()).loggedIn()) {
         throw new RedirectToUrlException("account.html");
      }

      contentBorder.add(mainMenuPanel);
      add(contentBorder);

      super.onInitialize();
   }
}
