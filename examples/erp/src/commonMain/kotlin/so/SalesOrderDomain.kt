package com.corex.erp.so

import app.dokt.Root
import com.corex.erp.shared.VatNo
import kotlinx.serialization.Serializable

//#region Exceptions
data class CustomerIsBanned(val customer: VatNo) : IllegalArgumentException()

class QuotationNotAccepted : IllegalArgumentException()
//#endregion

interface SalesBans : Collection<VatNo>

interface Events {
    fun customerChanged(customer: VatNo)

    fun delivered()

    fun salesOrderConfirmed()

    fun quotationCreated(customer: VatNo)
}

@Serializable
class SalesOrder : Root<Events>(), Events {
    companion object {
        lateinit var salesBans: SalesBans
    }

    var customer: VatNo? = null
    //lateinit var customer: VatNo

    var delivered = false

    var quotationAccepted = false

    //#region Command handlers
    fun acceptQuotation() {
        if (!quotationAccepted) emit.salesOrderConfirmed()
    }

    fun requestForQuotation(customer: VatNo) {
        if (customer in salesBans) throw CustomerIsBanned(customer)
        emit.quotationCreated(customer)
    }

    fun changeCustomer(customer: VatNo) {
        if (customer == this.customer) return
        if (customer in salesBans) throw CustomerIsBanned(customer)
        emit.customerChanged(customer)
    }

    fun deliver() {
        if (quotationAccepted) emit.delivered()
        else throw QuotationNotAccepted()
    }
    //#endregion

    //#region Event handlers
    override fun customerChanged(customer: VatNo) {
        this.customer = customer
        quotationAccepted = false
    }

    override fun delivered() {
        delivered = true
    }

    override fun salesOrderConfirmed() {
        quotationAccepted = true
    }

    override fun quotationCreated(customer: VatNo) {
        this.customer = customer
    }
    //#endregion
}
