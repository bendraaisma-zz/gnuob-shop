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

import static com.nimbusds.oauth2.sdk.http.HTTPResponse.SC_OK;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;

public abstract class MicrosoftUserInfoResponse extends UserInfoResponse {

  public static UserInfoResponse parse(final HTTPResponse httpResponse) throws ParseException {
    return SC_OK == httpResponse.getStatusCode() ? MicrosoftUserInfoSuccessResponse.parse(httpResponse)
        : UserInfoErrorResponse.parse(httpResponse);
  }
}
