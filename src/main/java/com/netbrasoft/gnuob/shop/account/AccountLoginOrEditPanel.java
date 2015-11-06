/*
 * Copyright 2015 Netbrasoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.netbrasoft.gnuob.shop.account;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
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
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.Contract;
import com.netbrasoft.gnuob.api.Customer;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.PostalCode;
import com.netbrasoft.gnuob.api.contract.ContractDataProvider;
import com.netbrasoft.gnuob.api.customer.PostalCodeDataProvider;
import com.netbrasoft.gnuob.api.generic.GNUOpenBusinessApplicationException;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.NetbrasoftShopMessageKeyConstants;
import com.netbrasoft.gnuob.shop.authentication.OAuthUtils;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;
import com.netbrasoft.gnuob.shop.shopper.ShopperDataProvider;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.LoadingBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.typeaheadV10.DataSet;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.typeaheadV10.Typeahead;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.typeaheadV10.TypeaheadConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.typeaheadV10.bloodhound.Bloodhound;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.typeaheadV10.bloodhound.BloodhoundConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;

/**
 * {@link AccountLoginOrEditPanel} manages two {@link Fragment}s for the login and registration of
 * {@link Customer}s. If a {@link Customer} is not logged in or registered, the
 * {@link AccountLoginFragment} is present where the {@link Customer} can select the prefered
 * provide OAUTH2 provider to verify his credentials. If the {@link Customer} registered
 * successfully through the selected OAUTH2 provider the {@link AccountEditFragment} is presenting a
 * registration form where the {@link Customer} can edit his/here personal data record.
 *
 * @author "Bernard Arjan Draaisma"
 * @version 1.0
 */
