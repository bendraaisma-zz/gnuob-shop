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

import com.netbrasoft.gnuob.api.generic.XMLGregorianCalendarConverter;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.page.MainPage;
import com.netbrasoft.gnuob.shop.page.SignInPage;
import com.netbrasoft.gnuob.shop.page.error.AccessDeniedPage;
import com.netbrasoft.gnuob.shop.page.error.InternalErrorPage;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.RenderJavaScriptToFooterHeaderResponseDecorator;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.webjars.WicketWebjars;
import de.agilecoders.wicket.webjars.settings.WebjarsSettings;
import net.ftlines.wicketsource.WicketSource;

@EnableCaching
@Service("wicketApplication")
public class NetbrasoftShop extends ServletContainerAuthenticatedWebApplication {

   private static final String INSPECTOR_PAGE_HTML = "InspectorPage.html";

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
   protected void init() {
      super.init();

      final BootstrapSettings bootstrapSettings = new BootstrapSettings();
      bootstrapSettings.useCdnResources(Boolean.valueOf(System.getProperty("gnuob.site.cdn.enabled", "false")));
      bootstrapSettings.setJsResourceFilterName("netbrasoft-shopping-javascript-container");
      Bootstrap.install(this, bootstrapSettings);

      final WebjarsSettings webjarsSettings = new WebjarsSettings();
      webjarsSettings.cdnUrl(System.getProperty("gnuob.site.cdn.url", "//cdnjs.cloudflare.com:80"));
      webjarsSettings.useCdnResources(Boolean.valueOf(System.getProperty("gnuob.site.cdn.enabled", "false")));
      WicketWebjars.install(this, webjarsSettings);

      setHeaderResponseDecorator(new RenderJavaScriptToFooterHeaderResponseDecorator());
      getComponentInstantiationListeners().add(new SpringComponentInjector(this));
      getApplicationSettings().setUploadProgressUpdatesEnabled(true);
      getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);
      getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
      getApplicationSettings().setPageExpiredErrorPage(PageExpiredErrorPage.class);
      getSecuritySettings().setCryptFactory(new CachingSunJceCryptFactory(System.getProperty("gnuob.site.encryption.key", SecuritySettings.DEFAULT_ENCRYPTION_KEY)));

      if (getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT) {

         mountPage(INSPECTOR_PAGE_HTML, InspectorPage.class);

         getDebugSettings().setDevelopmentUtilitiesEnabled(true);
         getDebugSettings().setAjaxDebugModeEnabled(true);

         WicketSource.configure(this);
      }
   }

   @Override
   protected IConverterLocator newConverterLocator() {
      final ConverterLocator locator = (ConverterLocator) super.newConverterLocator();
      locator.set(XMLGregorianCalendar.class, new XMLGregorianCalendarConverter());
      return locator;
   }
}
