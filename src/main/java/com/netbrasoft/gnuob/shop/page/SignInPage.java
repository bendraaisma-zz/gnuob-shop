package com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;

public class SignInPage extends BasePage {

   private static final long serialVersionUID = -8219855140262365434L;

   private boolean isSignedIn() {
      return AuthenticatedWebSession.get().isSignedIn();
   }

   @Override
   protected void onConfigure() {
      super.onConfigure();

      if (!isSignedIn()) {
         final String host =  getRequest().getClientUrl().getHost();
         signIn(System.getProperty("gnuob." + host + ".username", "guest"), System.getProperty("gnuob." + host + ".password", "guest"));
      }

      setResponsePage(getApplication().getHomePage());
   }

   private boolean signIn(String username, String password) {
      return AuthenticatedWebSession.get().signIn(username, password);
   }
}
