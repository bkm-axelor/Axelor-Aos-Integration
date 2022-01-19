package com.axelor.apps.gst.batch;

import java.util.List;

import com.axelor.apps.base.db.Batch;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.repo.BatchRepository;
import com.axelor.apps.message.db.Message;
import com.axelor.apps.message.db.Template;
import com.axelor.apps.message.db.repo.TemplateRepository;
import com.axelor.apps.message.service.MessageService;
import com.axelor.apps.message.service.TemplateMessageService;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.repo.SaleOrderRepository;
import com.axelor.apps.supplychain.service.batch.BatchStrategy;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.persist.Transactional;

public class SaleOrderReport extends BatchStrategy {

	@Override
	@Transactional
	protected void process() {
		Partner partner = batch.getSaleBatch().getPartner();
		Batch recentRanBatch = Beans.get(BatchRepository.class).all().filter("self.saleBatch != null")
				.order("-updatedOn").fetch(2, 1).get(0);
		List<SaleOrder> saleOrders = null;
		if (partner != null) {
			saleOrders = Beans.get(SaleOrderRepository.class).all().filter(
					"self.statusSelect = :status AND self.updatedOn > :updatedOn AND self.clientPartner = :partner")
					.bind("status", SaleOrderRepository.STATUS_ORDER_CONFIRMED).bind("partner", partner)
					.bind("updatedOn", recentRanBatch.getEndDate()).fetch();

		} else {
			saleOrders = Beans.get(SaleOrderRepository.class).all()
					.filter("self.statusSelect = :status AND self.updatedOn > :updatedOn")
					.bind("status", SaleOrderRepository.STATUS_ORDER_CONFIRMED)
					.bind("updatedOn", recentRanBatch.getEndDate()).fetch();
		}

		for (SaleOrder saleOrder : saleOrders) {
			try {
				Template template = Beans.get(TemplateRepository.class).findByName("SaleOrderReportAndSendMail2");
				Message message = Beans.get(TemplateMessageService.class).generateMessage(saleOrder, template);
				Beans.get(MessageService.class).sendMessage(message);
				incrementDone();
			} catch (AxelorException e) {
				incrementAnomaly();
			} catch (Exception e) {
				incrementAnomaly();
			}
		}

	}

	@Override
	protected void stop() {
		String comment = " Sale Order Report Generation And Send Mail :" + " ";
		comment += String.format("\t* %s " + "Order(s) processed" + "\n", batch.getDone());
		comment += String.format("\t" + "* %s anomaly(ies)", batch.getAnomaly());

		super.stop();
		addComment(comment);
	}
}
