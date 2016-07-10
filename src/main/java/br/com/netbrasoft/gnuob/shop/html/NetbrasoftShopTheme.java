package br.com.netbrasoft.gnuob.shop.html;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.resource.ResourceReference;

import de.agilecoders.wicket.core.settings.ITheme;

public enum NetbrasoftShopTheme implements ITheme {
  Blackfalcon, Ribelli, Localhost;

  private final ResourceReference reference;

  private NetbrasoftShopTheme() {
    this.reference = new NetbrasoftShopCssReference(name().toLowerCase());
  }

  @Override
  public Iterable<String> getCdnUrls() {
    throw new NotImplementedException("This method is not implemented by Netbrasoft Shop.");
  }

  @Override
  public List<HeaderItem> getDependencies() {
    return Collections.<HeaderItem>singletonList(CssHeaderItem.forReference(reference).setId(BOOTSTRAP_THEME_MARKUP_ID));
  }

  @Override
  public void renderHead(final IHeaderResponse response) {
    for (final HeaderItem headerItem : getDependencies()) {
      response.render(headerItem);
    }
  }
}
