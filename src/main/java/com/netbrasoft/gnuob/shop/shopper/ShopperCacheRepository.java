package com.netbrasoft.gnuob.shop.shopper;

import org.javasimon.aop.Monitored;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheRepository;

@CacheConfig(cacheNames = {"com.netbrasoft.gnuob.shop.shopper.Shopper"})
@Monitored
@Repository("ShopperCacheRepository")
public class ShopperCacheRepository<S extends Shopper> implements GenericTypeCacheRepository<S> {

  @Cacheable(key = "#paramType.id", condition = "#paramType.id != null")
  @Override
  public S find(S paramType) {
    return paramType;
  }

  @CachePut(key = "#paramType.id", condition = "#paramType.id != null")
  @Override
  public S merge(S paramType) {
    return paramType;
  }

  @CacheEvict(key = "#paramType.id", condition = "#paramType.id != null")
  @Override
  public void remove(S paramType) {
    return;
  }
}
