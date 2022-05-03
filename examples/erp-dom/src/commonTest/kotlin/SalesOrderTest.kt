package com.corex.erp.so

class SalesOrderTest : SalesOrderSpec({
    acceptQuotation {
        test("SalesOrderConfirmed") {
            salesOrder.act { acceptQuotation() }.emits(SalesOrderConfirmed)
        }
        test("quotationAccepted") {
            salesOrder { salesOrderConfirmed() }.act { acceptQuotation() }.nothing
        }
    }

    changeCustomer {

    }

    deliver {

    }

    requestForQuotation {

    }
})
