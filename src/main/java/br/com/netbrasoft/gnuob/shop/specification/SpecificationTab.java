package br.com.netbrasoft.gnuob.shop.specification;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.netbrasoft.gnuob.api.Order;

public class SpecificationTab extends AbstractTab {

  private static final long serialVersionUID = 6278429178773013326L;

  public SpecificationTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    return new SpecificationPanel(panelId, Model.of(new Order()));
  }
}
