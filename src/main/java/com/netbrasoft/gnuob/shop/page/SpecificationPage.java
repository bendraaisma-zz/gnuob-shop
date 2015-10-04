package com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.Model;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.shop.border.ContentBorder;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;
import com.netbrasoft.gnuob.shop.specification.SpecificationMainMenuPanel;

@MountPath("specification.html")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class SpecificationPage extends BasePage {

  private static final long serialVersionUID = 120059668578792943L;

  private SpecificationMainMenuPanel mainMenuPanel = new SpecificationMainMenuPanel("mainMenuPanel", Model.of(new Shopper()));

  private ContentBorder contentBorder = new ContentBorder("contentBorder");

  @Override
  protected void onInitialize() {
    contentBorder.add(mainMenuPanel);
    add(contentBorder);

    super.onInitialize();
  }
}
