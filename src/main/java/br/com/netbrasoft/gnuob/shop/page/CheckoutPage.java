package br.com.netbrasoft.gnuob.shop.page;

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SHOPPER_DATA_PROVIDER_NAME;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import br.com.netbrasoft.gnuob.shop.border.ContentBorder;
import br.com.netbrasoft.gnuob.shop.checkout.CheckoutMainMenuPanel;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.security.ShopRoles;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;

@MountPath(CheckoutPage.CHECKOUT_HTML_VALUE)
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CheckoutPage extends BasePage {

  private static final String CONTENT_BORDER_ID = "contentBorder";

  public static final String CHECKOUT_HTML_VALUE = "checkout.html";

  private static final String MAIN_MENU_PANEL_ID = "mainMenuPanel";

  private static final long serialVersionUID = 4051343927877779621L;

  private final CheckoutMainMenuPanel mainMenuPanel;

  private final ContentBorder contentBorder;

  @SpringBean(name = SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  public CheckoutPage() {
    mainMenuPanel = new CheckoutMainMenuPanel(MAIN_MENU_PANEL_ID);
    contentBorder = new ContentBorder(CONTENT_BORDER_ID, Model.of(new Shopper()));
  }

  @Override
  protected void onInitialize() {
    if (!shopperDataProvider.find(new Shopper()).isLoggedIn()) {
      throw new RedirectToUrlException(AccountPage.ACCOUNT_HTML_VALUE);
    }
    contentBorder.setDefaultModelObject(shopperDataProvider.find(new Shopper()));
    contentBorder.add(mainMenuPanel);
    add(contentBorder);
    super.onInitialize();
  }
}
