package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceLineRepository;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.account.service.config.AccountConfigService;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
import com.axelor.apps.account.service.invoice.factory.CancelFactory;
import com.axelor.apps.account.service.invoice.factory.ValidateFactory;
import com.axelor.apps.account.service.invoice.factory.VentilateFactory;
import com.axelor.apps.account.service.move.MoveToolService;
import com.axelor.apps.base.service.PartnerService;
import com.axelor.apps.base.service.alarm.AlarmEngineService;
import com.axelor.apps.cash.management.service.InvoiceEstimatedPaymentService;
import com.axelor.apps.cash.management.service.InvoiceServiceManagementImpl;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.math.BigDecimal;

public class InvoiceGstServiceImpl extends InvoiceServiceManagementImpl implements InvoiceGstService {

	@Inject
	public InvoiceGstServiceImpl(ValidateFactory validateFactory, VentilateFactory ventilateFactory,
			CancelFactory cancelFactory, AlarmEngineService<Invoice> alarmEngineService, InvoiceRepository invoiceRepo,
			AppAccountService appAccountService, PartnerService partnerService, InvoiceLineService invoiceLineService,
			AccountConfigService accountConfigService, MoveToolService moveToolService,
			InvoiceLineRepository invoiceLineRepo, InvoiceEstimatedPaymentService invoiceEstimatedPaymentService) {
		super(validateFactory, ventilateFactory, cancelFactory, alarmEngineService, invoiceRepo, appAccountService,
				partnerService, invoiceLineService, accountConfigService, moveToolService, invoiceLineRepo,
				invoiceEstimatedPaymentService);
	}

	@Override
	public BigDecimal getIgst(InvoiceLine invoiceLine) {
		BigDecimal igst = invoiceLine.getIgst();
		if (igst != null) {
			return invoiceLine.getIgst();
		} else {
			return BigDecimal.ZERO;
		}
	}

	@Override
	public BigDecimal getCgst(InvoiceLine invoiceLine) {
		BigDecimal cgst = invoiceLine.getCgst();
		if (cgst != null) {
			return cgst;
		} else {
			return BigDecimal.ZERO;
		}
	}

	@Override
	public Invoice compute(Invoice invoice) throws AxelorException {
		Invoice computedInvoice = super.compute(invoice);
		BigDecimal netIgst = new BigDecimal(0.00);
		BigDecimal netCgst = new BigDecimal(0.00);
//		computedInvoice.setNetCgst(BigDecimal.ZERO);
//		computedInvoice.setNetIgst(BigDecimal.ZERO);
//		computedInvoice.setNetSgst(BigDecimal.ZERO);

		if (!Beans.get(AppSupplychainService.class).isApp("gst")) {
			return computedInvoice;
		} else {
			if (!invoice.getInvoiceLineList().isEmpty()) {
				for (InvoiceLine invoiceLine : invoice.getInvoiceLineList()) {
					netIgst = netIgst.add(getIgst(invoiceLine));
					netCgst = netCgst.add(getCgst(invoiceLine));
				}
				computedInvoice.setNetCgst(netCgst);
				computedInvoice.setNetIgst(netIgst);
				computedInvoice.setNetSgst(netCgst);

				return computedInvoice;
			} else {
				computedInvoice.setNetCgst(BigDecimal.ZERO);
				computedInvoice.setNetIgst(BigDecimal.ZERO);
				computedInvoice.setNetSgst(BigDecimal.ZERO);
				return computedInvoice;
			}
		}
	}
}
