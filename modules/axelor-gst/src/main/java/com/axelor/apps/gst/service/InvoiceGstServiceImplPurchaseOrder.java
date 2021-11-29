package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.businessproject.service.PurchaseOrderInvoiceProjectServiceImpl;
import com.axelor.apps.purchase.db.PurchaseOrder;
import com.axelor.apps.purchase.db.PurchaseOrderLine;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class InvoiceGstServiceImplPurchaseOrder extends PurchaseOrderInvoiceProjectServiceImpl {

	@Inject
	InvoiceLineGstServiceImpl invoiceLineGstServiceImpl;

	@Inject
	InvoiceGstServiceImpl invoiceGstServiceImpl;

	@Override
	@Transactional(rollbackOn = { Exception.class })
	public Invoice generateInvoice(PurchaseOrder purchaseOrder) throws AxelorException {

		Invoice createInvoice = super.generateInvoice(purchaseOrder);

		if (!Beans.get(AppSupplychainService.class).isApp("gst") || createInvoice.getInvoiceLineList().isEmpty()) {
			return createInvoice;
		} else {

			return invoiceGstServiceImpl.compute(createInvoice);
		}

	}

	@Override
	public List<InvoiceLine> createInvoiceLine(Invoice invoice, PurchaseOrderLine purchaseOrderLine)
			throws AxelorException {

		List<InvoiceLine> createInvoiceLines = super.createInvoiceLine(invoice, purchaseOrderLine);
		if (!Beans.get(AppSupplychainService.class).isApp("gst")) {
			return createInvoiceLines;
		} else {
			for (InvoiceLine invoiceLine : createInvoiceLines) {
				Map<String, Object> fillProductInformation = invoiceLineGstServiceImpl.fillProductInformation(invoice,
						invoiceLine);
				invoiceLine.setGstRate((BigDecimal) fillProductInformation.get("gstRate"));
				invoiceLine.setSgst((BigDecimal) fillProductInformation.get("sgst"));
				invoiceLine.setCgst((BigDecimal) fillProductInformation.get("sgst"));
				invoiceLine.setIgst((BigDecimal) fillProductInformation.get("igst"));
			}
			return createInvoiceLines;
		}
	}

}
