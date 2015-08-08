package com.netbrasoft.gnuob.shop.authentication;

import java.net.URI;

import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.Nonce;

public class FacebookAuthenticationRequest extends AuthorizationRequest {

   public FacebookAuthenticationRequest(URI uri, ResponseType rt, ResponseMode rm, ClientID clientID, URI redirectURI, Scope scope, State state) {
      this(uri, rt, scope, clientID, redirectURI, state, null);
   }

   public FacebookAuthenticationRequest(URI uri, ResponseType rt, Scope scope, ClientID clientID, URI redirectURI, State state, Nonce nonce) {
      super(uri, rt, null, clientID, redirectURI, scope, state);
   }
}
