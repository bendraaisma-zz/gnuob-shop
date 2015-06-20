package com.netbrasoft.gnuob.shop.page.error;

import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.shop.border.ContentBorder;
import com.netbrasoft.gnuob.shop.page.BasePage;
import com.netbrasoft.gnuob.shop.panel.error.InternalErrorPanel;

@MountPath("internalError.html")
public class InternalErrorPage extends BasePage {

   private static final long serialVersionUID = 7999429438892271530L;

   private ContentBorder contentBorder = new ContentBorder("contentBorder");

   private InternalErrorPanel internalErrorPanel = new InternalErrorPanel("internalErrorPanel");

   @Override
   protected void onInitialize() {
      contentBorder.add(internalErrorPanel);
      add(contentBorder);

      super.onInitialize();
   }
}
