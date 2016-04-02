package com.netbrasoft.gnuob.shop.border;

import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.model.IModel;

import com.netbrasoft.gnuob.shop.panel.FooterPanel;
import com.netbrasoft.gnuob.shop.panel.HeaderPanel;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@SuppressWarnings("unchecked")
public class ContentBorder extends Border {

  private static final String FOOTER_PANEL_ID = "footerPanel";

  private static final String HEADER_PANEL_ID = "headerPanel";

  private static final long serialVersionUID = 6569587142042286311L;

  private final HeaderPanel headerPanel;

  private final FooterPanel footerPanel;

  public ContentBorder(final String id, final IModel<Shopper> model) {
    super(id, model);
    headerPanel = new HeaderPanel(HEADER_PANEL_ID, (IModel<Shopper>) ContentBorder.this.getDefaultModel());
    footerPanel = new FooterPanel(FOOTER_PANEL_ID, (IModel<Shopper>) ContentBorder.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    super.onInitialize();
    addToBorder(headerPanel.setOutputMarkupId(true));
    addToBorder(footerPanel.setOutputMarkupId(true));
  }
}
