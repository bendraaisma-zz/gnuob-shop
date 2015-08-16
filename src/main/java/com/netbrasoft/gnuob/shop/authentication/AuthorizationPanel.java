package com.netbrasoft.gnuob.shop.authentication;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.generic.GNUOpenBusinessApplicationException;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class AuthorizationPanel extends Panel {

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class FacebookAjaxLink extends AjaxLink<String> {

      private static final long serialVersionUID = -8317730269644885290L;

      public FacebookAjaxLink() {
         super("facebook");
      }

      @Override
      public void onClick(AjaxRequestTarget target) {
         try {
            final Shopper shopper = shopperDataProvider.find(new Shopper());
            shopper.setIssuer(OAuthUtils.ACCOUNTS_FACEBOOK_COM);

            final URI issuerURI = new URI(shopper.getIssuer());
            final ClientID clientID = OAuthUtils.getClientID(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
            final State state = new State(shopper.getId());
            final URI redirectURI = URI.create(System.getProperty("gnuob." + AppServletContainerAuthenticatedWebSession.getSite() + ".login.redirect"));
            final Scope scope = OAuthUtils.getScope(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
            final OIDCProviderMetadata providerConfiguration = OAuthUtils.getProviderConfigurationURL(issuerURI);

            shopperDataProvider.merge(shopper);

            throw new RedirectToUrlException(OAuthUtils.getFacebookAuthenticationRequest(providerConfiguration, clientID, redirectURI, scope, state).toURI().toString());
         } catch (GNUOpenBusinessApplicationException | URISyntaxException | SerializeException e) {
            LOGGER.warn("OAuth Exception with Facebook.",e);
         }
      }
   }

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class GoogleAjaxLink extends AjaxLink<String> {

      private static final long serialVersionUID = -8317730269644885290L;

      public GoogleAjaxLink() {
         super("google");
      }

      @Override
      public void onClick(AjaxRequestTarget target) {
         try {
            final Shopper shopper = shopperDataProvider.find(new Shopper());
            shopper.setIssuer(OAuthUtils.ACCOUNTS_GOOGLE_COM);

            final URI issuerURI = new URI(shopper.getIssuer());
            final ClientID clientID = OAuthUtils.getClientID(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
            final State state = new State(shopper.getId());
            final URI redirectURI = URI.create(System.getProperty("gnuob." + AppServletContainerAuthenticatedWebSession.getSite() + ".login.redirect"));
            final Scope scope = OAuthUtils.getScope(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
            final OIDCProviderMetadata providerConfiguration = OAuthUtils.getProviderConfigurationURL(issuerURI);

            shopperDataProvider.merge(shopper);

            throw new RedirectToUrlException(OAuthUtils.getAuthenticationRequest(providerConfiguration, clientID, redirectURI, scope, state).toURI().toString());
         } catch (GNUOpenBusinessApplicationException | URISyntaxException | SerializeException e) {
            LOGGER.warn("OAuth Exception with Google.",e);
         }
      }
   }

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class MicrosoftAjaxLink extends AjaxLink<String> {

      private static final long serialVersionUID = -8317730269644885290L;

      public MicrosoftAjaxLink() {
         super("microsoft");
      }

      @Override
      public void onClick(AjaxRequestTarget target) {
         try {
            final Shopper shopper = shopperDataProvider.find(new Shopper());
            shopper.setIssuer(OAuthUtils.ACCOUNTS_MICROSOFT_COM);

            final URI issuerURI = new URI(shopper.getIssuer());
            final ClientID clientID = OAuthUtils.getClientID(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
            final State state = new State(shopper.getId());
            final URI redirectURI = URI.create(System.getProperty("gnuob." + AppServletContainerAuthenticatedWebSession.getSite() + ".login.redirect"));
            final Scope scope = OAuthUtils.getScope(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
            final OIDCProviderMetadata providerConfiguration = OAuthUtils.getProviderConfigurationURL(issuerURI);

            shopperDataProvider.merge(shopper);

            throw new RedirectToUrlException(OAuthUtils.getMicrosoftAuthenticationRequest(providerConfiguration, clientID, redirectURI, scope, state).toURI().toString());
         } catch (GNUOpenBusinessApplicationException | URISyntaxException | SerializeException e) {
            LOGGER.warn("OAuth Exception with Microsoft.",e);
         }
      }
   }

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class PayPalAjaxLink extends AjaxLink<String> {

      private static final long serialVersionUID = -8317730269644885290L;

      public PayPalAjaxLink() {
         super("paypal");
      }

      @Override
      public void onClick(AjaxRequestTarget target) {
         try {
            final Shopper shopper = shopperDataProvider.find(new Shopper());
            shopper.setIssuer(OAuthUtils.ACCOUNTS_PAY_PAL_COM);

            final URI issuerURI = new URI(shopper.getIssuer());
            final ClientID clientID = OAuthUtils.getClientID(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
            final State state = new State(shopper.getId());
            final URI redirectURI = URI.create(System.getProperty("gnuob." + AppServletContainerAuthenticatedWebSession.getSite() + ".login.redirect"));
            final Scope scope = OAuthUtils.getScope(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
            final OIDCProviderMetadata providerConfiguration = OAuthUtils.getProviderConfigurationURL(issuerURI);

            shopperDataProvider.merge(shopper);

            throw new RedirectToUrlException(OAuthUtils.getAuthenticationRequest(providerConfiguration, clientID, redirectURI, scope, state).toURI().toString());
         } catch (GNUOpenBusinessApplicationException | URISyntaxException | SerializeException e) {
            LOGGER.warn("OAuth Exception with PayPal.",e);
         }
      }
   }

   private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationPanel.class);

   private static final long serialVersionUID = -7007737558968816459L;

   @SpringBean(name = "ShopperDataProvider", required = true)
   private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   public AuthorizationPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
      add(new GoogleAjaxLink());
      add(new FacebookAjaxLink());
      add(new PayPalAjaxLink());
      add(new MicrosoftAjaxLink());
      super.onInitialize();
   }
}
