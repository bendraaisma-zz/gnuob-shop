package br.com.netbrasoft.gnuob.shop.html;

import java.util.List;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;

import com.google.common.collect.ImmutableList;

import de.agilecoders.wicket.core.settings.ITheme;
import de.agilecoders.wicket.core.settings.ThemeProvider;

public class NetbrasoftShopThemeProvider implements ThemeProvider {

  private final List<ITheme> themes;
  private final ITheme defaultTheme;

  public NetbrasoftShopThemeProvider(final ITheme defaultTheme) {
    this.themes = ImmutableList.<ITheme>builder().add(NetbrasoftShopTheme.values()).build();
    this.defaultTheme = Args.notNull(defaultTheme, "defaultTheme");
  }

  public NetbrasoftShopThemeProvider(final String name) {
    this(NetbrasoftShopTheme.valueOf(name));
  }

  @Override
  public List<ITheme> available() {
    return themes;
  }

  @Override
  public ITheme byName(final String name) {
    if (!Strings.isEmpty(name)) {
      for (final ITheme theme : themes) {
        if (name.equalsIgnoreCase(theme.name())) {
          return theme;
        }
      }
    }
    throw new WicketRuntimeException("theme does not exists: " + name);
  }

  @Override
  public ITheme defaultTheme() {
    return defaultTheme;
  }
}
