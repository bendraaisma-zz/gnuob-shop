package com.netbrasoft.gnuob.shop.page;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.head.CssContentHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.shop.NetbrasoftShop;
import com.netbrasoft.gnuob.shop.border.ContentBorder;
import com.netbrasoft.gnuob.shop.panel.MainMenuPanel;
import com.netbrasoft.gnuob.shop.security.ShopRoles;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class MainPage extends WebPage {

   private static final long serialVersionUID = 7583829533111693200L;

   private static final String GNUOB_SITE_TITLE_PROPERTY = "gnuob.shop.site.title";

   private static final JavaScriptReferenceHeaderItem JS_VALIDATOR_REFERENCE = JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("bootstrap-validator/0.8.1/dist/validator.min.js"));

   private static final JavaScriptReferenceHeaderItem JS_JQUERY_COOKIE = JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("jquery.cookie/1.4.1/jquery.cookie.js"));

   private static final JavaScriptReferenceHeaderItem JS_BOOTSTRAP_3_DATEPICKER = JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("bootstrap-3-datepicker/1.4.0/dist/js/bootstrap-datepicker.min.js"));

   private static final CssReferenceHeaderItem CSS_BOOTSTRAP_3_DATEPICKER = CssContentHeaderItem.forReference(new WebjarsCssResourceReference("bootstrap-3-datepicker/1.4.0/dist/css/bootstrap-datepicker3.min.css"));

   private static final JavaScriptReferenceHeaderItem JS_JQUERY = JavaScriptHeaderItem.forReference(NetbrasoftShop.get().getJavaScriptLibrarySettings().getJQueryReference());

   private MainMenuPanel mainMenuPanel = new MainMenuPanel("mainMenuPanel", new Model<Category>(new Category()));

   @Override
   protected void onInitialize() {
      String site = getRequest().getClientUrl().getHost();
      String title = site.replaceFirst("www.", "").split("\\.")[0];

      add(new Label(GNUOB_SITE_TITLE_PROPERTY, System.getProperty(GNUOB_SITE_TITLE_PROPERTY, WordUtils.capitalize(title))));

      ContentBorder contentBorder = new ContentBorder("contentBorder");

      contentBorder.add(mainMenuPanel);
      add(contentBorder);

      super.onInitialize();
   }

   @Override
   public void renderHead(IHeaderResponse response) {
      response.render(JS_JQUERY);
      response.render(JS_VALIDATOR_REFERENCE);
      response.render(JS_JQUERY_COOKIE);
      response.render(JS_BOOTSTRAP_3_DATEPICKER);
      response.render(CSS_BOOTSTRAP_3_DATEPICKER);

      super.renderHead(response);
   }
}
