package br.com.netbrasoft.gnuob.shop.panel;

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SHOPPER_DATA_PROVIDER_NAME;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants;
import br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.page.AccountPage;
import br.com.netbrasoft.gnuob.shop.security.ShopRoles;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;
import de.agilecoders.wicket.core.markup.html.bootstrap.block.BadgeBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class HeaderPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class LoginLogoutAjaxLink extends BootstrapAjaxLink<Shopper> {

    private static final long serialVersionUID = 5723161748885398672L;

    public LoginLogoutAjaxLink(final String id, final IModel<Shopper> model, final Type type,
        final IModel<String> labelModel) {
      super(id, model, type, labelModel);
      setSize(Buttons.Size.Small);
    }

    @Override
    public void onClick(final AjaxRequestTarget target) {
      final Shopper shopper = LoginLogoutAjaxLink.this.getModelObject();
      if (shopper.isLoggedIn()) {
        shopper.logout();
        shopperDataProvider.merge(shopper);
        ((AppServletContainerAuthenticatedWebSession) getSession()).signOut();
        ((AppServletContainerAuthenticatedWebSession) getSession()).invalidate();
      }
      throw new RedirectToUrlException(AccountPage.ACCOUNT_HTML_VALUE);
    }
  }

  private static final String LOGIN_LOGOUT_ID = "loginLogout";

  private static final String CART_SIZE_ID = "cartSize";

  private static final long serialVersionUID = 3137234732197409313L;

  private static final String GNUOB_SITE_TITLE_PROPERTY = "gnuob.shop.site.title";

  private static final String GNUOB_SITE_SUBTITLE_PROPERTY = "gnuob.shop.site.subtitle";

  @SpringBean(name = SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  private final Label titleLabel;

  private final Label subTitleLabel;

  private final Label cartSizeLabel;

  private final LoginLogoutAjaxLink loginAjaxLink;

  private final LoginLogoutAjaxLink logoutAjaxLink;

  public HeaderPanel(final String id, final IModel<Shopper> model) {
    super(id, model);
    titleLabel = new Label(GNUOB_SITE_TITLE_PROPERTY);
    subTitleLabel = new Label(GNUOB_SITE_SUBTITLE_PROPERTY);
    cartSizeLabel = new Label(CART_SIZE_ID);
    loginAjaxLink = new LoginLogoutAjaxLink(LOGIN_LOGOUT_ID, (IModel<Shopper>) HeaderPanel.this.getDefaultModel(),
        Type.Menu, Model.of(HeaderPanel.this.getString(NetbrasoftShopConstants.LOGIN_MESSAGE_KEY)));
    logoutAjaxLink = new LoginLogoutAjaxLink(LOGIN_LOGOUT_ID, (IModel<Shopper>) HeaderPanel.this.getDefaultModel(),
        Type.Menu, Model.of(HeaderPanel.this.getString(NetbrasoftShopConstants.LOGOUT_MESSAGE_KEY)));
  }

  @Override
  protected void onInitialize() {
    final String site = getRequest().getClientUrl().getHost();
    final String title = site.replaceFirst("www.", "").split("\\.")[0];
    final String subTitle = site.replaceFirst("www.", "").replaceFirst(title, "");
    titleLabel.setDefaultModel(Model.of(System.getProperty(GNUOB_SITE_TITLE_PROPERTY, WordUtils.capitalize(title))));
    subTitleLabel.setDefaultModel(Model.of(System.getProperty(GNUOB_SITE_SUBTITLE_PROPERTY, subTitle)));
    cartSizeLabel.setDefaultModel(Model.of(shopperDataProvider.find(new Shopper()).getCart().getRecords().size()));
    add(titleLabel.setOutputMarkupId(true));
    add(subTitleLabel.setOutputMarkupId(true));
    add(cartSizeLabel.add(new BadgeBehavior()).setOutputMarkupId(true));
    if (!((Shopper) HeaderPanel.this.getDefaultModelObject()).isLoggedIn()) {
      add(loginAjaxLink.setOutputMarkupId(true));
    } else {
      add(logoutAjaxLink.setOutputMarkupId(true));
    }
    super.onInitialize();
  }
}
