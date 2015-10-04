package com.netbrasoft.gnuob.shop.shopper;

import java.math.BigDecimal;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.io.IClusterable;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;

import com.netbrasoft.gnuob.api.Contract;
import com.netbrasoft.gnuob.api.Customer;
import com.netbrasoft.gnuob.api.Offer;
import com.netbrasoft.gnuob.api.OfferRecord;
import com.netbrasoft.gnuob.api.Option;
import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.SubOption;

@Cacheable()
public class Shopper implements IClusterable {

  private static final long serialVersionUID = -3944018215261797780L;

  private String id = UUID.randomUUID().toString();

  private String issuer = "";

  private Contract contract = new Contract();

  private Offer cart = new Offer();

  private Order checkout = new Order();

  private boolean loggedIn = false;

  public Shopper() {}

  private Offer copyPropertiesCart(final Offer sourceCart) {
    final Offer targetCart = new Offer();

    BeanUtils.copyProperties(sourceCart, targetCart, "id", "version", "permission");

    for (final OfferRecord sourceOfferRecord : sourceCart.getRecords()) {
      final OfferRecord targetOfferRecord = new OfferRecord();

      BeanUtils.copyProperties(sourceOfferRecord, targetOfferRecord, "id", "version");

      for (final Option sourceRootOption : sourceOfferRecord.getOptions()) {
        final Option targetRootOption = new Option();

        BeanUtils.copyProperties(sourceRootOption, targetRootOption, "id", "version");

        for (final SubOption sourceChildSubOption : sourceRootOption.getSubOptions()) {
          final SubOption targetChildSubOption = new SubOption();

          BeanUtils.copyProperties(sourceChildSubOption, targetChildSubOption, "id", "version");
          targetRootOption.getSubOptions().add(targetChildSubOption);
        }

        targetOfferRecord.getOptions().add(targetRootOption);
      }

      targetCart.getRecords().add(targetOfferRecord);
    }

    return targetCart;
  }

  public Offer getCart() {
    return cart;
  }

  public BigDecimal getCartTotal() {
    BigDecimal total = BigDecimal.ZERO;

    for (final OfferRecord offerRecord : cart.getRecords()) {
      total = total.add(offerRecord.getAmount().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
    }
    return total.add(getTaxTotal()).add(getShippingCostTotal());
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

  public boolean isLoggedIn() {
    return loggedIn;
  }

  public boolean login() {
    return issuer != null && !"".equals(issuer) && RequestCycle.get().getRequest().getClientUrl().getQueryParameter("state") != null;
  }

  public void logout() {
    issuer = "";
    loggedIn = false;

    contract = new Contract();
    contract.setActive(true);
    contract.setCustomer(new Customer());
    contract.getCustomer().setActive(true);

    checkout = new Order();
    checkout.setActive(true);

    // Make new copy of current cart in a new cart.
    cart = copyPropertiesCart(cart);
    cart.setActive(true);
  }

  public void setCart(@NotNull Offer cart) {
    this.cart = cart;
  }

  public void setCheckout(@NotNull Order checkout) {
    this.checkout = checkout;
  }

  public void setContract(@NotNull Contract contract) {
    this.contract = contract;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setIsLoggedIn(boolean loggedIn) {
    this.loggedIn = loggedIn;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }
}
