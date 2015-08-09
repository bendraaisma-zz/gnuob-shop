package com.netbrasoft.gnuob.shop.border;

import org.apache.wicket.markup.html.border.Border;

import com.netbrasoft.gnuob.shop.panel.FooterPanel;
import com.netbrasoft.gnuob.shop.panel.HeaderPanel;

public class ContentBorder extends Border {

   private static final long serialVersionUID = 6569587142042286311L;

   private HeaderPanel headerPanel = new HeaderPanel("headerPanel");

   private FooterPanel footerPanel = new FooterPanel("footerPanel");

   public ContentBorder(String id) {
      super(id);
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();

      addToBorder(headerPanel.setOutputMarkupId(true));
      addToBorder(footerPanel.setOutputMarkupId(true));
   }
}
