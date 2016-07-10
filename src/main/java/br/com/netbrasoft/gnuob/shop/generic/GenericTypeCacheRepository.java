package br.com.netbrasoft.gnuob.shop.generic;

public interface GenericTypeCacheRepository<T> {

  T find(T type);

  T merge(T type);

  void remove(T type);
}
