package com.netbrasoft.gnuob.shop.html;

import org.apache.wicket.request.resource.CssResourceReference;

public class NetbrasoftShopCssReference extends CssResourceReference {

  private static final long serialVersionUID = 9180570471942894240L;

  public NetbrasoftShopCssReference(final String swatchName) {
    super(NetbrasoftShopCssReference.class, "css/bootstrap." + swatchName + ".css");
  }
}
