package com.netbrasoft.gnuob.shop.page.tab;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.flow.RedirectToUrlException;

import com.netbrasoft.gnuob.shop.page.MainPage;

public class HomeTab extends AbstractTab {

  private static final long serialVersionUID = -6273530217694775697L;

  public HomeTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    throw new RedirectToUrlException(MainPage.SHOP_HTML_VALUE);
  }
}
