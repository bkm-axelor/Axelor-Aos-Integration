package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

public class InvoiceGstServiceImplSaleOrder extends SaleOrderInvoiceProjectServiceImpl {

	@Inject
	public InvoiceGstServiceImplSaleOrder(AppBaseService appBaseService, AppSupplychainService appSupplychainService,
			SaleOrderRepository saleOrderRepo, InvoiceRepository invoiceRepo, InvoiceService invoiceService,
			AppBusinessProjectService appBusinessProjectService, StockMoveRepository stockMoveRepository,
			SaleOrderLineService saleOrderLineService, SaleOrderWorkflowServiceImpl saleOrderWorkflowServiceImpl) {
		super(appBaseService, appSupplychainService, saleOrderRepo, invoiceRepo, invoiceService,
				appBusinessProjectService, stockMoveRepository, saleOrderLineService, saleOrderWorkflowServiceImpl);
		// TODO Auto-generated constructor stub
	}

	@Inject
	InvoiceLineGstServiceImpl invoiceLineGstServiceImpl;

	@Inject
	InvoiceGstServiceImpl invoiceGstServiceImpl;

	@Override
	public Invoice createInvoice(SaleOrder saleOrder, List<SaleOrderLine> saleOrderLineList,
			Map<Long, BigDecimal> qtyToInvoiceMap) throws AxelorException {
		Invoice createInvoice = super.createInvoice(saleOrder, saleOrderLineList, qtyToInvoiceMap);

		if (!Beans.get(AppSupplychainService.class).isApp("gst") || createInvoice.getInvoiceLineList().isEmpty()) {
			return createInvoice;
		} else {

			return invoiceGstServiceImpl.compute(createInvoice);
		}
	}

	@Override
	public List<InvoiceLine> createInvoiceLines(Invoice invoice, List<SaleOrderLine> saleOrderLineList,
			Map<Long, BigDecimal> qtyToInvoiceMap) throws AxelorException {
		List<InvoiceLine> createInvoiceLines = super.createInvoiceLines(invoice, saleOrderLineList, qtyToInvoiceMap);
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
