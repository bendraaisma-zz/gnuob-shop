package com.netbrasoft.gnuob.shop.border;

import org.apache.wicket.markup.html.border.Border;

import com.netbrasoft.gnuob.shop.panel.FooterPanel;
import com.netbrasoft.gnuob.shop.panel.HeaderPanel;
import com.netbrasoft.gnuob.shop.panel.MainMenuPanel;
import com.netbrasoft.gnuob.shop.panel.SlideShowPanel;

public class ContentBorder extends Border {

   private static final long serialVersionUID = 6569587142042286311L;

   private HeaderPanel headerPanel = new HeaderPanel("headerPanel");
   private MainMenuPanel mainMenuPanel = new MainMenuPanel("mainMenuPanel");
   private SlideShowPanel slideShowPanel = new SlideShowPanel("slideShowPanel");
   private FooterPanel footerPanel = new FooterPanel("footerPanel");

   public ContentBorder(String id) {
      super(id);
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();

      addToBorder(headerPanel);
      addToBorder(mainMenuPanel);
      addToBorder(slideShowPanel);
      addToBorder(footerPanel);
   }
}
