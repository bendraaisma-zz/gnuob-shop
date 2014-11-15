package com.netbrasoft.gnuob.shop.panel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class HeaderPanel extends Panel {

   private static final long serialVersionUID = 3137234732197409313L;
   private static final String GNUOB_SITE_TITLE_PROPERTY = "gnuob.shop.site.title";
   private static final String GNUOB_SITE_SUBTITLE_PROPERTY = "gnuob.shop.site.subtitle";
   private static final String SITE_TITLE_PROPERTY = System.getProperty(GNUOB_SITE_TITLE_PROPERTY, "Netbrasoft");
   private static final String SITE_SUBTITLE_PROPERTY = System.getProperty(GNUOB_SITE_SUBTITLE_PROPERTY, ".com");

   private Label siteTitleLabel = new Label(GNUOB_SITE_TITLE_PROPERTY, SITE_TITLE_PROPERTY);
   private Label siteSubTitleLabel = new Label(GNUOB_SITE_SUBTITLE_PROPERTY, SITE_SUBTITLE_PROPERTY);

   public HeaderPanel(String id) {
      super(id);
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();
      add(siteTitleLabel);
      add(siteSubTitleLabel);
   }
}
