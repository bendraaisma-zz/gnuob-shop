package com.netbrasoft.gnuob.shop.category;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.shop.security.ShopRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;

/**
 * Panel for viewing, selecting {@link Category} entities.
 *
 * @author Bernard Arjan Draaisma
 *
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CategoryPanel extends Panel {

  private static final String CATEGORY_BREAD_CRUMB_BAR_ID = "categoryBreadCrumbBar";

  private static final String CATEGORY_VIEW_PANEL_ID = "categoryViewPanel";

  private static final long serialVersionUID = 1970275605681803223L;

  private final CategoryViewPanel categoryViewPanel;

  private final Breadcrumb categoryBreadCrumbBar;

  public CategoryPanel(final String id, final IModel<Category> model) {
    super(id, model);
    categoryBreadCrumbBar = new Breadcrumb(CATEGORY_BREAD_CRUMB_BAR_ID);
    categoryViewPanel = new CategoryViewPanel(CATEGORY_VIEW_PANEL_ID, categoryBreadCrumbBar, (IModel<Category>) CategoryPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    categoryViewPanel.add(categoryViewPanel.new SubCategoryViewFragment());
    add(categoryBreadCrumbBar.setOutputMarkupId(true));
    add(categoryViewPanel.setOutputMarkupId(true));
    super.onInitialize();
  }
}
