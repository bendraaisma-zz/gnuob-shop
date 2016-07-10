package br.com.netbrasoft.gnuob.shop.shopper;

import org.javasimon.aop.Monitored;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheRepository;

@CacheConfig(cacheNames = {"br.com.netbrasoft.gnuob.shop.shopper.Shopper"})
@Monitored
@Repository(ShopperCacheRepository.SHOPPER_CACHE_REPOSITORY_NAME)
public class ShopperCacheRepository<T extends Shopper> implements GenericTypeCacheRepository<T> {

  protected static final String SHOPPER_CACHE_REPOSITORY_NAME = "ShopperCacheRepository";

  @Cacheable(key = "#type.id", condition = "#type.id != null")
  @Override
  public T find(final T type) {
    return type;
  }

  @CachePut(key = "#type.id", condition = "#type.id != null")
  @Override
  public T merge(final T type) {
    return type;
  }

  @CacheEvict(key = "#type.id", condition = "#type.id != null")
  @Override
  public void remove(final T type) {
    return;
  }
}
