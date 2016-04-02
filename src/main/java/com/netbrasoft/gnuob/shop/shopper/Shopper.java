package com.netbrasoft.gnuob.shop.shopper;

import java.math.BigDecimal;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.io.IClusterable;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;

import com.netbrasoft.gnuob.api.Address;
import com.netbrasoft.gnuob.api.Contract;
import com.netbrasoft.gnuob.api.Customer;
import com.netbrasoft.gnuob.api.Invoice;
import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.OfferRecord;
import com.netbrasoft.gnuob.api.Option;
import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.Shipment;
import com.netbrasoft.gnuob.api.SubOption;

@Cacheable()
public class Shopper implements IClusterable {

  private static final String PERMISSION_IGNORE_PROPERTIES = "permission";

  private static final String VERSION_IGNORE_PROPERTIES = "version";

  private static final String ID_IGNORE_PROPERTIES = "id";

  private static final long serialVersionUID = -3944018215261797780L;

  private String id = UUID.randomUUID().toString();

  private String issuer = "";

  private Offer cart = new Offer();

  private Order checkout = new Order();

  private boolean loggedIn = false;

  public synchronized void calculateCart() {
    calculateDiscountTotal();
    calculateHandlingTotal();
    calculateTaxTotal();
    calculateInsuranceTotal();
    calculateExtraAmount();
    calculateItemTotal();
    calculateShippingTotal();
    calculateShippingDiscount();
    calculateOfferTotal();
    calculateMaxTotal();
  }

