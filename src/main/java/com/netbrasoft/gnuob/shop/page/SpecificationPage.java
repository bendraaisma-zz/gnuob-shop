package com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.shop.border.ContentBorder;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;
import com.netbrasoft.gnuob.shop.shopper.ShopperDataProvider;
import com.netbrasoft.gnuob.shop.specification.SpecificationMainMenuPanel;

@MountPath(SpecificationPage.SPECIFICATION_HTML_VALUE)
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class SpecificationPage extends BasePage {

  public static final String SPECIFICATION_HTML_VALUE = "specification.html";

  private static final String CONTENT_BORDER_ID = "contentBorder";

  private static final String MAIN_MENU_PANEL_ID = "mainMenuPanel";

  private static final long serialVersionUID = 120059668578792943L;

  private final SpecificationMainMenuPanel mainMenuPanel;

  private final ContentBorder contentBorder;

  @SpringBean(name = ShopperDataProvider.SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  public SpecificationPage() {
    mainMenuPanel = new SpecificationMainMenuPanel(MAIN_MENU_PANEL_ID);
    contentBorder = new ContentBorder(CONTENT_BORDER_ID, Model.of(new Shopper()));
  }

  @Override
  protected void onInitialize() {
    contentBorder.setDefaultModelObject(shopperDataProvider.find(new Shopper()));
    contentBorder.add(mainMenuPanel);
    add(contentBorder);
    super.onInitialize();
  }
}
