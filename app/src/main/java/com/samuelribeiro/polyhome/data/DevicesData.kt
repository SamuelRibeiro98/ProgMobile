package com.samuelribeiro.polyhome.data

data class DevicesData(
    val id: String,
    val type: String,
    val availableCommands: List<String>,
    val opening: Int? = null,
    val power: Int? = null
)
