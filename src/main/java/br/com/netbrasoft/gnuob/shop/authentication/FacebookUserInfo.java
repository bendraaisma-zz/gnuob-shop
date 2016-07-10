package br.com.netbrasoft.gnuob.shop.authentication;

import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import net.minidev.json.JSONObject;

public class FacebookUserInfo extends UserInfo {

  public FacebookUserInfo(final JSONObject contentAsJSONObject) {
    super(contentAsJSONObject);
  }

  @Override
  public String getFamilyName() {
    return getStringClaim("last_name");
  }

  @Override
  public String getGivenName() {
    return getStringClaim("first_name");
  }
}
