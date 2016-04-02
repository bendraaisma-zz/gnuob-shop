package com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.shop.security.ShopRoles;

@MountPath(StorePage.STORE_HTML_VALUE)
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class StorePage extends BasePage {

  protected static final String STORE_HTML_VALUE = "store.html";

  private static final long serialVersionUID = -7721060791790055851L;
}
