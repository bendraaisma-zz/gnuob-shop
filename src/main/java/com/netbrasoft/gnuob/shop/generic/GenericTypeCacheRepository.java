package com.netbrasoft.gnuob.shop.generic;

public interface GenericTypeCacheRepository<T> {

  T find(T paramType);

  T merge(T paramType);

  void remove(T paramType);
}
