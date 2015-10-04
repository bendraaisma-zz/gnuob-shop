package com.netbrasoft.gnuob.shop.confirmation;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Url.QueryParameter;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class ConfirmationPanel extends Panel {

  private static final String TRANSACTION_ID = "transaction_id";

  private static final String PAYER_ID = "PayerID";

  private static final long serialVersionUID = -305453593387396902L;

  private final ConfirmationViewPanel confirmationViewPanel;

  @SpringBean(name = "ShopperDataProvider", required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  public ConfirmationPanel(final String id, final IModel<Shopper> model) {
    super(id, model);
    confirmationViewPanel = new ConfirmationViewPanel("confirmationViewPanel", model);
  }

  @Override
  protected void onInitialize() {
    final QueryParameter payerId = getRequest().getClientUrl().getQueryParameter(PAYER_ID);
    final QueryParameter transactionId = getRequest().getClientUrl().getQueryParameter(TRANSACTION_ID);
    final Shopper shopper = shopperDataProvider.find(new Shopper());

    // Checkout if the returned customer from the payment provider has a valid payed order.
    if ((payerId != null || transactionId != null) && shopper.getCheckout().getOrderId() != null) {
      add(confirmationViewPanel.add(confirmationViewPanel.new ConfirmationViewFragement()).setOutputMarkupId(true));
      super.onInitialize();
    } else {
      throw new RedirectToUrlException("specification.html");
    }
  }
}
