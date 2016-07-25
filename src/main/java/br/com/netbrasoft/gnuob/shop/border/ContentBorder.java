/*
 * Copyright 2016 Netbrasoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package br.com.netbrasoft.gnuob.shop.border;

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.FOOTER_PANEL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.HEADER_PANEL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.UNCHECKED;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.model.IModel;

import br.com.netbrasoft.gnuob.shop.panel.FooterPanel;
import br.com.netbrasoft.gnuob.shop.panel.HeaderPanel;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;

@SuppressWarnings(UNCHECKED)
public class ContentBorder extends Border {

  private static final long serialVersionUID = 6569587142042286311L;

  public ContentBorder(final String id, final IModel<Shopper> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    super.onInitialize();
    addToBorder(getFooterPanelComponent());
    addToBorder(getHeaderPanelComponent());
  }

  private Component getHeaderPanelComponent() {
    return getHeaderPanel().setOutputMarkupId(true);
  }

  private Component getFooterPanelComponent() {
    return getFooterPanel().setOutputMarkupId(true);
  }

  private FooterPanel getFooterPanel() {
    return new FooterPanel(FOOTER_PANEL_ID, (IModel<Shopper>) ContentBorder.this.getDefaultModel());
  }

  private HeaderPanel getHeaderPanel() {
    return new HeaderPanel(HEADER_PANEL_ID, (IModel<Shopper>) ContentBorder.this.getDefaultModel());
  }
}
