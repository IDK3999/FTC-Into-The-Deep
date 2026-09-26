package PrimeTechV3.Components

/**
 * A plain stopwatch, used by autonomous OpModes to pause between steps.
 *
 * Nothing in FTC code may call `sleep()` - the OpMode's `loop()` has to return quickly so
 * the SDK can keep talking to the hardware. So instead of blocking, you note the time,
 * return, and check [isDone] on later loops.
 *
 * **There is only one of these.** It is a Kotlin `object`, so every caller shares the same
 * timer, and a second [start] throws away the first one's deadline. That is fine for a
 * single autonomous sequence running one step at a time, but if you ever need two waits at
 * once, make this a normal `class` and create one instance per wait.
 */
object Delay {
    private var startTimeMs: Long = 0
    private var durationMs: Double = 0.0

    /**
     * Starts the timer.
     *
     * @param seconds how long to wait, in seconds.
     */
    fun start(seconds: Double) {
        durationMs = seconds * 1000
        startTimeMs = System.currentTimeMillis()
    }

    /** True once the time given to [start] has passed. */
    fun isDone(): Boolean {
        return System.currentTimeMillis() - startTimeMs >= durationMs
    }

    fun reset() {
        startTimeMs = 0
        durationMs = 0.0
    }
}
