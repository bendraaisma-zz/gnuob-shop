package com.netbrasoft.gnuob.shop.product.page;

import org.apache.wicket.markup.head.IHeaderResponse;

import com.netbrasoft.gnuob.shop.border.ContentBorder;
import com.netbrasoft.gnuob.shop.page.BasePage;

public class ProductListPage extends BasePage {

	private static final long serialVersionUID = 7583829533111693200L;

	@Override
	protected void onInitialize() {
		super.onInitialize();

		ContentBorder contentBorder = new ContentBorder("contentBorder");

		add(contentBorder);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
	}

}
