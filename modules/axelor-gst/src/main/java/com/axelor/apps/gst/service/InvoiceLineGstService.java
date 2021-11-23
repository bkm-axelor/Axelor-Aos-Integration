package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.InvoiceLine;
import java.math.BigDecimal;

public interface InvoiceLineGstService {

  BigDecimal calculateGst(InvoiceLine invoiceLine);

  BigDecimal calculateCGst(InvoiceLine invoiceLine, BigDecimal calculateGst);

  BigDecimal calculateIGst(InvoiceLine invoiceLine, BigDecimal calculateGst);
}
