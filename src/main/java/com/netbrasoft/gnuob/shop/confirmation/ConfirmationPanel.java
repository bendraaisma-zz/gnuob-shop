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

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class ConfirmationPanel extends Panel {

   private static final long serialVersionUID = -305453593387396902L;

   private final ConfirmationViewPanel confirmationViewPanel = new ConfirmationViewPanel("confirmationViewPanel", (IModel<Shopper>) getDefaultModel());

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   public ConfirmationPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
      final QueryParameter payerId = getRequest().getClientUrl().getQueryParameter("PayerID");
      final QueryParameter transactionId = getRequest().getClientUrl().getQueryParameter("transaction_id");
      final Shopper shopper = shopperDataProvider.find(new Shopper());

      if ((payerId != null || transactionId != null) && shopper.getCheckout().getOrderId() != null) {
         add(confirmationViewPanel.add(confirmationViewPanel.new ConfirmationViewFragement()).setOutputMarkupId(true));
         super.onInitialize();
      } else {
         throw new RedirectToUrlException("specification.html");
      }
   }
}
