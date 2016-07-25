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

package br.com.netbrasoft.gnuob.shop;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import br.com.netbrasoft.gnuob.api.generic.GNUOpenBusinessApplicationException;

public final class NetbrasoftShopConstants {

  public static final Logger LOGGER = getLogger(NetbrasoftShopConstants.class);
  public static final String SUB_CATEGORY_VIEW_FRAGMENT_MARKUP_ID = "subCategoryViewFragment";
  public static final String SUB_CATEGORY_BOOTSTRAP_LIST_VIEW_ID = "subCategoryBootstrapListView";
  public static final String ACCOUNT_EDIT_CONTAINER_ID = "accountEditContainer";
  public static final String LINK_ID = "link";
  public static final String PRODUCT_DATAVIEW_ID = "productDataview";
  public static final String SUB_CATEGORY_DATAVIEW_ID = "subCategoryDataview";
  public static final String PRODUCT_VIEW_FRAGMENT_MARKUP_ID = "productViewFragment";
  public static final String SUB_CATEGORY_PRODUCT_VIEW_FRAGMENT_ID = "subCategoryProductViewFragment";
  public static final String PRODUCT_DATAVIEW_CONTAINER_ID = "productDataviewContainer";
  public static final String PRODUCT_PAGING_NAVIGATOR_MARKUP_ID = "productPagingNavigator";
  public static final String SUB_CATEGORY_MENU_BOOTSTRAP_LIST_VIEW_ID = "subCategoryMenuBootstrapListView";
  public static final String SUB_CATEGORY_DATAVIEW_CONTAINER_ID = "subCategoryDataviewContainer";
  public static final String CATEGORY_BREAD_CRUMB_BAR_ID = "categoryBreadCrumbBar";
  public static final String STOCK_QUANTITY_ID = "stock.quantity";
  public static final String CATEGORY_VIEW_PANEL_ID = "categoryViewPanel";
  public static final String PRODUCT_CAROUSEL_ID = "productCarousel";
  public static final String RATING_ID = "rating";
  public static final String AMOUNT_WITH_DISCOUNT_ID = "amountWithDiscount";
  public static final String ACCOUNT_EDIT_FORM_COMPONENT_ID = "accountEditForm";
  public static final String AMOUNT_ID = "amount";
  public static final String ACCOUNT_EDIT_FRAGMENT_MARKUP_ID = "accountEditFragment";
  public static final String PURCHASE_ID = "purchase";
  public static final String ACCOUNT_LOGIN_CONTAINER_ID = "accountLoginContainer";
  public static final String ACCOUNT_LOGIN_FORM_COMPONENT_ID = "accountLoginForm";
  public static final String ACCOUNT_LOGIN_FRAGMENT_MARKUP_ID = "accountLoginFragment";
  public static final String ACCOUNT_LOGIN_OR_EDIT_FRAGMENT_ID = "accountLoginOrEditFragment";
  public static final String ACCOUNT_LOGIN_OR_EDIT_PANEL_ID = "accountLoginOrEditPanel";
  public static final String ACCOUNT_MESSAGE_KEY = "accountMessage";
  public static final String ACCOUNTS_FACEBOOK_COM = "http://localhost:8080/json/facebook/openid-configuration";
  public static final String ACCOUNTS_GOOGLE_COM = "http://localhost:8080/json/google/openid-configuration";
  public static final String ACCOUNTS_MICROSOFT_COM = "http://localhost:8080/json/microsoft/openid-configuration";
  public static final String ACCOUNTS_PAY_PAL_COM = "http://localhost:8080/json/paypal/openid-configuration";
  public static final String ADD_TO_WISH_LIST_MESSAGE_KEY = "addToWishListMessage";
  public static final String AMOUNT_TOTAL_ID = "amountTotal";
  public static final String BUYER_EMAIL_MESSAGE_KEY = "buyerEmailMessage";
  public static final String CANCEL_MESSAGE_KEY = "cancelMessage";
  public static final String CART_DATAVIEW_CONTAINER_ID = "cartDataviewContainer";
  public static final String CART_DATAVIEW_ID = "cartDataview";
  public static final String CART_EDIT_CONTAINER_ID = "cartEditContainer";
  public static final String CART_EDIT_FORM_COMPONENT_ID = "cartEditForm";
  public static final String CART_EDIT_FRAGMENT_MARKUP_ID = "cartEditFragment";
  public static final String CART_EDIT_TABLE = "cartEditTable";
  public static final String CART_EMPTY_FRAGMENT_MARKUP_ID = "cartEmptyFragment";
  public static final String CART_EMPTY_OR_EDIT_FRAGMENT_ID = "cartEmptyOrEditFragment";
  public static final String CART_EMPTY_OR_EDIT_PANEL_ID = "cartEmptyOrEditPanel";
  public static final String CART_MESSAGE_KEY = "cartMessage";
  public static final String CART_VIEW_OR_EDIT_PANEL_ID = "cartViewOrEditPanel";
  public static final String CATEGORY_DATA_VIEW_ID = "categoryDataView";
  public static final String CDN_ENABLED_KEY = "gnuob.site.cdn.enabled";
  public static final String CDN_URL_KEY = "gnuob.site.cdn.url";
  public static final String CDNJS_CLOUDFLARE_COM = "//cdnjs.cloudflare.com:80";
  public static final String CHECKOUT_MESSAGE_KEY = "checkoutMessage";
  public static final String CITY_NAME_MESSAGE_KEY = "cityNameMessage";
  public static final String CLASS_ATTRIBUTE = "class";
  public static final String CLICK_EVENT = "click";
  public static final String CLIENT_SECRET = "client_secret";
  public static final String CONFIRM_MESSAGE_KEY = "confirmMessage";
  public static final String CONFIRMATION_FUNCTION_NAME = "confirmation";
  public static final String CONFIRMATION_MESSAGE_KEY = "confirmationMessage";
  public static final String CONTACT_MESSAGE_KEY = "contactMessage";
  public static final String CONTENT_BORDER_CONTENT_BORDER_BODY_MAIN_MENU_PANEL_MAIN_MENU_TABBED_PANEL =
      "contentBorder:contentBorder_body:mainMenuPanel:mainMenuTabbedPanel";
  public static final String CONTENT_ID = "content";
  public static final String COUNTRY_NAME_MESSAGE_KEY = "countryNameMessage";
  public static final String CUSTOMER_ADDRESS_CITY_NAME_ID = "customer.address.cityName";
  public static final String CUSTOMER_ADDRESS_COUNTRY_ID = "customer.address.country";
  public static final String CUSTOMER_ADDRESS_PHONE_ID = "customer.address.phone";
  public static final String CUSTOMER_ADDRESS_POSTAL_CODE_ID = "customer.address.postalCode";
  public static final String CUSTOMER_ADDRESS_STATE_OR_PROVINCE_ID = "customer.address.stateOrProvince";
  public static final String CUSTOMER_ADDRESS_STREET1_ID = "customer.address.street1";
  public static final String CUSTOMER_ADDRESS_STREET2_ID = "customer.address.street2";
  public static final String CUSTOMER_BUYER_EMAIL_ID = "customer.buyerEmail";
  public static final String CUSTOMER_FIRST_NAME_ID = "customer.firstName";
  public static final String CUSTOMER_LAST_NAME_ID = "customer.lastName";
  public static final String DESCRIPTION_MESSAGE_KEY = "descriptionMessage";
  public static final String DIRECTING_TO_PAYMENT_PROVIDER_MESSAGE_KEY = "directingToPaymentProviderMessage";
  public static final String DISCOUNT_TOTAL_ID = "discountTotal";
  public static final String EMAILS = "emails";
  public static final String ACCOUNT = "account";
  public static final String FACEBOOK_CLIENT_ID_PREFIX_PROPERTY = ".facebook.clientId";
  public static final String FACEBOOK_CLIENT_SECRET_PREFIX_PROPERTY = ".facebook.clientSecret";
  public static final String FACEBOOK_ID = "facebook";
  public static final String FACEBOOK_SCOPE_PREFIX_PROPERTY = ".facebook.scope";
  public static final String FALSE = "false";
  public static final String FEEDBACK_ID = "feedback";
  public static final String FIRST_NAME = "first_name";
  public static final String FIRST_NAME_MESSAGE_KEY = "firstNameMessage";
  public static final String FOOTER_PANEL_ID = "footerPanel";
  public static final String GNUOB_PREFIX_PROPERTY = "gnuob.";
  public static final String GOOGLE_CLIENT_ID_PREFIX_PROPERTY = ".google.clientId";
  public static final String GOOGLE_CLIENT_SECRET_PREFIX_PROPERTY = ".google.clientSecret";
  public static final String GOOGLE_ID = "google";
  public static final String GOOGLE_SCOPE_PREFIX_PROPERTY = ".google.scope";
  public static final String HEADER_PANEL_ID = "headerPanel";
  public static final String HOME_MESSAGE_KEY = "homeMessage";
  public static final String ID_IGNORE_PROPERTIES = "id";
  public static final String INFO_VALUE = "info";
  public static final String INSPECTOR_PAGE = "inspectorPage.html";
  public static final String ISSUER_FACEBOOK = "https://www.facebook.com";
  public static final String ISSUER_MICROSOFT = "https://www.microsoft.com";
  public static final String ISSUER_PAY_PAL = "https://www.paypal.com";
  public static final String ITEM_TOTAL_ID = "itemTotal";
  public static final String JAVASCRIPT_RESOURCE_FILTER_NAME = "netbrasoft-shopping-javascript-container";
  public static final String LAST_NAME = "last_name";
  public static final String LAST_NAME_MESSAGE_KEY = "lastNameMessage";
  public static final String LOGIN_MESSAGE_KEY = "loginMessage";
  public static final String LOGIN_REDIRECT_PREFIX_PROPERTY = ".login.redirect";
  public static final String LOGOUT_MESSAGE_KEY = "logoutMessage";
  public static final String MAIN_MENU_TABBED_PANEL_ID = "mainMenuTabbedPanel";
  public static final String MICROSOFT_CLIENT_ID_PREFIX_PROPERTY = ".microsoft.clientId";
  public static final String MICROSOFT_CLIENT_SECRET_PREFIX_PROPERTY = ".microsoft.clientSecret";
  public static final String MICROSOFT_ID = "microsoft";
  public static final String MICROSOFT_SCOPE_PREFIX_PROPERTY = ".microsoft.scope";
  public static final String NAME_ID = "name";
  public static final String NAV_NAV_PILLS_NAV_JUSTIFIED_CSS_CLASS = "nav nav-pills nav-justified";
  public static final String NEXT_MESSAGE_KEY = "nextMessage";
  public static final String OFFER_TOTAL_ID = "offerTotal";
  public static final String OPTIONS_ID = "options";
  public static final String PATTERN_0_9_5_0_9_3 = "([0-9]){5}([-])([0-9]){3}";
  public static final String PAY_MESSAGE_KEY = "payMessage";
  public static final String PAYPAL_CLIENT_ID_PREFIX_PROPERTY = ".paypal.clientId";
  public static final String PAYPAL_CLIENT_SECRET_PREFIX_PROPERTY = ".paypal.clientSecret";
  public static final String PAYPAL_ID = "paypal";
  public static final String PAYPAL_SCOPE_PREFIX_PROPERTY = ".paypal.scope";
  public static final String POSTAL_CODE_MESSAGE_KEY = "postalCodeMessage";
  public static final String PURCHASE_MESSAGE_KEY = "purchaseMessage";
  public static final String QUANTITY_ID = "quantity";
  public static final String REMOTE_NAME = "remote";
  public static final String REMOVE_ID = "remove";
  public static final String REMOVE_MESSAGE_KEY = "removeMessage";
  public static final String SAVE_AND_CLOSE_MESSAGE_KEY = "saveAndCloseMessage";
  public static final String SAVE_AS_OFFER_ID = "saveAsOffer";
  public static final String SAVE_ID = "save";
  public static final String SAVE_MESSAGE_KEY = "saveMessage";
  public static final String SAVING_MESSAGE_KEY = "savingMessage";
  public static final String SAVING_TO_WISH_LIST_MESSAGE_KEY = "savingToWishListMessage";
  public static final String SHIPPING_TOTAL_ID = "shippingTotal";
  public static final String SHOPPER_DATA_PROVIDER_NAME = "ShopperDataProvider";
  public static final String SITE_ENCRYPTION_KEY = "gnuob.site.encryption.key";
  public static final String SPECIFICATION_MESSAGE_KEY = "specificationMessage";
  public static final String STATE_OR_PROVINCE_MESSAGE_KEY = "stateOrProvinceMessage";
  public static final String STREET1_MESSAGE_KEY = "street1Message";
  public static final String UNCHECKED = "unchecked";
  public static final String VALUE_S_FORMAT = "{\"value\":\"%s\"}";
  public static final String VERSION_IGNORE_PROPERTIES = "version";
  public static final String WICKET_APPLICATION = "wicketApplication";
  public static final String WISH_LIST_MESSAGE_KEY = "wishListMessage";

  public static String getProperty(String key) {
    if (isBlank(System.getProperty(key))) {
      LOGGER.error("No property found for the given key: [" + key + "]");
      throw new GNUOpenBusinessApplicationException("No property found for the given key: [" + key + "]");
    }
    return System.getProperty(key);
  }

  public static String getProperty(String key, String def) {
    if (isBlank(System.getProperty(key))) {
      LOGGER.warn("No property found for the given key [" + key + "], going to use the default value: [" + def + "]");
    }
    return System.getProperty(key, def);
  }

  private NetbrasoftShopConstants() {}
}
