package com.netbrasoft.gnuob.shop.page.error;

import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.shop.border.ContentBorder;
import com.netbrasoft.gnuob.shop.page.BasePage;
import com.netbrasoft.gnuob.shop.panel.error.NotFoundPanel;

@MountPath("notFound.html")
public class NotFoundPage extends BasePage {

  private static final long serialVersionUID = -4283181889540584265L;

  private ContentBorder contentBorder = new ContentBorder("contentBorder");

  private NotFoundPanel notFoundPanel = new NotFoundPanel("notFoundPanel");

  @Override
  protected void onInitialize() {
    contentBorder.add(notFoundPanel);
    add(contentBorder);

    super.onInitialize();
  }
}
