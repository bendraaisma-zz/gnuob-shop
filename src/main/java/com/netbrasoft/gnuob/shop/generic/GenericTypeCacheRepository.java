package com.netbrasoft.gnuob.shop.generic;

public interface GenericTypeCacheRepository<T> {

   T merge(T paramType);

   T find(T paramType);

   void remove(T paramType);
}
