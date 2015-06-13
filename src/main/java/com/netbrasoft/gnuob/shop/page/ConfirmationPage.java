package com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.shop.border.ContentBorder;
import com.netbrasoft.gnuob.shop.confirmation.ConfirmationMainMenuPanel;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@MountPath("confirmation.html")
@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class ConfirmationPage extends BasePage {

   private static final long serialVersionUID = -4414527389707408188L;

   private ConfirmationMainMenuPanel mainMenuPanel = new ConfirmationMainMenuPanel("mainMenuPanel", Model.of(new Shopper()));

   private ContentBorder contentBorder = new ContentBorder("contentBorder");

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   @Override
   protected void onInitialize() {
      contentBorder.add(mainMenuPanel);
      add(contentBorder);

      super.onInitialize();
   }
}
