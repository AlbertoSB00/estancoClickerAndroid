package com.appberto.estancoclickerandroid

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    var unlocked: Boolean = false
)

object AchievementData {
    fun getAllAchievements(): List<Achievement> {
        return listOf(
            // Logros básicos
            Achievement("first_click", "Primer Cliente", "Haz tu primer click", "🎉"),
            Achievement("hundred_euros", "Primer Billete", "Gana 100€", "💶"),
            Achievement("thousand_euros", "Mil Euros", "Gana 1000€", "💰"),
            Achievement("ten_thousand_euros", "Rico", "Gana 10,000€", "💸"),
            Achievement("hundred_thousand_euros", "Muy Rico", "Gana 100,000€", "🤑"),
            Achievement("million_euros", "Millonario", "Gana 1,000,000€", "💵"),
            Achievement("millionaire", "Multimillonario", "Gana 25,000,000€", "💎"),

            // Logros de clicks
            Achievement("click_master", "Maestro del Click", "Haz 1000 clicks", "🖱️"),
            Achievement("click_veteran", "Veterano del Click", "Haz 5000 clicks", "⚡"),
            Achievement("click_legend", "Leyenda del Click", "Haz 10,000 clicks", "🔥"),
            Achievement("click_god", "Dios del Click", "Haz 50,000 clicks", "⚡"),

            // Logros de cursores automáticos
            Achievement("first_cursor", "Primer Cursor", "Compra tu primer cursor automático", "🖱️"),
            Achievement("cursor_collector", "Coleccionista de Cursores", "Compra 10 cursores automáticos", "🖲️"),
            Achievement("cursor_army", "Ejército de Cursores", "Compra 50 cursores automáticos", "⚔️"),
            Achievement("cursor_empire", "Imperio de Cursores", "Compra 100 cursores automáticos", "🏰"),

            // Logros de negocios
            Achievement("pequeno_estanco", "Pequeño Comerciante", "Abre tu pequeño estanco", "🏪"),
            Achievement("franquicia", "Franquiciado", "Expande con una franquicia", "🏬"),
            Achievement("distribuidor", "Distribuidor Regional", "Conviértete en distribuidor", "🚚"),
            Achievement("marca_propia", "Marca Propia", "Crea tu propia marca", "🏷️"),
            Achievement("fabrica", "Industrial", "Construye tu fábrica", "🏭"),
            Achievement("marketing", "Magnate del Marketing", "Domina el marketing", "📺"),
            Achievement("global", "Empresario Global", "Expande globalmente", "🌍"),
            Achievement("emperador", "Emperador del Tabaco", "Domina el mundo", "👑"),

            // Logros de ingresos pasivos
            Achievement("passive_income_1", "Ingresos Pasivos", "Genera 1€/seg en ingresos pasivos", "📈"),
            Achievement("passive_income_10", "Flujo de Dinero", "Genera 10€/seg en ingresos pasivos", "💹"),
            Achievement("passive_income_100", "Máquina de Dinero", "Genera 100€/seg en ingresos pasivos", "🏦"),
            Achievement("passive_income_1000", "Imperio Financiero", "Genera 1000€/seg en ingresos pasivos", "🏛️"),

            // Logros de prestigio
            Achievement("first_prestige", "Primer Prestigio", "Haz tu primer prestigio", "⭐"),
            Achievement("prestige_master", "Maestro del Prestigio", "Alcanza prestigio nivel 5", "🌟"),
            Achievement("prestige_legend", "Leyenda del Prestigio", "Alcanza prestigio nivel 10", "✨"),
            Achievement("prestige_god", "Dios del Prestigio", "Alcanza prestigio nivel 25", "🌠"),

            // Logros especiales
            Achievement("speed_demon", "Demonio de la Velocidad", "Haz 100 clicks en 10 segundos", "💨"),
            Achievement("patient_player", "Jugador Paciente", "Juega durante 1 hora", "⏰"),
            Achievement("dedicated_player", "Jugador Dedicado", "Juega durante 5 horas", "🕐"),
            Achievement("business_mogul", "Magnate de Negocios", "Compra todas las mejoras de negocio", "🎩"),
            Achievement("completionist", "Completista", "Desbloquea todos los demás logros", "🏆")
        )
    }

    fun getAchievementCategories(): Map<String, List<String>> {
        return mapOf(
            "Dinero" to listOf("first_click", "hundred_euros", "thousand_euros", "ten_thousand_euros", 
                              "hundred_thousand_euros", "million_euros", "millionaire"),
            "Clicks" to listOf("click_master", "click_veteran", "click_legend", "click_god"),
            "Cursores Manuales" to listOf("first_cursor", "cursor_collector", "cursor_army", "cursor_empire"),
            "Evolución del Negocio" to listOf("pequeno_estanco", "franquicia", "distribuidor", "marca_propia", 
                                            "fabrica", "marketing", "global", "emperador"),
            "Ingresos Pasivos" to listOf("passive_income_1", "passive_income_10", "passive_income_100", "passive_income_1000"),
            "Prestigio" to listOf("first_prestige", "prestige_master", "prestige_legend", "prestige_god"),
            "Especiales" to listOf("speed_demon", "patient_player", "dedicated_player", "business_mogul", "completionist")
        )
    }
}
