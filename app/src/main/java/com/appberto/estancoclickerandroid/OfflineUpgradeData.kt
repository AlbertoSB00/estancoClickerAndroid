package com.appberto.estancoclickerandroid

object OfflineUpgradeData {
    
    fun getAllOfflineUpgrades(): List<OfflineUpgrade> {
        return listOf(
            OfflineUpgrade(
                id = "basic_storage",
                name = "Caja Registradora Mejorada",
                description = "Una caja registradora más grande para almacenar más dinero",
                icon = "💰",
                baseCost = 1000,
                hoursBonus = 1.0
            ),
            OfflineUpgrade(
                id = "safe_deposit",
                name = "Caja Fuerte",
                description = "Una caja fuerte para guardar las ganancias de forma segura",
                icon = "🔒",
                baseCost = 5000,
                hoursBonus = 2.0
            ),
            OfflineUpgrade(
                id = "bank_account",
                name = "Cuenta Bancaria",
                description = "Una cuenta bancaria para gestionar grandes cantidades",
                icon = "🏦",
                baseCost = 25000,
                hoursBonus = 3.0
            ),
            OfflineUpgrade(
                id = "investment_fund",
                name = "Fondo de Inversión",
                description = "Un fondo que hace crecer tu dinero mientras no estás",
                icon = "📈",
                baseCost = 100000,
                hoursBonus = 4.0
            ),
            OfflineUpgrade(
                id = "offshore_account",
                name = "Cuenta Offshore",
                description = "Cuenta en paraíso fiscal para máximas ganancias",
                icon = "🏝️",
                baseCost = 500000,
                hoursBonus = 6.0
            ),
            OfflineUpgrade(
                id = "crypto_wallet",
                name = "Cartera Crypto",
                description = "Inversiones en criptomonedas para el futuro",
                icon = "₿",
                baseCost = 2000000,
                hoursBonus = 8.0
            ),
            OfflineUpgrade(
                id = "ai_manager",
                name = "Gerente IA",
                description = "Inteligencia artificial que gestiona tu negocio 24/7",
                icon = "🤖",
                baseCost = 10000000,
                hoursBonus = 12.0
            ),
            OfflineUpgrade(
                id = "time_machine",
                name = "Máquina del Tiempo",
                description = "Tecnología futurista para ganancias temporales infinitas",
                icon = "⏰",
                baseCost = 50000000,
                hoursBonus = 24.0
            )
        )
    }
    
    fun getOfflineUpgradeCategories(): Map<String, List<String>> {
        return mapOf(
            "💰 Almacenamiento Básico" to listOf("basic_storage", "safe_deposit"),
            "🏦 Servicios Financieros" to listOf("bank_account", "investment_fund"),
            "🌍 Inversiones Avanzadas" to listOf("offshore_account", "crypto_wallet"),
            "🚀 Tecnología Futurista" to listOf("ai_manager", "time_machine")
        )
    }
}
