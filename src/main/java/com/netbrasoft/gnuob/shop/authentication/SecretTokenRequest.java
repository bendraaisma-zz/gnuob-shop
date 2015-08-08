package com.netbrasoft.gnuob.shop.authentication;

import java.net.URI;
import java.util.Map;

import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.util.URLUtils;

public class SecretTokenRequest extends TokenRequest {

   /**
    * The Secret or password..
    */
   private final Secret secret;

   public SecretTokenRequest(URI uri, ClientID clientID, Secret secret, AuthorizationGrant authzGrant) {
      super(uri, clientID, authzGrant);

      this.secret = secret;
   }

   @Override
   public HTTPRequest toHTTPRequest() throws SerializeException {
      final HTTPRequest httpRequest = super.toHTTPRequest();

      final Map<String,String> params = httpRequest.getQueryParameters();

      if (secret != null) {
         params.put("client_secret", secret.getValue());
      }

      httpRequest.setQuery(URLUtils.serializeParameters(params));

      return httpRequest;
   }

}
