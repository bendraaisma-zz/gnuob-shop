package com.netbrasoft.gnuob.shop.account;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.Contract;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.authentication.AuthorizationPanel;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.LoadingBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class AccountViewPanel extends Panel {

  class AccountViewFragement extends Fragment {

    class State implements IClusterable {

      private static final long serialVersionUID = -318735320199663283L;

      private final String code;
      private final String name;

      public State(String code, String name) {
        this.code = code;
        this.name = name;
      }

      public String getName() {
        return name;
      }

      @Override
      public String toString() {
        return code;
      }
    }

    private static final long serialVersionUID = 1948798072333311170L;

    public AccountViewFragement() {
      super("accountCustomerViewFragement", "accountViewFragement", AccountViewPanel.this, AccountViewPanel.this.getDefaultModel());
    }

    // FIXME BD: get this information from http://www.geonames.org/ to support
    // all counties and address formats etc.
    public List<State> getStatesOfBrazil() {
      final List<State> states = new ArrayList<>();
      final String[][] statesOfBrazil = new String[][] {{"AC", "Acre"}, {"AL", "Alagoas"}, {"AP", "Amapá"}, {"AM", "Amazonas"}, {"BA", "Bahia"}, {"CE", "Ceará"},
          {"ES", "Espírito Santo"}, {"GO", "Goiás"}, {"MA", "Maranhão"}, {"MT", "Mato Grosso"}, {"MS", "Mato Grosso do Sul"}, {"MG", "Minas Gerais"}, {"PA", "Pará"},
          {"PB", "Paraíba"}, {"PR", "Paraná"}, {"PE", "Pernambuco"}, {"PI", "Piauí"}, {"RJ", "Rio de Janeiro"}, {"RN", "Rio Grande do Norte"}, {"RS", "Rio Grande do Sul"},
          {"RO", "Rondônia"}, {"RR", "Roraima"}, {"SC", "Santa Catarina"}, {"SP", "São Paulo"}, {"SE", "Sergipe"}, {"TO", "Tocantins"}};

      for (final String[] state : statesOfBrazil) {
        states.add(new State(state[0], state[1]));
      }

      return states;
    }

    @Override
    protected void onInitialize() {
      final BootstrapForm<Contract> customerEditForm = new BootstrapForm<Contract>("customerEditForm");
      customerEditForm.setModel(new CompoundPropertyModel<Contract>(Model.of(shopperDataProvider.find(new Shopper()).getContract())));
      customerEditForm.add(new RequiredTextField<String>("customer.buyerEmail").setLabel(Model.of(getString("buyerEmailMessage"))).add(EmailAddressValidator.getInstance())
          .add(StringValidator.maximumLength(60)));
      customerEditForm.add(new RequiredTextField<String>("customer.firstName").setLabel(Model.of(getString("firstNameMessage"))).add(StringValidator.maximumLength(40)));
      customerEditForm.add(new RequiredTextField<String>("customer.lastName").setLabel(Model.of(getString("lastNameMessage"))).add(StringValidator.maximumLength(40)));
      customerEditForm.add(new TextField<String>("customer.address.phone").add(StringValidator.maximumLength(40)));
      customerEditForm.add(new RequiredTextField<String>("customer.address.street1").setLabel(Model.of(getString("street1Message"))).add(StringValidator.maximumLength(40)));
      customerEditForm.add(new TextField<String>("customer.address.street2").add(StringValidator.maximumLength(40)));
      customerEditForm.add(new TextField<String>("customer.address.country", Model.of("Brasil")).setLabel(Model.of(getString("countryNameMessage"))).setEnabled(false));
      customerEditForm.add(new RequiredTextField<String>("customer.address.cityName").setLabel(Model.of(getString("cityNameMessage"))).add(StringValidator.maximumLength(40)));
      // FIXME: BD get this from geonames how to format ZIP code.
      customerEditForm.add(new RequiredTextField<String>("customer.address.postalCode").setLabel(Model.of(getString("postalCodeMessage")))
          .add(new PatternValidator("([0-9]){5}([-])([0-9]){3}")));
      customerEditForm.add(new DropDownChoice<State>("customer.address.stateOrProvince", getStatesOfBrazil(), new ChoiceRenderer<State>("name", "")).setRequired(true)
          .setLabel(Model.of(getString("stateOrProvinceMessage"))));
      customerEditForm.add(new SaveAjaxButton(customerEditForm).setOutputMarkupId(true));

      add(customerEditForm.setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  class LoginViewFragement extends Fragment {
    private static final long serialVersionUID = 1193409377850497931L;

    public LoginViewFragement() {
      super("accountCustomerViewFragement", "loginViewFragement", AccountViewPanel.this, AccountViewPanel.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      final AuthorizationPanel authorizationPanel = new AuthorizationPanel("authorizationPanel", (IModel<Shopper>) AccountViewPanel.this.getDefaultModel());
      add(authorizationPanel);
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class SaveAjaxButton extends BootstrapAjaxButton {

    private static final long serialVersionUID = 2695394292963384938L;

    public SaveAjaxButton(final Form<Contract> form) {
      super("save", Model.of(AccountViewPanel.this.getString("saveMessage")), form, Buttons.Type.Primary);
      setSize(Buttons.Size.Small);
      add(new LoadingBehavior(Model.of(AccountViewPanel.this.getString("saveMessage"))));
    }

    @Override
    protected void onError(AjaxRequestTarget target, Form<?> form) {
      form.add(new TooltipValidation());
      target.add(form);
      target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(AccountViewPanel.this.getString("saveMessage")))));
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
      try {
        saveContract((Contract) form.getDefaultModelObject());
        target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(AccountViewPanel.this.getString("savingMessage")))));
      } catch (final RuntimeException e) {
        LOGGER.warn(e.getMessage(), e);
      }
      throw new RedirectToUrlException("account.html");
    }

    private void saveContract(Contract contract) {
      final Shopper shopper = shopperDataProvider.find(new Shopper());
      // FIXME: BD change this to configuration or geonames etc.
      contract.getCustomer().getAddress().setCountry("BR");
      shopper.setContract(contractDataProvider.findById(contractDataProvider.merge(contract)));

      shopperDataProvider.merge(shopper);
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountViewPanel.class);

  private static final long serialVersionUID = -4406441947235524118L;

  @SpringBean(name = "ShopperDataProvider", required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  @SpringBean(name = "ContractDataProvider", required = true)
  private GenericTypeDataProvider<Contract> contractDataProvider;

  public AccountViewPanel(final String id, final IModel<Shopper> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    if (!shopperDataProvider.find(new Shopper()).isLoggedIn()) {
      removeAll();
      add(new LoginViewFragement());
    }

    contractDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    contractDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    contractDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    contractDataProvider.setType(new Contract());
    contractDataProvider.getType().setActive(true);
    contractDataProvider.setOrderBy(OrderBy.NONE);

    super.onInitialize();
  }
}
