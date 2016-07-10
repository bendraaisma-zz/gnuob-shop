package br.com.netbrasoft.gnuob.shop.page.tab;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.flow.RedirectToUrlException;

import br.com.netbrasoft.gnuob.shop.page.AccountPage;

public class AccountTab extends AbstractTab {

  private static final long serialVersionUID = -7414478523953380913L;

  public AccountTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    throw new RedirectToUrlException(AccountPage.ACCOUNT_HTML_VALUE);
  }
}
