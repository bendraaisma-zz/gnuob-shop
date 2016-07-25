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

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNT;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.EMAILS;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.FIRST_NAME;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.LAST_NAME;
import static org.slf4j.LoggerFactory.getLogger;

import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import net.minidev.json.JSONObject;

public class MicrosoftUserInfo extends UserInfo {

  private static final Logger LOGGER = getLogger(MicrosoftUserInfo.class);

  public MicrosoftUserInfo(final JSONObject contentAsJSONObject) {
    super(contentAsJSONObject);
  }

  @Override
  public InternetAddress getEmail() {
    try {
      return JSONObjectUtils.getEmail(JSONObjectUtils.getJSONObject(toJSONObject(), EMAILS), ACCOUNT);
    } catch (final ParseException e) {
      LOGGER.warn("Could not parse email address from Microsoft User Info", e);
      return null;
    }
  }

  @Override
  public String getFamilyName() {
    return getStringClaim(LAST_NAME);
  }

  @Override
  public String getGivenName() {
    return getStringClaim(FIRST_NAME);
  }
}
