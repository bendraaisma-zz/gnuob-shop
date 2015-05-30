package com.netbrasoft.gnuob.shop.shopper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.wicket.util.io.IClusterable;
import org.springframework.cache.annotation.Cacheable;

import com.netbrasoft.gnuob.api.Customer;
import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.Order;

@Cacheable()
public class Shopper implements IClusterable {

   private static final long serialVersionUID = -3944018215261797780L;

   private String id = UUID.randomUUID().toString();

   private Customer customer = new Customer();

   private Offer cart = new Offer();

   private List<Offer> offers = new ArrayList<Offer>();

   private List<Order> orders = new ArrayList<Order>();

   public Offer getCart() {
      return cart;
   }

   public Customer getCustomer() {
      return customer;
   }

   public String getId() {
      return id;
   }

   public List<Offer> getOffers() {
      return offers;
   }

   public List<Order> getOrders() {
      return orders;
   }

   public void setCart(Offer cart) {
      this.cart = cart;
   }

   public void setCustomer(Customer customer) {
      this.customer = customer;
   }

   public void setId(String id) {
      this.id = id;
   }

   public void setOffers(List<Offer> offers) {
      this.offers = offers;
   }

   public void setOrders(List<Order> orders) {
      this.orders = orders;
   }
}
