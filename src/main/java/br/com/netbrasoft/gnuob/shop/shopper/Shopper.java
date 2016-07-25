package br.com.netbrasoft.gnuob.shop.shopper;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.io.IClusterable;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;

import br.com.netbrasoft.gnuob.api.Address;
import br.com.netbrasoft.gnuob.api.Contract;
import br.com.netbrasoft.gnuob.api.Customer;
import br.com.netbrasoft.gnuob.api.Invoice;
import br.com.netbrasoft.gnuob.api.Offer;
import br.com.netbrasoft.gnuob.api.OfferRecord;
import br.com.netbrasoft.gnuob.api.Option;
import br.com.netbrasoft.gnuob.api.Order;
import br.com.netbrasoft.gnuob.api.OrderRecord;
import br.com.netbrasoft.gnuob.api.Product;
import br.com.netbrasoft.gnuob.api.Shipment;
import br.com.netbrasoft.gnuob.api.SubOption;

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

  public static final Shopper getInstance() {
    return new Shopper();
  }

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
      discountTotal = discountTotal
          .add(offerRecord.getDiscount().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
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
      itemTotal =
          itemTotal.add(offerRecord.getAmount().multiply(BigDecimal.valueOf(offerRecord.getQuantity().longValue())));
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
    offerTotal = offerTotal.add(cart.getItemTotal()).add(cart.getShippingTotal()).subtract(cart.getShippingDiscount())
        .add(cart.getExtraAmount());
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
      shippingTotal =
          shippingTotal.add(offerRecord.getShippingCost().multiply(new BigDecimal(offerRecord.getQuantity())));
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

  public void addToCart(Product product) {
    final OfferRecord offerRecord = new OfferRecord();
    BeanUtils.copyProperties(product, offerRecord, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
    offerRecord.setProduct(product);
    offerRecord.setProductNumber(product.getNumber());
    offerRecord.setAmount(product.getAmount().subtract(product.getDiscount()));
    offerRecord.setQuantity(BigInteger.ONE);
    for (final Option rootOption : product.getOptions().stream().filter(e -> !e.isDisabled())
        .collect(Collectors.toList())) {
      final Option offerRecordRootOption = new Option();
      BeanUtils.copyProperties(rootOption, offerRecordRootOption, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
      final SubOption offerRecordChildSubOption = new SubOption();
      BeanUtils.copyProperties(rootOption.getSubOptions().stream().filter(e -> !e.isDisabled()).findFirst(),
          offerRecordChildSubOption, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
      offerRecordRootOption.getSubOptions().add(offerRecordChildSubOption);
      offerRecord.getOptions().add(offerRecordRootOption);
    }
    cart.getRecords().add(0, offerRecord);
  }

  public void emptyCart(final Contract contract) {
    cart = copyPropertiesCart(cart);
    cart.setContract(contract);
    cart.setActive(true);
  }

  private Offer copyPropertiesCart(final Offer sourceOffer) {
    final Offer targetOffer = new Offer();
    copyProperties(sourceOffer, targetOffer, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES,
        PERMISSION_IGNORE_PROPERTIES);
    for (final OfferRecord sourceOfferRecord : sourceOffer.getRecords()) {
      final OfferRecord targetOfferRecord = new OfferRecord();
      copyProperties(sourceOfferRecord, targetOfferRecord, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
      for (final Option sourceRootOption : sourceOfferRecord.getOptions()) {
        final Option targetRootOption = new Option();
        copyProperties(sourceRootOption, targetRootOption, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
        for (final SubOption sourceChildSubOption : sourceRootOption.getSubOptions()) {
          final SubOption targetChildSubOption = new SubOption();
          copyProperties(sourceChildSubOption, targetChildSubOption, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
          targetRootOption.getSubOptions().add(targetChildSubOption);
        }
        targetOfferRecord.getOptions().add(targetRootOption);
      }
      targetOffer.getRecords().add(targetOfferRecord);
    }
    return targetOffer;
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
    return issuer != null && !"".equals(issuer)
        && RequestCycle.get().getRequest().getClientUrl().getQueryParameter("state") != null;
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

  public void setCheckOutByOffer(Offer offer) {
    copyProperties(offer, checkout, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
    for (final OfferRecord offerRecord : offer.getRecords()) {
      final OrderRecord orderRecord = new OrderRecord();
      copyProperties(offerRecord, orderRecord, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
      for (final Option offerRecordOption : offerRecord.getOptions()) {
        final Option option = new Option();
        copyProperties(offerRecordOption, option, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
        for (final SubOption offerRecordSubOption : offerRecordOption.getSubOptions()) {
          final SubOption subOption = new SubOption();
          copyProperties(offerRecordSubOption, subOption, ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
          option.getSubOptions().add(subOption);
        }
        orderRecord.getOptions().add(option);
      }
      checkout.getRecords().add(orderRecord);
    }
    checkout.setOrderTotal(offer.getOfferTotal());
    checkout.setOrderDescription(offer.getOfferDescription());
    checkout.setInvoice(new Invoice());
    checkout.setShipment(new Shipment());
    checkout.getInvoice().setAddress(new Address());
    checkout.getShipment().setAddress(new Address());
    if (cart.getContract() != null) {
      copyProperties(cart.getContract().getCustomer().getAddress(), checkout.getInvoice().getAddress(),
          ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
      copyProperties(cart.getContract().getCustomer().getAddress(), checkout.getShipment().getAddress(),
          ID_IGNORE_PROPERTIES, VERSION_IGNORE_PROPERTIES);
    }
  }

  public synchronized Shopper setContract(@NotNull final Contract contract) {
    cart.setContract(contract);
    checkout.setContract(contract);
    return this;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public synchronized Shopper setIssuer(@NotNull final String issuer) {
    this.issuer = issuer;
    return this;
  }

  public void setLoggedIn(final boolean loggedIn) {
    this.loggedIn = loggedIn;
  }
}
