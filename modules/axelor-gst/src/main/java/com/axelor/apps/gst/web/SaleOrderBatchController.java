package com.axelor.apps.gst.web;

import com.axelor.apps.base.db.Batch;
import com.axelor.apps.gst.batch.SaleOrderReport;
import com.axelor.apps.sale.db.SaleBatch;
import com.axelor.apps.sale.db.repo.SaleBatchRepository;
import com.axelor.apps.supplychain.web.SaleBatchController;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class SaleOrderBatchController extends SaleBatchController {

  public void actionSaleOrder(ActionRequest request, ActionResponse response)
      throws AxelorException {

    SaleBatch saleBatch = request.getContext().asType(SaleBatch.class);
    saleBatch = Beans.get(SaleBatchRepository.class).find(saleBatch.getId());
    Batch batch = Beans.get(SaleOrderReport.class).run(saleBatch);
    response.setFlash(batch.getComments());
    response.setReload(true);
  }
}
