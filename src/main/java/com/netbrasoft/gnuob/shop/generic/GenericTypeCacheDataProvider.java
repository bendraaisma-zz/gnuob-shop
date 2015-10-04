package com.netbrasoft.gnuob.shop.generic;

public interface GenericTypeCacheDataProvider<T> {

  T find(T type);

  T merge(T type);

  void remove(T type);
}
