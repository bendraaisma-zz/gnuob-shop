package br.com.netbrasoft.gnuob.shop.authentication;

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNTS_GOOGLE_COM;
import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import br.com.netbrasoft.gnuob.shop.utils.Utils;

@RunWith(Arquillian.class)
public class GoogleOAuthTest {

  @Deployment(testable = false)
  public static Archive<?> createDeployment() {
    return Utils.createDeployment();
  }

  private URI issuerURI = null;
  private ClientID clientID = null;
  private State state = null;
  private URI redirectURI = null;
  private Scope scope = null;
  private OIDCProviderMetadata providerConfiguration = null;

  @Drone
  private WebDriver driver;

  @Before
  public void testBefore() throws URISyntaxException {
    System.setProperty("gnuob.localhost.google.clientId",
        "1046071506023-95es29on7glbbjgh3nlb2v9m76hn78jp.apps.googleusercontent.com");
    System.setProperty("gnuob.localhost.google.clientSecret", "Rc-NPf-QMcGpTdOKjaNpxvbt");
    System.setProperty("gnuob.localhost.google.scope", "openid profile email");

    issuerURI = new URI(ACCOUNTS_GOOGLE_COM);
    clientID = OAuthUtils.getClientID("localhost", issuerURI);
    state = new State(UUID.randomUUID().toString());
    redirectURI = URI.create("http://localhost:8080/account.html");
    scope = OAuthUtils.getScope("localhost", issuerURI);
    providerConfiguration = OAuthUtils.getOIDCProviderMetaData(issuerURI);
  }

  @Test
  public void testGoogleOAuthLoginVersionLogin() throws SerializeException {
    final AuthenticationRequest authenticationRequest =
        OAuthUtils.getAuthenticationRequest(providerConfiguration, clientID, redirectURI, scope, state);

    final WebDriverWait webDriverWait = new WebDriverWait(driver, 60);

    driver.get("http://localhost:8080/");
    driver.get(authenticationRequest.toURI().toString());

    webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("facebook")));

    final UserInfo userInfo = OAuthUtils.getUserInfo(providerConfiguration, clientID, state,
        URI.create(driver.getCurrentUrl()), redirectURI, OAuthUtils.getSecret("localhost", issuerURI));

    assertEquals("Bernard Arjan Draaisma", userInfo.getName());
    assertEquals("bendraaisma@gmail.com", userInfo.getEmail().toString());
    assertEquals("Bernard Arjan", userInfo.getGivenName());
    assertEquals("Draaisma", userInfo.getFamilyName());
  }
}
