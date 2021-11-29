package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceLineRepository;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.businessproject.service.ProjectStockMoveInvoiceServiceImpl;
import com.axelor.apps.purchase.db.repo.PurchaseOrderRepository;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.repo.SaleOrderRepository;
import com.axelor.apps.stock.db.StockMove;
import com.axelor.apps.stock.db.StockMoveLine;
import com.axelor.apps.stock.db.repo.StockMoveLineRepository;
import com.axelor.apps.supplychain.service.PurchaseOrderInvoiceService;
import com.axelor.apps.supplychain.service.SaleOrderInvoiceService;
import com.axelor.apps.supplychain.service.StockMoveLineServiceSupplychain;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.apps.supplychain.service.config.SupplyChainConfigService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class InvoiceGstServiceImplStockMove extends ProjectStockMoveInvoiceServiceImpl {

	@Inject
	public InvoiceGstServiceImplStockMove(SaleOrderInvoiceService saleOrderInvoiceService,
			PurchaseOrderInvoiceService purchaseOrderInvoiceService,
			StockMoveLineServiceSupplychain stockMoveLineServiceSupplychain, InvoiceRepository invoiceRepository,
			SaleOrderRepository saleOrderRepo, PurchaseOrderRepository purchaseOrderRepo,
			StockMoveLineRepository stockMoveLineRepository, InvoiceLineRepository invoiceLineRepository,
			SupplyChainConfigService supplyChainConfigService, AppSupplychainService appSupplychainService) {
		super(saleOrderInvoiceService, purchaseOrderInvoiceService, stockMoveLineServiceSupplychain, invoiceRepository,
				saleOrderRepo, purchaseOrderRepo, stockMoveLineRepository, invoiceLineRepository,
				supplyChainConfigService, appSupplychainService);
		// TODO Auto-generated constructor stub
	}

	@Inject
	InvoiceLineGstServiceImpl invoiceLineGstServiceImpl;

	@Inject
	InvoiceGstServiceImpl invoiceGstServiceImpl;

	  @Override
	  @Transactional(rollbackOn = {Exception.class})
	  public Invoice createInvoice(
	      StockMove stockMove,
	      Integer operationSelect,
	      List<Map<String, Object>> stockMoveLineListContext)
	      throws AxelorException {
		Invoice createInvoice = super.createInvoice(stockMove, operationSelect, stockMoveLineListContext);

		if (!Beans.get(AppSupplychainService.class).isApp("gst") || createInvoice.getInvoiceLineList().isEmpty()) {
			return createInvoice;
		} else {

			return invoiceGstServiceImpl.compute(createInvoice);
		}
	}

	@Override
	public List<InvoiceLine> createInvoiceLines(Invoice invoice, StockMove stockMove,
			List<StockMoveLine> stockMoveLineList, Map<Long, BigDecimal> qtyToInvoiceMap) throws AxelorException {
		List<InvoiceLine> createInvoiceLines = super.createInvoiceLines(invoice, stockMove, stockMoveLineList,
				qtyToInvoiceMap);
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
