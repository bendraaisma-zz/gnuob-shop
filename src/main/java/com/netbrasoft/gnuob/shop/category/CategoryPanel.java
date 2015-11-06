package com.netbrasoft.gnuob.shop.category;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.shop.security.ShopRoles;

/**
 * Panel for viewing, selecting {@link Category} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CategoryPanel extends Panel {

  private static final String CATEGORY_VIEW_PANEL_ID = "categoryViewPanel";

  private static final long serialVersionUID = 1970275605681803223L;

  private final CategoryViewPanel categoryViewPanel;

  public CategoryPanel(final String id, final IModel<Category> model) {
    super(id, model);
    categoryViewPanel = new CategoryViewPanel(CATEGORY_VIEW_PANEL_ID, (IModel<Category>) CategoryPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    categoryViewPanel.add(categoryViewPanel.new SubCategoryViewFragment());
    add(categoryViewPanel.setOutputMarkupId(true));
    super.onInitialize();
  }
}
