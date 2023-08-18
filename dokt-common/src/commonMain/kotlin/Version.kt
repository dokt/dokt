package app.dokt.common

import kotlinx.serialization.Serializable

/** [Semantic version](https://semver.org) **/
@JvmInline
@Serializable
value class Version(private val no: String) : Comparable<Version> {
    /** Build metadata. */
    val build get() = no.substringAfter('+', "").ifBlank { null }

    /** Core version doesn't contain pre-release or build metadata. */
    val core get() = no.substringBefore('-').substringBefore("+")

    /** Increment major version when you make incompatible API changes. */
    val major get() = core.major

    /** Increment minor version when you add functionality in a backward compatible manner. */
    val minor get() = core.minor

    /** Increment patch version when you make backward compatible bug fixes. */
    val patch get() = core.patch

    /** A pre-release version. */
    val preRelease get() = no.substringAfter('-', "").substringBefore("+")
        .ifBlank { null }

    /** A pre-release identifiers. */
    val preReleaseIds get() = preRelease.ids

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: Version): Int {
        val thisCore = core
        val otherCore = other.core
        var result = thisCore.major.compareTo(otherCore.major)
        if (result == 0) {
            result = thisCore.minor.compareTo(otherCore.minor)
            if (result == 0) {
                result = thisCore.patch.compareTo(otherCore.patch)
                if (result == 0) {
                    val thisPre = preRelease
                    val otherPre = other.preRelease
                    if (thisPre == null) {
                        return if (otherPre == null) 0 else 1
                    } else if (otherPre == null) return -1
                    val thisIds = thisPre.ids
                    val otherIds = otherPre.ids
                    val max = maxOf(thisIds.size, otherIds.size)
                    for (i in 0..max) {
                        val thisId = thisIds.getOrNull(i) ?: return -1
                        val otherId = otherIds.getOrNull(i) ?: return 1
                        val thisInt = thisId.toIntOrNull()
                        val otherInt = otherId.toIntOrNull()
                        result =
                            if (thisInt != null && otherInt != null) thisInt.compareTo(otherInt)
                            else thisId.compareTo(otherId)
                        if (result != 0) return result
                    }
                }
            }
        }
        return result
    }

    override fun toString() = no

    private val String?.ids get() = this?.split('.') ?: emptyList()

    private val String.major get() = substringBefore('.').toInt()

    private val String.minor get() = substringAfter('.', "0").substringBefore(".")
        .toInt()

    private val String.patch get() = substringAfter('.', "0.0")
        .substringAfter(".", "0").toInt()
}