@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class AccountLoginOrEditPanel extends Panel {

  /**
   * {@link AccountEditFragment} is presenting a registration form where the {@link Customer} can
   * edit his/here personal data record.
   *
   * @author "Bernard Arjan Draaisma"
   * @version 1.0
   */
  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class AccountEditFragment extends Fragment {

    /**
     * {@link AccountEditContainer} is a web markup container grouping the actual registration form
     * components.
     *
     * @author "Bernard Arjan Draaisma"
     * @version 1.0
     */
    @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
    class AccountEditContainer extends WebMarkupContainer {

      /**
       * {@link BloodhoundPlaceNames} retrieves a list of matched place names based on the input
       * that is given as the starting place name input string.
       *
       * @author "Bernard Arjan Draaisma"
       * @version 1.0
       */
      class BloodhoundPlaceNames extends Bloodhound<String> {

        private static final String VALUE_S_FORMAT = "{\"value\":\"%s\"}";

        private static final long serialVersionUID = 697786098676195271L;

        /**
         * Constructor that initialize the place name Bloodhound source.
         *
         * @param name the name of this bloodhound instance.
         * @param config the configuration of this bloodhound instance.
         */
        public BloodhoundPlaceNames(final String name, final BloodhoundConfig config) {
          super(name, config);
        }

        /**
         * {@inheritDoc}.
         */
        @Override
        public Iterable<String> getChoices(final String input) {
          final List<String> cityNames = new ArrayList<String>();
          postalCodeDataProvider.getType().setPlaceName(input + "%");
          postalCodeDataProvider.setOrderBy(OrderBy.PLACE_NAME_A_Z);
          for (final Iterator<? extends PostalCode> iterator = postalCodeDataProvider.iterator(0, 5); iterator.hasNext();) {
            cityNames.add(iterator.next().getPlaceName());
          }
          return cityNames;
        }

        /**
         * {@inheritDoc}.
         */
        @Override
        public String renderChoice(final String choice) {
          return String.format(VALUE_S_FORMAT, choice);
        }
      }

      /**
       * {@link SaveAjaxButton} verifies that all mandatory fields of the registration form are
       * filled in correctly by the {@link Customer}. When verification is OK, the {@link Customer}
       * his {@link Contract} is created or updated and stored back inside the back-end repository
       * plus it will be temporary stored and available inside the {@link Shopper} repository cache.
       *
       * @author "Bernard Arjan Draaisma"
       * @version 1.0
       */
      @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
      class SaveAjaxButton extends BootstrapAjaxButton {

        private static final long serialVersionUID = 2695394292963384938L;

        /**
         * Constructor that initialize the {@link SaveAjaxButton} which is styled by bootstrap.
         *
         * @param id The component id.
         * @param model The label.
         * @param form The assigned form.
         * @param type The type of button.
         */
        public SaveAjaxButton(final String id, final IModel<String> model, final Form<Contract> form, final Buttons.Type type) {
          super(id, model, form, type);
          setSize(Buttons.Size.Small);
          add(new LoadingBehavior(Model.of(AccountLoginOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.SAVE_MESSAGE_KEY))));
        }

        /**
         * {@inheritDoc}.
         */
        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          form.add(new TooltipValidation());
          target.add(form);
          target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(AccountLoginOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.SAVE_MESSAGE_KEY)))));
        }

        /**
         * {@inheritDoc}.
         */
        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          try {
            final Shopper shopper = shopperDataProvider.find(new Shopper());
            ((Contract) form.getDefaultModelObject()).getCustomer().getAddress().setCountry("BR");
            if (((Contract) form.getDefaultModelObject()).getId() == 0) {
              AccountEditContainer.this.setDefaultModelObject(contractDataProvider.findById(contractDataProvider.persist((Contract) form.getDefaultModelObject())));
            } else {
              AccountEditContainer.this.setDefaultModelObject(contractDataProvider.findById(contractDataProvider.merge((Contract) form.getDefaultModelObject())));
            }
            shopper.setContract((Contract) AccountEditContainer.this.getDefaultModelObject());
            shopperDataProvider.merge(shopper);
            AccountLoginOrEditPanel.this.removeAll();
            target.add(AccountLoginOrEditPanel.this.add(AccountLoginOrEditPanel.this.new AccountEditFragment()).setOutputMarkupId(true));
          } catch (final RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
            feedbackPanel.warn(e.getLocalizedMessage());
            target.add(feedbackPanel.setOutputMarkupId(true));
            target.add(SaveAjaxButton.this.add(new LoadingBehavior(Model.of(AccountLoginOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.SAVE_MESSAGE_KEY)))));
          }
        }
      }

      /**
       * {@link State} is a POJO that stores a country state code and name property.
       *
       * @author "Bernard Arjan Draaisma"
       * @version 1.0
       */
      class State implements IClusterable {

        private static final long serialVersionUID = -318735320199663283L;

        private final String code;

        private final String name;

        /**
         *
         * @param code Country state code.
         * @param name Country state name.
         */
        public State(final String code, final String name) {
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

      private static final String ACCOUNT_EDIT_FORM_COMPONENT_ID = "accountEditForm";

      private static final String REMOTE_NAME = "remote";

      private static final String PATTERN_0_9_5_0_9_3 = "([0-9]){5}([-])([0-9]){3}";

      private static final String CUSTOMER_ADDRESS_STATE_OR_PROVINCE_ID = "customer.address.stateOrProvince";

      private static final String CUSTOMER_ADDRESS_POSTAL_CODE_ID = "customer.address.postalCode";

      private static final String CUSTOMER_ADDRESS_CITY_NAME_ID = "customer.address.cityName";

      private static final String CUSTOMER_ADDRESS_COUNTRY_ID = "customer.address.country";

      private static final String CUSTOMER_ADDRESS_STREET2_ID = "customer.address.street2";

      private static final String CUSTOMER_ADDRESS_STREET1_ID = "customer.address.street1";

      private static final String CUSTOMER_ADDRESS_PHONE_ID = "customer.address.phone";

      private static final String CUSTOMER_LAST_NAME_ID = "customer.lastName";

      private static final String CUSTOMER_FIRST_NAME_ID = "customer.firstName";

      private static final String CUSTOMER_BUYER_EMAIL_ID = "customer.buyerEmail";

      private static final long serialVersionUID = 5886294846061574581L;

      private static final String SAVE_ID = "save";

      private static final String FEEDBACK_ID = "feedback";

      private final BootstrapForm<Contract> accountEditForm;

      private final SaveAjaxButton saveAjaxButton;

      private final NotificationPanel feedbackPanel;

      private final RequiredTextField<String> customerBuyerEmailTextField;

      private final RequiredTextField<String> customerFirstNameTextField;

      private final RequiredTextField<String> customerLastNameTextField;

      private final TextField<String> customerAddressPhoneTextField;

      private final RequiredTextField<String> customerAddressStreet1TextField;

      private final TextField<String> customerAddressStreet2TextField;

      private final TextField<String> customerAddressCountryTextField;

      private final Typeahead<String> customerAddressCityNameTextField;

      private final RequiredTextField<String> customerAddressPostalCodeTextField;

      private final BootstrapSelect<State> customerAddressStateOrProvinceDropDownChoice;

      /**
       * Constructor that initialize the {@link AccountEditContainer} and initialize the actual
       * registration form which his field and button components
       *
       * @param id The component id.
       * @param model The component's {@link Contract} model.
       */
      public AccountEditContainer(final String id, final IModel<Contract> model) {
        super(id, model);
        final BloodhoundPlaceNames bloodhoundPlaceNames = new BloodhoundPlaceNames(REMOTE_NAME, new BloodhoundConfig());
        final TypeaheadConfig<String> config = new TypeaheadConfig<String>(new DataSet<>(bloodhoundPlaceNames));
        accountEditForm =
            new BootstrapForm<Contract>(ACCOUNT_EDIT_FORM_COMPONENT_ID, new CompoundPropertyModel<Contract>((IModel<Contract>) AccountEditContainer.this.getDefaultModel()));
        saveAjaxButton = new SaveAjaxButton(SAVE_ID, Model.of(AccountLoginOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.SAVE_MESSAGE_KEY)), accountEditForm,
            Buttons.Type.Primary);
        feedbackPanel = new NotificationPanel(FEEDBACK_ID);
        customerBuyerEmailTextField = new RequiredTextField<String>(CUSTOMER_BUYER_EMAIL_ID);
        customerFirstNameTextField = new RequiredTextField<String>(CUSTOMER_FIRST_NAME_ID);
        customerLastNameTextField = new RequiredTextField<String>(CUSTOMER_LAST_NAME_ID);
        customerAddressPhoneTextField = new TextField<String>(CUSTOMER_ADDRESS_PHONE_ID);
        customerAddressStreet1TextField = new RequiredTextField<String>(CUSTOMER_ADDRESS_STREET1_ID);
        customerAddressStreet2TextField = new TextField<String>(CUSTOMER_ADDRESS_STREET2_ID);
        customerAddressCountryTextField = new TextField<String>(CUSTOMER_ADDRESS_COUNTRY_ID, Model.of("Brasil"));
        customerAddressCityNameTextField = new Typeahead<String>(CUSTOMER_ADDRESS_CITY_NAME_ID, null, config);
        customerAddressPostalCodeTextField = new RequiredTextField<String>(CUSTOMER_ADDRESS_POSTAL_CODE_ID);
        customerAddressStateOrProvinceDropDownChoice =
            new BootstrapSelect<State>(CUSTOMER_ADDRESS_STATE_OR_PROVINCE_ID, getStatesOfBrazil(), new ChoiceRenderer<State>("name", ""));
      }

      // FIXME Get this information from http://www.geonames.org/ to support state formats
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

      /**
       * {@inheritDoc}.
       */
      @Override
      protected void onInitialize() {
        customerBuyerEmailTextField.setLabel(Model.of(AccountLoginOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.BUYER_EMAIL_MESSAGE_KEY)));
        customerBuyerEmailTextField.add(EmailAddressValidator.getInstance());
        customerBuyerEmailTextField.add(StringValidator.maximumLength(60));
        customerFirstNameTextField.setLabel(Model.of(AccountLoginOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.FIRST_NAME_MESSAGE_KEY)));
        customerFirstNameTextField.add(StringValidator.maximumLength(40));
        customerLastNameTextField.setLabel(Model.of(AccountLoginOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.LAST_NAME_MESSAGE_KEY)));
        customerLastNameTextField.add(StringValidator.maximumLength(40));
        customerAddressPhoneTextField.add(StringValidator.maximumLength(40));
        customerAddressStreet1TextField.setLabel(Model.of(AccountLoginOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.STREET1_MESSAGE_KEY)));
        customerAddressStreet1TextField.add(StringValidator.maximumLength(40));
        customerAddressStreet2TextField.add(StringValidator.maximumLength(40));
        customerAddressCountryTextField.setLabel(Model.of(AccountLoginOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.COUNTRY_NAME_MESSAGE_KEY)));
        customerAddressCountryTextField.setEnabled(false);
        customerAddressCityNameTextField.setRequired(true);
        customerAddressCityNameTextField.setLabel(Model.of(AccountLoginOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.CITY_NAME_MESSAGE_KEY)));
        customerAddressCityNameTextField.add(StringValidator.maximumLength(40)).setOutputMarkupId(true);
        customerAddressPostalCodeTextField.setLabel(Model.of(AccountLoginOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.POSTAL_CODE_MESSAGE_KEY)));
        customerAddressPostalCodeTextField.add(new PatternValidator(PATTERN_0_9_5_0_9_3));
        customerAddressStateOrProvinceDropDownChoice.setRequired(true);
        customerAddressStateOrProvinceDropDownChoice.setLabel(Model.of(AccountLoginOrEditPanel.this.getString(NetbrasoftShopMessageKeyConstants.STATE_OR_PROVINCE_MESSAGE_KEY)));
        accountEditForm.add(customerBuyerEmailTextField.setOutputMarkupId(true));
        accountEditForm.add(customerFirstNameTextField.setOutputMarkupId(true));
        accountEditForm.add(customerLastNameTextField.setOutputMarkupId(true));
        accountEditForm.add(customerAddressPhoneTextField.setOutputMarkupId(true));
        accountEditForm.add(customerAddressStreet1TextField.setOutputMarkupId(true));
        accountEditForm.add(customerAddressStreet2TextField.setOutputMarkupId(true));
        accountEditForm.add(customerAddressCountryTextField.setOutputMarkupId(true));
        accountEditForm.add(customerAddressCityNameTextField.setOutputMarkupId(true));
        accountEditForm.add(customerAddressPostalCodeTextField.setOutputMarkupId(true));
        accountEditForm.add(customerAddressStateOrProvinceDropDownChoice.setOutputMarkupId(true));
        accountEditForm.add(saveAjaxButton.setOutputMarkupId(true));
        accountEditForm.add(new FormBehavior(FormType.Horizontal));
        add(accountEditForm.setOutputMarkupId(true));
        add(feedbackPanel.hideAfter(Duration.seconds(5)).setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String ACCOUNT_EDIT_CONTAINER_ID = "accountEditContainer";

    private static final String ACCOUNT_LOGIN_OR_EDIT_FRAGMENT_ID = "accountLoginOrEditFragment";

    private static final String ACCOUNT_EDIT_FRAGMENT_MARKUP_ID = "accountEditFragment";

    private static final long serialVersionUID = 1948798072333311170L;

    private final AccountEditContainer accountEditContainer;

    /**
     * Constructor that initialize the {@link AccountEditFragment} presenting the registration form
     * where the {@link Customer} can edit his/here personal data record.
     */
    public AccountEditFragment() {
      super(ACCOUNT_LOGIN_OR_EDIT_FRAGMENT_ID, ACCOUNT_EDIT_FRAGMENT_MARKUP_ID, AccountLoginOrEditPanel.this, AccountLoginOrEditPanel.this.getDefaultModel());
      accountEditContainer = new AccountEditContainer(ACCOUNT_EDIT_CONTAINER_ID, (IModel<Contract>) AccountEditFragment.this.getDefaultModel());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onInitialize() {
      add(accountEditContainer.setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class AccountLoginFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
    class AccountLoginContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
      class FacebookAjaxLink extends AjaxLink<String> {

        private static final long serialVersionUID = -8317730269644885290L;

        public FacebookAjaxLink(final String id, final IModel<String> model) {
          super(id, model);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          try {
            final Shopper shopper = shopperDataProvider.find(new Shopper());
            final URI issuerURI = new URI(FacebookAjaxLink.this.getDefaultModelObjectAsString());
            final ClientID clientID = OAuthUtils.getClientID(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
            final State state = new State(shopper.getId());
            final URI redirectURI = URI.create(System.getProperty(GNUOB_PREFIX_PROPERTY + AppServletContainerAuthenticatedWebSession.getSite() + LOGIN_REDIRECT_PREFIX_PROPERTY));
            final Scope scope = OAuthUtils.getScope(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
            final OIDCProviderMetadata providerConfiguration = OAuthUtils.getProviderConfigurationURL(issuerURI);
            shopper.setIssuer(FacebookAjaxLink.this.getDefaultModelObjectAsString());
            shopperDataProvider.merge(shopper);
            throw new RedirectToUrlException(OAuthUtils.getFacebookAuthenticationRequest(providerConfiguration, clientID, redirectURI, scope, state).toURI().toString());
          } catch (GNUOpenBusinessApplicationException | URISyntaxException | SerializeException e) {
            LOGGER.warn("OAuth Exception with Facebook.", e);
          }
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
      class GoogleAjaxLink extends AjaxLink<String> {

        private static final long serialVersionUID = -8317730269644885290L;

        public GoogleAjaxLink(final String id, final IModel<String> model) {
          super(id, model);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          try {
            final Shopper shopper = shopperDataProvider.find(new Shopper());
            final URI issuerURI = new URI(GoogleAjaxLink.this.getDefaultModelObjectAsString());
            final ClientID clientID = OAuthUtils.getClientID(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
            final State state = new State(shopper.getId());
            final URI redirectURI = URI.create(System.getProperty(GNUOB_PREFIX_PROPERTY + AppServletContainerAuthenticatedWebSession.getSite() + LOGIN_REDIRECT_PREFIX_PROPERTY));
            final Scope scope = OAuthUtils.getScope(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
            final OIDCProviderMetadata providerConfiguration = OAuthUtils.getProviderConfigurationURL(issuerURI);
            shopper.setIssuer(GoogleAjaxLink.this.getDefaultModelObjectAsString());
            shopperDataProvider.merge(shopper);
            throw new RedirectToUrlException(OAuthUtils.getAuthenticationRequest(providerConfiguration, clientID, redirectURI, scope, state).toURI().toString());
          } catch (GNUOpenBusinessApplicationException | URISyntaxException | SerializeException e) {
            LOGGER.warn("OAuth Exception with Google.", e);
          }
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
      class MicrosoftAjaxLink extends AjaxLink<String> {

        private static final long serialVersionUID = -8317730269644885290L;

        public MicrosoftAjaxLink(final String id, final IModel<String> model) {
          super(id, model);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          try {
            final Shopper shopper = shopperDataProvider.find(new Shopper());
            final URI issuerURI = new URI(MicrosoftAjaxLink.this.getDefaultModelObjectAsString());
            final ClientID clientID = OAuthUtils.getClientID(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
            final State state = new State(shopper.getId());
            final URI redirectURI = URI.create(System.getProperty(GNUOB_PREFIX_PROPERTY + AppServletContainerAuthenticatedWebSession.getSite() + LOGIN_REDIRECT_PREFIX_PROPERTY));
            final Scope scope = OAuthUtils.getScope(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
            final OIDCProviderMetadata providerConfiguration = OAuthUtils.getProviderConfigurationURL(issuerURI);
            shopper.setIssuer(MicrosoftAjaxLink.this.getDefaultModelObjectAsString());
            shopperDataProvider.merge(shopper);
            throw new RedirectToUrlException(OAuthUtils.getMicrosoftAuthenticationRequest(providerConfiguration, clientID, redirectURI, scope, state).toURI().toString());
          } catch (GNUOpenBusinessApplicationException | URISyntaxException | SerializeException e) {
            LOGGER.warn("OAuth Exception with Microsoft.", e);
          }
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
      class PayPalAjaxLink extends AjaxLink<String> {

        private static final long serialVersionUID = -8317730269644885290L;

        public PayPalAjaxLink(final String id, final IModel<String> model) {
          super(id, model);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          try {
            final Shopper shopper = shopperDataProvider.find(new Shopper());
            final URI issuerURI = new URI(PayPalAjaxLink.this.getDefaultModelObjectAsString());
            final ClientID clientID = OAuthUtils.getClientID(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
            final State state = new State(shopper.getId());
            final URI redirectURI = URI.create(System.getProperty(GNUOB_PREFIX_PROPERTY + AppServletContainerAuthenticatedWebSession.getSite() + LOGIN_REDIRECT_PREFIX_PROPERTY));
            final Scope scope = OAuthUtils.getScope(AppServletContainerAuthenticatedWebSession.getSite(), issuerURI);
            final OIDCProviderMetadata providerConfiguration = OAuthUtils.getProviderConfigurationURL(issuerURI);
            shopper.setIssuer(PayPalAjaxLink.this.getDefaultModelObjectAsString());
            shopperDataProvider.merge(shopper);
            throw new RedirectToUrlException(OAuthUtils.getAuthenticationRequest(providerConfiguration, clientID, redirectURI, scope, state).toURI().toString());
          } catch (GNUOpenBusinessApplicationException | URISyntaxException | SerializeException e) {
            LOGGER.warn("OAuth Exception with PayPal.", e);
          }
        }
      }

      private static final String LOGIN_REDIRECT_PREFIX_PROPERTY = ".login.redirect";

      private static final String GNUOB_PREFIX_PROPERTY = "gnuob.";

      private static final String MICROSOFT_ID = "microsoft";

      private static final String PAYPAL_ID = "paypal";

      private static final String GOOGLE_ID = "google";

      private static final String FACEBOOK_ID = "facebook";

      private static final long serialVersionUID = -4245331044839125796L;

      private static final String ACCOUNT_LOGIN_FORM_COMPONENT_ID = "accountLoginForm";

      private final BootstrapForm<Contract> accountLoginForm;

      private final FacebookAjaxLink facebookAjaxLink;

      private final GoogleAjaxLink googleAjaxLink;

      private final PayPalAjaxLink payPalAjaxLink;

      private final MicrosoftAjaxLink microsoftAjaxLink;

      public AccountLoginContainer(final String id, final IModel<Category> model) {
        super(id, model);
        accountLoginForm =
            new BootstrapForm<Contract>(ACCOUNT_LOGIN_FORM_COMPONENT_ID, new CompoundPropertyModel<Contract>((IModel<Contract>) AccountLoginContainer.this.getDefaultModel()));
        facebookAjaxLink = new FacebookAjaxLink(FACEBOOK_ID, Model.of(OAuthUtils.ACCOUNTS_FACEBOOK_COM));
        googleAjaxLink = new GoogleAjaxLink(GOOGLE_ID, Model.of(OAuthUtils.ACCOUNTS_GOOGLE_COM));
        payPalAjaxLink = new PayPalAjaxLink(PAYPAL_ID, Model.of(OAuthUtils.ACCOUNTS_PAY_PAL_COM));
        microsoftAjaxLink = new MicrosoftAjaxLink(MICROSOFT_ID, Model.of(OAuthUtils.ACCOUNTS_MICROSOFT_COM));
      }

      @Override
      protected void onInitialize() {
        accountLoginForm.add(googleAjaxLink.setOutputMarkupId(true));
        accountLoginForm.add(facebookAjaxLink.setOutputMarkupId(true));
        accountLoginForm.add(payPalAjaxLink.setOutputMarkupId(true));
        accountLoginForm.add(microsoftAjaxLink.setOutputMarkupId(true));
        accountLoginForm.add(new FormBehavior(FormType.Horizontal));
        add(accountLoginForm.setOutputMarkupId(true));
        super.onInitialize();
      }
    }

    private static final String ACCOUNT_LOGIN_OR_EDIT_FRAGMENT_ID = "accountLoginOrEditFragment";

    private static final String ACCOUNT_LOGIN_FRAGMENT_MARKUP_ID = "accountLoginFragment";

    private static final String ACCOUNT_LOGIN_CONTAINER_ID = "accountLoginContainer";

    private static final long serialVersionUID = 1193409377850497931L;

    private final AccountLoginContainer accountLoginContainer;

    public AccountLoginFragment() {
      super(ACCOUNT_LOGIN_OR_EDIT_FRAGMENT_ID, ACCOUNT_LOGIN_FRAGMENT_MARKUP_ID, AccountLoginOrEditPanel.this, AccountLoginOrEditPanel.this.getDefaultModel());
      accountLoginContainer = new AccountLoginContainer(ACCOUNT_LOGIN_CONTAINER_ID, (IModel<Category>) AccountLoginFragment.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(accountLoginContainer.setOutputMarkupId(true));
      super.onInitialize();
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountLoginOrEditPanel.class);

  private static final long serialVersionUID = -4406441947235524118L;

  @SpringBean(name = ShopperDataProvider.SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  @SpringBean(name = ContractDataProvider.CONTRACT_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeDataProvider<Contract> contractDataProvider;

  @SpringBean(name = PostalCodeDataProvider.POSTAL_CODE_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeDataProvider<PostalCode> postalCodeDataProvider;

  public AccountLoginOrEditPanel(final String id, final IModel<Contract> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    contractDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    contractDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    contractDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    contractDataProvider.setType(new Contract());
    contractDataProvider.getType().setActive(true);
    postalCodeDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    postalCodeDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    postalCodeDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    postalCodeDataProvider.setType(new PostalCode());
    postalCodeDataProvider.getType().setCountryCode("BR"); // FIXME: No fixed value for Brazil.
    super.onInitialize();
  }
}
