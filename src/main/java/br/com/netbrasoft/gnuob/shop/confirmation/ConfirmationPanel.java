package br.com.netbrasoft.gnuob.shop.confirmation;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Url.QueryParameter;
import org.apache.wicket.request.flow.RedirectToUrlException;

import br.com.netbrasoft.gnuob.api.Order;
import br.com.netbrasoft.gnuob.shop.page.SpecificationPage;
import br.com.netbrasoft.gnuob.shop.security.ShopRoles;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class ConfirmationPanel extends Panel {

  private static final String CONFIRMATION_VIEW_PANEL_ID = "confirmationViewPanel";

  private static final String TRANSACTION_ID = "transaction_id";

  private static final String PAYER_ID = "PayerID";

  private static final long serialVersionUID = -305453593387396902L;

  private final ConfirmationEmptyOrEditPanel confirmationViewPanel;

  public ConfirmationPanel(final String id, final IModel<Order> model) {
    super(id, model);
    confirmationViewPanel = new ConfirmationEmptyOrEditPanel(CONFIRMATION_VIEW_PANEL_ID, (IModel<Order>) ConfirmationPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    final QueryParameter payerId = getRequest().getClientUrl().getQueryParameter(PAYER_ID);
    final QueryParameter transactionId = getRequest().getClientUrl().getQueryParameter(TRANSACTION_ID);
    // Checkout if the returned customer from the payment provider has a valid payed order.
    if ((payerId != null || transactionId != null) && ((Order) ConfirmationPanel.this.getDefaultModelObject()).getOrderId() != null) {
      add(confirmationViewPanel.add(confirmationViewPanel.new ConfirmationViewFragement()).setOutputMarkupId(true));
      super.onInitialize();
    } else {
      throw new RedirectToUrlException(SpecificationPage.SPECIFICATION_HTML_VALUE);
    }
  }
}
