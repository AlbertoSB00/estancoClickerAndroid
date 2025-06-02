package com.appberto.estancoclickerandroid

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.URLDecoder
import java.net.URLEncoder

data class GameData(
    var money: Long = 0,
    var moneyPerClick: Long = 1,
    var incomePerSecond: Double = 0.0,
    var totalClicks: Long = 0,
    var totalEarned: Long = 0,
    var currentBusinessLevel: Int = 1,
    var prestigeLevel: Int = 0,
    var prestigePoints: Int = 0,
    var totalLifetimeEarnings: Long = 0,
    var gameStartTime: Long = System.currentTimeMillis(),
    var lastPlayTime: Long = System.currentTimeMillis(),
    var speedClickCount: Int = 0,
    var speedClickStartTime: Long = 0,
    var clickUpgrades: MutableMap<String, Int> = mutableMapOf(),
    var businessUpgrades: MutableMap<String, Int> = mutableMapOf(),
    var offlineUpgrades: MutableMap<String, Int> = mutableMapOf(),
    var achievements: MutableMap<String, Boolean> = mutableMapOf()
) {
    fun calculateMoneyPerClick(clickUpgradesList: List<ClickUpgrade>): Long {
        var baseClick = 1L
        
        // Bonus de cursores manuales
        clickUpgradesList.forEach { upgrade ->
            val count = clickUpgrades[upgrade.id] ?: 0
            baseClick += count * upgrade.clickBonus
        }
        
        // Aplicar bonus de prestigio (10% por nivel de prestigio)
        val prestigeMultiplier = 1.0 + (prestigeLevel * 0.1)
        val finalClick = (baseClick * prestigeMultiplier).toLong()
        
        return maxOf(1L, finalClick)
    }

    fun getCurrentBusiness(businessUpgradesList: List<BusinessUpgrade>): BusinessUpgrade {
        var currentBusiness = BusinessUpgrade(
            "default", "Vendedor de Cigarrillos", "Vendes cigarrillos sueltos en la calle",
            0, 0.0, 1, "🚬", "VENDIENDO CIGARRILLOS"
        )
        
        businessUpgradesList.forEach { upgrade ->
            val count = businessUpgrades[upgrade.id] ?: 0
            if (count > 0 && upgrade.level > currentBusiness.level) {
                currentBusiness = upgrade
            }
        }
        
        return currentBusiness
    }
    
    fun recalculateIncomePerSecond(businessUpgradesList: List<BusinessUpgrade>) {
        incomePerSecond = 0.0
        val prestigeMultiplier = 1.0 + (prestigeLevel * 0.1)
        
        businessUpgradesList.forEach { upgrade ->
            val count = businessUpgrades[upgrade.id] ?: 0
            incomePerSecond += count * upgrade.incomeBonus * prestigeMultiplier
        }
    }

    fun calculateMaxOfflineHours(offlineUpgradesList: List<OfflineUpgrade>): Double {
        var baseHours = 1.0 // Base: 1 hour

        offlineUpgradesList.forEach { upgrade ->
            val isPurchased = offlineUpgrades[upgrade.id] ?: 0
            if (isPurchased >= 1) {
                baseHours += upgrade.hoursBonus
            }
        }

        return baseHours
    }

    fun canPrestige(): Boolean {
        return totalEarned >= 25000000000L // 25 billion
    }

    fun calculatePrestigePoints(): Int {
        return if (canPrestige()) {
            (totalEarned / 1000000000L).toInt() // 1 point per billion earned
        } else {
            0
        }
    }
}

class GameDataManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("fumadero_tycoon_save", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun saveGame(gameData: GameData) {
        val json = gson.toJson(gameData)
        prefs.edit().putString("game_data", json).apply()
    }
    
    fun loadGame(): GameData {
        val json = prefs.getString("game_data", null)
        return if (json != null) {
            try {
                gson.fromJson(json, GameData::class.java)
            } catch (e: Exception) {
                GameData() // Return default if parsing fails
            }
        } else {
            GameData()
        }
    }
    
    fun exportGame(gameData: GameData): String {
        try {
            val exportData = mapOf(
                "money" to gameData.money,
                "moneyPerClick" to gameData.moneyPerClick,
                "incomePerSecond" to gameData.incomePerSecond,
                "totalClicks" to gameData.totalClicks,
                "totalEarned" to gameData.totalEarned,
                "currentBusinessLevel" to gameData.currentBusinessLevel,
                "prestigeLevel" to gameData.prestigeLevel,
                "prestigePoints" to gameData.prestigePoints,
                "totalLifetimeEarnings" to gameData.totalLifetimeEarnings,
                "clickUpgrades" to gameData.clickUpgrades,
                "businessUpgrades" to gameData.businessUpgrades,
                "achievements" to gameData.achievements,
                "exportDate" to System.currentTimeMillis(),
                "gameVersion" to "1.0"
            )
            
            val jsonString = gson.toJson(exportData)
            val encodedString = URLEncoder.encode(jsonString, "UTF-8")
            val base64String = Base64.encodeToString(encodedString.toByteArray(), Base64.DEFAULT)
            
            return "FT_${base64String.replace("\n", "")}_END"
        } catch (e: Exception) {
            throw Exception("Error al generar el código de partida: ${e.message}")
        }
    }
    
    fun importGame(saveCode: String): GameData {
        try {
            if (!saveCode.startsWith("FT_") || !saveCode.endsWith("_END")) {
                throw Exception("Código de partida inválido. Asegúrate de copiar el código completo.")
            }
            
            val base64String = saveCode.substring(3, saveCode.length - 4)
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val decodedString = String(decodedBytes)
            val jsonString = URLDecoder.decode(decodedString, "UTF-8")
            
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val importData: Map<String, Any> = gson.fromJson(jsonString, type)
            
            // Verificar que es un save válido
            if (!importData.containsKey("money") || !importData.containsKey("businessUpgrades")) {
                throw Exception("El código no corresponde a una partida válida de Fumadero Tycoon.")
            }
            
            val gameData = GameData()
            gameData.money = (importData["money"] as? Double)?.toLong() ?: 0L
            gameData.moneyPerClick = (importData["moneyPerClick"] as? Double)?.toLong() ?: 1L
            gameData.incomePerSecond = importData["incomePerSecond"] as? Double ?: 0.0
            gameData.totalClicks = (importData["totalClicks"] as? Double)?.toLong() ?: 0L
            gameData.totalEarned = (importData["totalEarned"] as? Double)?.toLong() ?: 0L
            gameData.currentBusinessLevel = (importData["currentBusinessLevel"] as? Double)?.toInt() ?: 1
            gameData.prestigeLevel = (importData["prestigeLevel"] as? Double)?.toInt() ?: 0
            gameData.prestigePoints = (importData["prestigePoints"] as? Double)?.toInt() ?: 0
            gameData.totalLifetimeEarnings = (importData["totalLifetimeEarnings"] as? Double)?.toLong() ?: 0L
            
            // Cargar mejoras
            (importData["clickUpgrades"] as? Map<String, Double>)?.let { upgrades ->
                gameData.clickUpgrades = upgrades.mapValues { it.value.toInt() }.toMutableMap()
            }
            
            (importData["businessUpgrades"] as? Map<String, Double>)?.let { upgrades ->
                gameData.businessUpgrades = upgrades.mapValues { it.value.toInt() }.toMutableMap()
            }
            
            // Cargar logros
            (importData["achievements"] as? Map<String, Boolean>)?.let { achievements ->
                gameData.achievements = achievements.toMutableMap()
            }
            
            return gameData
        } catch (e: Exception) {
            throw Exception("Error al cargar la partida: ${e.message}")
        }
    }
    
    fun resetGame() {
        prefs.edit().clear().apply()
    }
}
