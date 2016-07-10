package br.com.netbrasoft.gnuob.shop.specification;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.netbrasoft.gnuob.api.Order;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.page.CartPage;
import br.com.netbrasoft.gnuob.shop.security.ShopRoles;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;
import br.com.netbrasoft.gnuob.shop.shopper.ShopperDataProvider;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class SpecificationPanel extends Panel {

  private static final String SPECIFICATION_EMPTY_OR_EDIT_PANEL_ID = "specificationEmptyOrEditPanel";

  private static final long serialVersionUID = -2071564475086309712L;

  private final SpecificationEmptyOrEditPanel specificationEmptyOrViewPanel;

  @SpringBean(name = ShopperDataProvider.SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  public SpecificationPanel(final String id, final IModel<Order> model) {
    super(id, model);
    specificationEmptyOrViewPanel = new SpecificationEmptyOrEditPanel(SPECIFICATION_EMPTY_OR_EDIT_PANEL_ID, (IModel<Order>) SpecificationPanel.this.getDefaultModel());
  }

  @Override
  protected void onInitialize() {
    if (shopperDataProvider.find(new Shopper()).getCheckout().getRecords().isEmpty()) {
      throw new RedirectToUrlException(CartPage.CART_HTML_VALUE);
    }

    specificationEmptyOrViewPanel.setDefaultModelObject(shopperDataProvider.find(new Shopper()).getCheckout());
    add(specificationEmptyOrViewPanel.add(specificationEmptyOrViewPanel.new SpecificationEditFragment()).setOutputMarkupId(true));
    super.onInitialize();
  }
}
