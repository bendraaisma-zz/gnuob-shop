package com.netbrasoft.gnuob.shop.border;

import org.apache.wicket.markup.html.border.Border;

import com.netbrasoft.gnuob.shop.panel.FooterPanel;
import com.netbrasoft.gnuob.shop.panel.HeaderPanel;
import com.netbrasoft.gnuob.shop.panel.MainMenuPanel;
import com.netbrasoft.gnuob.shop.panel.SlideShowPanel;

public class ContentBorder extends Border {

	private static final long serialVersionUID = 6569587142042286311L;

	public ContentBorder(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		HeaderPanel headerPanel = new HeaderPanel("headerPanel");
		MainMenuPanel mainMenuPanel = new MainMenuPanel("mainMenuPanel");
		SlideShowPanel slideShowPanel = new SlideShowPanel("slideShowPanel");
		FooterPanel footerPanel = new FooterPanel("footerPanel");

		addToBorder(headerPanel);
		addToBorder(mainMenuPanel);
		addToBorder(slideShowPanel);
		addToBorder(footerPanel);
	}

}
