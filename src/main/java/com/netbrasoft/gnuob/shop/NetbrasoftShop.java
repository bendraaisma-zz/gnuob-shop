package com.netbrasoft.gnuob.shop;

import net.ftlines.wicketsource.WicketSource;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netbrasoft.gnuob.api.category.CategoryWebServiceRepository;
import com.netbrasoft.gnuob.api.content.ContentWebServiceRepository;
import com.netbrasoft.gnuob.api.contract.ContractWebServiceRepository;
import com.netbrasoft.gnuob.api.customer.CustomerWebServiceRepository;
import com.netbrasoft.gnuob.api.offer.OfferWebServiceRepository;
import com.netbrasoft.gnuob.api.order.OrderWebServiceRepository;
import com.netbrasoft.gnuob.api.order.PagseguroCheckOutWebServiceRepository;
import com.netbrasoft.gnuob.api.order.PayPalExpressCheckOutWebServiceRepository;
import com.netbrasoft.gnuob.api.product.ProductWebServiceRepository;
import com.netbrasoft.gnuob.api.setting.SettingWebServiceRepository;
import com.netbrasoft.gnuob.shop.product.page.ProductListPage;

@Service("wicketApplication")
public class NetbrasoftShop extends WebApplication {

    @Autowired(required = true)
    private CategoryWebServiceRepository categoryWebServiceRepository;

    @Autowired(required = true)
    private PayPalExpressCheckOutWebServiceRepository payPalExpressCheckOutWebServiceRepository;

    @Autowired(required = true)
    private PagseguroCheckOutWebServiceRepository pagseguroCheckOutWebServiceRepository;

    @Autowired(required = true)
    private OrderWebServiceRepository orderWebServiceRepository;

    @Autowired(required = true)
    private ProductWebServiceRepository productWebServiceRepository;

    @Autowired(required = true)
    private OfferWebServiceRepository offerWebServiceRepository;

    @Autowired(required = true)
    private ContentWebServiceRepository contentWebServiceRepository;

    @Autowired(required = true)
    private ContractWebServiceRepository contractWebServiceRepository;

    @Autowired(required = true)
    private CustomerWebServiceRepository customerWebServiceRepository;

    @Autowired(required = true)
    private SettingWebServiceRepository settingWebServiceRepository;

    public CategoryWebServiceRepository getCategoryWebServiceRepository() {
        return categoryWebServiceRepository;
    }

    public ContentWebServiceRepository getContentWebServiceRepository() {
        return contentWebServiceRepository;
    }

    public ContractWebServiceRepository getContractWebServiceRepository() {
        return contractWebServiceRepository;
    }

    public CustomerWebServiceRepository getCustomerWebServiceRepository() {
        return customerWebServiceRepository;
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return ProductListPage.class;
    }

    public OfferWebServiceRepository getOfferWebServiceRepository() {
        return offerWebServiceRepository;
    }

    public OrderWebServiceRepository getOrderWebServiceRepository() {
        return orderWebServiceRepository;
    }

    public PagseguroCheckOutWebServiceRepository getPagseguroCheckOutWebServiceRepository() {
        return pagseguroCheckOutWebServiceRepository;
    }

    public PayPalExpressCheckOutWebServiceRepository getPayPalExpressCheckOutWebServiceRepository() {
        return payPalExpressCheckOutWebServiceRepository;
    }

    public ProductWebServiceRepository getProductWebServiceRepository() {
        return productWebServiceRepository;
    }

    public SettingWebServiceRepository getSettingWebServiceRepository() {
        return settingWebServiceRepository;
    }

    @Override
    protected void init() {
        super.init();

        if ("true".equalsIgnoreCase(System.getProperty("gnuob.debug.enabled", "false"))) {
            WicketSource.configure(this);
        }
    }
}
