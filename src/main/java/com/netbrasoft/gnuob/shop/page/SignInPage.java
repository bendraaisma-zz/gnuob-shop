package com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;

public class SignInPage extends BasePage {

   private static final long serialVersionUID = -8219855140262365434L;

   @Override
   protected void onConfigure() {
      if (!isSignedIn()) {
         signIn(System.getProperty("gnuob.site.username", "guest"), System.getProperty("gnuob.site.password", "guest"));
      }

      super.onConfigure();
      setResponsePage(getApplication().getHomePage());
   }

   private boolean signIn(String username, String password) {
      return AuthenticatedWebSession.get().signIn(username, password);
   }

   private boolean isSignedIn() {
      return AuthenticatedWebSession.get().isSignedIn();
   }
}
