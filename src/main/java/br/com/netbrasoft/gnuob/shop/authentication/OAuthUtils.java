package br.com.netbrasoft.gnuob.shop.authentication;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import br.com.netbrasoft.gnuob.api.generic.GNUOpenBusinessApplicationException;

public final class OAuthUtils {

  private static final String GOOGLE_SCOPE_PREFIX_PROPERTY = ".google.scope";

  private static final String MICROSOFT_SCOPE_PREFIX_PROPERTY = ".microsoft.scope";

  private static final String PAYPAL_SCOPE_PREFIX_PROPERTY = ".paypal.scope";

  private static final String FACEBOOK_SCOPE_PREFIX_PROPERTY = ".facebook.scope";

  private static final String GOOGLE_CLIENT_SECRET_PREFIX_PROPERTY = ".google.clientSecret";

  private static final String MICROSOFT_CLIENT_SECRET_PREFIX_PROPERTY = ".microsoft.clientSecret";

  private static final String PAYPAL_CLIENT_SECRET_PREFIX_PROPERTY = ".paypal.clientSecret";

  private static final String FACEBOOK_CLIENT_SECRET_PREFIX_PROPERTY = ".facebook.clientSecret";

  private static final String GOOGLE_CLIENT_ID_PREFIX_PROPERTY = ".google.clientId";

  private static final String MICROSOFT_CLIENT_ID_PREFIX_PROPERTY = ".microsoft.clientId";

  private static final String PAYPAL_CLIENT_ID_PREFIX_PROPERTY = ".paypal.clientId";

  private static final String FACEBOOK_CLIENT_ID_PREFIX_PROPERTY = ".facebook.clientId";

  private static final String GNUOB_PREFIX_PROPERTY = "gnuob.";

  public static final String ISSUER_FACEBOOK = "https://www.facebook.com";

  public static final String ISSUER_PAY_PAL = "https://www.paypal.com";

  public static final String ISSUER_MICROSOFT = "https://www.microsoft.com";

  public static final String ACCOUNTS_GOOGLE_COM = "http://localhost:8080/json/google/openid-configuration";

  public static final String ACCOUNTS_PAY_PAL_COM = "http://localhost:8080/json/paypal/openid-configuration";

  public static final String ACCOUNTS_FACEBOOK_COM = "http://localhost:8080/json/facebook/openid-configuration";

  public static final String ACCOUNTS_MICROSOFT_COM = "http://localhost:8080/json/microsoft/openid-configuration";

  public static AuthenticationRequest getAuthenticationRequest(final OIDCProviderMetadata providerConfiguration, final ClientID clientID, final URI redirectURI, final Scope scope,
      final State state) {
    return new AuthenticationRequest(providerConfiguration.getAuthorizationEndpointURI(), new ResponseType(ResponseType.Value.CODE), scope, clientID, redirectURI, state,
        new Nonce());
  }

  public static ClientID getClientID(final String site, final URI issuerURI) {
    switch (issuerURI.toString()) {
      case ACCOUNTS_FACEBOOK_COM:
        return new ClientID(System.getProperty(GNUOB_PREFIX_PROPERTY + site + FACEBOOK_CLIENT_ID_PREFIX_PROPERTY));
      case ACCOUNTS_PAY_PAL_COM:
        return new ClientID(System.getProperty(GNUOB_PREFIX_PROPERTY + site + PAYPAL_CLIENT_ID_PREFIX_PROPERTY));
      case ACCOUNTS_MICROSOFT_COM:
        return new ClientID(System.getProperty(GNUOB_PREFIX_PROPERTY + site + MICROSOFT_CLIENT_ID_PREFIX_PROPERTY));
      default: // Google.
        return new ClientID(System.getProperty(GNUOB_PREFIX_PROPERTY + site + GOOGLE_CLIENT_ID_PREFIX_PROPERTY));
    }
  }

