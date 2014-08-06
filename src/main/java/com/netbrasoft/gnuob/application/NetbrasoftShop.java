package com.netbrasoft.gnuob.application;

import net.ftlines.wicketsource.WicketSource;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netbrasoft.gnuob.generic.category.CategoryWebServiceRepository;
import com.netbrasoft.gnuob.generic.content.ContentWebServiceRepository;
import com.netbrasoft.gnuob.generic.contract.ContractWebServiceRepository;
import com.netbrasoft.gnuob.generic.customer.CustomerWebServiceRepository;
import com.netbrasoft.gnuob.generic.offer.OfferWebServiceRepository;
import com.netbrasoft.gnuob.generic.order.OrderWebServiceRepository;
import com.netbrasoft.gnuob.generic.order.PayPalExpressCheckOutWebServiceRepository;
import com.netbrasoft.gnuob.generic.product.ProductWebServiceRepository;
import com.netbrasoft.gnuob.product.application.page.ProductListPage;

@Service("wicketApplication")
public class NetbrasoftShop extends WebApplication {

	@Autowired(required = true)
	private CategoryWebServiceRepository categoryWebServiceRepository;

	@Autowired(required = true)
	private PayPalExpressCheckOutWebServiceRepository payPalExpressCheckOutWebServiceRepository;

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

	public PayPalExpressCheckOutWebServiceRepository getPayPalExpressCheckOutWebServiceRepository() {
		return payPalExpressCheckOutWebServiceRepository;
	}

	public ProductWebServiceRepository getProductWebServiceRepository() {
		return productWebServiceRepository;
	}

	@Override
	protected void init() {
		super.init();

		if ("true".equalsIgnoreCase(System.getProperty("gnuob.debug.enabled", "false"))) {
			WicketSource.configure(this);
		}
	}
}
