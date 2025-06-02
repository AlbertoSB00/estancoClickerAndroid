package com.appberto.estancoclickerandroid

data class ClickUpgrade(
    val id: String,
    val name: String,
    val description: String,
    val baseCost: Long,
    val clickBonus: Long,
    var count: Int = 0
) {
    fun getCost(currentCount: Int = count): Long {
        return (baseCost * Math.pow(1.15, currentCount.toDouble())).toLong()
    }
}

object ClickUpgradeData {
    fun getAllClickUpgrades(): List<ClickUpgrade> {
        return listOf(
            ClickUpgrade(
                id = "cursor",
                name = "Cursor",
                description = "Mejora tus clicks manuales",
                baseCost = 15,
                clickBonus = 1
            )
        )
    }
}
