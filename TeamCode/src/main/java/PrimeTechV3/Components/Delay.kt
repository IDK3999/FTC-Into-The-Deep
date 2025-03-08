package PrimeTechV3.Components

object Delay {
    private var delayStartTime: Long = 0
    private var delayDuration: Double = 0.0

    fun init() {}

    /**
     * Starts the delay timer.
     *
     * @param seconds The duration of the delay in seconds.
     */
    fun start(seconds: Double) {
        delayDuration = seconds * 1000 // Convert seconds to milliseconds
        delayStartTime = System.currentTimeMillis()
    }

    fun isDone(): Boolean {
        return System.currentTimeMillis() - delayStartTime >= delayDuration
    }

    fun reset() {
        delayStartTime = 0
        delayDuration = 0.0
    }
}