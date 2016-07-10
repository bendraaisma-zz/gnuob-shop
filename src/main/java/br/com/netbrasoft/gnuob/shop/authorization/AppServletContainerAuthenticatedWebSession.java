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
    final GNUOBPrincipal gnuobPrincipal = (GNUOBPrincipal) getRequest().getUserPrincipal();
    if (gnuobPrincipal == null) {
      return null;
    }
    return gnuobPrincipal.getPassword();
  }

  private static HttpServletRequest getRequest() {
    return (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();
  }

  public static String getSite() {
    final GNUOBPrincipal gnuobPrincipal = (GNUOBPrincipal) getRequest().getUserPrincipal();
    if (gnuobPrincipal == null) {
      return null;
    }
    return gnuobPrincipal.getSite();
  }

  public static String getUserName() {
    return ServletContainerAuthenticatedWebSession.getUserName();
  }

}
