package br.com.netbrasoft.gnuob.shop.page.error;

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SHOPPER_DATA_PROVIDER_NAME;

import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import br.com.netbrasoft.gnuob.shop.border.ContentBorder;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.page.BasePage;
import br.com.netbrasoft.gnuob.shop.panel.error.InternalErrorPanel;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;

@MountPath(InternalErrorPage.INTERNAL_ERROR_HTML_VALUE)
public class InternalErrorPage extends BasePage {

  private static final String INTERNAL_ERROR_PANEL_ID = "internalErrorPanel";

  private static final String CONTENT_BORDER_ID = "contentBorder";

  protected static final String INTERNAL_ERROR_HTML_VALUE = "internalError.html";

  private static final long serialVersionUID = 7999429438892271530L;

  private final ContentBorder contentBorder;

  private final InternalErrorPanel internalErrorPanel;

  @SpringBean(name = SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  public InternalErrorPage() {
    contentBorder = new ContentBorder(CONTENT_BORDER_ID, Model.of(new Shopper()));
    internalErrorPanel = new InternalErrorPanel(INTERNAL_ERROR_PANEL_ID);
  }

  @Override
  protected void onInitialize() {
    contentBorder.setDefaultModelObject(shopperDataProvider.find(new Shopper()));
    contentBorder.add(internalErrorPanel);
    add(contentBorder);
    super.onInitialize();
  }
}
