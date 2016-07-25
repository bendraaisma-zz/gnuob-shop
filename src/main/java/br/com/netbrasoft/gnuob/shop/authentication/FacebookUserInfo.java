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

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.FIRST_NAME;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.LAST_NAME;

import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import net.minidev.json.JSONObject;

public class FacebookUserInfo extends UserInfo {

  public FacebookUserInfo(final JSONObject contentAsJSONObject) {
    super(contentAsJSONObject);
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
