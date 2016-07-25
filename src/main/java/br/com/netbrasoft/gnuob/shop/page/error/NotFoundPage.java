package br.com.netbrasoft.gnuob.shop.page.error;

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SHOPPER_DATA_PROVIDER_NAME;

import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import br.com.netbrasoft.gnuob.shop.border.ContentBorder;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.page.BasePage;
import br.com.netbrasoft.gnuob.shop.panel.error.NotFoundPanel;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;

@MountPath(NotFoundPage.NOT_FOUND_HTML_VALUE)
public class NotFoundPage extends BasePage {

  private static final String NOT_FOUND_PANEL_ID = "notFoundPanel";

  private static final String CONTENT_BORDER_ID = "contentBorder";

  static final String NOT_FOUND_HTML_VALUE = "notFound.html";

  private static final long serialVersionUID = -4283181889540584265L;

  private final ContentBorder contentBorder;

  private final NotFoundPanel notFoundPanel;

  @SpringBean(name = SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  public NotFoundPage() {
    contentBorder = new ContentBorder(CONTENT_BORDER_ID, Model.of(new Shopper()));
    notFoundPanel = new NotFoundPanel(NOT_FOUND_PANEL_ID);
  }

  @Override
  protected void onInitialize() {
    contentBorder.setDefaultModelObject(shopperDataProvider.find(new Shopper()));
    contentBorder.add(notFoundPanel);
    add(contentBorder);
    super.onInitialize();
  }
}
