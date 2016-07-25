/*
 * Copyright 2016 Netbrasoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package br.com.netbrasoft.gnuob.shop.authentication;

import java.net.URI;

import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;

public class FacebookAuthenticationRequest extends AuthorizationRequest {

  public FacebookAuthenticationRequest(final URI uri, final ResponseType rt, final ResponseMode rm,
      final ClientID clientID, final URI redirectURI, final Scope scope, final State state) {
    this(uri, rt, scope, clientID, redirectURI, state);
  }

  public FacebookAuthenticationRequest(final URI uri, final ResponseType rt, final Scope scope, final ClientID clientID,
      final URI redirectURI, final State state) {
    super(uri, rt, null, clientID, redirectURI, scope, state);
  }
}
