package br.com.netbrasoft.gnuob.shop.authentication;

import java.net.URI;

import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;

public class FacebookAuthenticationRequest extends AuthorizationRequest {

  public FacebookAuthenticationRequest(final URI uri, final ResponseType rt, final ResponseMode rm, final ClientID clientID, final URI redirectURI, final Scope scope,
      final State state) {
    this(uri, rt, scope, clientID, redirectURI, state);
  }

  public FacebookAuthenticationRequest(final URI uri, final ResponseType rt, final Scope scope, final ClientID clientID, final URI redirectURI, final State state) {
    super(uri, rt, null, clientID, redirectURI, scope, state);
  }
}
