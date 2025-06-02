package com.appberto.estancoclickerandroid

data class OfflineUpgrade(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val baseCost: Long,
    val hoursBonus: Double, // Horas adicionales que otorga
    var count: Int = 0
) {
    fun getCost(currentCount: Int = count): Long {
        return (baseCost * Math.pow(1.5, currentCount.toDouble())).toLong()
    }
    
    fun getTotalHoursBonus(currentCount: Int = count): Double {
        return hoursBonus * currentCount
    }
}
