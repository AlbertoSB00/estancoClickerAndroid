package com.appberto.estancoclickerandroid

data class BusinessUpgrade(
    val id: String,
    val name: String,
    val description: String,
    val baseCost: Long,
    val incomeBonus: Double,
    val level: Int,
    val icon: String,
    val signText: String,
    var count: Int = 0
) {
    fun getCost(currentCount: Int = count): Long {
        return (baseCost * Math.pow(1.15, currentCount.toDouble())).toLong()
    }
}

object BusinessUpgradeData {
    fun getAllBusinessUpgrades(): List<BusinessUpgrade> {
        return listOf(
            BusinessUpgrade("miron", "Mirón del Estanco", "Solo ves cómo otros compran cigarros", 
                5, 0.1, 2, "👀", "MIRANDO ESTANCOS"),
            BusinessUpgrade("recolector", "Recolector de Colillas", "Recoges colillas medio fumadas en la calle", 
                10, 0.2, 3, "🚬", "RECOLECTANDO COLILLAS"),
            BusinessUpgrade("casero", "Cigarrillo Casero", "Aprendes a liar cigarros con papel de periódico", 
                25, 0.5, 4, "📰", "CIGARROS CASEROS"),
            BusinessUpgrade("dealer", "Dealer de Cigarros Sueltos", "Vendes cigarros sueltos en la esquina", 
                50, 1.0, 5, "🚭", "CIGARROS SUELTOS"),
            BusinessUpgrade("callejon", "Vendedor de Callejón", "Llevas una cajita de cigarros baratos a escondidas", 
                100, 2.0, 6, "📦", "VENTA EN CALLEJÓN"),
            BusinessUpgrade("mercado", "Puesto en el Mercado", "Tienes un lugar fijo los fines de semana", 
                500, 10.0, 8, "🏪", "PUESTO DEL MERCADO"),
            BusinessUpgrade("mini_estanco", "Mini Estanco", "Primer local en un barrio sencillo", 
                1000, 20.0, 9, "🏬", "MINI ESTANCO"),
            BusinessUpgrade("legalizado", "Estanco Legalizado", "Tienes licencia municipal", 
                2500, 40.0, 10, "📋", "ESTANCO LEGAL"),
            BusinessUpgrade("empleados", "Estanquero con Empleados", "Contratas tu primer ayudante", 
                5000, 80.0, 11, "👥", "ESTANCO CON EMPLEADOS"),
            BusinessUpgrade("veinticuatro", "Estanco 24 Horas", "Ahora también vendes de noche", 
                10000, 150.0, 12, "🌙", "ABIERTO 24H"),
            BusinessUpgrade("cadena", "Cadena de Estancos", "Abres más sucursales por la ciudad", 
                25000, 300.0, 13, "🏢", "CADENA DE ESTANCOS"),
            BusinessUpgrade("almacen", "Almacén Centralizado", "Tienes un centro de distribución", 
                250000, 2500.0, 16, "🏭", "ALMACÉN CENTRAL"),
            BusinessUpgrade("puros", "Puros Premium Artesanales", "Lanzamiento de línea de lujo", 
                2500000, 25000.0, 19, "🚬", "PUROS PREMIUM"),
            BusinessUpgrade("publicidad", "Inversiones en Publicidad", "Contratas influencers del humo", 
                5000000, 50000.0, 20, "📺", "PUBLICIDAD MASIVA"),
            BusinessUpgrade("contrabando", "Contrabando Creativo", "Encuentras formas de burlar regulaciones", 
                10000000, 100000.0, 21, "🕵️", "OPERACIONES ESPECIALES"),
            BusinessUpgrade("patrocinador", "Patrocinador de Eventos", "Apareces en fiestas, carreras, etc", 
                25000000, 250000.0, 22, "🎪", "PATROCINIOS"),
            BusinessUpgrade("lobby", "Lobby Político", "Convences a políticos para flexibilizar leyes", 
                50000000, 500000.0, 23, "🏛️", "LOBBY POLÍTICO"),
            BusinessUpgrade("exportador", "Exportador Internacional", "Tus cigarros llegan a Europa y Asia", 
                100000000, 1000000.0, 24, "🌍", "EXPORTACIÓN GLOBAL"),
            BusinessUpgrade("multinacional", "Compañía Multinacional", "Abres sedes en varios países", 
                250000000, 2500000.0, 25, "🌐", "MULTINACIONAL"),
            BusinessUpgrade("adquisicion", "Adquisición de Competencia", "Compras otras marcas más pequeñas", 
                500000000, 5000000.0, 26, "💼", "ADQUISICIONES"),
            BusinessUpgrade("grupo", "Grupo Tabacalero Global", "Tienes diferentes marcas, estilos y sabores", 
                1000000000, 10000000.0, 27, "🏢", "GRUPO GLOBAL"),
            BusinessUpgrade("fusion", "Fusión con Industria del Alcohol", "Tabaco + licor = imperio combinado", 
                2500000000, 25000000.0, 28, "🥃", "FUSIÓN TABACO-ALCOHOL"),
            BusinessUpgrade("magnate", "Magnate del Tabaco", "Eres portada de revistas económicas", 
                5000000000, 50000000.0, 29, "📰", "MAGNATE"),
            BusinessUpgrade("isla", "Dueño de una Isla Tabacalera", "Toda una isla dedicada a tu marca", 
                25000000000, 250000000.0, 31, "🏝️", "ISLA TABACALERA"),
            BusinessUpgrade("dios", "Dios del Estanco", "Nivel místico desbloqueado. Apareces como leyenda urbana", 
                100000000000, 1000000000.0, 32, "⚡", "DIOS DEL ESTANCO")
        )
    }
}
