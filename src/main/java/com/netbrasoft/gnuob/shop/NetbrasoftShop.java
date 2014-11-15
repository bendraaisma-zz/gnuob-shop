package com.netbrasoft.gnuob.shop;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.devutils.inspector.InspectorPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.stereotype.Service;

import com.netbrasoft.gnuob.shop.product.page.ProductListPage;
 
import de.agilecoders.wicket.core.Bootstrap;
import net.ftlines.wicketsource.WicketSource;

@Service("wicketApplication")
public class NetbrasoftShop extends WebApplication {

   private static final String INSPECTOR_PAGE_HTML = "InspectorPage.html";

   @Override
   public Class<ProductListPage> getHomePage() {
      return ProductListPage.class;
   }

   @Override
   protected void init() {
      super.init();

      Bootstrap.install(this);

      getComponentInstantiationListeners().add(new SpringComponentInjector(this));
      getApplicationSettings().setUploadProgressUpdatesEnabled(true);

      if (getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT) {

         mountPage(INSPECTOR_PAGE_HTML, InspectorPage.class);

         getDebugSettings().setDevelopmentUtilitiesEnabled(true);
         getDebugSettings().setAjaxDebugModeEnabled(true);

         WicketSource.configure(this);
      }
   }
}
