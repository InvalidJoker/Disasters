package de.joker.disasters.common

abstract class Disaster(
    val name: String,
    val explanation: String,
    val incompatibleWith: List<String> = emptyList(),
    val disableOnNextDisaster: Boolean = false,
    val canBeFirstDisaster: Boolean = true,
) {
    abstract fun start()
    abstract fun stop()
    open fun second() {}
}