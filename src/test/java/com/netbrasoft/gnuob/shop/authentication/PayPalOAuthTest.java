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
public class PayPalOAuthTest {

   @Deployment(testable = false)
   public static Archive<?> createDeployment() {
      return Utils.createDeployment();
   }

   private final URI issuerURI = null;
   private ClientID clientID = null;
   private State state = null;
   private URI redirectURI = null;
   private Scope scope = null;
   private OIDCProviderMetadata providerConfiguration = null;

   @Drone
   private WebDriver driver;

   @Before
   public void testBefore() throws URISyntaxException {
      System.setProperty("gnuob.localhost.paypal.clientId", "Ad0Qqt8C8GTgsW6V0PrwuE49MiRUxNanQbmptFIxKH_VjovYNZrfl8MTg3mmaENIG1PlfOozj_r-1Ek4");
      System.setProperty("gnuob.localhost.paypal.clientSecret", "ENGboXWLXQnpLbauwIL0xzLOSLCnIDOxjWpBPmzArUXBTm_V5kN5f68gnjK24ceCU9QWQ-uYGeyExgaI");
      System.setProperty("gnuob.localhost.paypal.scope", "openid profile email");

      clientID = OAuthUtils.getClientID("localhost", issuerURI);
      state = new State(UUID.randomUUID().toString());
      redirectURI = URI.create("http://localhost:8080/account.html");
      scope = OAuthUtils.getScope("localhost", issuerURI);
      providerConfiguration = OAuthUtils.getProviderConfigurationURL(issuerURI);
   }

   @Test
   public void testFaceBookOAuthLoginVersionV2_4Login() throws SerializeException {
      final AuthenticationRequest authenticationRequest = OAuthUtils.getAuthenticationRequest(providerConfiguration, clientID, redirectURI, scope, state);

      final WebDriverWait webDriverWait = new WebDriverWait(driver, 60);

      driver.get("http://localhost:8080/");
      driver.get(authenticationRequest.toURI().toString());

      webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("google")));

      final UserInfo userInfo = OAuthUtils.getUserInfo(providerConfiguration, clientID, state, URI.create(driver.getCurrentUrl()), redirectURI, OAuthUtils.getClientSecret("localhost", issuerURI));

      assertEquals("Bernard Draaisma", userInfo.getName());
      assertEquals("badraaisma@msn.com", userInfo.getEmail().getAddress());
      assertEquals("Bernard", userInfo.getGivenName());
      assertEquals("Draaisma", userInfo.getFamilyName());
   }
}
