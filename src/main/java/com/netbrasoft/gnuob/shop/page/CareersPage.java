package com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.shop.security.ShopRoles;

@MountPath("careers.html")
@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class CareersPage extends BasePage {

   private static final long serialVersionUID = -7721060791790055851L;
}
