/*
 * Copyright 2016 Netbrasoft
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

package br.com.netbrasoft.gnuob.shop.account;

import static br.com.netbrasoft.gnuob.api.OrderBy.PLACE_NAME_A_Z;
import static br.com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.CONTRACT_DATA_PROVIDER_NAME;
import static br.com.netbrasoft.gnuob.api.generic.NetbrasoftApiConstants.POSTAL_CODE_DATA_PROVIDER_NAME;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNTS_FACEBOOK_COM;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNTS_GOOGLE_COM;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNTS_MICROSOFT_COM;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNTS_PAY_PAL_COM;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNT_EDIT_CONTAINER_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNT_EDIT_FORM_COMPONENT_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNT_EDIT_FRAGMENT_MARKUP_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNT_LOGIN_CONTAINER_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNT_LOGIN_FORM_COMPONENT_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNT_LOGIN_FRAGMENT_MARKUP_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNT_LOGIN_OR_EDIT_FRAGMENT_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.BUYER_EMAIL_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CITY_NAME_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CUSTOMER_ADDRESS_CITY_NAME_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CUSTOMER_ADDRESS_COUNTRY_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CUSTOMER_ADDRESS_PHONE_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CUSTOMER_ADDRESS_POSTAL_CODE_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CUSTOMER_ADDRESS_STATE_OR_PROVINCE_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CUSTOMER_ADDRESS_STREET1_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CUSTOMER_ADDRESS_STREET2_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CUSTOMER_BUYER_EMAIL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CUSTOMER_FIRST_NAME_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CUSTOMER_LAST_NAME_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.FACEBOOK_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.FEEDBACK_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.FIRST_NAME_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.GNUOB_PREFIX_PROPERTY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.GOOGLE_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.LAST_NAME_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.LOGIN_REDIRECT_PREFIX_PROPERTY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.MICROSOFT_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.PAYPAL_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.POSTAL_CODE_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.REMOTE_NAME;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SAVE_ID;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SAVE_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SHOPPER_DATA_PROVIDER_NAME;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.STATE_OR_PROVINCE_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.STREET1_MESSAGE_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.UNCHECKED;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.VALUE_S_FORMAT;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.getProperty;
import static br.com.netbrasoft.gnuob.shop.authentication.OAuthUtils.getAuthenticationRequest;
import static br.com.netbrasoft.gnuob.shop.authentication.OAuthUtils.getClientID;
import static br.com.netbrasoft.gnuob.shop.authentication.OAuthUtils.getOIDCProviderMetaData;
import static br.com.netbrasoft.gnuob.shop.authentication.OAuthUtils.getScope;
import static br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession.getPassword;
import static br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession.getSite;
import static br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession.getUserName;
import static br.com.netbrasoft.gnuob.shop.security.ShopRoles.GUEST;
import static com.google.common.collect.Lists.newArrayList;
import static de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Size.Small;
import static de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType.Horizontal;
import static java.net.URI.create;
import static java.util.stream.Collectors.toList;
import static org.apache.wicket.model.Model.of;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.URI;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.bean.validation.PropertyValidator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;

import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.id.State;

import br.com.netbrasoft.gnuob.api.Contract;
import br.com.netbrasoft.gnuob.api.PostalCode;
import br.com.netbrasoft.gnuob.api.generic.GNUOpenBusinessApplicationException;
import br.com.netbrasoft.gnuob.api.generic.IGenericTypeDataProvider;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.LoadingBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.typeaheadV10.DataSet;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.typeaheadV10.Typeahead;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.typeaheadV10.TypeaheadConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.typeaheadV10.bloodhound.Bloodhound;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.typeaheadV10.bloodhound.BloodhoundConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.validation.TooltipValidation;

@SuppressWarnings(UNCHECKED)
@AuthorizeAction(action = Action.RENDER, roles = {GUEST})
public class AccountLoginOrEditPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
  class AccountEditFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
    class AccountEditContainer extends WebMarkupContainer {

      class BloodhoundPlaceNames extends Bloodhound<String> {

        private static final long serialVersionUID = 697786098676195271L;

        public BloodhoundPlaceNames(final String name, final BloodhoundConfig config) {
          super(name, config);
        }

        @Override
        public Iterable<String> getChoices(final String input) {
          postalCodeDataProvider.getType().setPlaceName(input + "%");
          postalCodeDataProvider.setOrderBy(PLACE_NAME_A_Z);
          return newArrayList(postalCodeDataProvider.iterator(0, 5)).stream().map(PostalCode::getPlaceName)
              .collect(toList());
        }

        @Override
        public String renderChoice(final String choice) {
          return String.format(VALUE_S_FORMAT, choice);
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
      class SaveAjaxButton extends BootstrapAjaxButton {

        private static final long serialVersionUID = 2695394292963384938L;

        public SaveAjaxButton(final String id, final IModel<String> model, final Form<Contract> form,
            final Buttons.Type type) {
          super(id, model, form, type);
        }

        @Override
        protected void onInitialize() {
          setSize(Small);
          add(getLoadingBehavior());
          super.onInitialize();
        }

        @Override
        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
          target.add(form.add(getTooltipValidation()));
          target.add(SaveAjaxButton.this.add(getLoadingBehavior()));
        }

        private TooltipValidation getTooltipValidation() {
          return new TooltipValidation();
        }

        private LoadingBehavior getLoadingBehavior() {
          return new LoadingBehavior(of(AccountLoginOrEditPanel.this.getString(SAVE_MESSAGE_KEY)));
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
          try {
            LOGGER.info("Saving customer contract information");
            final Contract contract = (Contract) form.getDefaultModelObject();
            contract.getCustomer().getAddress().setCountry("BR"); // FIXME: make constant BR value
                                                                  // configurable.
            AccountEditContainer.this.setDefaultModelObject(shopperDataProvider.merge(shopperDataProvider
                .find(Shopper.getInstance()).setContract(contractDataProvider.findById(contract.getId() == 0
                    ? contractDataProvider.persist(contract) : contractDataProvider.merge(contract)))));
            AccountLoginOrEditPanel.this.removeAll();
            target.add(getAccountLoginOrEditComponent());
          } catch (final RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
            warn(e.getLocalizedMessage());
            target.add(SaveAjaxButton.this.add(getLoadingBehavior()));
          }
        }

        private Component getAccountLoginOrEditComponent() {
          return AccountLoginOrEditPanel.this.add(getAccountEditFragment()).setOutputMarkupId(true);
        }

        private AccountEditFragment getAccountEditFragment() {
          return AccountLoginOrEditPanel.this.new AccountEditFragment();
        }
      }

      class State implements IClusterable {

        private static final long serialVersionUID = -318735320199663283L;
        private final String code;
        private final String name;

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

      private static final long serialVersionUID = 5886294846061574581L;

      public AccountEditContainer(final String id, final IModel<Contract> model) {
        super(id, model);
      }

      public List<State> getStatesOfCountry() {
        final List<State> states = newArrayList();
        // FIXME Get this information from http://www.geonames.org/ to support state formats.
        final String[][] statesOfBrazil = new String[][] {{"AC", "Acre"}, {"AL", "Alagoas"}, {"AP", "Amapá"},
            {"AM", "Amazonas"}, {"BA", "Bahia"}, {"CE", "Ceará"}, {"ES", "Espírito Santo"}, {"GO", "Goiás"},
            {"MA", "Maranhão"}, {"MT", "Mato Grosso"}, {"MS", "Mato Grosso do Sul"}, {"MG", "Minas Gerais"},
            {"PA", "Pará"}, {"PB", "Paraíba"}, {"PR", "Paraná"}, {"PE", "Pernambuco"}, {"PI", "Piauí"},
            {"RJ", "Rio de Janeiro"}, {"RN", "Rio Grande do Norte"}, {"RS", "Rio Grande do Sul"}, {"RO", "Rondônia"},
            {"RR", "Roraima"}, {"SC", "Santa Catarina"}, {"SP", "São Paulo"}, {"SE", "Sergipe"}, {"TO", "Tocantins"}};

        for (final String[] state : statesOfBrazil) {
          states.add(new State(state[0], state[1]));
        }
        return states;
      }

      @Override
      protected void onInitialize() {
        add(getAccountEditFormComponent());
        add(getFeedbackComponent());
        super.onInitialize();
      }

      private Component getFeedbackComponent() {
        return new NotificationPanel(FEEDBACK_ID).hideAfter(Duration.seconds(10)).setOutputMarkupId(true);
      }

      private Component getAccountEditFormComponent() {
        final BootstrapForm<Contract> accountEditForm = getAccountEditForm();
        return accountEditForm.add(getBuyerEmailComponent()).add(getFirstNameComponent()).add(getLastNameComponent())
            .add(getPhoneComponent()).add(getStreet1Component()).add(getStreet2Component()).add(getCountryComponent())
            .add(getCityComponent()).add(getPostalCodeComponent()).add(getStateOrProvinceCompontent())
            .add(getSaveAjaxButtonComponent(accountEditForm)).add(getHorizontalFormBehavior()).setOutputMarkupId(true);
      }


      private BootstrapForm<Contract> getAccountEditForm() {
        return new BootstrapForm<>(ACCOUNT_EDIT_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Contract>((IModel<Contract>) AccountEditContainer.this.getDefaultModel()));
      }


      private Component getBuyerEmailComponent() {
        return new TextField<>(CUSTOMER_BUYER_EMAIL_ID)
            .setLabel(of(AccountLoginOrEditPanel.this.getString(BUYER_EMAIL_MESSAGE_KEY)))
            .add(new PropertyValidator<String>()).setOutputMarkupId(true);
      }


      private Component getFirstNameComponent() {
        return new TextField<>(CUSTOMER_FIRST_NAME_ID)
            .setLabel(of(AccountLoginOrEditPanel.this.getString(FIRST_NAME_MESSAGE_KEY)))
            .add(new PropertyValidator<String>()).setOutputMarkupId(true);
      }

      private Component getLastNameComponent() {
        return new TextField<>(CUSTOMER_LAST_NAME_ID)
            .setLabel(of(AccountLoginOrEditPanel.this.getString(LAST_NAME_MESSAGE_KEY)))
            .add(new PropertyValidator<String>()).setOutputMarkupId(true);
      }

      private Component getPhoneComponent() {
        return new TextField<>(CUSTOMER_ADDRESS_PHONE_ID).add(new PropertyValidator<String>()).setOutputMarkupId(true);
      }

      private Component getStreet1Component() {
        return new TextField<>(CUSTOMER_ADDRESS_STREET1_ID)
            .setLabel(of(AccountLoginOrEditPanel.this.getString(STREET1_MESSAGE_KEY)))
            .add(new PropertyValidator<String>()).setOutputMarkupId(true);
      }

      private Component getStreet2Component() {
        return new TextField<>(CUSTOMER_ADDRESS_STREET2_ID).add(new PropertyValidator<String>())
            .setOutputMarkupId(true);
      }

      private Component getCountryComponent() {
        // FIXME: Remove constant field name Brasil, make configurable.
        return new TextField<>(CUSTOMER_ADDRESS_COUNTRY_ID, of("Brasil")).setEnabled(false).setOutputMarkupId(true);
      }

      private Component getCityComponent() {
        return new Typeahead<>(CUSTOMER_ADDRESS_CITY_NAME_ID, null,
            new TypeaheadConfig<>(new DataSet<>(new BloodhoundPlaceNames(REMOTE_NAME, new BloodhoundConfig()))))
                .setLabel(of(AccountLoginOrEditPanel.this.getString(CITY_NAME_MESSAGE_KEY)))
                .add(new PropertyValidator<String>()).setOutputMarkupId(true);
      }

      private Component getPostalCodeComponent() {
        return new TextField<>(CUSTOMER_ADDRESS_POSTAL_CODE_ID)
            .setLabel(of(AccountLoginOrEditPanel.this.getString(POSTAL_CODE_MESSAGE_KEY)))
            .add(new PropertyValidator<String>()).setOutputMarkupId(true);
      }

      private Component getStateOrProvinceCompontent() {
        return new BootstrapSelect<>(CUSTOMER_ADDRESS_STATE_OR_PROVINCE_ID, getStatesOfCountry(),
            new ChoiceRenderer<State>("name", ""))
                .setLabel(of(AccountLoginOrEditPanel.this.getString(STATE_OR_PROVINCE_MESSAGE_KEY)))
                .add(new PropertyValidator<String>()).setOutputMarkupId(true);
      }

      private Component getSaveAjaxButtonComponent(BootstrapForm<Contract> accountEditForm) {
        return new SaveAjaxButton(SAVE_ID, of(AccountLoginOrEditPanel.this.getString(SAVE_MESSAGE_KEY)),
            accountEditForm, Buttons.Type.Primary).setOutputMarkupId(true);
      }

      private FormBehavior getHorizontalFormBehavior() {
        return new FormBehavior(Horizontal);
      }
    }

    private static final long serialVersionUID = 1948798072333311170L;

    public AccountEditFragment() {
      super(ACCOUNT_LOGIN_OR_EDIT_FRAGMENT_ID, ACCOUNT_EDIT_FRAGMENT_MARKUP_ID, AccountLoginOrEditPanel.this,
          AccountLoginOrEditPanel.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      add(getAccountEditContainerComponent());
      success(
          "Welcome, you successfuly logged into our site, please verify your account details below and make sure the required fields are filled in and saved.");
      super.onInitialize();
    }

    private Component getAccountEditContainerComponent() {
      return getAccountEditContainer().setOutputMarkupId(true);
    }

    private AccountEditContainer getAccountEditContainer() {
      return new AccountEditContainer(ACCOUNT_EDIT_CONTAINER_ID,
          (IModel<Contract>) AccountEditFragment.this.getDefaultModel());
    }
  }

  @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
  class AccountLoginFragment extends Fragment {

    @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
    class AccountLoginContainer extends WebMarkupContainer {

      @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
      class FacebookAjaxLink extends AjaxLink<String> {

        private static final long serialVersionUID = -8317730269644885290L;

        public FacebookAjaxLink(final String id, final IModel<String> model) {
          super(id, model);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          try {
            LOGGER.info("Redirecting customer to Facebook authentication service.");
            throw new RedirectToUrlException(getFacebookRedirectUrlAsString());
          } catch (GNUOpenBusinessApplicationException | SerializeException e) {
            LOGGER.warn("OAuth exception with Facebook authentication service.", e);
            warn(
                "Sorry something went wrong and we couldn't redirecting to the Facebook authentication service, please try again later or use one of the other authentication options.");
          }
        }

        private String getFacebookRedirectUrlAsString() {
          return getAuthenticationRequest(getOIDCProviderMetaData(getFacebookIssuerURI()),
              getClientID(getSite(), getFacebookIssuerURI()), getRedirectUrl(),
              getScope(getSite(), getFacebookIssuerURI()), getState()).toURI().toString();
        }

        private URI getFacebookIssuerURI() {
          return create(FacebookAjaxLink.this.getDefaultModelObjectAsString());
        }

        private State getState() {
          return new State(shopperDataProvider.merge(shopperDataProvider.find(Shopper.getInstance())
              .setIssuer(FacebookAjaxLink.this.getDefaultModelObjectAsString())).getId());
        }

        private URI getRedirectUrl() {
          return create(getProperty(GNUOB_PREFIX_PROPERTY + getSite() + LOGIN_REDIRECT_PREFIX_PROPERTY));
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
      class GoogleAjaxLink extends AjaxLink<String> {

        private static final long serialVersionUID = -8317730269644885290L;

        public GoogleAjaxLink(final String id, final IModel<String> model) {
          super(id, model);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          try {
            LOGGER.info("Redirecting customer to Google authentication service.");
            throw new RedirectToUrlException(getGoogleRedirectUrlAsString());
          } catch (GNUOpenBusinessApplicationException | SerializeException e) {
            LOGGER.warn("OAuth exception with Google authentication service.", e);
            warn(
                "Sorry something went wrong and we couldn't redirecting to the Google authentication service, please try again later or use one of the other authentication options.");
          }
        }

        private String getGoogleRedirectUrlAsString() {
          return getAuthenticationRequest(getOIDCProviderMetaData(getGoogleIssuerURI()),
              getClientID(getSite(), getGoogleIssuerURI()), getRedirectUrl(), getScope(getSite(), getGoogleIssuerURI()),
              getState()).toURI().toString();
        }

        private URI getGoogleIssuerURI() {
          return create(GoogleAjaxLink.this.getDefaultModelObjectAsString());
        }

        private State getState() {
          return new State(shopperDataProvider.merge(shopperDataProvider.find(Shopper.getInstance())
              .setIssuer(GoogleAjaxLink.this.getDefaultModelObjectAsString())).getId());
        }

        private URI getRedirectUrl() {
          return create(getProperty(GNUOB_PREFIX_PROPERTY + getSite() + LOGIN_REDIRECT_PREFIX_PROPERTY));
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
      class MicrosoftAjaxLink extends AjaxLink<String> {

        private static final long serialVersionUID = -8317730269644885290L;

        public MicrosoftAjaxLink(final String id, final IModel<String> model) {
          super(id, model);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          try {
            LOGGER.info("Redirecting customer to Microsoft authentication service.");
            throw new RedirectToUrlException(getMicrosoftRedirectUrlAsString());
          } catch (GNUOpenBusinessApplicationException | SerializeException e) {
            LOGGER.warn("OAuth exception with Microsoft authentication service.", e);
            warn(
                "Sorry something went wrong and we couldn't redirecting to the Microsoft authentication service, please try again later or use one of the other authentication options.");
          }
        }

        private String getMicrosoftRedirectUrlAsString() {
          return getAuthenticationRequest(getOIDCProviderMetaData(getMicrosoftIssuerURI()),
              getClientID(getSite(), getMicrosoftIssuerURI()), getRedirectUrl(),
              getScope(getSite(), getMicrosoftIssuerURI()), getState()).toURI().toString();
        }

        private URI getMicrosoftIssuerURI() {
          return create(MicrosoftAjaxLink.this.getDefaultModelObjectAsString());
        }

        private State getState() {
          return new State(shopperDataProvider.merge(shopperDataProvider.find(Shopper.getInstance())
              .setIssuer(MicrosoftAjaxLink.this.getDefaultModelObjectAsString())).getId());
        }

        private URI getRedirectUrl() {
          return create(getProperty(GNUOB_PREFIX_PROPERTY + getSite() + LOGIN_REDIRECT_PREFIX_PROPERTY));
        }
      }

      @AuthorizeAction(action = Action.RENDER, roles = {GUEST})
      class PayPalAjaxLink extends AjaxLink<String> {

        private static final long serialVersionUID = -8317730269644885290L;

        public PayPalAjaxLink(final String id, final IModel<String> model) {
          super(id, model);
        }

        @Override
        public void onClick(final AjaxRequestTarget target) {
          try {
            LOGGER.info("Redirecting customer to PayPal authentication service.");
            throw new RedirectToUrlException(getPayPalRedirectUrlAsString());
          } catch (GNUOpenBusinessApplicationException | SerializeException e) {
            LOGGER.warn("OAuth exception with PayPal authentication service.", e);
            warn(
                "Sorry something went wrong and we couldn't redirecting to the PayPal authentication service, please try again later or use one of the other authentication options.");
          }
        }

        private String getPayPalRedirectUrlAsString() {
          return getAuthenticationRequest(getOIDCProviderMetaData(getPayPalIssuerURI()),
              getClientID(getSite(), getPayPalIssuerURI()), getRedirectUrl(), getScope(getSite(), getPayPalIssuerURI()),
              getState()).toURI().toString();
        }

        private URI getPayPalIssuerURI() {
          return create(PayPalAjaxLink.this.getDefaultModelObjectAsString());
        }

        private State getState() {
          return new State(shopperDataProvider.merge(shopperDataProvider.find(Shopper.getInstance())
              .setIssuer(PayPalAjaxLink.this.getDefaultModelObjectAsString())).getId());
        }

        private URI getRedirectUrl() {
          return create(getProperty(GNUOB_PREFIX_PROPERTY + getSite() + LOGIN_REDIRECT_PREFIX_PROPERTY));
        }
      }

      private static final long serialVersionUID = -4245331044839125796L;

      public AccountLoginContainer(final String id, final IModel<Contract> model) {
        super(id, model);
      }

      @Override
      protected void onInitialize() {
        add(getAccountLoginFormComponent());
        super.onInitialize();
      }

      private Component getAccountLoginFormComponent() {
        return getAccountLoginForm().add(getGoogleAjaxLinkComponent()).add(getFaceBookAjaxLinkComponent())
            .add(getPayPalAjaxLinkComponent()).add(getMicrosoftAjaxLinkComponent()).add(getHorizontalFormBehavior())
            .setOutputMarkupId(true);
      }

      private BootstrapForm<Contract> getAccountLoginForm() {
        return new BootstrapForm<>(ACCOUNT_LOGIN_FORM_COMPONENT_ID,
            new CompoundPropertyModel<Contract>((IModel<Contract>) AccountLoginContainer.this.getDefaultModel()));
      }

      private Component getGoogleAjaxLinkComponent() {
        return new GoogleAjaxLink(GOOGLE_ID, of(ACCOUNTS_GOOGLE_COM)).setOutputMarkupId(true);
      }

      private Component getFaceBookAjaxLinkComponent() {
        return new FacebookAjaxLink(FACEBOOK_ID, of(ACCOUNTS_FACEBOOK_COM)).setOutputMarkupId(true);
      }

      private Component getPayPalAjaxLinkComponent() {
        return new PayPalAjaxLink(PAYPAL_ID, of(ACCOUNTS_PAY_PAL_COM)).setOutputMarkupId(true);
      }

      private Component getMicrosoftAjaxLinkComponent() {
        return new MicrosoftAjaxLink(MICROSOFT_ID, of(ACCOUNTS_MICROSOFT_COM)).setOutputMarkupId(true);
      }

      private FormBehavior getHorizontalFormBehavior() {
        return new FormBehavior(Horizontal);
      }
    }

    private static final long serialVersionUID = 1193409377850497931L;

    public AccountLoginFragment() {
      super(ACCOUNT_LOGIN_OR_EDIT_FRAGMENT_ID, ACCOUNT_LOGIN_FRAGMENT_MARKUP_ID, AccountLoginOrEditPanel.this,
          AccountLoginOrEditPanel.this.getDefaultModel());
    }

    @Override
    protected void onInitialize() {
      initializeAccountLoginContainerComponent();
      super.onInitialize();
    }

    private void initializeAccountLoginContainerComponent() {
      add(getAccountLoginContainerComponent());
    }

    private Component getAccountLoginContainerComponent() {
      return new AccountLoginContainer(ACCOUNT_LOGIN_CONTAINER_ID,
          (IModel<Contract>) AccountLoginFragment.this.getDefaultModel()).setOutputMarkupId(true);
    }
  }

  private static final long serialVersionUID = -4406441947235524118L;
  private static final Logger LOGGER = getLogger(AccountLoginOrEditPanel.class);

  @SpringBean(name = SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  @SpringBean(name = CONTRACT_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<Contract> contractDataProvider;

  @SpringBean(name = POSTAL_CODE_DATA_PROVIDER_NAME, required = true)
  private transient IGenericTypeDataProvider<PostalCode> postalCodeDataProvider;

  public AccountLoginOrEditPanel(final String id, final IModel<Contract> model) {
    super(id, model);
  }

  @Override
  protected void onInitialize() {
    initializeContractDataProvider();
    initializePostalCodeDataProvider();
    super.onInitialize();
  }

  private void initializeContractDataProvider() {
    LOGGER.debug("Setting up the contract data provider using the next values: user=[{}] site=[{}]", getUserName(),
        getSite());
    contractDataProvider.setUser(getUserName());
    contractDataProvider.setPassword(getPassword());
    contractDataProvider.setSite(getSite());
    contractDataProvider.setType(new Contract());
    contractDataProvider.getType().setActive(true);
  }

  private void initializePostalCodeDataProvider() {
    LOGGER.debug("Setting up the postal code data provider using the next values: user=[{}] site=[{}]", getUserName(),
        getSite());
    postalCodeDataProvider.setUser(getUserName());
    postalCodeDataProvider.setPassword(getPassword());
    postalCodeDataProvider.setSite(getSite());
    postalCodeDataProvider.setType(new PostalCode());
    postalCodeDataProvider.getType().setCountryCode("BR"); // FIXME: No fixed value for Brazil.
  }
}
