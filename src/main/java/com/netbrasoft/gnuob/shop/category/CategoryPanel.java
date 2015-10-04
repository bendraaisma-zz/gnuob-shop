package com.netbrasoft.gnuob.shop.category;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.shop.security.ShopRoles;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CategoryPanel extends Panel {

  private static final long serialVersionUID = 1970275605681803223L;

  private CategoryViewPanel categoryViewPanel = new CategoryViewPanel("categoryViewPanel", (IModel<Category>) getDefaultModel());

  public CategoryPanel(final String id, final IModel<Category> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    add(categoryViewPanel.add(categoryViewPanel.new SubCategoryViewFragement()).setOutputMarkupId(true));
    super.onInitialize();
  }
}
