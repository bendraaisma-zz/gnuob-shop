package br.com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import br.com.netbrasoft.gnuob.shop.security.ShopRoles;

@MountPath(CareersPage.CAREERS_HTML_VALUE)
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CareersPage extends BasePage {

  protected static final String CAREERS_HTML_VALUE = "careers.html";

  private static final long serialVersionUID = -7721060791790055851L;
}
