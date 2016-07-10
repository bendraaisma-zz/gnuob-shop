package br.com.netbrasoft.gnuob.shop.confirmation;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.netbrasoft.gnuob.api.Order;

public class ConfirmationTab extends AbstractTab {

  private static final long serialVersionUID = 6278429178773013326L;

  public ConfirmationTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    return new ConfirmationPanel(panelId, Model.of(new Order()));
  }
}
