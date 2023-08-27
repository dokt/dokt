package app.dokt.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Geolocation */
@Serializable
@SerialName("loc")
data class Geolocation(
    /** Route name aka street */
    @SerialName("rt")
    val street: String,

    /** House number on route */
    @SerialName("no")
    val houseNumber: Int,
)
