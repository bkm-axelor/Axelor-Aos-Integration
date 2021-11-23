package com.axelor.apps.gst.web;

import java.math.BigDecimal;
import java.util.List;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.gst.service.InvoiceGstServiceImpl;
import com.axelor.apps.gst.service.InvoiceLineGstServiceImpl;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.persist.Transactional;

public class InvoiceGstController {

	@Transactional
	public void calculateallGsts(ActionRequest request, ActionResponse response) throws AxelorException {

		Invoice invoice = request.getContext().asType(Invoice.class);

		if (invoice.getPartner() != null && Beans.get(AppSupplychainService.class).isApp("gst")
				&& invoice.getInvoiceLineList() != null && !invoice.getInvoiceLineList().isEmpty()) {
			
			List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();

			for (InvoiceLine invoiceLine : invoiceLineList) {

				BigDecimal calculateGst = Beans.get(InvoiceLineGstServiceImpl.class).calculateGst(invoiceLine);

				String companyState = invoice.getCompany().getAddress().getState().getName();
				String addressState = invoice.getAddress().getState().getName();

				if (companyState.equals(addressState)) {
					BigDecimal calculateCGst = Beans.get(InvoiceLineGstServiceImpl.class).calculateCGst(invoiceLine,
							calculateGst);
					invoiceLine.setCgst(calculateCGst);
					invoiceLine.setSgst(calculateCGst);
					invoiceLine.setIgst(BigDecimal.ZERO);
				} else {
					BigDecimal calculateIGst = Beans.get(InvoiceLineGstServiceImpl.class).calculateIGst(invoiceLine,
							calculateGst);
					invoiceLine.setCgst(BigDecimal.ZERO);
					invoiceLine.setSgst(BigDecimal.ZERO);
					invoiceLine.setIgst(calculateIGst);
				}

			}
			response.setValue("invoiceLineList", invoiceLineList);
			Invoice compute = Beans.get(InvoiceGstServiceImpl.class).compute(invoice);
			response.setValue("netCgst", compute.getNetCgst());
			response.setValue("netSgst", compute.getNetSgst());
			response.setValue("netIgst", compute.getNetIgst());

		} else {
			response.setValue("cgst", BigDecimal.ZERO);
			response.setValue("sgst", BigDecimal.ZERO);
			response.setValue("igst", BigDecimal.ZERO);
			response.setValue("netCgst", BigDecimal.ZERO);
			response.setValue("netSgst", BigDecimal.ZERO);
			response.setValue("netIgst", BigDecimal.ZERO);
		}
	}
}
