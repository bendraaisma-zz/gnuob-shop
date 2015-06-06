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

import com.netbrasoft.gnuob.api.generic.GNUOpenBusinessApplicationException;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class AuthorizationPanel extends Panel {

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class GoogleAjaxLink extends AjaxLink<String> {

      private static final long serialVersionUID = -8317730269644885290L;

      public GoogleAjaxLink() {
         super("google");
      }

      @Override
      public void onClick(AjaxRequestTarget target) {
         try {
            Shopper shopper = shopperDataProvider.find(new Shopper());
            shopper.setIssuer("https://accounts.google.com/");

            URI issuerURI = new URI(shopper.getIssuer());
            ClientID clientID = OAuthUtils.getClientID(issuerURI);
            State state = new State(shopper.getId());
            URI redirectURI = URI.create(getRequestCycle().getUrlRenderer().renderFullUrl(getRequest().getClientUrl()).split("\\?")[0]);

            shopperDataProvider.merge(shopper);

            throw new RedirectToUrlException(OAuthUtils.getAuthenticationRequest(issuerURI, clientID, redirectURI, state).toURI().toString());
         } catch (GNUOpenBusinessApplicationException | URISyntaxException | SerializeException e) {

         }
      }
   }

   private static final long serialVersionUID = -7007737558968816459L;

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   public AuthorizationPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
      add(new GoogleAjaxLink());
      super.onInitialize();
   }
}
