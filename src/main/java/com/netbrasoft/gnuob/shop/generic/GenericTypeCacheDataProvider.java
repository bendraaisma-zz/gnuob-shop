package com.netbrasoft.gnuob.shop.generic;

public interface GenericTypeCacheDataProvider<T> {

   T merge(T type);

   void remove(T type);

   T find(T type);
}
