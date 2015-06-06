package com.netbrasoft.gnuob.shop.shopper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.io.IClusterable;
import org.springframework.cache.annotation.Cacheable;

import com.netbrasoft.gnuob.api.Contract;
import com.netbrasoft.gnuob.api.Customer;
import com.netbrasoft.gnuob.api.OfferRecord;

@Cacheable()
public class Shopper implements IClusterable {

   private static final long serialVersionUID = -3944018215261797780L;

   private String id = UUID.randomUUID().toString();

   private String issuer = "";

   private Contract contract = new Contract();

   private List<OfferRecord> cart = new ArrayList<OfferRecord>();

   public Shopper() {
      logout();
   }

   public List<OfferRecord> getCart() {
      return cart;
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

   public boolean login() {
      return issuer != null && !issuer.equals("") && RequestCycle.get().getRequest().getClientUrl().getQueryParameter("state") != null;
   }

   public void logout() {
      contract.setActive(true);
      contract.setCustomer(new Customer());
      contract.getCustomer().setActive(true);
   }

   public boolean loggedIn() {
      return contract.getContractId() != null && !"".equals(contract.getContractId()) && contract.getId() != 0;
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

   public BigDecimal getChartTotal() {
      BigDecimal total = BigDecimal.ZERO;

      for (OfferRecord offerRecord : cart) {
         if (offerRecord.getProduct() != null) {
            total = total.add(offerRecord.getProduct().getAmount().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
         } else {
            total = total.add(offerRecord.getAmount().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
         }
      }
      return total.subtract(getChartTotalDiscount());
   }

   public BigDecimal getChartTotalDiscount() {
      BigDecimal discountTotal = BigDecimal.ZERO;

      for (OfferRecord offerRecord : cart) {
         if (offerRecord.getProduct() != null) {
            discountTotal = discountTotal.add(offerRecord.getProduct().getDiscount().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
         } else {
            discountTotal = discountTotal.add(offerRecord.getDiscount().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
         }
      }
      return discountTotal;
   }
}