package br.com.netbrasoft.gnuob.shop.page;

import static br.com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.CONTRACT_DATA_PROVIDER_NAME;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SHOPPER_DATA_PROVIDER_NAME;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.filter.FilteredHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import br.com.netbrasoft.gnuob.api.Contract;
import br.com.netbrasoft.gnuob.api.OrderBy;
import br.com.netbrasoft.gnuob.api.generic.GNUOpenBusinessApplicationException;
import br.com.netbrasoft.gnuob.api.generic.IGenericTypeDataProvider;
import br.com.netbrasoft.gnuob.shop.authentication.OAuthUtils;
import br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.security.ShopRoles;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;
import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.references.JQueryCookieJsReference;

@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public abstract class BasePage extends WebPage implements IAjaxIndicatorAware {

  class NetbrasoftApplicationJavaScript extends JavaScriptResourceReference {

    private static final String BOOTSTRAP_CONFIRMATION_JS_NAME = "bootstrap-confirmation.js";

    private static final long serialVersionUID = 62421909883685410L;

    private NetbrasoftApplicationJavaScript() {
      super(ConfirmationBehavior.class, BOOTSTRAP_CONFIRMATION_JS_NAME);
    }

    @Override
    public List<HeaderItem> getDependencies() {
      final List<HeaderItem> dependencies = Lists.newArrayList(super.getDependencies());
      dependencies.add(JavaScriptHeaderItem.forReference(JQueryCookieJsReference.INSTANCE));
      dependencies.add(
          JavaScriptHeaderItem.forReference(WebApplication.get().getJavaScriptLibrarySettings().getJQueryReference()));
      dependencies.add(JavaScriptHeaderItem.forReference(Bootstrap.getSettings().getJsResourceReference()));
      return dependencies;
    }
  }

  private static final String NETBRASOFT_SHOPPING_JAVASCRIPT_CONTAINER_ID = "netbrasoft-shopping-javascript-container";

  private static final long serialVersionUID = 8192334293970678397L;

  private static final String GNUOB_SITE_TITLE_PROPERTY = "gnuob.site.title";

  private static final String VEIL_HEX_LOADING = "veil-hex-loading";

  private static final Logger LOGGER = LoggerFactory.getLogger(BasePage.class);

  @SpringBean(name = SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  @SpringBean(name = CONTRACT_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Contract> contractDataProvider;

  private void authenticateShopper() {
    final Shopper shopper = shopperDataProvider.find(new Shopper());
    if (shopper.login()) {
      try {
        getShopperContractFromUserInfo(shopper);
        shopperDataProvider.merge(shopper);
      } catch (GNUOpenBusinessApplicationException | URISyntaxException e) {
        LOGGER.warn(e.getMessage(), e);
      }
      final URI redirectURI = URI.create(
          System.getProperty("gnuob." + AppServletContainerAuthenticatedWebSession.getSite() + ".login.redirect"));
      throw new RedirectToUrlException(redirectURI.toString());
    }
  }

  private void configureActiveTheme() {
    final String site = getRequest().getClientUrl().getHost();
    final String title = site.replaceFirst("www.", "").split("\\.")[0].replace("-", "");
    final IBootstrapSettings settings = Bootstrap.getSettings(getApplication());
    settings.getActiveThemeProvider().setActiveTheme(WordUtils.capitalize(title));
  }

  @Override
  public String getAjaxIndicatorMarkupId() {
    return VEIL_HEX_LOADING;
  }

  private void getShopperContractFromUserInfo(final Shopper shopper) throws URISyntaxException {
    final UserInfo userInfo = getUserInfo(shopper);
    shopper.logout();
    shopper.setLoggedIn(true);
    shopper.getContract().setContractId(userInfo.getEmail().toString());
    shopper.getContract().getCustomer().setBuyerEmail(userInfo.getEmail().toString());
    shopper.getContract().getCustomer().setFirstName(userInfo.getGivenName());
    shopper.getContract().getCustomer().setLastName(userInfo.getFamilyName());
    shopper.getContract().getCustomer().setFriendlyName(userInfo.getName());
    saveOrLoadShopperContract(shopper);
  }

  private UserInfo getUserInfo(final Shopper shopper) throws URISyntaxException {
    final URI issuerURI = new URI(shopper.getIssuer());
    final ClientID clientID = OAuthUtils.getClientID(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
    final State state = new State(shopper.getId());
    final URI requestURI = URI.create(getRequest().getClientUrl().toString());
    final URI redirectURI = URI.create(
        System.getProperty("gnuob." + AppServletContainerAuthenticatedWebSession.getSite() + ".login.redirect"));
    final OIDCProviderMetadata providerConfiguration = OAuthUtils.getOIDCProviderMetaData(issuerURI);
    return OAuthUtils.getUserInfo(providerConfiguration, clientID, state, requestURI, redirectURI,
        OAuthUtils.getSecret(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI));
  }

  private void initializeContractDataProvider() {
    contractDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    contractDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    contractDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    contractDataProvider.setType(new Contract());
    contractDataProvider.getType().setActive(true);
    contractDataProvider.setOrderBy(OrderBy.NONE);
  }

  private void initializePageTitle() {
    final String site = getRequest().getClientUrl().getHost();
    final String title = site.replaceFirst("www.", "").split("\\.")[0];
    add(new Label(GNUOB_SITE_TITLE_PROPERTY,
        System.getProperty(GNUOB_SITE_TITLE_PROPERTY, WordUtils.capitalize(title))));
  }

  @Override
  protected void onConfigure() {
    configureActiveTheme();
    super.onConfigure();
  }

  @Override
  protected void onInitialize() {
    initializeContractDataProvider();
    initializePageTitle();
    add(new HeaderResponseContainer(NETBRASOFT_SHOPPING_JAVASCRIPT_CONTAINER_ID,
        NETBRASOFT_SHOPPING_JAVASCRIPT_CONTAINER_ID));
    authenticateShopper();
    super.onInitialize();
  }

  @Override
  public void renderHead(final IHeaderResponse response) {
    response.render(new FilteredHeaderItem(JavaScriptHeaderItem.forReference(new NetbrasoftApplicationJavaScript()),
        NETBRASOFT_SHOPPING_JAVASCRIPT_CONTAINER_ID));
    super.renderHead(response);
  }

  private void saveOrLoadShopperContract(final Shopper shopper) {
    contractDataProvider.setType(shopper.getContract());
    if (contractDataProvider.size() > 0) {
      final Iterator<? extends Contract> iterator = contractDataProvider.iterator(0, 1);
      shopper.setContract(iterator.next());
    }
  }
}
