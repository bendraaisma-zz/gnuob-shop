package com.netbrasoft.gnuob.shop.confirmation;

import static com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.ORDER_DATA_PROVIDER_NAME;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.order.IGenericOrderCheckoutDataProvider;
import com.netbrasoft.gnuob.api.order.OrderDataProvider.PaymentProviderEnum;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;
import com.netbrasoft.gnuob.shop.shopper.ShopperDataProvider;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class ConfirmationEmptyOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class ConfirmationViewFragement extends Fragment {

    private static final String CONFIRMATION_EDIT_FORM_COMPONENT_ID = "confirmationEditForm";

    private static final String BUYER_EMAIL_ID = "buyerEmail";

    private static final String ORDER_ID_ID = "orderId";

    private static final String CONFIRMATION_EDIT_FRAGMENT_MARKUP_ID = "confirmationEditFragment";

    private static final String CONFIRMATION_EMPTY_OR_EDIT_FRAGMENT_ID = "confirmationEmptyOrEditFragment";

    private static final String TRANSACTION_ID = "transaction_id";

    private static final long serialVersionUID = 1948798072333311170L;

    private final BootstrapForm<Order> confirmationEditForm;

    private final Label orderIdLabel;

    private final Label buyerEmailLabel;

    public ConfirmationViewFragement() {
      super(CONFIRMATION_EMPTY_OR_EDIT_FRAGMENT_ID, CONFIRMATION_EDIT_FRAGMENT_MARKUP_ID,
          ConfirmationEmptyOrEditPanel.this, ConfirmationEmptyOrEditPanel.this.getDefaultModel());
      confirmationEditForm = new BootstrapForm<>(CONFIRMATION_EDIT_FORM_COMPONENT_ID,
          new CompoundPropertyModel<Order>((IModel<Order>) ConfirmationViewFragement.this.getDefaultModel()));
      orderIdLabel = new Label(ORDER_ID_ID);
      buyerEmailLabel = new Label(BUYER_EMAIL_ID);
    }

    public void doCheckoutPayment() {
      Order order = (Order) ConfirmationEmptyOrEditPanel.this.getDefaultModelObject();
      orderDataProvider.setPaymentProvider(PaymentProviderEnum.valueOf(order.getCheckout()));
      order.setTransactionId(getRequest().getQueryParameters().getParameterValue(TRANSACTION_ID).toString());
      order =
          orderDataProvider.doCheckoutPayment(orderDataProvider.findById(orderDataProvider.doCheckoutDetails(order)));
      shopperDataProvider.find(new Shopper()).emptyCheckOut(order.getContract());
    }

    @Override
    protected void onInitialize() {
      confirmationEditForm.add(orderIdLabel.setOutputMarkupId(true));
      confirmationEditForm.add(buyerEmailLabel.setOutputMarkupId(true));
      confirmationEditForm.add(new FormBehavior(FormType.Horizontal));
      add(confirmationEditForm.setOutputMarkupId(true));
      super.onInitialize();
      doCheckoutPayment();
    }
  }

  private static final long serialVersionUID = 4629799686885772339L;

  @SpringBean(name = ORDER_DATA_PROVIDER_NAME, required = true)
  private IGenericOrderCheckoutDataProvider<Order> orderDataProvider;

  @SpringBean(name = ShopperDataProvider.SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  public ConfirmationEmptyOrEditPanel(final String id, final IModel<Order> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    orderDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    orderDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    orderDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    orderDataProvider.setType(new Order());
    orderDataProvider.getType().setActive(true);
    orderDataProvider.setOrderBy(OrderBy.NONE);
    orderDataProvider.setPaymentProvider(PaymentProviderEnum.PAGSEGURO);
    super.onInitialize();
  }
}
