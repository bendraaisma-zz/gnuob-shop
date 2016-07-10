package br.com.netbrasoft.gnuob.shop.page.tab;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.flow.RedirectToUrlException;

import br.com.netbrasoft.gnuob.shop.page.SpecificationPage;

public class SpecificationTab extends AbstractTab {

  private static final long serialVersionUID = -2199080888114094533L;

  public SpecificationTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    throw new RedirectToUrlException(SpecificationPage.SPECIFICATION_HTML_VALUE);
  }
}
