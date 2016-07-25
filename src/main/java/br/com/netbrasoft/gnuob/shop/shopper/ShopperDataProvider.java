package br.com.netbrasoft.gnuob.shop.shopper;

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SHOPPER_DATA_PROVIDER_NAME;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.javasimon.aop.Monitored;
import org.springframework.stereotype.Controller;

import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheRepository;

@Monitored
@Controller(SHOPPER_DATA_PROVIDER_NAME)
public class ShopperDataProvider<T extends Shopper> implements GenericTypeCacheDataProvider<T> {

  private static final String GNUOB_COOKIE_SECURE_ENABLED_PROPERTY = "gnuob.cookie.secure.enabled";

  private static final String FALSE = "false";

  private static final String TRUE = "true";

  private static final int ONE_DAY = 3600;

  private static final String SHOPPER_ID = "SHOPPER_ID";

  @Resource(name = ShopperCacheRepository.SHOPPER_CACHE_REPOSITORY_NAME)
  private GenericTypeCacheRepository<T> shopperCacheRepository;

  @Override
  public T find(final T type) {
    return shopperCacheRepository.find(setShopperId(type));
  }

  @Override
  public T merge(final T type) {
    return shopperCacheRepository.merge(setShopperId(type));
  }

  @Override
  public void remove(final T type) {
    shopperCacheRepository.remove(setShopperId(type));
  }

  private T setShopperId(final T type) {
    final List<Cookie> cookies = ((WebRequest) RequestCycle.get().getRequest()).getCookies();
    final Cookie shopperId = new Cookie(SHOPPER_ID, type.getId());
    for (final Cookie cookie : cookies) {
      if (SHOPPER_ID.equals(cookie.getName())) {
        shopperId.setValue(cookie.getValue());
        if (TRUE.equalsIgnoreCase(System.getProperty(GNUOB_COOKIE_SECURE_ENABLED_PROPERTY, FALSE))) {
          shopperId.setSecure(true);
        }
        shopperId.setMaxAge(ONE_DAY);
        type.setId(cookie.getValue());
        break;
      }
    }
    ((WebResponse) RequestCycle.get().getResponse()).addCookie(shopperId);
    return type;
  }
}
