package com.netbrasoft.gnuob.shop.checkout;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.api.Order;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class CheckoutViewPanel extends Panel {

   class CheckoutViewFragment extends Fragment {

      private static final long serialVersionUID = -6168511667223170398L;

      public CheckoutViewFragment() {
         super("checkoutOrdersViewFragement", "checkoutViewFragement", CheckoutViewPanel.this, CheckoutViewPanel.this.getDefaultModel());
      }

      @Override
      protected void onInitialize() {
         add(orderDataviewContainer.setOutputMarkupId(true));
         super.onInitialize();
      }
   }

   class OrderDataview extends DataView<Order> {

      private static final long serialVersionUID = -708080269042062417L;

      protected OrderDataview() {
         super("orderDataview", orderDataProvider);
      }

      @Override
      protected void populateItem(Item<Order> item) {
         item.setModel(new CompoundPropertyModel<Order>(item.getModelObject()));
         item.add(new Label("orderId"));
         item.add(new CheckoutOrderViewPanel("orderViewPanel", item.getModel()));
      }
   }

   private static final long serialVersionUID = -4406441947235524118L;

   private final WebMarkupContainer orderDataviewContainer = new WebMarkupContainer("orderDataviewContainer") {

      private static final long serialVersionUID = 5642254953273644428L;

      @Override
      protected void onInitialize() {
         add(orderDataview.setOutputMarkupId(true));
         super.onInitialize();
      }
   };

   private final OrderDataview orderDataview = new OrderDataview();

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   @SpringBean(name = "OrderDataProvider", required = true)
   private GenericTypeDataProvider<Order> orderDataProvider;

   public CheckoutViewPanel(final String id, final IModel<Shopper> model) {
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
      orderDataProvider.getType().setContract(shopperDataProvider.find(new Shopper()).getContract());

      super.onInitialize();
   }
}
