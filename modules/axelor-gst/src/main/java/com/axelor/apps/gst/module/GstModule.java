/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2021 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.account.service.AccountManagementServiceAccountImpl;
import com.axelor.apps.businessproject.service.InvoiceLineProjectServiceImpl;
import com.axelor.apps.businessproject.service.SaleOrderInvoiceProjectServiceImpl;
import com.axelor.apps.cash.management.service.InvoiceServiceManagementImpl;
import com.axelor.apps.gst.service.InvoiceGstService;
import com.axelor.apps.gst.service.InvoiceGstServiceImpl;
import com.axelor.apps.gst.service.InvoiceLineGstService;
import com.axelor.apps.gst.service.InvoiceLineGstServiceImpl;
import com.axelor.apps.gst.service.InvoiceLineGstServiceImpleTax;
import com.axelor.apps.gst.service.ProductGstService;
import com.axelor.apps.gst.service.ProductGstServiceImpl;
import com.axelor.apps.gst.service.invoiceGstServiceImplSaleOrder;

public class GstModule extends AxelorModule {

	@Override
	protected void configure() {
		bind(ProductGstService.class).to(ProductGstServiceImpl.class);
		bind(InvoiceGstService.class).to(InvoiceGstServiceImpl.class);
		bind(InvoiceServiceManagementImpl.class).to(InvoiceGstServiceImpl.class);
		bind(InvoiceLineProjectServiceImpl.class).to(InvoiceLineGstServiceImpl.class);
		bind(InvoiceLineGstService.class).to(InvoiceLineGstServiceImpl.class);
		bind(AccountManagementServiceAccountImpl.class).to(InvoiceLineGstServiceImpleTax.class);
		bind(SaleOrderInvoiceProjectServiceImpl.class).to(invoiceGstServiceImplSaleOrder.class);
	}
}
