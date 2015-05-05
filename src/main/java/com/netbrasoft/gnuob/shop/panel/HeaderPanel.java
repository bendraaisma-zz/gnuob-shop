package com.netbrasoft.gnuob.shop.panel;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class HeaderPanel extends Panel {

   private static final long serialVersionUID = 3137234732197409313L;
   private static final String GNUOB_SITE_TITLE_PROPERTY = "gnuob.shop.site.title";
   private static final String GNUOB_SITE_SUBTITLE_PROPERTY = "gnuob.shop.site.subtitle";

   public HeaderPanel(String id) {
      super(id);
   }

   @Override
   protected void onInitialize() {
      String site = getRequest().getClientUrl().getHost();
      String title = site.replaceFirst("www.", "").split("\\.")[0];
      String subTitle = site.replaceFirst("www.", "").replaceFirst(title, "");

      add(new Label(GNUOB_SITE_TITLE_PROPERTY, System.getProperty(GNUOB_SITE_TITLE_PROPERTY, WordUtils.capitalize(title))));
      add(new Label(GNUOB_SITE_SUBTITLE_PROPERTY, System.getProperty(GNUOB_SITE_SUBTITLE_PROPERTY, subTitle)));

      super.onInitialize();
   }
}
