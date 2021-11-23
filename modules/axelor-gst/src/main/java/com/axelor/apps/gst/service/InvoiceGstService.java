package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.InvoiceLine;
import java.math.BigDecimal;

public interface InvoiceGstService {

  BigDecimal getIgst(InvoiceLine invoiceLine);

  BigDecimal getCgst(InvoiceLine invoiceLine);
}
