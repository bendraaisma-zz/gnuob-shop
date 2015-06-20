package com.netbrasoft.gnuob.shop.shopper;

import java.math.BigDecimal;
import java.util.UUID;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.io.IClusterable;
import org.springframework.cache.annotation.Cacheable;

import com.netbrasoft.gnuob.api.Contract;
import com.netbrasoft.gnuob.api.Customer;
import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.OfferRecord;
import com.netbrasoft.gnuob.api.Order;

@Cacheable()
public class Shopper implements IClusterable {

   private static final long serialVersionUID = -3944018215261797780L;

   private String id = UUID.randomUUID().toString();

   private String issuer = "";

   private Contract contract = new Contract();

   private Offer cart = new Offer();

   private Order checkout = new Order();

   public Shopper() {
      logout();
   }

   public Offer getCart() {
      return cart;
   }

   public BigDecimal getCartTotal() {
      BigDecimal total = BigDecimal.ZERO;

      for (final OfferRecord offerRecord : cart.getRecords()) {
         total = total.add(offerRecord.getAmount().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
      }
      return total.add(getTaxTotal()).add(getShippingCostTotal()).subtract(getCartTotalDiscount());
   }

   public BigDecimal getCartTotalDiscount() {
      BigDecimal discountTotal = BigDecimal.ZERO;

      for (final OfferRecord offerRecord : cart.getRecords()) {
         discountTotal = discountTotal.add(offerRecord.getDiscount().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
      }
      return discountTotal;
   }

   public Order getCheckout() {
      return checkout;
   }

   public Contract getContract() {
      return contract;
   }

   public String getId() {
      return id;
   }

   public String getIssuer() {
      return issuer;
   }

   public String getOrderId() {
      return checkout.getOrderId();
   }

   public BigDecimal getShippingCostTotal() {
      BigDecimal shippingCostTotal = BigDecimal.ZERO;

      for (final OfferRecord offerRecord : cart.getRecords()) {
         shippingCostTotal = shippingCostTotal.add(offerRecord.getShippingCost().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
      }
      return shippingCostTotal;
   }

   public BigDecimal getTaxTotal() {
      BigDecimal taxTotal = BigDecimal.ZERO;

      for (final OfferRecord offerRecord : cart.getRecords()) {
         taxTotal = taxTotal.add(offerRecord.getTax().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
      }
      return taxTotal;
   }

   public boolean loggedIn() {
      return contract.getContractId() != null && !"".equals(contract.getContractId()) && contract.getId() != 0;
   }

   public boolean login() {
      return issuer != null && !issuer.equals("") && RequestCycle.get().getRequest().getClientUrl().getQueryParameter("state") != null;
   }

   public void logout() {
      contract = new Contract();
      contract.setActive(true);
      contract.setCustomer(new Customer());
      contract.getCustomer().setActive(true);
   }

   public void setCart(Offer cart) {
      this.cart = cart;
   }

   public void setCheckout(Order checkout) {
      this.checkout = checkout;
   }

   public void setContract(Contract contract) {
      this.contract = contract;
   }

   public void setId(String id) {
      this.id = id;
   }

   public void setIssuer(String issuer) {
      this.issuer = issuer;
   }

   public void setOrderId(String orderId) {
      this.checkout.setOrderId(orderId);
   }
}
