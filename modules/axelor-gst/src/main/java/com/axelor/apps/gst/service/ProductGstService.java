package com.axelor.apps.gst.service;

import com.axelor.apps.base.db.Product;
import java.math.BigDecimal;

public interface ProductGstService {

  BigDecimal calculateGst(Product product);
}
