package com.axelor.apps.gst.web;

import com.axelor.apps.base.db.Product;
import com.axelor.apps.gst.service.ProductGstService;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.math.BigDecimal;

public class ProductGstController {

  public void getGst(ActionRequest request, ActionResponse response) {
    Product product = request.getContext().asType(Product.class);
    if (Beans.get(AppSupplychainService.class).isApp("gst")) {
      if (product.getProductCategory() != null) {
        BigDecimal calculategstRate = Beans.get(ProductGstService.class).calculateGst(product);
        response.setValue("gstRate", calculategstRate);
      } else {
        response.setValue("gstRate", BigDecimal.ZERO);
      }
    }
  }
}
