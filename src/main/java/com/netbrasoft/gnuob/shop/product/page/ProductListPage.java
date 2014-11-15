package com.netbrasoft.gnuob.shop.product.page;

import com.netbrasoft.gnuob.shop.border.ContentBorder;
import com.netbrasoft.gnuob.wicket.bootstrap.markup.html.BootstrapPage;

public class ProductListPage extends BootstrapPage {

   private static final long serialVersionUID = 7583829533111693200L;

   @Override
   public String getTitle() {
      return getString("gnuob.site.title");
   }

   @Override
   protected void onInitialize() {
      super.onInitialize();

      ContentBorder contentBorder = new ContentBorder("contentBorder");
      add(contentBorder);
   }
}
