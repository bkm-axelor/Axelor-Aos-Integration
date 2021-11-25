package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.Tax;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.InvoiceLineRepository;
import com.axelor.apps.account.db.repo.TaxLineRepository;
import com.axelor.apps.account.db.repo.TaxRepository;
import com.axelor.apps.account.service.AccountManagementAccountService;
import com.axelor.apps.account.service.AnalyticMoveLineService;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.base.service.CurrencyService;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.apps.base.service.ProductCompanyService;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.businessproject.service.InvoiceLineProjectServiceImpl;
import com.axelor.apps.purchase.service.PurchaseProductService;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.db.Query;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class InvoiceLineGstServiceImpl extends InvoiceLineProjectServiceImpl implements InvoiceLineGstService {

	@Inject
	TaxRepository taxRepository;

	@Inject
	TaxLineRepository taxLineRepository;

	@Inject
	public InvoiceLineGstServiceImpl(CurrencyService currencyService, PriceListService priceListService,
			AppAccountService appAccountService, AnalyticMoveLineService analyticMoveLineService,
			AccountManagementAccountService accountManagementAccountService,
			PurchaseProductService purchaseProductService, ProductCompanyService productCompanyService,
			InvoiceLineRepository invoiceLineRepo, AppBaseService appBaseService) {
		super(currencyService, priceListService, appAccountService, analyticMoveLineService,
				accountManagementAccountService, purchaseProductService, productCompanyService, invoiceLineRepo,
				appBaseService);
	}

	public BigDecimal calculateGst(InvoiceLine invoiceLine) {
		if (invoiceLine.getProduct().getGstRate() != null) {
			return invoiceLine.getProduct().getGstRate().divide(BigDecimal.valueOf(100));
		} else {
			return BigDecimal.ZERO;
		}
	}

	public BigDecimal calculateCGst(InvoiceLine invoiceLine, BigDecimal calculateGst) {
		return ((invoiceLine.getQty()).multiply(invoiceLine.getProduct().getSalePrice()).multiply(calculateGst)).divide(new BigDecimal(2.00));
	}

	public BigDecimal calculateIGst(InvoiceLine invoiceLine, BigDecimal calculateGst) {
		return (invoiceLine.getQty()).multiply(invoiceLine.getProduct().getSalePrice()).multiply(calculateGst);
	}
	
	@Override
	public Map<String, Object> fillProductInformation(Invoice invoice, InvoiceLine invoiceLine) throws AxelorException {

		if (invoiceLine.getProduct().getGstRate() != null && Beans.get(AppSupplychainService.class).isApp("gst")) {
			Map<String, Object> fillProductInformation = super.fillProductInformation(invoice, invoiceLine);

			BigDecimal calculateGst = calculateGst(invoiceLine);

			String companyState = invoice.getCompany().getAddress().getState().getName();
			String addressState = invoice.getAddress().getState().getName();
			fillProductInformation.put("gstRate", calculateGst);
			if (companyState.equals(addressState)) {
				fillProductInformation.put("cgst", calculateCGst(invoiceLine, calculateGst));
				fillProductInformation.put("sgst", calculateCGst(invoiceLine, calculateGst));
				fillProductInformation.put("igst", BigDecimal.ZERO);
			} else {
				fillProductInformation.put("cgst", BigDecimal.ZERO);
				fillProductInformation.put("sgst", BigDecimal.ZERO);
				fillProductInformation.put("igst", calculateIGst(invoiceLine, calculateGst));
			}

			return fillProductInformation;
		} else {
			return super.fillProductInformation(invoice, invoiceLine);
		}
	}

	@Override
	public TaxLine getTaxLine(Invoice invoice, InvoiceLine invoiceLine, boolean isPurchase) throws AxelorException {

		if (invoiceLine.getProduct().getGstRate().compareTo(BigDecimal.ZERO) > 0
				&& Beans.get(AppSupplychainService.class).isApp("gst")) {
		
			TaxLine fetchOne = taxLineRepository.all().filter("self.tax.code = 'GS_T' and self.value = ?", invoiceLine.getProduct().getGstRate().divide(BigDecimal.valueOf(100))).fetchOne();
			
			return fetchOne;
		} else {

			return super.getTaxLine(invoice, invoiceLine, isPurchase);
		}
	}
}
