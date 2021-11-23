package com.axelor.apps.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.gst.service.InvoiceLineGstService;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.math.BigDecimal;

public class InvoiceLineGstController {

  public void calculateGsts(ActionRequest request, ActionResponse response) {

    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
    Invoice invoice = request.getContext().getParent().asType(Invoice.class);

    if (Beans.get(AppSupplychainService.class).isApp("gst")) {

      BigDecimal gstRate = invoiceLine.getGstRate();
      String companyState = invoice.getCompany().getAddress().getState().getName();
      String addressState = invoice.getAddress().getState().getName();
      if (companyState.equals(addressState)) {
        BigDecimal calculatedCGst =
            Beans.get(InvoiceLineGstService.class).calculateCGst(invoiceLine, gstRate);
        response.setValue("cgst", calculatedCGst);
        response.setValue("sgst", calculatedCGst);
      } else {
        BigDecimal calculatedIGst =
            Beans.get(InvoiceLineGstService.class).calculateIGst(invoiceLine, gstRate);
        response.setValue("igst", calculatedIGst);
      }
    }
  }
}
