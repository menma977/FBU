package com.owl.minerva.fbu.cores.configs

object AutomationConfig {
    /**
     * Typing speed in milliseconds. 
     * Min/Max range to simulate human variance.
     */
    const val MIN_TYPING_DELAY_MS: Long = 50L
    const val MAX_TYPING_DELAY_MS: Long = 200L

    /**
     * Delay between different automation steps (e.g. after clicking a button).
     */
    const val MIN_STEP_DELAY_MS: Long = 2000L
    const val MAX_STEP_DELAY_MS: Long = 5000L

    /**
     * Delay when searching or waiting for elements to appear.
     */
    const val DEFAULT_TIMEOUT_MS: Double = 30000.0
    
    /**
     * Human-like scroll speed configuration.
     */
    const val SCROLL_STEP_PIXELS: Int = 100
    const val SCROLL_DELAY_MS: Long = 150L
}
