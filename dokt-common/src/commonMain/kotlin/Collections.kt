package app.dokt.common

private const val FOURTH_INDEX = 3

val <T> List<T>.first get() = first()
val <T> List<T>.fourth get() = get(FOURTH_INDEX)
val <T> List<T>.last get() = last()
val <T> List<T>.second get() = get(1)
val <T> List<T>.third get() = get(2)

/**
 * @return List which contains the object if it's not null.
 */
val <T> T?.list get() = if (this == null) emptyList<T>() else listOf(this)
