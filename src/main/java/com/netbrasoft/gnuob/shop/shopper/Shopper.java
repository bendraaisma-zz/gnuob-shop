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

   public BigDecimal getChartTotal() {
      BigDecimal total = BigDecimal.ZERO;

      for (OfferRecord offerRecord : cart) {
         if (offerRecord.getProduct() != null) {
            total = total.add(offerRecord.getProduct().getAmount().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
         } else {
            total = total.add(offerRecord.getAmount().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
         }
      }
      return total.add(getTaxTotal()).add(getShippingCostTotal()).subtract(getChartTotalDiscount());
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

   public Contract getContract() {
      return contract;
   }

   public String getId() {
      return id;
   }

   public String getIssuer() {
      return issuer;
   }

   public BigDecimal getShippingCostTotal() {
      BigDecimal shippingCostTotal = BigDecimal.ZERO;

      for (OfferRecord offerRecord : cart) {
         if (offerRecord.getProduct() != null) {
            shippingCostTotal = shippingCostTotal.add(offerRecord.getProduct().getShippingCost().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
         } else {
            shippingCostTotal = shippingCostTotal.add(offerRecord.getShippingCost().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
         }
      }
      return shippingCostTotal;
   }

   public BigDecimal getTaxTotal() {
      BigDecimal taxTotal = BigDecimal.ZERO;

      for (OfferRecord offerRecord : cart) {
         if (offerRecord.getProduct() != null) {
            taxTotal = taxTotal.add(offerRecord.getProduct().getTax().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
         } else {
            taxTotal = taxTotal.add(offerRecord.getTax().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
         }
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

   public void setContract(Contract contract) {
      this.contract = contract;
   }

   public void setId(String id) {
      this.id = id;
   }

   public void setIssuer(String issuer) {
      this.issuer = issuer;
   }
}
