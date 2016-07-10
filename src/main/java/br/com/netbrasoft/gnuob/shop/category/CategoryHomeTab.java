package br.com.netbrasoft.gnuob.shop.category;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.netbrasoft.gnuob.api.Category;

public class CategoryHomeTab extends AbstractTab {

  private static final long serialVersionUID = 4835579949680085443L;

  public CategoryHomeTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    return new CategoryHomePanel(panelId, Model.of(new Category()));
  }
}
