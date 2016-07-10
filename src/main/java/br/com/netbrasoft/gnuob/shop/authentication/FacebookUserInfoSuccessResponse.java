package br.com.netbrasoft.gnuob.shop.authentication;

import javax.mail.internet.ContentType;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.CommonContentTypes;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import net.minidev.json.JSONObject;

public class FacebookUserInfoSuccessResponse extends UserInfoSuccessResponse {

  public static FacebookUserInfoSuccessResponse parse(final HTTPResponse httpResponse) throws ParseException {
    httpResponse.ensureStatusCode(HTTPResponse.SC_OK);
    httpResponse.ensureContentType();
    final ContentType ct = httpResponse.getContentType();
    FacebookUserInfoSuccessResponse response;
    if (ct.match(CommonContentTypes.APPLICATION_JSON)) {
      FacebookUserInfo claimsSet;
      try {
        final JSONObject jsonObject = httpResponse.getContentAsJSONObject();
        jsonObject.put(UserInfo.SUB_CLAIM_NAME, JSONObjectUtils.getString(jsonObject, "id"));
        claimsSet = new FacebookUserInfo(jsonObject);
      } catch (final Exception e) {
        throw new ParseException("Couldn't parse UserInfo claims: " + e.getMessage(), e);
      }
      response = new FacebookUserInfoSuccessResponse(claimsSet);
    } else {
      if (ct.match(CommonContentTypes.APPLICATION_JWT)) {
        JWT jwt;
        try {
          jwt = httpResponse.getContentAsJWT();
        } catch (final ParseException e) {
          throw new ParseException("Couldn't parse UserInfo claims JWT: " + e.getMessage(), e);
        }
        response = new FacebookUserInfoSuccessResponse(jwt);
      } else {
        throw new ParseException("Unexpected Content-Type, must be " + CommonContentTypes.APPLICATION_JSON + " or " + CommonContentTypes.APPLICATION_JWT);
      }
    }
    return response;
  }

  public FacebookUserInfoSuccessResponse(final JWT jwt) {
    super(jwt);
  }

  public FacebookUserInfoSuccessResponse(final UserInfo claimsSet) {
    super(claimsSet);
  }
}
