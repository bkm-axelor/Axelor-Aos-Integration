package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.invoice.InvoiceService;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.businessproject.service.SaleOrderInvoiceProjectServiceImpl;
import com.axelor.apps.businessproject.service.app.AppBusinessProjectService;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.db.repo.SaleOrderRepository;
import com.axelor.apps.sale.service.saleorder.SaleOrderLineService;
import com.axelor.apps.sale.service.saleorder.SaleOrderWorkflowServiceImpl;
import com.axelor.apps.stock.db.repo.StockMoveRepository;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class invoiceGstServiceImplSaleOrder extends SaleOrderInvoiceProjectServiceImpl {

  @Inject
  public invoiceGstServiceImplSaleOrder(
      AppBaseService appBaseService,
      AppSupplychainService appSupplychainService,
      SaleOrderRepository saleOrderRepo,
      InvoiceRepository invoiceRepo,
      InvoiceService invoiceService,
      AppBusinessProjectService appBusinessProjectService,
      StockMoveRepository stockMoveRepository,
      SaleOrderLineService saleOrderLineService,
      SaleOrderWorkflowServiceImpl saleOrderWorkflowServiceImpl) {
    super(
        appBaseService,
        appSupplychainService,
        saleOrderRepo,
        invoiceRepo,
        invoiceService,
        appBusinessProjectService,
        stockMoveRepository,
        saleOrderLineService,
        saleOrderWorkflowServiceImpl);
    // TODO Auto-generated constructor stub
  }

  @Inject InvoiceLineGstServiceImpl invoiceLineGstServiceImpl;

  @Inject InvoiceGstServiceImpl invoiceGstServiceImpl;

  @Override
  public Invoice createInvoice(
      SaleOrder saleOrder,
      List<SaleOrderLine> saleOrderLineList,
      Map<Long, BigDecimal> qtyToInvoiceMap)
      throws AxelorException {
    Invoice createInvoice = super.createInvoice(saleOrder, saleOrderLineList, qtyToInvoiceMap);

    if (!Beans.get(AppSupplychainService.class).isApp("gst")) {
      return createInvoice;
    } else {

      return invoiceGstServiceImpl.compute(createInvoice);
    }
  }

  @Override
  public List<InvoiceLine> createInvoiceLines(
      Invoice invoice, List<SaleOrderLine> saleOrderLineList, Map<Long, BigDecimal> qtyToInvoiceMap)
      throws AxelorException {
    // TODO Auto-generated method stub
    List<InvoiceLine> createInvoiceLines =
        super.createInvoiceLines(invoice, saleOrderLineList, qtyToInvoiceMap);

    for (InvoiceLine invoiceLine : createInvoiceLines) {
      BigDecimal calculateGst = invoiceLineGstServiceImpl.calculateGst(invoiceLine);
      BigDecimal calculateCGst = invoiceLineGstServiceImpl.calculateCGst(invoiceLine, calculateGst);
      BigDecimal calculateIGst = invoiceLineGstServiceImpl.calculateIGst(invoiceLine, calculateGst);
      invoiceLine.setCgst(calculateCGst);
      invoiceLine.setIgst(calculateIGst);
    }

    return createInvoiceLines;
  }

  //	@Override
  //	public Invoice createInvoice(SaleOrder saleOrder, List<SaleOrderLine> saleOrderLineList) throws
  // AxelorException {
  //
  //
  //	}

}
