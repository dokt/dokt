@file:Suppress("MagicNumber")

package app.dokt.generator

import app.dokt.common.Version

/** Current Dokt version. */
val vDokt = Version(0, 2, 10)

/** Minimum Gradle version that Dokt plugins support. */
val vGradleMin = Version(8, 2)

/** Latest known version of https://splitties.github.io/refreshVersions */
val vRefreshVersions = Version(0, 51) // TODO 0.60.0 doesn't work
