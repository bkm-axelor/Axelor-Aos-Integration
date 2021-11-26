package com.axelor.apps.gst.service;

import java.math.BigDecimal;

import com.axelor.apps.account.db.Tax;
import com.axelor.apps.account.db.repo.TaxLineRepository;
import com.axelor.apps.account.service.AccountManagementServiceAccountImpl;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.service.tax.FiscalPositionService;
import com.axelor.apps.base.service.tax.TaxService;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.inject.Beans;
import com.google.inject.Inject;

public class InvoiceLineGstServiceImpleTax extends AccountManagementServiceAccountImpl {

	@Inject
	TaxLineRepository taxLineRepository;

	@Inject
	public InvoiceLineGstServiceImpleTax(FiscalPositionService fiscalPositionService, TaxService taxService) {
		super(fiscalPositionService, taxService);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Tax getProductTax(Product product, Company company, boolean isPurchase, int configObject) {

		if (product.getGstRate() != null && product.getGstRate().compareTo(BigDecimal.ZERO) > 0
				&& Beans.get(AppSupplychainService.class).isApp("gst")) {

			return taxLineRepository.all().filter("self.tax.code = 'GS_T' and self.value = ?",
					product.getGstRate().divide(BigDecimal.valueOf(100))).fetchOne().getTax();

		} else {
			return super.getProductTax(product, company, isPurchase, configObject);
		}

	}

}
