package br.com.netbrasoft.gnuob.shop.page.error;

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SHOPPER_DATA_PROVIDER_NAME;

import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import br.com.netbrasoft.gnuob.shop.border.ContentBorder;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.page.BasePage;
import br.com.netbrasoft.gnuob.shop.panel.error.ExpiredPanel;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;

@MountPath(ExpiredPage.EXPIRED_HTML_VALUE)
public class ExpiredPage extends BasePage {

  private static final String EXPIRED_PANEL_ID = "expiredPanel";

  private static final String CONTENT_BORDER_ID = "contentBorder";

  protected static final String EXPIRED_HTML_VALUE = "expired.html";

  private static final long serialVersionUID = 5008565787309933520L;

  private final ContentBorder contentBorder;

  private final ExpiredPanel expiredPanel;

  @SpringBean(name = SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  public ExpiredPage() {
    contentBorder = new ContentBorder(CONTENT_BORDER_ID, Model.of(new Shopper()));
    expiredPanel = new ExpiredPanel(EXPIRED_PANEL_ID);
  }

  @Override
  protected void onInitialize() {
    contentBorder.setDefaultModelObject(shopperDataProvider.find(new Shopper()));
    contentBorder.add(expiredPanel);
    add(contentBorder);
    super.onInitialize();
  }
}
