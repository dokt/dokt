package com.corex.erp.shared

import kotlinx.serialization.Serializable

/**
 * Value added tax number which identifies company
 **/
@Serializable
data class VatNo(val value: String) {
    init {
        if (value.startsWith("FI")) {
            if (value.length != 10 || value.substring(2).any { !it.isDigit() })
                throw IllegalArgumentException(value)
        } else {
            throw IllegalArgumentException(value)
        }
    }
}
