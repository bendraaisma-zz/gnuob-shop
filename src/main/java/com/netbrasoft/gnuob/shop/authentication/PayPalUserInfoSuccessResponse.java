package com.netbrasoft.gnuob.shop.authentication;

import javax.mail.internet.ContentType;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.CommonContentTypes;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import net.minidev.json.JSONObject;

public class PayPalUserInfoSuccessResponse extends UserInfoSuccessResponse {

  public static PayPalUserInfoSuccessResponse parse(final HTTPResponse httpResponse) throws ParseException {
    httpResponse.ensureStatusCode(HTTPResponse.SC_OK);

    httpResponse.ensureContentType();

    final ContentType ct = httpResponse.getContentType();

    PayPalUserInfoSuccessResponse response;

    if (ct.match(CommonContentTypes.APPLICATION_JSON)) {

      UserInfo claimsSet;

      try {
        final JSONObject jsonObject = httpResponse.getContentAsJSONObject();
        jsonObject.put(UserInfo.SUB_CLAIM_NAME, JSONObjectUtils.getString(jsonObject, "user_id"));
        claimsSet = new UserInfo(jsonObject);

      } catch (final Exception e) {

        throw new ParseException("Couldn't parse UserInfo claims: " + e.getMessage(), e);
      }

      response = new PayPalUserInfoSuccessResponse(claimsSet);
    } else if (ct.match(CommonContentTypes.APPLICATION_JWT)) {

      JWT jwt;

      try {
        jwt = httpResponse.getContentAsJWT();

      } catch (final ParseException e) {

        throw new ParseException("Couldn't parse UserInfo claims JWT: " + e.getMessage(), e);
      }

      response = new PayPalUserInfoSuccessResponse(jwt);
    } else {
      throw new ParseException("Unexpected Content-Type, must be " + CommonContentTypes.APPLICATION_JSON + " or " + CommonContentTypes.APPLICATION_JWT);
    }

    return response;
  }

  public PayPalUserInfoSuccessResponse(JWT jwt) {
    super(jwt);
  }

  public PayPalUserInfoSuccessResponse(UserInfo claimsSet) {
    super(claimsSet);
  }
}
