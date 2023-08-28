@file:Suppress("unused")

package app.dokt.ui

import java.util.*

open class BundleResources(baseName: String? = null) : Resources {
    private val bundle = ResourceBundle.getBundle(baseName ?: "${javaClass.packageName}.Bundle")!!

    /**
     * Strings provided by keys
     */
    protected val s = bundle.keys.toList().associate { it!! to bundle.getString(it) }
}
