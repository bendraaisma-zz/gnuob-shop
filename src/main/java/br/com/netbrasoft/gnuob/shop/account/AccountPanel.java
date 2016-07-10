package br.com.netbrasoft.gnuob.shop.account;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.netbrasoft.gnuob.api.Contract;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.security.ShopRoles;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;
import br.com.netbrasoft.gnuob.shop.shopper.ShopperDataProvider;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class AccountPanel extends Panel {

  private static final String ACCOUNT_LOGIN_OR_EDIT_PANEL_ID = "accountLoginOrEditPanel";

  private static final long serialVersionUID = 2034566325989232879L;

  @SpringBean(name = ShopperDataProvider.SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  private final AccountLoginOrEditPanel accountLoginOrEditPanel;

  public AccountPanel(final String id, final IModel<Contract> model) {
    super(id, model);
    accountLoginOrEditPanel = new AccountLoginOrEditPanel(ACCOUNT_LOGIN_OR_EDIT_PANEL_ID, (IModel<Contract>) AccountPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    accountLoginOrEditPanel.setDefaultModelObject(shopperDataProvider.find(new Shopper()).getContract());
    if (!shopperDataProvider.find(new Shopper()).isLoggedIn()) {
      add(accountLoginOrEditPanel.add(accountLoginOrEditPanel.new AccountLoginFragment()).setOutputMarkupId(true));
    } else {
      add(accountLoginOrEditPanel.add(accountLoginOrEditPanel.new AccountEditFragment()).setOutputMarkupId(true));
    }
    super.onInitialize();
  }
}
