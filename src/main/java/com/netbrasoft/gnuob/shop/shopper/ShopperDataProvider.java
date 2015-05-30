package com.netbrasoft.gnuob.shop.shopper;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.javasimon.aop.Monitored;
import org.springframework.stereotype.Controller;

import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheRepository;

@Monitored
@Controller("ShopperDataProvider")
public class ShopperDataProvider<S extends Shopper> implements GenericTypeCacheDataProvider<S> {

   private static final String SHOPPER_ID = "SHOPPER_ID";

   @Resource(name = "ShopperCacheRepository")
   private transient GenericTypeCacheRepository<S> shopperCacheRepository;

   @Override
   public S merge(S type) {
      return shopperCacheRepository.merge(setShopperId(type));
   }

   @Override
   public S find(S type) {
      return shopperCacheRepository.find(setShopperId(type));
   }

   @Override
   public void remove(S type) {
      shopperCacheRepository.remove(setShopperId(type));
   }

   private S setShopperId(S type) {
      List<Cookie> cookies = ((WebRequest) RequestCycle.get().getRequest()).getCookies();
      Cookie shopperId = new Cookie(SHOPPER_ID, type.getId());

      if (cookies != null) {
         for (Cookie cookie : cookies) {
            if (SHOPPER_ID.equals(cookie.getName())) {
               shopperId.setValue(cookie.getValue());
               type.setId(cookie.getValue());
               break;
            }
         }
      }

      ((WebResponse) RequestCycle.get().getResponse()).addCookie(shopperId);
      return type;
   }
}
