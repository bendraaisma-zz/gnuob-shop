package com.netbrasoft.gnuob.shop.page.error;

import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.shop.border.ContentBorder;
import com.netbrasoft.gnuob.shop.page.BasePage;
import com.netbrasoft.gnuob.shop.panel.error.ExpiredPanel;

@MountPath("expired.html")
public class ExpiredPage extends BasePage {

   private static final long serialVersionUID = 5008565787309933520L;

   private ContentBorder contentBorder = new ContentBorder("contentBorder");

   private ExpiredPanel expiredPanel = new ExpiredPanel("expiredPanel");

   @Override
   protected void onInitialize() {
      contentBorder.add(expiredPanel);
      add(contentBorder);

      super.onInitialize();
   }
}
