package br.com.netbrasoft.gnuob.shop.authentication;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;

public abstract class PayPalUserInfoResponse extends UserInfoResponse {

  public static UserInfoResponse parse(final HTTPResponse httpResponse) throws ParseException {

    if (httpResponse.getStatusCode() == HTTPResponse.SC_OK) {
      return PayPalUserInfoSuccessResponse.parse(httpResponse);
    } else {
      return UserInfoErrorResponse.parse(httpResponse);
    }
  }
}
