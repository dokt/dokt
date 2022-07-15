package app.dokt

@Suppress("unused")
class StopWatch {
    private var start = System.currentTimeMillis()

    fun ms(): Long {
        val stop = System.currentTimeMillis()
        val duration = stop - start
        start = stop
        return duration
    }
}
