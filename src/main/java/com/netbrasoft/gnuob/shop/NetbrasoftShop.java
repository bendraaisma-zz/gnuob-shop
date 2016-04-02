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

package com.netbrasoft.gnuob.shop;

import static com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CDNJS_CLOUDFLARE_COM_80_DEF;
import static com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.FALSE_DEF;
import static com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.GNUOB_SITE_CDN_ENABLED_KEY;
import static com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.GNUOB_SITE_CDN_URL_KEY;
import static com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.GNUOB_SITE_ENCRYPTION_KEY;
import static com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.INSPECTOR_PAGE_HTML;
import static com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.NETBRASOFT_SHOPPING_JAVASCRIPT_CONTAINER_NAME;
import static com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.WICKET_APPLICATION_VALUE;
import static org.apache.wicket.RuntimeConfigurationType.DEVELOPMENT;
import static org.apache.wicket.settings.SecuritySettings.DEFAULT_ENCRYPTION_KEY;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.bean.validation.BeanValidationConfiguration;
import org.apache.wicket.devutils.inspector.InspectorPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.pages.PageExpiredErrorPage;
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
 * @author "Bernard Arjan Draaisma"
 * @version 1.0
 */
@EnableCaching
@Service(WICKET_APPLICATION_VALUE)
public class NetbrasoftShop extends ServletContainerAuthenticatedWebApplication {

  private static final BootstrapSettings BOOTSTRAP_SETTINGS = new BootstrapSettings();
  private static final WebjarsSettings WEBJARS_SETTINGS = new WebjarsSettings();

  static {
    BOOTSTRAP_SETTINGS.useCdnResources(Boolean.valueOf(System.getProperty(GNUOB_SITE_CDN_ENABLED_KEY, FALSE_DEF)));
    BOOTSTRAP_SETTINGS.setJsResourceFilterName(NETBRASOFT_SHOPPING_JAVASCRIPT_CONTAINER_NAME);
    BOOTSTRAP_SETTINGS.setThemeProvider(new NetbrasoftShopThemeProvider(NetbrasoftShopTheme.Localhost));
    BOOTSTRAP_SETTINGS.setActiveThemeProvider(new CookieThemeProvider());
    WEBJARS_SETTINGS.cdnUrl(System.getProperty(GNUOB_SITE_CDN_URL_KEY, CDNJS_CLOUDFLARE_COM_80_DEF));
    WEBJARS_SETTINGS.useCdnResources(Boolean.valueOf(System.getProperty(GNUOB_SITE_CDN_ENABLED_KEY, FALSE_DEF)));
  }

  @Override
  protected void init() {
    super.init();
    initDeploymentSettings();
  }

  private void initDeploymentSettings() {
    installBootstrapSettings();
    installWebjarsSettings();
    setupApplicationSettings();
    setupBeanValidationSettings();
    setupSecurityCryptoFactorySettings();
    setupJavaScriptToFooterHeaderResponseDecorator();
    setupSpringCompInjectorForCompInstantListeners();
    setupDevelopmentModeSettings();
  }

  private void installBootstrapSettings() {
    Bootstrap.install(this, BOOTSTRAP_SETTINGS);
  }

  private void installWebjarsSettings() {
    WicketWebjars.install(this, WEBJARS_SETTINGS);
  }

  private void setupApplicationSettings() {
    getApplicationSettings().setUploadProgressUpdatesEnabled(true);
    getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);
    getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
    getApplicationSettings().setPageExpiredErrorPage(PageExpiredErrorPage.class);
  }

  private void setupBeanValidationSettings() {
    new BeanValidationConfiguration().configure(this);
  }

  private void setupSecurityCryptoFactorySettings() {
    getSecuritySettings().setCryptFactory(
        new CachingSunJceCryptFactory(System.getProperty(GNUOB_SITE_ENCRYPTION_KEY, DEFAULT_ENCRYPTION_KEY)));
  }

  private void setupJavaScriptToFooterHeaderResponseDecorator() {
    setHeaderResponseDecorator(new RenderJavaScriptToFooterHeaderResponseDecorator());
  }

  private void setupSpringCompInjectorForCompInstantListeners() {
    getComponentInstantiationListeners().add(new SpringComponentInjector(this));
  }

  private void setupDevelopmentModeSettings() {
    if (isDevelopmentModeEnabled()) {
      enableDevelopmentSettings();
    }
  }

  private boolean isDevelopmentModeEnabled() {
    return DEVELOPMENT == getConfigurationType();
  }

  private void enableDevelopmentSettings() {
    mountInspectorPage();
    enableDevelopmentUtilsAndAjaxDebugMode();
    configureWicketSource();
  }

  private void mountInspectorPage() {
    mountPage(INSPECTOR_PAGE_HTML, InspectorPage.class);
  }

  private void enableDevelopmentUtilsAndAjaxDebugMode() {
    getDebugSettings().setDevelopmentUtilitiesEnabled(true);
    getDebugSettings().setAjaxDebugModeEnabled(true);
  }

  private void configureWicketSource() {
    WicketSource.configure(this);
  }

  @Override
  protected Class<? extends ServletContainerAuthenticatedWebSession> getContainerManagedWebSessionClass() {
    return AppServletContainerAuthenticatedWebSession.class;
  }

  @Override
  public Class<? extends Page> getHomePage() {
    return MainPage.class;
  }

  @Override
  protected Class<? extends WebPage> getSignInPageClass() {
    return SignInPage.class;
  }

  @Override
  protected IConverterLocator newConverterLocator() {
    return newXmlGregorianCalanderLocator();
  }

  private ConverterLocator newXmlGregorianCalanderLocator() {
    final ConverterLocator locator = (ConverterLocator) super.newConverterLocator();
    locator.set(XMLGregorianCalendar.class, new XmlGregorianCalendarConverter());
    return locator;
  }
}
