package com.netbrasoft.gnuob.shop.shopper;

import java.util.UUID;

import org.apache.wicket.util.io.IClusterable;
import org.springframework.cache.annotation.Cacheable;

import com.netbrasoft.gnuob.api.Contract;
import com.netbrasoft.gnuob.api.Offer;

@Cacheable()
public class Shopper implements IClusterable {

   private static final long serialVersionUID = -3944018215261797780L;

   private String id = UUID.randomUUID().toString();

   private Contract contract = new Contract();

   private Offer cart = new Offer();

   public Offer getCart() {
      return cart;
   }

   public Contract getContract() {
      return contract;
   }

   public String getId() {
      return id;
   }

   public void setCart(Offer cart) {
      this.cart = cart;
   }

   public void setContract(Contract contract) {
      this.contract = contract;
   }

   public void setId(String id) {
      this.id = id;
   }
}
