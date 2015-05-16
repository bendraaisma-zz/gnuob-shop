package com.netbrasoft.gnuob.shop.panel;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.shop.security.ShopRoles;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class ContactPanel extends Panel {

   private static final long serialVersionUID = 5442962435614721448L;

   public ContactPanel(final String id, final IModel<Category> model) {
      super(id, model);
   }
}
