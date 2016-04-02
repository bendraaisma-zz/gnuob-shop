package com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.shop.security.ShopRoles;

@MountPath(ReturnsPage.RETURNS_HTML_VALUE)
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class ReturnsPage extends BasePage {

  protected static final String RETURNS_HTML_VALUE = "returns.html";

  private static final long serialVersionUID = -7721060791790055851L;
}
