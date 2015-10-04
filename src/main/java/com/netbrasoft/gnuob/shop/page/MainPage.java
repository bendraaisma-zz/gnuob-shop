package com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.Model;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.shop.border.ContentBorder;
import com.netbrasoft.gnuob.shop.category.CategoryMainMenuPanel;
import com.netbrasoft.gnuob.shop.security.ShopRoles;

@MountPath("shop.html")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class MainPage extends BasePage {

  private static final long serialVersionUID = 7583829533111693200L;

  private CategoryMainMenuPanel mainMenuPanel = new CategoryMainMenuPanel("mainMenuPanel", Model.of(new Category()));

  private ContentBorder contentBorder = new ContentBorder("contentBorder");

  @Override
  protected void onInitialize() {
    contentBorder.add(mainMenuPanel);
    add(contentBorder);

    super.onInitialize();
  }
}