  public static Secret getClientSecret(final String site, final URI issuerURI) {
    switch (issuerURI.toString()) {
      case ACCOUNTS_FACEBOOK_COM:
        return new Secret(System.getProperty(GNUOB_PREFIX_PROPERTY + site + FACEBOOK_CLIENT_SECRET_PREFIX_PROPERTY));
      case ACCOUNTS_PAY_PAL_COM:
        return new Secret(System.getProperty(GNUOB_PREFIX_PROPERTY + site + PAYPAL_CLIENT_SECRET_PREFIX_PROPERTY));
      case ACCOUNTS_MICROSOFT_COM:
        return new Secret(System.getProperty(GNUOB_PREFIX_PROPERTY + site + MICROSOFT_CLIENT_SECRET_PREFIX_PROPERTY));
      default: // Google.
        return new Secret(System.getProperty(GNUOB_PREFIX_PROPERTY + site + GOOGLE_CLIENT_SECRET_PREFIX_PROPERTY));
    }
  }

  public static FacebookAuthenticationRequest getFacebookAuthenticationRequest(final OIDCProviderMetadata providerConfiguration, final ClientID clientID, final URI redirectURI,
      final Scope scope, final State state) {
    return new FacebookAuthenticationRequest(providerConfiguration.getAuthorizationEndpointURI(), new ResponseType(ResponseType.Value.CODE), scope, clientID, redirectURI, state);
  }

  public static MicrosoftAuthenticationRequest getMicrosoftAuthenticationRequest(final OIDCProviderMetadata providerConfiguration, final ClientID clientID, final URI redirectURI,
      final Scope scope, final State state) {
    return new MicrosoftAuthenticationRequest(providerConfiguration.getAuthorizationEndpointURI(), new ResponseType(ResponseType.Value.CODE), scope, clientID, redirectURI, state);
  }

  public static OIDCProviderMetadata getProviderConfigurationURL(final URI issuerURI) {
    try {
      final URL providerConfigurationURL = issuerURI.toURL();
      final InputStream inputStream = providerConfigurationURL.openStream();

      String providerInfo = null;

      try (java.util.Scanner json = new java.util.Scanner(inputStream)) {
        providerInfo = json.useDelimiter("\\A").hasNext() ? json.next() : "";
      }

      return OIDCProviderMetadata.parse(providerInfo);
    } catch (ParseException | IOException e) {
      throw new GNUOpenBusinessApplicationException("Couldn't get OIDCProviderMetadata", e);
    }
  }

  public static Scope getScope(final String site, final URI issuerURI) {
    switch (issuerURI.toString()) {
      case ACCOUNTS_FACEBOOK_COM:
        return Scope.parse(System.getProperty(GNUOB_PREFIX_PROPERTY + site + FACEBOOK_SCOPE_PREFIX_PROPERTY));
      case ACCOUNTS_PAY_PAL_COM:
        return Scope.parse(System.getProperty(GNUOB_PREFIX_PROPERTY + site + PAYPAL_SCOPE_PREFIX_PROPERTY));
      case ACCOUNTS_MICROSOFT_COM:
        return Scope.parse(System.getProperty(GNUOB_PREFIX_PROPERTY + site + MICROSOFT_SCOPE_PREFIX_PROPERTY));
      default: // Google.
        return Scope.parse(System.getProperty(GNUOB_PREFIX_PROPERTY + site + GOOGLE_SCOPE_PREFIX_PROPERTY));
    }
  }

