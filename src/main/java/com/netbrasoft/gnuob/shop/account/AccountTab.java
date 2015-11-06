package com.netbrasoft.gnuob.shop.account;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.api.Contract;

public class AccountTab extends AbstractTab {

  private static final long serialVersionUID = 6278429178773013326L;

  public AccountTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    return new AccountPanel(panelId, Model.of(new Contract()));
  }
}
