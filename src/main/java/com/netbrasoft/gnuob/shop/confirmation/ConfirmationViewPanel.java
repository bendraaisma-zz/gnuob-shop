package com.netbrasoft.gnuob.shop.confirmation;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.order.GenericOrderCheckoutDataProvider;
import com.netbrasoft.gnuob.api.order.OrderDataProvider.CheckOut;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class ConfirmationViewPanel extends Panel {

   class ConfirmationViewFragement extends Fragment {

      private static final long serialVersionUID = 1948798072333311170L;

      public ConfirmationViewFragement() {
         super("confirmationCustomerViewFragement", "confirmationViewFragement", ConfirmationViewPanel.this, ConfirmationViewPanel.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
         final Shopper shopper = shopperDataProvider.find(new Shopper());
         Order order = orderDataProvider.findById(shopper.getCheckout());

         orderDataProvider.setCheckOut(CheckOut.valueOf(order.getCheckout()));

         order.setTransactionId(getRequest().getQueryParameters().getParameterValue("transaction_id").toString());
         order = orderDataProvider.findById(orderDataProvider.doCheckoutPayment(orderDataProvider.findById(orderDataProvider.doCheckoutDetails(order))));

         add(new Label("orderId", Model.of(order.getOrderId())));
         add(new Label("buyerEmail", Model.of(order.getContract().getCustomer().getBuyerEmail())));

         shopper.setContract(order.getContract());
         shopper.setCheckout(new Order());
         shopperDataProvider.merge(shopper);

         super.onInitialize();
      }
   }

   private static final long serialVersionUID = 4629799686885772339L;

   @SpringBean(name = "OrderDataProvider", required = true)
   private GenericOrderCheckoutDataProvider<Order> orderDataProvider;

   @SpringBean(name = "ShopperDataProvider", required = true)
   private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   public ConfirmationViewPanel(final String id, final IModel<Shopper> model) {
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
      orderDataProvider.setCheckOut(CheckOut.PAGSEGURO);
      super.onInitialize();
   }
}
