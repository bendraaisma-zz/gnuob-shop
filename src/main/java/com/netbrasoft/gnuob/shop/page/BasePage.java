package com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.markup.head.CssUrlReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptUrlReferenceHeaderItem;
import org.apache.wicket.markup.html.WebPage;

public abstract class BasePage extends WebPage {

	private static final long serialVersionUID = 2104311609974795936L;

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		response.render(JavaScriptHeaderItem.forReference(getApplication().getJavaScriptLibrarySettings().getJQueryReference()));
		response.render(new CssUrlReferenceHeaderItem("//netdna.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css", "", ""));
		response.render(new CssUrlReferenceHeaderItem("//netdna.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap-theme.min.css", "", ""));
		response.render(new JavaScriptUrlReferenceHeaderItem("//netdna.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js", "bootstrap", false, "UTF-8", ""));
		response.render(new JavaScriptUrlReferenceHeaderItem("./script/jcookie.js", "jquery.cookie", false, "UTF-8", ""));
	}
}