  private void calculateDiscountTotal() {
    BigDecimal discountTotal = BigDecimal.ZERO;
    for (final OfferRecord offerRecord : cart.getRecords()) {
      if (offerRecord.getDiscount() == null) {
        offerRecord.setDiscount(offerRecord.getProduct().getDiscount());
      }
      discountTotal = discountTotal.add(offerRecord.getDiscount().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
    }
    cart.setDiscountTotal(discountTotal);
  }

  private void calculateExtraAmount() {
    BigDecimal extraAmount = BigDecimal.ZERO;
    extraAmount = extraAmount.add(cart.getHandlingTotal()).add(cart.getTaxTotal()).add(cart.getInsuranceTotal());
    cart.setExtraAmount(extraAmount);
  }

  private void calculateHandlingTotal() {
    if (cart.getHandlingTotal() == null) {
      cart.setHandlingTotal(BigDecimal.ZERO);
    }
  }

  private void calculateInsuranceTotal() {
    if (cart.getInsuranceTotal() == null) {
      cart.setInsuranceTotal(BigDecimal.ZERO);
    }
  }

  private void calculateItemTotal() {
    BigDecimal itemTotal = BigDecimal.ZERO;
    for (final OfferRecord offerRecord : cart.getRecords()) {
      if (offerRecord.getAmount() == null && offerRecord.getDiscount() == null) {
        offerRecord.setAmount(offerRecord.getProduct().getAmount().subtract(offerRecord.getProduct().getDiscount()));
      } else {
        if (offerRecord.getAmount() == null) {
          offerRecord.setAmount(offerRecord.getProduct().getAmount().subtract(offerRecord.getDiscount()));
        }
      }
      itemTotal = itemTotal.add(offerRecord.getAmount().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
    }
    cart.setItemTotal(itemTotal);
  }

  private void calculateMaxTotal() {
    BigDecimal maxTotal = BigDecimal.ZERO;
    maxTotal = maxTotal.add(cart.getOfferTotal());
    cart.setMaxTotal(maxTotal);
  }

  private void calculateOfferTotal() {
    BigDecimal offerTotal = BigDecimal.ZERO;
    offerTotal = offerTotal.add(cart.getItemTotal()).add(cart.getShippingTotal()).subtract(cart.getShippingDiscount()).add(cart.getExtraAmount());
    cart.setOfferTotal(offerTotal);
  }

  private void calculateShippingDiscount() {
    if (cart.getShippingDiscount() == null) {
      cart.setShippingDiscount(BigDecimal.ZERO);
    }
  }

  private void calculateShippingTotal() {
    BigDecimal shippingTotal = BigDecimal.ZERO;
    for (final OfferRecord offerRecord : cart.getRecords()) {
      if (offerRecord.getShippingCost() == null) {
        offerRecord.setShippingCost(offerRecord.getProduct().getShippingCost());
      }
      shippingTotal = shippingTotal.add(offerRecord.getShippingCost().multiply(new BigDecimal(offerRecord.getQuantity())));
    }
    cart.setShippingTotal(shippingTotal);
  }

  private void calculateTaxTotal() {
    BigDecimal taxTotal = BigDecimal.ZERO;
    for (final OfferRecord offerRecord : cart.getRecords()) {
      if (offerRecord.getTax() == null) {
        offerRecord.setTax(offerRecord.getProduct().getTax());
      }
      taxTotal = taxTotal.add(offerRecord.getTax().multiply(new BigDecimal(offerRecord.getQuantity())));
    }
    cart.setTaxTotal(taxTotal);
  }

  private Offer copyPropertiesCart(final Offer sourceOffer) {
    final Offer targetOffer = new Offer();
    BeanUtils.copyProperties(sourceOffer, targetOffer, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES, PERMISSION_IGNORE_PROPERTIES);
    for (final OfferRecord sourceOfferRecord : sourceOffer.getRecords()) {
      final OfferRecord targetOfferRecord = new OfferRecord();
      BeanUtils.copyProperties(sourceOfferRecord, targetOfferRecord, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
      for (final Option sourceRootOption : sourceOfferRecord.getOptions()) {
        final Option targetRootOption = new Option();
        BeanUtils.copyProperties(sourceRootOption, targetRootOption, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
        for (final SubOption sourceChildSubOption : sourceRootOption.getSubOptions()) {
          final SubOption targetChildSubOption = new SubOption();
          BeanUtils.copyProperties(sourceChildSubOption, targetChildSubOption, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
          targetRootOption.getSubOptions().add(targetChildSubOption);
        }
        targetOfferRecord.getOptions().add(targetRootOption);
      }
      targetOffer.getRecords().add(targetOfferRecord);
    }
    return targetOffer;
  }

  public void emptyCart(final Contract contract) {
    cart = copyPropertiesCart(cart);
    cart.setContract(contract);
    cart.setActive(true);
  }

  public void emptyCheckOut(final Contract contract) {
    checkout = new Order();
    checkout.setContract(contract);
    checkout.setShipment(new Shipment());
    checkout.getShipment().setAddress(new Address());
    checkout.setInvoice(new Invoice());
    checkout.getInvoice().setAddress(new Address());
    checkout.setActive(true);
  }

  public Contract emptyContract() {
    final Contract contract = new Contract();
    contract.setActive(true);
    contract.setCustomer(new Customer());
    contract.getCustomer().setAddress(new Address());
    contract.getCustomer().setActive(true);
    return contract;
  }

  public Offer getCart() {
    return cart;
  }

  public Order getCheckout() {
    return checkout;
  }

  public Contract getContract() {
    return cart.getContract();
  }

  public String getId() {
    return id;
  }

  public String getIssuer() {
    return issuer;
  }

  public synchronized boolean isLoggedIn() {
    return loggedIn;
  }

  public synchronized boolean login() {
    return issuer != null && !"".equals(issuer) && RequestCycle.get().getRequest().getClientUrl().getQueryParameter("state") != null;
  }

  public synchronized void logout() {
    final Contract contract = emptyContract();
    emptyCheckOut(contract);
    emptyCart(contract);
    issuer = "";
    loggedIn = false;
  }

  public synchronized void setCart(@NotNull final Offer cart) {
    this.cart = cart;
  }

  public synchronized void setCheckout(@NotNull final Order checkout) {
    this.checkout = checkout;
  }

  public synchronized void setContract(@NotNull final Contract contract) {
    this.cart.setContract(contract);
    this.checkout.setContract(contract);
  }

  public void setId(final String id) {
    this.id = id;
  }

  public synchronized void setIssuer(@NotNull final String issuer) {
    this.issuer = issuer;
  }

  public void setLoggedIn(final boolean loggedIn) {
    this.loggedIn = loggedIn;
  }
}