  private static BearerAccessToken getTokenRequest(final OIDCProviderMetadata providerConfiguration, final ClientID clientID, final AuthorizationCode authorizationCode,
      final URI redirectURI, final Secret clientSecret)
          throws SerializeException, ParseException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, java.text.ParseException, JOSEException {
    final SecretTokenRequest tokenRequest =
        new SecretTokenRequest(providerConfiguration.getTokenEndpointURI(), clientID, clientSecret, new AuthorizationCodeGrant(authorizationCode, redirectURI));

    TokenResponse tokenResponse;

    switch (providerConfiguration.getIssuer().getValue()) {
      case ISSUER_FACEBOOK:
        tokenResponse = AccessTokenResponse.parse(tokenRequest.toHTTPRequest().send());
        break;
      case ISSUER_PAY_PAL:
        tokenResponse = OIDCTokenResponseParser.parse(tokenRequest.toHTTPRequest().send());
        break;
      case ISSUER_MICROSOFT:
        tokenResponse = AccessTokenResponse.parse(tokenRequest.toHTTPRequest().send());
        break;
      default: // Google.
        tokenResponse = OIDCTokenResponseParser.parse(tokenRequest.toHTTPRequest().send());
        break;
    }

    if (tokenResponse instanceof TokenErrorResponse) {
      final ErrorObject error = ((TokenErrorResponse) tokenResponse).getErrorObject();
      throw new GNUOpenBusinessApplicationException(error.getDescription());
    }

    if (tokenResponse instanceof OIDCTokenResponse) {
      return ((OIDCTokenResponse) tokenResponse).getOIDCTokens().getBearerAccessToken();
    }

    if (tokenResponse instanceof AccessTokenResponse) {
      return ((AccessTokenResponse) tokenResponse).getTokens().getBearerAccessToken();
    }

    throw new GNUOpenBusinessApplicationException("Couldn't get a BearerAccessToken");
  }

  private static UserInfo getUserInfo(final OIDCProviderMetadata providerConfiguration, final BearerAccessToken bearerAccessToken)
      throws ParseException, SerializeException, IOException {
    final UserInfoRequest userInfoRequest = new UserInfoRequest(providerConfiguration.getUserInfoEndpointURI(), bearerAccessToken);

    UserInfoResponse userInfoResponse;

    switch (providerConfiguration.getIssuer().getValue()) {
      case ISSUER_FACEBOOK:
        userInfoResponse = FacebookUserInfoResponse.parse(userInfoRequest.toHTTPRequest().send());
        break;
      case ISSUER_PAY_PAL:
        userInfoResponse = PayPalUserInfoResponse.parse(userInfoRequest.toHTTPRequest().send());
        break;
      case ISSUER_MICROSOFT:
        userInfoResponse = MicrosoftUserInfoResponse.parse(userInfoRequest.toHTTPRequest().send());
        break;
      default: // Google.
        userInfoResponse = UserInfoResponse.parse(userInfoRequest.toHTTPRequest().send());
        break;
    }

    if (userInfoResponse instanceof UserInfoErrorResponse) {
      final ErrorObject error = ((UserInfoErrorResponse) userInfoResponse).getErrorObject();
      throw new GNUOpenBusinessApplicationException(error.getDescription());
    }

    return ((UserInfoSuccessResponse) userInfoResponse).getUserInfo();
  }

  public static UserInfo getUserInfo(final OIDCProviderMetadata providerConfiguration, final ClientID clientID, final State state, final URI requestURI, final URI redirectURI,
      final Secret clientSecret) {
    try {
      final AuthorizationCode authorizationCode = retrieveAuthenticationCode(requestURI, state);
      final BearerAccessToken bearerAccessToken = getTokenRequest(providerConfiguration, clientID, authorizationCode, redirectURI, clientSecret);
      return getUserInfo(providerConfiguration, bearerAccessToken);
    } catch (ParseException | SerializeException | IOException | NoSuchAlgorithmException | InvalidKeySpecException | java.text.ParseException | JOSEException e) {
      throw new GNUOpenBusinessApplicationException("Couldn't get UserInfo", e);
    }
  }

  private static AuthorizationCode retrieveAuthenticationCode(final URI requestURI, final State state) throws ParseException {
    final AuthenticationResponse authenticationResponse = AuthenticationResponseParser.parse(requestURI);

    if (authenticationResponse instanceof AuthenticationErrorResponse) {
      final ErrorObject error = ((AuthenticationErrorResponse) authenticationResponse).getErrorObject();
      throw new GNUOpenBusinessApplicationException(error.getDescription());
    }

    if (((AuthenticationSuccessResponse) authenticationResponse).getState() == null
        || !((AuthenticationSuccessResponse) authenticationResponse).getState().getValue().equals(state.getValue())) {
      throw new GNUOpenBusinessApplicationException("State verification failed, recieved stated is not correct");
    }

    return ((AuthenticationSuccessResponse) authenticationResponse).getAuthorizationCode();
  }

  private OAuthUtils() {

  }
}
