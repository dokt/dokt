@file:Suppress("unused")

package app.dokt.ui

import java.util.*

val en: Locale = Locale.ENGLISH
val fi = Locale("fi", "FI")
val svFI = Locale("sv", "FI")

lateinit var bundle: ResourceBundle

val Locale.defaultLanguage get() = Locale.getDefault().language == language
val Locale.nativeDisplayLanguage: String get() = getDisplayLanguage(this)
val Locale.nativeDisplayName: String get() = getDisplayName(this)

interface Localized {
    fun localize(locale: Locale = Locale.getDefault())
}
