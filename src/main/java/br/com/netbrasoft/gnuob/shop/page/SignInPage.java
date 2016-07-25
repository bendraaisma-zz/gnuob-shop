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

package br.com.netbrasoft.gnuob.shop.page;

import static org.slf4j.LoggerFactory.getLogger;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.slf4j.Logger;

public class SignInPage extends BasePage {

  private static final long serialVersionUID = -8219855140262365434L;

  private static final Logger LOGGER = getLogger(SignInPage.class);

  private boolean isSignedIn() {
    return AuthenticatedWebSession.get().isSignedIn();
  }

  @Override
  protected void onConfigure() {
    super.onConfigure();
    if (!isSignedIn()) {
      doSignIn();
    }
    setResponsePage(getApplication().getHomePage());
  }

  private void doSignIn() {
    final String site = getRequest().getClientUrl().getHost();
    LOGGER.debug("Signing into the site with the found site name: [{}]", site);
    signIn(getUsername(site), getPassword(site));
  }

  private String getPassword(final String site) {
    return System.getProperty("gnuob." + site + ".password", "guest");
  }

  private String getUsername(final String site) {
    return System.getProperty("gnuob." + site + ".username", "guest");
  }

  private boolean signIn(final String username, final String password) {
    return AuthenticatedWebSession.get().signIn(username, password);
  }
}
