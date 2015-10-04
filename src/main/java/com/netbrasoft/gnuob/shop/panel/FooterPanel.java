package com.netbrasoft.gnuob.shop.panel;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;

import com.netbrasoft.gnuob.shop.security.ShopRoles;

@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class FooterPanel extends Panel {

  private static final long serialVersionUID = 2384748913794006217L;

  public FooterPanel(String id) {
    super(id);
  }
}
