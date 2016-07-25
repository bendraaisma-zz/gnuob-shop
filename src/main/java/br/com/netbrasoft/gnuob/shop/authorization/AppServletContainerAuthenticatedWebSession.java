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

package br.com.netbrasoft.gnuob.shop.authorization;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Session;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.wicketstuff.wicket.servlet3.auth.ServletContainerAuthenticatedWebSession;

import br.com.netbrasoft.gnuob.security.GNUOBPrincipal;

public class AppServletContainerAuthenticatedWebSession extends ServletContainerAuthenticatedWebSession {

  private static final long serialVersionUID = 2503512201455796747L;

  public AppServletContainerAuthenticatedWebSession(final Request request) {
    super(request);
  }

  public static AppServletContainerAuthenticatedWebSession get() {
    return (AppServletContainerAuthenticatedWebSession) Session.get();
  }

  public static String getPassword() {
    return getRequest().getUserPrincipal() instanceof GNUOBPrincipal
        ? ((GNUOBPrincipal) getRequest().getUserPrincipal()).getPassword() : null;
  }

  private static HttpServletRequest getRequest() {
    return (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();
  }

  public static String getSite() {
    return getRequest().getUserPrincipal() instanceof GNUOBPrincipal
        ? ((GNUOBPrincipal) getRequest().getUserPrincipal()).getSite() : null;
  }

  public static String getUserName() {
    return ServletContainerAuthenticatedWebSession.getUserName();
  }
}
