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

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CLIENT_SECRET;
import static com.nimbusds.oauth2.sdk.util.URLUtils.serializeParameters;

import java.net.URI;
import java.util.Map;

import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;

public class SecretTokenRequest extends TokenRequest {

  private final Secret secret;

  public SecretTokenRequest(final URI uri, final ClientID clientID, final Secret secret,
      final AuthorizationGrant authzGrant) {
    super(uri, clientID, authzGrant);
    this.secret = secret;
  }

  @Override
  public HTTPRequest toHTTPRequest() throws SerializeException {
    final HTTPRequest httpRequest = super.toHTTPRequest();
    httpRequest.setQuery(serializeParameters(addSecretParams(httpRequest.getQueryParameters())));
    return httpRequest;
  }

  private Map<String, String> addSecretParams(final Map<String, String> params) {
    if (secret != null) {
      params.put(CLIENT_SECRET, secret.getValue());
    }
    return params;
  }
}
