package com.netbrasoft.gnuob.shop.panel;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

import de.agilecoders.wicket.core.markup.html.bootstrap.block.BadgeBehavior;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class HeaderPanel extends Panel {

   private static final long serialVersionUID = 3137234732197409313L;
   private static final String GNUOB_SITE_TITLE_PROPERTY = "gnuob.shop.site.title";
   private static final String GNUOB_SITE_SUBTITLE_PROPERTY = "gnuob.shop.site.subtitle";

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

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

      add(new Label("chartSize", Model.of(shopperDataProvider.find(new Shopper()).getCart().size())).add(new BadgeBehavior()));

      super.onInitialize();
   }
}
