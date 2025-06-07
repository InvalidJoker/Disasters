package de.joker.disasters.common

interface Disaster {
    val name: String
    val explanation: String

    val incompatibleWith: List<String>

    fun start()

    fun stop()

    fun second()
}