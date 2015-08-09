package com.netbrasoft.gnuob.shop.page.error;

import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.shop.border.ContentBorder;
import com.netbrasoft.gnuob.shop.page.BasePage;
import com.netbrasoft.gnuob.shop.panel.error.AccessDeniedPanel;

@MountPath("accessDenied.html")
public class AccessDeniedPage extends BasePage {

   private static final long serialVersionUID = -738433892437166398L;

   private AccessDeniedPanel accessDeniedPanel = new AccessDeniedPanel("accessDeniedPanel");

   private ContentBorder contentBorder = new ContentBorder("contentBorder");

   @Override
   protected void onInitialize() {
      contentBorder.add(accessDeniedPanel);
      add(contentBorder);

      super.onInitialize();
   }
}
