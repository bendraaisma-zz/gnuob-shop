package com.netbrasoft.gnuob.shop.product.page;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

import com.netbrasoft.gnuob.shop.border.ContentBorder;

public class ProductListPage extends WebPage {

   private static final long serialVersionUID = 7583829533111693200L;

   @Override
   protected void onInitialize() {
      ContentBorder contentBorder = new ContentBorder("contentBorder");
      add(new Label("title", "Amaristore.com"));
      add(contentBorder);
      super.onInitialize();
   }
}
