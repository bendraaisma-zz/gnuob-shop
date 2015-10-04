package com.netbrasoft.gnuob.shop.authentication;

import javax.mail.internet.InternetAddress;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import net.minidev.json.JSONObject;

public class MicrosoftUserInfo extends UserInfo {

  public MicrosoftUserInfo(JSONObject contentAsJSONObject) {
    super(contentAsJSONObject);
  }

  @Override
  public InternetAddress getEmail() {
    try {
      final JSONObject jsonObject = JSONObjectUtils.getJSONObject(toJSONObject(), "emails");
      return JSONObjectUtils.getEmail(jsonObject, "account");
    } catch (final ParseException e) {
      return null;
    }
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
