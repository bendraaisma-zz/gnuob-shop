/*
 * Copyright 2015 Netbrasoft
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

package com.netbrasoft.gnuob.shop;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.devutils.inspector.InspectorPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.pages.PageExpiredErrorPage;
import org.apache.wicket.settings.SecuritySettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.crypt.CachingSunJceCryptFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.wicketstuff.wicket.servlet3.auth.ServletContainerAuthenticatedWebApplication;
import org.wicketstuff.wicket.servlet3.auth.ServletContainerAuthenticatedWebSession;

import com.netbrasoft.gnuob.api.generic.converter.XmlGregorianCalendarConverter;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.html.NetbrasoftShopTheme;
import com.netbrasoft.gnuob.shop.html.NetbrasoftShopThemeProvider;
import com.netbrasoft.gnuob.shop.page.MainPage;
import com.netbrasoft.gnuob.shop.page.SignInPage;
import com.netbrasoft.gnuob.shop.page.error.AccessDeniedPage;
import com.netbrasoft.gnuob.shop.page.error.InternalErrorPage;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.RenderJavaScriptToFooterHeaderResponseDecorator;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.CookieThemeProvider;
import de.agilecoders.wicket.webjars.WicketWebjars;
import de.agilecoders.wicket.webjars.settings.WebjarsSettings;
import net.ftlines.wicketsource.WicketSource;

/**
 * Central location where the custom Wicket-, Bootstrap- and Web-jars configuration setup is managed
 * for running the web application correctly on WildFly application server.
 *
 * @author "Bernard Arjan Draaisma" *
 * @version 1.0
 */
@EnableCaching
@Service(NetbrasoftShop.WICKET_APPLICATION_VALUE)
public class NetbrasoftShop extends ServletContainerAuthenticatedWebApplication {

  /**
   * JavaScript resource filter name that is configured inside the Bootstrap settings for grouping
   * all the used JavaScript libraries together in one single mark up container on every page.
   */
  private static final String NETBRASOFT_SHOPPING_JAVASCRIPT_CONTAINER_NAME = "netbrasoft-shopping-javascript-container";

  /**
   * Default Content Delivery Network URL where used web-jars can be found.
   */
  private static final String CDNJS_CLOUDFLARE_COM_80_DEFAULT_VALUE = "//cdnjs.cloudflare.com:80";

  /**
   * Default Boolean false value.
   */
  private static final String FALSE_DEFAULT_VALUE = "false";

  /**
   * The default Spring bean name of this Wicket application.
   */
  protected static final String WICKET_APPLICATION_VALUE = "wicketApplication";

  /**
   * Page name where the @InspectorPage will be mounted when Wicket is running in DEVELOPMENT mode.
   */
  private static final String INSPECTOR_PAGE_HTML = "inspectorPage.html";

  /**
   * System property key to configure the security encryption key, default is
   * {@link SecuritySettings.DEFAULT_ENCRYPTION_KEY}
   */
  private static final String GNUOB_SITE_ENCRYPTION_KEY_PROPERTY = "gnuob.site.encryption.key";

  /**
   * System property key to enable or disable Content Delivery Network URL's where used web-jars can
   * be found, default is {@link NetbrasoftShop.FALSE_DEFAULT_VALUE}
   */
  private static final String GNUOB_SITE_CDN_ENABLED_PROPERTY = "gnuob.site.cdn.enabled";

  /**
   * System property key to set the Content Delivery Networks URL where web-jars can be found,
   * default is {@link NetbrasoftShop.CDNJS_CLOUDFLARE_COM_80_DEFAULT_VALUE}
   */
  private static final String GNUOB_SITE_CDN_URL_PROPERTY = "gnuob.site.cdn.url";

  /**
   * Default constructor.
   */
  public NetbrasoftShop() {
    super();
  }

  /**
   * {@inheritDoc}.
   */
  @Override
  protected Class<? extends ServletContainerAuthenticatedWebSession> getContainerManagedWebSessionClass() {
    return AppServletContainerAuthenticatedWebSession.class;
  }

  /**
   * {@inheritDoc}.
   */
  @Override
  public Class<? extends Page> getHomePage() {
    return MainPage.class;
  }

  /**
   * {@inheritDoc}.
   */
  @Override
  protected Class<? extends WebPage> getSignInPageClass() {
    return SignInPage.class;
  }

  /**
   * {@inheritDoc}.
   */
  @Override
  protected void init() {
    super.init();
    final NetbrasoftShopThemeProvider netbrasoftShopThemeProvider = new NetbrasoftShopThemeProvider(NetbrasoftShopTheme.Localhost);
    final CookieThemeProvider activeThemeProvider = new CookieThemeProvider();
    final BootstrapSettings bootstrapSettings = new BootstrapSettings();
    final WebjarsSettings webjarsSettings = new WebjarsSettings();
    final SpringComponentInjector springComponentInjector = new SpringComponentInjector(this);
    bootstrapSettings.useCdnResources(Boolean.valueOf(System.getProperty(GNUOB_SITE_CDN_ENABLED_PROPERTY, FALSE_DEFAULT_VALUE)));
    bootstrapSettings.setJsResourceFilterName(NETBRASOFT_SHOPPING_JAVASCRIPT_CONTAINER_NAME);
    bootstrapSettings.setThemeProvider(netbrasoftShopThemeProvider);
    bootstrapSettings.setActiveThemeProvider(activeThemeProvider);
    webjarsSettings.cdnUrl(System.getProperty(GNUOB_SITE_CDN_URL_PROPERTY, CDNJS_CLOUDFLARE_COM_80_DEFAULT_VALUE));
    webjarsSettings.useCdnResources(Boolean.valueOf(System.getProperty(GNUOB_SITE_CDN_ENABLED_PROPERTY, FALSE_DEFAULT_VALUE)));
    Bootstrap.install(this, bootstrapSettings);
    WicketWebjars.install(this, webjarsSettings);
    setHeaderResponseDecorator(new RenderJavaScriptToFooterHeaderResponseDecorator());
    getComponentInstantiationListeners().add(springComponentInjector);
    getApplicationSettings().setUploadProgressUpdatesEnabled(true);
    getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);
    getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
    getApplicationSettings().setPageExpiredErrorPage(PageExpiredErrorPage.class);
    getSecuritySettings().setCryptFactory(new CachingSunJceCryptFactory(System.getProperty(GNUOB_SITE_ENCRYPTION_KEY_PROPERTY, SecuritySettings.DEFAULT_ENCRYPTION_KEY)));
    if (getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT) {
      mountPage(INSPECTOR_PAGE_HTML, InspectorPage.class);
      getDebugSettings().setDevelopmentUtilitiesEnabled(true);
      getDebugSettings().setAjaxDebugModeEnabled(true);
      WicketSource.configure(this);
    }
  }

  /**
   * {@inheritDoc}.
   */
  @Override
  protected IConverterLocator newConverterLocator() {
    final ConverterLocator locator = (ConverterLocator) super.newConverterLocator();
    locator.set(XMLGregorianCalendar.class, new XmlGregorianCalendarConverter());
    return locator;
  }
}
