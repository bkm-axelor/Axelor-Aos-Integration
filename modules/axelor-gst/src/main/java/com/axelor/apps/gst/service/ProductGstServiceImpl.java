package com.axelor.apps.gst.service;

import com.axelor.apps.base.db.Product;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.inject.Beans;
import java.math.BigDecimal;

public class ProductGstServiceImpl implements ProductGstService {

  @Override
  public BigDecimal calculateGst(Product product) {
    BigDecimal gstRate = product.getProductCategory().getGstRate();
    if (gstRate != null && Beans.get(AppSupplychainService.class).isApp("gst")) {
      return gstRate;
    } else {
      return BigDecimal.ZERO;
    }
  }
}
