package app.dokt.infra

import org.apache.commons.lang3.SystemUtils

actual val osArch: String? = SystemUtils.OS_ARCH
actual val osName: String? = SystemUtils.OS_NAME
actual val osVer: String? = SystemUtils.OS_VERSION
actual val linux = SystemUtils.IS_OS_LINUX
actual val mac = SystemUtils.IS_OS_MAC
actual val windows = SystemUtils.IS_OS_WINDOWS

actual val hostname: String? = SystemUtils.getHostName()
actual val java = SystemUtils.JAVA_SPECIFICATION_VERSION?.substringAfter('.')?.toInt()
actual val username: String = SystemUtils.getUserName()
actual val userDir = SystemUtils.getUserDir().toString()

object SystemJvm : Logger({}) {
    /**
     * Is user-interface scale enabled
     * https://news.kynosarges.org/2019/03/24/swing-high-dpi-properties/
     */
    var scale = true
        set(value) {
            debug { "${if (value) "Enabling" else "Disabling"} scale." }
            when {
                value == field -> warn { "Setting scale twice!" }
                !value -> {
                    defineProperty2d("dpiaware")
                    defineProperty2d("uiScale", 1.0)
                    defineProperty2d("uiScale.enabled")
                    definePropertyWinScale("")
                    definePropertyWinScale("X")
                    definePropertyWinScale("Y")
                    info { "Scale disabled." }
                } else -> throw UnsupportedOperationException("Scale can't be enabled afterwards!")
            }
            field = value
        }

    fun defineProperty(key: String, value: Any) {
        val old = System.getProperty(key)
        if (value == old) info { "Property $key value '$value' keep unchanged." }
        else {
            System.setProperty(key, value.toString())
            info { "Property $key value changed from '$old' to '$value'." }
        }
    }

    private fun defineProperty2d(suffix: String, value: Any = false) =
        defineProperty("sun.java2d.$suffix", value)

    private fun definePropertyWinScale(suffix: String, scale: Double = 1.0) =
        defineProperty2d("win.uiScale$suffix", scale)
}