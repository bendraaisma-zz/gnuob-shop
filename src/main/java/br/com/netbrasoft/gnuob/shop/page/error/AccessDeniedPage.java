package br.com.netbrasoft.gnuob.shop.page.error;

import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import br.com.netbrasoft.gnuob.shop.border.ContentBorder;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.page.BasePage;
import br.com.netbrasoft.gnuob.shop.panel.error.AccessDeniedPanel;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;
import br.com.netbrasoft.gnuob.shop.shopper.ShopperDataProvider;

@MountPath(AccessDeniedPage.ACCESS_DENIED_HTML_VALUE)
public class AccessDeniedPage extends BasePage {

  private static final String CONTENT_BORDER_ID = "contentBorder";

  private static final String ACCESS_DENIED_PANEL_ID = "accessDeniedPanel";

  protected static final String ACCESS_DENIED_HTML_VALUE = "accessDenied.html";

  private static final long serialVersionUID = -738433892437166398L;

  private final AccessDeniedPanel accessDeniedPanel;

  private final ContentBorder contentBorder;

  @SpringBean(name = ShopperDataProvider.SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  public AccessDeniedPage() {
    accessDeniedPanel = new AccessDeniedPanel(ACCESS_DENIED_PANEL_ID);
    contentBorder = new ContentBorder(CONTENT_BORDER_ID, Model.of(new Shopper()));
  }

  @Override
  protected void onInitialize() {
    contentBorder.setDefaultModelObject(shopperDataProvider.find(new Shopper()));
    contentBorder.add(accessDeniedPanel);
    add(contentBorder);
    super.onInitialize();
  }
}
