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

import static com.nimbusds.oauth2.sdk.http.CommonContentTypes.APPLICATION_JSON;
import static com.nimbusds.oauth2.sdk.http.CommonContentTypes.APPLICATION_JWT;
import static com.nimbusds.oauth2.sdk.http.HTTPResponse.SC_OK;
import static com.nimbusds.oauth2.sdk.util.JSONObjectUtils.getString;
import static com.nimbusds.openid.connect.sdk.claims.UserInfo.SUB_CLAIM_NAME;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import net.minidev.json.JSONObject;

public class PayPalUserInfoSuccessResponse extends UserInfoSuccessResponse {

  public PayPalUserInfoSuccessResponse(final JWT jwt) {
    super(jwt);
  }

  public PayPalUserInfoSuccessResponse(final UserInfo claimsSet) {
    super(claimsSet);
  }

  public static PayPalUserInfoSuccessResponse parse(final HTTPResponse httpResponse) throws ParseException {
    httpResponse.ensureStatusCode(SC_OK);
    httpResponse.ensureContentType();
    if (httpResponse.getContentType().match(APPLICATION_JSON)) {
      try {
        return getPayPalUserInfoSuccessResponseFromJsonObject(httpResponse.getContentAsJSONObject());
      } catch (final Exception e) {
        throw new ParseException("Couldn't parse UserInfo claims: " + e.getMessage(), e);
      }
    } else {
      if (httpResponse.getContentType().match(APPLICATION_JWT)) {
        try {
          return getPayPalUserInfoSuccessResponseJWTObject(httpResponse.getContentAsJWT());
        } catch (final ParseException e) {
          throw new ParseException("Couldn't parse UserInfo claims JWT: " + e.getMessage(), e);
        }
      } else {
        throw new ParseException("Unexpected Content-Type, must be " + APPLICATION_JSON + " or " + APPLICATION_JWT);
      }
    }
  }

  private static PayPalUserInfoSuccessResponse getPayPalUserInfoSuccessResponseFromJsonObject(
      final JSONObject jsonObject) throws ParseException {
    jsonObject.put(SUB_CLAIM_NAME, getString(jsonObject, "id"));
    return new PayPalUserInfoSuccessResponse(new UserInfo(jsonObject));
  }

  private static PayPalUserInfoSuccessResponse getPayPalUserInfoSuccessResponseJWTObject(final JWT jwtObject)
      throws ParseException {
    return new PayPalUserInfoSuccessResponse(jwtObject);
  }
}
