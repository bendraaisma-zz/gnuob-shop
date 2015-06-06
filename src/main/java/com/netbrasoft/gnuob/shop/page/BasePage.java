package com.netbrasoft.gnuob.shop.page;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.wicket.markup.head.CssContentHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.Contract;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.generic.GNUOpenBusinessApplicationException;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.NetbrasoftShop;
import com.netbrasoft.gnuob.shop.authentication.OAuthUtils;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.shopper.Shopper;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

public abstract class BasePage extends WebPage {

   private static final long serialVersionUID = 8192334293970678397L;

   private static final String GNUOB_SITE_TITLE_PROPERTY = "gnuob.site.title";

   private static final JavaScriptReferenceHeaderItem JS_VALIDATOR_REFERENCE = JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("/ajax/libs/bootstrap-validator/0.4.5/js/bootstrapvalidator.min.js"));

   private static final JavaScriptReferenceHeaderItem JS_JQUERY_COOKIE = JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("/ajax/libs/jquery-cookie/1.4.1/jquery.cookie.min.js"));

   private static final JavaScriptReferenceHeaderItem JS_BOOTSTRAP_3_DATEPICKER = JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("/ajax/libs/bootstrap-datepicker/1.4.0/js/bootstrap-datepicker.min.js"));

   private static final CssReferenceHeaderItem CSS_BOOTSTRAP_3_DATEPICKER = CssContentHeaderItem.forReference(new WebjarsCssResourceReference("/ajax/libs/bootstrap-datepicker/1.4.0/css/bootstrap-datepicker.min.css"));

   private static final JavaScriptReferenceHeaderItem JS_JQUERY = JavaScriptHeaderItem.forReference(NetbrasoftShop.get().getJavaScriptLibrarySettings().getJQueryReference());

   private static final Logger LOGGER = LoggerFactory.getLogger(BasePage.class);

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   @SpringBean(name = "ContractDataProvider", required = true)
   private GenericTypeDataProvider<Contract> contractDataProvider;

   private void authenticateShopper() {
      Shopper shopper = shopperDataProvider.find(new Shopper());

      if (shopper.login()) {
         try {
            getShopperContractFromUserInfo(shopper);

            shopperDataProvider.merge(shopper);
         } catch (GNUOpenBusinessApplicationException | URISyntaxException e) {
            LOGGER.warn(e.getMessage(), e);
         }

         URI redirectURI = URI.create(getRequestCycle().getUrlRenderer().renderFullUrl(getRequest().getClientUrl()).split("\\?")[0]);
         throw new RedirectToUrlException(redirectURI.toString());
      }
   }

   private void getShopperContractFromUserInfo(Shopper shopper) throws URISyntaxException {
      UserInfo userInfo = getUserInfo(shopper);

      shopper.logout();
      shopper.getContract().setContractId(userInfo.getEmail().toString());
      shopper.getContract().getCustomer().setBuyerEmail(userInfo.getEmail().toString());
      shopper.getContract().getCustomer().setFirstName(userInfo.getGivenName());
      shopper.getContract().getCustomer().setLastName(userInfo.getFamilyName());
      shopper.getContract().getCustomer().setFriendlyName(userInfo.getName());

      saveOrLoadShopperContract(shopper);
   }

   private UserInfo getUserInfo(Shopper shopper) throws URISyntaxException {
      URI issuerURI = new URI(shopper.getIssuer());
      ClientID clientID = OAuthUtils.getClientID(issuerURI);
      State state = new State(shopper.getId());
      URI requestURI = URI.create(getRequest().getClientUrl().toString());
      URI redirectURI = URI.create(getRequestCycle().getUrlRenderer().renderFullUrl(getRequest().getClientUrl()).split("\\?")[0]);
      return OAuthUtils.getUserInfo(issuerURI, clientID, state, requestURI, redirectURI, OAuthUtils.getClientSecret(issuerURI));
   }

   @Override
   protected void onInitialize() {
      contractDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
      contractDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
      contractDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
      contractDataProvider.setType(new Contract());
      contractDataProvider.getType().setActive(true);
      contractDataProvider.setOrderBy(OrderBy.NONE);

      String site = getRequest().getClientUrl().getHost();
      String title = site.replaceFirst("www.", "").split("\\.")[0];

      add(new Label(GNUOB_SITE_TITLE_PROPERTY, System.getProperty(GNUOB_SITE_TITLE_PROPERTY, WordUtils.capitalize(title))));

      authenticateShopper();
      super.onInitialize();
   }

   @Override
   public void renderHead(IHeaderResponse response) {
      response.render(JS_JQUERY);
      response.render(JS_VALIDATOR_REFERENCE);
      response.render(JS_JQUERY_COOKIE);
      response.render(JS_BOOTSTRAP_3_DATEPICKER);
      response.render(CSS_BOOTSTRAP_3_DATEPICKER);

      super.renderHead(response);
   }

   private void saveOrLoadShopperContract(Shopper shopper) {
      contractDataProvider.setType(shopper.getContract());

      @SuppressWarnings("unchecked")
      Iterator<Contract> iterator = (Iterator<Contract>) contractDataProvider.iterator(0, 1);

      if (iterator.hasNext()) {
         shopper.setContract(iterator.next());
      } else {
         shopper.setContract(contractDataProvider.persist(shopper.getContract()));
      }
   }
}