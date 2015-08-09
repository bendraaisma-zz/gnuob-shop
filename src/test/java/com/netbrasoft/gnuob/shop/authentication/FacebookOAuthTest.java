package com.netbrasoft.gnuob.shop.authentication;

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

import com.netbrasoft.gnuob.shop.utils.Utils;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

@RunWith(Arquillian.class)
public class FacebookOAuthTest {

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
      System.setProperty("gnuob.localhost.facebook.clientId", "1629794503933891");
      System.setProperty("gnuob.localhost.facebook.clientSecret", "115bb80745066faa447340be468fa7fd");
      System.setProperty("gnuob.localhost.facebook.scope", "email");

      issuerURI = new URI(OAuthUtils.ACCOUNTS_FACEBOOK_COM);
      clientID = OAuthUtils.getClientID("localhost", issuerURI);
      state = new State(UUID.randomUUID().toString());
      redirectURI = URI.create("http://localhost:8080/account.html");
      scope = OAuthUtils.getScope("localhost", issuerURI);
      providerConfiguration = OAuthUtils.getProviderConfigurationURL(issuerURI);
   }

   @Test
   public void testFaceBookOAuthLoginVersionV2_4Login() throws SerializeException {
      final AuthenticationRequest authenticationRequest = OAuthUtils.getAuthenticationRequest(providerConfiguration, issuerURI, clientID, redirectURI, scope, state);

      final WebDriverWait webDriverWait = new WebDriverWait(driver, 60);

      driver.get("http://localhost:8080/");
      driver.get(authenticationRequest.toURI().toString());

      webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("google")));

      final UserInfo userInfo = OAuthUtils.getUserInfo(providerConfiguration, issuerURI, clientID, state, URI.create(driver.getCurrentUrl()), redirectURI, OAuthUtils.getClientSecret("localhost", issuerURI));

      assertEquals("Bernard Arjan Draaisma", userInfo.getName());
      assertEquals("bendraaisma@gmail.com", userInfo.getEmail());
      assertEquals("Bernard Arjan", userInfo.getGivenName());
      assertEquals("Draaisma", userInfo.getFamilyName());
   }
}
