package com.appberto.estancoclickerandroid

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

class MainActivity : AppCompatActivity() {

    // Game Data
    private lateinit var gameData: GameData
    private lateinit var gameDataManager: GameDataManager
    private lateinit var clickUpgrades: List<ClickUpgrade>
    private lateinit var businessUpgrades: List<BusinessUpgrade>
    private lateinit var offlineUpgrades: List<OfflineUpgrade>
    private lateinit var achievements: MutableList<Achievement>

    // UI Elements
    private lateinit var moneyText: TextView
    private lateinit var incomeText: TextView
    private lateinit var clickValueText: TextView
    private lateinit var prestigeText: TextView
    private lateinit var businessSign: TextView
    private lateinit var businessIcon: TextView
    private lateinit var clickValueDisplay: TextView
    private lateinit var businessLevel: TextView
    private lateinit var businessName: TextView
    private lateinit var businessDescription: TextView
    private lateinit var clickArea: LinearLayout
    private lateinit var clickUpgradesContainer: LinearLayout
    private lateinit var businessUpgradesContainer: LinearLayout
    private lateinit var offlineUpgradesContainer: LinearLayout
    private lateinit var achievementsContainer: LinearLayout
    private lateinit var recentAchievementsContainer: LinearLayout
    private lateinit var achievementsTitle: TextView
    private lateinit var achievementsHeader: LinearLayout
    private lateinit var achievementsToggle: TextView
    private var achievementsExpanded = false

    // Offline Upgrades UI
    private lateinit var offlineUpgradesHeader: LinearLayout
    private lateinit var offlineUpgradesToggle: TextView
    private lateinit var offlineTimeDisplay: TextView
    private lateinit var offlineEarningsDisplay: TextView
    private lateinit var offlineProgressDisplay: TextView
    private var offlineUpgradesExpanded = false

    // Click Upgrades UI
    private lateinit var clickUpgradesHeader: LinearLayout
    private lateinit var clickUpgradesToggle: TextView
    private var clickUpgradesExpanded = false

    // Business Upgrades UI
    private lateinit var businessUpgradesHeader: LinearLayout
    private lateinit var businessUpgradesToggle: TextView
    private var businessUpgradesExpanded = false

    // Prestige UI
    private lateinit var prestigeLevelDisplay: TextView
    private lateinit var prestigePointsDisplay: TextView
    private lateinit var prestigeBonusDisplay: TextView
    private lateinit var prestigeNextPoints: TextView
    private lateinit var prestigeButton: TextView

    // Save System UI (COMMENTED OUT)
    // private lateinit var exportCode: TextView
    // private lateinit var importCode: TextView

    // Game Loop
    private val handler = Handler(Looper.getMainLooper())
    private var incomeRunnable: Runnable? = null
    private var saveRunnable: Runnable? = null

    // Speed click tracking
    private var speedClickCount = 0
    private var speedClickStartTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeGame()
        initializeUI()
        setupEventListeners()
        startGameLoops()
        calculateOfflineEarnings()
        updateDisplay()
    }

    private fun initializeGame() {
        gameDataManager = GameDataManager(this)
        gameData = gameDataManager.loadGame()
        clickUpgrades = ClickUpgradeData.getAllClickUpgrades()
        businessUpgrades = BusinessUpgradeData.getAllBusinessUpgrades()
        offlineUpgrades = OfflineUpgradeData.getAllOfflineUpgrades()
        achievements = AchievementData.getAllAchievements().toMutableList()

        // Load saved achievements
        achievements.forEach { achievement ->
            achievement.unlocked = gameData.achievements[achievement.id] ?: false
        }

        // Recalculate values
        gameData.moneyPerClick = gameData.calculateMoneyPerClick(clickUpgrades)
        gameData.recalculateIncomePerSecond(businessUpgrades)
    }

    private fun initializeUI() {
        // Main stats
        moneyText = findViewById(R.id.moneyText)
        incomeText = findViewById(R.id.incomeText)
        clickValueText = findViewById(R.id.clickValueText)
        prestigeText = findViewById(R.id.prestigeText)

        // Business display
        businessSign = findViewById(R.id.businessSign)
        businessIcon = findViewById(R.id.businessIcon)
        clickValueDisplay = findViewById(R.id.clickValueDisplay)
        businessLevel = findViewById(R.id.businessLevel)
        businessName = findViewById(R.id.businessName)
        businessDescription = findViewById(R.id.businessDescription)

        // Containers
        clickArea = findViewById(R.id.clickArea)
        clickUpgradesContainer = findViewById(R.id.clickUpgradesContainer)
        businessUpgradesContainer = findViewById(R.id.businessUpgradesContainer)
        offlineUpgradesContainer = findViewById(R.id.offlineUpgradesContainer)
        achievementsContainer = findViewById(R.id.achievementsContainer)
        recentAchievementsContainer = findViewById(R.id.recentAchievementsContainer)
        achievementsTitle = findViewById(R.id.achievementsTitle)
        achievementsHeader = findViewById(R.id.achievementsHeader)
        achievementsToggle = findViewById(R.id.achievementsToggle)

        // Offline Upgrades
        offlineUpgradesHeader = findViewById(R.id.offlineUpgradesHeader)
        offlineUpgradesToggle = findViewById(R.id.offlineUpgradesToggle)
        offlineTimeDisplay = findViewById(R.id.offlineTimeDisplay)
        offlineEarningsDisplay = findViewById(R.id.offlineEarningsDisplay)
        offlineProgressDisplay = findViewById(R.id.offlineProgressDisplay)

        // Click Upgrades
        clickUpgradesHeader = findViewById(R.id.clickUpgradesHeader)
        clickUpgradesToggle = findViewById(R.id.clickUpgradesToggle)

        // Business Upgrades
        businessUpgradesHeader = findViewById(R.id.businessUpgradesHeader)
        businessUpgradesToggle = findViewById(R.id.businessUpgradesToggle)

        // Prestige
        prestigeLevelDisplay = findViewById(R.id.prestigeLevelDisplay)
        prestigePointsDisplay = findViewById(R.id.prestigePointsDisplay)
        prestigeBonusDisplay = findViewById(R.id.prestigeBonusDisplay)
        prestigeNextPoints = findViewById(R.id.prestigeNextPoints)
        prestigeButton = findViewById(R.id.prestigeButton)

        // Save System (COMMENTED OUT)
        // exportCode = findViewById(R.id.exportCode)
        // importCode = findViewById(R.id.importCode)

        // Render upgrades and achievements
        renderClickUpgrades()
        renderBusinessUpgrades()
        renderOfflineUpgrades()
        renderAchievements()
    }

    private fun setupEventListeners() {
        // Main click area
        clickArea.setOnClickListener { handleMainClick() }

        // Reset button
        findViewById<View>(R.id.resetButton).setOnClickListener { showResetDialog() }

        // Prestige button
        prestigeButton.setOnClickListener { showPrestigeDialog() }

        // Export/Import buttons (COMMENTED OUT)
        // findViewById<View>(R.id.exportButton).setOnClickListener { exportGame() }
        // findViewById<View>(R.id.importButton).setOnClickListener { importGame() }
        // exportCode.setOnClickListener { copyExportCode() }

        // Achievements toggle
        achievementsHeader.setOnClickListener { toggleAchievements() }

        // Offline upgrades toggle
        offlineUpgradesHeader.setOnClickListener { toggleOfflineUpgrades() }

        // Click upgrades toggle
        clickUpgradesHeader.setOnClickListener { toggleClickUpgrades() }

        // Business upgrades toggle
        businessUpgradesHeader.setOnClickListener { toggleBusinessUpgrades() }
    }

    private fun startGameLoops() {
        // Income loop - runs every 100ms
        incomeRunnable = object : Runnable {
            override fun run() {
                if (gameData.incomePerSecond > 0) {
                    val income = gameData.incomePerSecond / 10.0 // Divided by 10 because it runs every 100ms
                    gameData.money += income.toLong()
                    gameData.totalEarned += income.toLong()
                    updateDisplay()
                }
                handler.postDelayed(this, 100)
            }
        }
        handler.post(incomeRunnable!!)

        // Auto-save loop - runs every 10 seconds
        saveRunnable = object : Runnable {
            override fun run() {
                saveGame()
                handler.postDelayed(this, 10000)
            }
        }
        handler.post(saveRunnable!!)
    }

    private fun calculateOfflineEarnings() {
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - gameData.lastPlayTime

        // Only calculate if away for more than 30 seconds and has income
        if (timeDifference > 30000 && gameData.incomePerSecond > 0) {
            val secondsAway = timeDifference / 1000.0
            val maxOfflineHours = gameData.calculateMaxOfflineHours(offlineUpgrades)
            val maxOfflineSeconds = maxOfflineHours * 3600

            val effectiveSecondsAway = minOf(secondsAway, maxOfflineSeconds)
            val offlineEarnings = (gameData.incomePerSecond * effectiveSecondsAway).toLong()

            if (offlineEarnings > 0) {
                showOfflineEarningsDialog(offlineEarnings, effectiveSecondsAway)
            }
        }

        // Update last play time
        gameData.lastPlayTime = currentTime
    }

    private fun showOfflineEarningsDialog(earnings: Long, secondsAway: Double) {
        val hoursAway = secondsAway / 3600
        val minutesAway = secondsAway / 60

        val timeAwayText = when {
            hoursAway >= 1 -> {
                val hours = hoursAway.toInt()
                val remainingMinutes = ((secondsAway % 3600) / 60).toInt()
                if (remainingMinutes > 0) {
                    "$hours horas $remainingMinutes minutos"
                } else {
                    "$hours horas"
                }
            }
            minutesAway >= 1 -> "${minutesAway.toInt()} minutos"
            else -> "${secondsAway.toInt()} segundos"
        }

        showOfflineEarningsOverlay(earnings, timeAwayText, secondsAway)
    }

    private fun showOfflineEarningsOverlay(earnings: Long, timeAwayText: String, secondsAway: Double) {
        // Inflate the overlay layout
        val overlayView = LayoutInflater.from(this).inflate(R.layout.offline_earnings_overlay, null)

        // Get references to the views
        val timeAwayDisplay = overlayView.findViewById<TextView>(R.id.timeAwayDisplay)
        val earningsDisplay = overlayView.findViewById<TextView>(R.id.earningsDisplay)
        val offlineLimitInfo = overlayView.findViewById<TextView>(R.id.offlineLimitInfo)
        val claimButton = overlayView.findViewById<Button>(R.id.claimButton)
        val dismissButton = overlayView.findViewById<Button>(R.id.dismissButton)

        // Set the data
        timeAwayDisplay.text = timeAwayText
        earningsDisplay.text = formatNumber(earnings) + "€"

        val maxOfflineHours = gameData.calculateMaxOfflineHours(offlineUpgrades)
        offlineLimitInfo.text = "📊 Límite offline: ${String.format("%.1f", maxOfflineHours)} horas"

        // Add overlay to the main layout
        val rootLayout = findViewById<ViewGroup>(android.R.id.content)
        rootLayout.addView(overlayView)

        // Set up button listeners
        claimButton.setOnClickListener {
            gameData.money += earnings
            gameData.totalEarned += earnings
            updateDisplay()
            showNotification("¡Has ganado ${formatNumber(earnings)}€ mientras estabas ausente!")
            rootLayout.removeView(overlayView)
        }

        dismissButton.setOnClickListener {
            rootLayout.removeView(overlayView)
        }

        // Make overlay non-cancelable by clicking outside
        overlayView.setOnClickListener { /* Do nothing - prevents dismissal */ }
    }

    private fun handleMainClick() {
        // Verify moneyPerClick is valid
        if (gameData.moneyPerClick < 1) {
            gameData.moneyPerClick = 1
        }

        gameData.money += gameData.moneyPerClick
        gameData.totalClicks++
        gameData.totalEarned += gameData.moneyPerClick

        // Speed click tracking for achievement
        val currentTime = System.currentTimeMillis()
        if (speedClickStartTime == 0L || currentTime - speedClickStartTime > 10000) {
            speedClickStartTime = currentTime
            speedClickCount = 1
        } else {
            speedClickCount++
            if (speedClickCount >= 100) {
                unlockAchievement("speed_demon")
            }
        }

        // Create floating money animation
        createFloatingMoney()

        // Animate click area
        animateClickArea()

        updateDisplay()
        checkAchievements()
        saveGame()
    }

    private fun createFloatingMoney() {
        val floatingText = TextView(this)
        floatingText.text = "+${formatNumber(gameData.moneyPerClick)}€"
        floatingText.setTextColor(resources.getColor(R.color.floating_money, null))
        floatingText.textSize = 16f
        floatingText.alpha = 1f

        // Add to click area temporarily
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        clickArea.addView(floatingText, params)

        // Animate floating up and fade out
        val animatorSet = AnimatorSet()
        val translateY = ObjectAnimator.ofFloat(floatingText, "translationY", 0f, -100f)
        val alpha = ObjectAnimator.ofFloat(floatingText, "alpha", 1f, 0f)

        animatorSet.playTogether(translateY, alpha)
        animatorSet.duration = 1000
        animatorSet.interpolator = AccelerateDecelerateInterpolator()

        animatorSet.start()

        // Remove view after animation
        handler.postDelayed({
            clickArea.removeView(floatingText)
        }, 1000)
    }

    private fun animateClickArea() {
        val scaleDown = AnimatorSet()
        val scaleDownX = ObjectAnimator.ofFloat(clickArea, "scaleX", 1f, 0.95f)
        val scaleDownY = ObjectAnimator.ofFloat(clickArea, "scaleY", 1f, 0.95f)
        scaleDown.playTogether(scaleDownX, scaleDownY)
        scaleDown.duration = 50

        val scaleUp = AnimatorSet()
        val scaleUpX = ObjectAnimator.ofFloat(clickArea, "scaleX", 0.95f, 1f)
        val scaleUpY = ObjectAnimator.ofFloat(clickArea, "scaleY", 0.95f, 1f)
        scaleUp.playTogether(scaleUpX, scaleUpY)
        scaleUp.duration = 100

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(scaleDown, scaleUp)
        animatorSet.start()
    }

    private fun updateDisplay() {
        // Main stats
        moneyText.text = getString(R.string.money_format, formatNumber(gameData.money))
        incomeText.text = getString(R.string.income_format, formatNumber(gameData.incomePerSecond))
        clickValueText.text = getString(R.string.click_value_format, formatNumber(gameData.moneyPerClick))
        prestigeText.text = getString(R.string.prestige_level_format, gameData.prestigeLevel, gameData.prestigeLevel * 10)

        // Click value display
        clickValueDisplay.text = getString(R.string.click_value_display, formatNumber(gameData.moneyPerClick))

        // Business info
        val currentBusiness = gameData.getCurrentBusiness(businessUpgrades)
        businessLevel.text = getString(R.string.business_level_format, currentBusiness.level)
        businessName.text = currentBusiness.name
        businessDescription.text = currentBusiness.description
        businessSign.text = currentBusiness.signText
        businessIcon.text = currentBusiness.icon

        // Prestige info
        prestigeLevelDisplay.text = getString(R.string.prestige_level_display, gameData.prestigeLevel)
        prestigePointsDisplay.text = getString(R.string.prestige_points_display, gameData.prestigePoints)
        prestigeBonusDisplay.text = getString(R.string.prestige_bonus_display, gameData.prestigeLevel * 10)
        prestigeNextPoints.text = getString(R.string.prestige_next_points, gameData.calculatePrestigePoints())

        // Prestige button state
        prestigeButton.isEnabled = gameData.canPrestige()

        // Offline info
        val maxOfflineHours = gameData.calculateMaxOfflineHours(offlineUpgrades)
        val maxOfflineEarnings = (gameData.incomePerSecond * maxOfflineHours * 3600).toLong()
        val purchasedCount = gameData.offlineUpgrades.values.count { it >= 1 }
        val totalCount = offlineUpgrades.size

        offlineTimeDisplay.text = "⏰ ${String.format("%.1f", maxOfflineHours)} horas máximo"
        offlineEarningsDisplay.text = "💸 Ganancias máximas: ${formatNumber(maxOfflineEarnings)}€"
        offlineProgressDisplay.text = "📊 Mejoras compradas: $purchasedCount/$totalCount"

        // Update upgrade displays
        updateUpgradeDisplays()
    }

    private fun renderClickUpgrades() {
        clickUpgradesContainer.removeAllViews()

        clickUpgrades.forEach { upgrade ->
            val upgradeView = LayoutInflater.from(this).inflate(R.layout.upgrade_item, clickUpgradesContainer, false)

            val icon = upgradeView.findViewById<TextView>(R.id.upgradeIcon)
            val name = upgradeView.findViewById<TextView>(R.id.upgradeName)
            val description = upgradeView.findViewById<TextView>(R.id.upgradeDescription)
            val benefit = upgradeView.findViewById<TextView>(R.id.upgradeBenefit)
            val count = upgradeView.findViewById<TextView>(R.id.upgradeCount)
            val cost = upgradeView.findViewById<TextView>(R.id.upgradeCost)

            icon.text = "🖱️"
            name.text = upgrade.name
            description.text = upgrade.description
            benefit.text = getString(R.string.upgrade_benefit_format, formatNumber(upgrade.clickBonus))

            upgradeView.setOnClickListener { handleClickUpgradeClick(upgrade) }
            clickUpgradesContainer.addView(upgradeView)
        }
    }

    private fun renderBusinessUpgrades() {
        businessUpgradesContainer.removeAllViews()

        businessUpgrades.forEach { upgrade ->
            val upgradeView = LayoutInflater.from(this).inflate(R.layout.upgrade_item, businessUpgradesContainer, false)

            val icon = upgradeView.findViewById<TextView>(R.id.upgradeIcon)
            val name = upgradeView.findViewById<TextView>(R.id.upgradeName)
            val description = upgradeView.findViewById<TextView>(R.id.upgradeDescription)
            val benefit = upgradeView.findViewById<TextView>(R.id.upgradeBenefit)
            val count = upgradeView.findViewById<TextView>(R.id.upgradeCount)
            val cost = upgradeView.findViewById<TextView>(R.id.upgradeCost)

            icon.text = upgrade.icon
            name.text = upgrade.name
            description.text = upgrade.description
            benefit.text = getString(R.string.upgrade_income_format, formatNumber(upgrade.incomeBonus))

            upgradeView.setOnClickListener { handleBusinessUpgradeClick(upgrade) }
            businessUpgradesContainer.addView(upgradeView)
        }
    }

    private fun renderAchievements() {
        achievementsContainer.removeAllViews()
        recentAchievementsContainer.removeAllViews()

        val categories = AchievementData.getAchievementCategories()
        val recentUnlocked = achievements.filter { it.unlocked }.takeLast(3)

        // Render recent achievements (always visible)
        if (recentUnlocked.isNotEmpty()) {
            val recentHeader = TextView(this)
            recentHeader.text = "🏆 Logros Recientes"
            recentHeader.textSize = 12f
            recentHeader.setTextColor(resources.getColor(R.color.primary_brown, null))
            recentHeader.setTypeface(null, android.graphics.Typeface.BOLD)
            recentHeader.setPadding(0, 0, 0, 8)
            recentAchievementsContainer.addView(recentHeader)

            recentUnlocked.forEach { ach ->
                val achievementView = createAchievementView(ach)
                recentAchievementsContainer.addView(achievementView)
            }
        } else {
            val noAchievements = TextView(this)
            noAchievements.text = "🎯 ¡Empieza a jugar para desbloquear logros!"
            noAchievements.textSize = 11f
            noAchievements.setTextColor(resources.getColor(R.color.text_secondary, null))
            noAchievements.gravity = android.view.Gravity.CENTER
            noAchievements.setPadding(0, 10, 0, 10)
            recentAchievementsContainer.addView(noAchievements)
        }

        // Render all achievements (collapsible)
        categories.forEach { (categoryName, achievementIds) ->
            // Add category header
            val categoryHeader = TextView(this)
            categoryHeader.text = categoryName
            categoryHeader.textSize = 14f
            categoryHeader.setTextColor(resources.getColor(R.color.primary_brown, null))
            categoryHeader.setTypeface(null, android.graphics.Typeface.BOLD)
            categoryHeader.setPadding(0, 20, 0, 10)
            achievementsContainer.addView(categoryHeader)

            // Add achievements in category
            achievementIds.forEach { achievementId ->
                val achievement = achievements.find { it.id == achievementId }
                achievement?.let { ach ->
                    val achievementView = createAchievementView(ach)
                    achievementsContainer.addView(achievementView)
                }
            }
        }

        // Update achievements title
        val unlockedCount = achievements.count { it.unlocked }
        val totalCount = achievements.size
        achievementsTitle.text = getString(R.string.achievements_title, unlockedCount, totalCount)
    }

    private fun createAchievementView(ach: Achievement): View {
        val achievementView = LayoutInflater.from(this).inflate(R.layout.achievement_item, null, false)

        val icon = achievementView.findViewById<TextView>(R.id.achievementIcon)
        val name = achievementView.findViewById<TextView>(R.id.achievementName)
        val desc = achievementView.findViewById<TextView>(R.id.achievementDescription)

        icon.text = ach.icon
        name.text = ach.name
        desc.text = ach.description

        // Set background based on unlock status
        achievementView.isSelected = ach.unlocked

        // Set text color based on unlock status
        if (ach.unlocked) {
            name.setTextColor(resources.getColor(R.color.primary_brown, null))
            desc.setTextColor(resources.getColor(R.color.text_secondary, null))
        } else {
            name.setTextColor(resources.getColor(R.color.text_secondary, null))
            desc.setTextColor(resources.getColor(R.color.text_secondary, null))
            achievementView.alpha = 0.6f
        }

        return achievementView
    }

    private fun toggleAchievements() {
        achievementsExpanded = !achievementsExpanded

        if (achievementsExpanded) {
            achievementsContainer.visibility = View.VISIBLE
            achievementsToggle.text = "▲"
        } else {
            achievementsContainer.visibility = View.GONE
            achievementsToggle.text = "▼"
        }
    }

    private fun updateUpgradeDisplays() {
        // Update click upgrades
        for (i in 0 until clickUpgradesContainer.childCount) {
            val upgradeView = clickUpgradesContainer.getChildAt(i)
            val upgrade = clickUpgrades[i]

            val count = upgradeView.findViewById<TextView>(R.id.upgradeCount)
            val cost = upgradeView.findViewById<TextView>(R.id.upgradeCost)

            val upgradeCount = gameData.clickUpgrades[upgrade.id] ?: 0
            count.text = getString(R.string.upgrade_count_format, upgradeCount)
            cost.text = getString(R.string.upgrade_cost_format, formatNumber(upgrade.getCost(upgradeCount)))

            // Update affordability
            val canAfford = gameData.money >= upgrade.getCost(upgradeCount)
            upgradeView.isSelected = canAfford
            upgradeView.alpha = if (canAfford) 1.0f else 0.7f
        }

        // Update business upgrades
        for (i in 0 until businessUpgradesContainer.childCount) {
            val upgradeView = businessUpgradesContainer.getChildAt(i)
            val upgrade = businessUpgrades[i]

            val count = upgradeView.findViewById<TextView>(R.id.upgradeCount)
            val cost = upgradeView.findViewById<TextView>(R.id.upgradeCost)

            val upgradeCount = gameData.businessUpgrades[upgrade.id] ?: 0
            count.text = getString(R.string.upgrade_count_format, upgradeCount)
            cost.text = getString(R.string.upgrade_cost_format, formatNumber(upgrade.getCost(upgradeCount)))

            // Update affordability
            val canAfford = gameData.money >= upgrade.getCost(upgradeCount)
            upgradeView.isSelected = canAfford
            upgradeView.alpha = if (canAfford) 1.0f else 0.7f
        }

        // Update offline upgrades affordability
        for (i in 0 until offlineUpgradesContainer.childCount) {
            val child = offlineUpgradesContainer.getChildAt(i)
            if (child is LinearLayout && child.findViewById<TextView>(R.id.upgradeIcon) != null) {
                // This is an upgrade item, not a category header
                val upgradeIndex = getOfflineUpgradeIndex(i)
                if (upgradeIndex >= 0 && upgradeIndex < offlineUpgrades.size) {
                    val upgrade = offlineUpgrades[upgradeIndex]
                    val upgradeCount = gameData.offlineUpgrades[upgrade.id] ?: 0
                    val isPurchased = upgradeCount >= 1

                    if (!isPurchased) {
                        val canAfford = gameData.money >= upgrade.baseCost
                        child.isSelected = canAfford
                    }
                }
            }
        }
    }

    private fun getOfflineUpgradeIndex(containerIndex: Int): Int {
        // Count how many category headers (TextViews) come before this index
        var headerCount = 0
        for (i in 0 until containerIndex) {
            val child = offlineUpgradesContainer.getChildAt(i)
            if (child is TextView) {
                headerCount++
            }
        }
        // The upgrade index is the container index minus the number of headers
        return containerIndex - headerCount
    }

    private fun handleClickUpgradeClick(upgrade: ClickUpgrade) {
        val currentCount = gameData.clickUpgrades[upgrade.id] ?: 0
        val cost = upgrade.getCost(currentCount)
        if (gameData.money >= cost) {
            gameData.money -= cost

            gameData.clickUpgrades[upgrade.id] = currentCount + 1

            // Recalculate money per click
            gameData.moneyPerClick = gameData.calculateMoneyPerClick(clickUpgrades)

            updateDisplay()
            checkAchievements()
            saveGame()

            showNotification(getString(R.string.upgrade_purchased, upgrade.name, formatNumber(upgrade.clickBonus)))
        }
    }

    private fun handleBusinessUpgradeClick(upgrade: BusinessUpgrade) {
        val currentCount = gameData.businessUpgrades[upgrade.id] ?: 0
        val cost = upgrade.getCost(currentCount)
        if (gameData.money >= cost) {
            gameData.money -= cost

            gameData.businessUpgrades[upgrade.id] = currentCount + 1

            // Update business level if first time buying this upgrade
            if (currentCount == 0 && upgrade.level > gameData.currentBusinessLevel) {
                gameData.currentBusinessLevel = upgrade.level
                showNotification(getString(R.string.business_evolved, upgrade.name))
            }

            // Add income with prestige multiplier
            val prestigeMultiplier = 1.0 + (gameData.prestigeLevel * 0.1)
            gameData.incomePerSecond += upgrade.incomeBonus * prestigeMultiplier

            updateDisplay()
            checkAchievements()
            saveGame()
        }
    }

    private fun renderOfflineUpgrades() {
        offlineUpgradesContainer.removeAllViews()

        val categories = OfflineUpgradeData.getOfflineUpgradeCategories()

        categories.forEach { (categoryName, upgradeIds) ->
            // Add category header
            val categoryHeader = TextView(this)
            categoryHeader.text = categoryName
            categoryHeader.textSize = 14f
            categoryHeader.setTextColor(resources.getColor(R.color.primary_brown, null))
            categoryHeader.setTypeface(null, android.graphics.Typeface.BOLD)
            categoryHeader.setPadding(0, 20, 0, 10)
            offlineUpgradesContainer.addView(categoryHeader)

            // Add upgrades in category
            upgradeIds.forEach { upgradeId ->
                val upgrade = offlineUpgrades.find { it.id == upgradeId }
                upgrade?.let { offUpgrade ->
                    val upgradeView = LayoutInflater.from(this).inflate(R.layout.upgrade_item, offlineUpgradesContainer, false)

                    val icon = upgradeView.findViewById<TextView>(R.id.upgradeIcon)
                    val name = upgradeView.findViewById<TextView>(R.id.upgradeName)
                    val description = upgradeView.findViewById<TextView>(R.id.upgradeDescription)
                    val benefit = upgradeView.findViewById<TextView>(R.id.upgradeBenefit)
                    val count = upgradeView.findViewById<TextView>(R.id.upgradeCount)
                    val cost = upgradeView.findViewById<TextView>(R.id.upgradeCost)

                    icon.text = offUpgrade.icon
                    name.text = offUpgrade.name
                    description.text = offUpgrade.description
                    benefit.text = "+${offUpgrade.hoursBonus}h offline"

                    val upgradeCount = gameData.offlineUpgrades[offUpgrade.id] ?: 0
                    val isPurchased = upgradeCount >= 1

                    if (isPurchased) {
                        count.text = "✅ COMPRADO"
                        cost.text = "---"
                        upgradeView.alpha = 0.6f
                        upgradeView.isClickable = false
                        upgradeView.setBackgroundColor(resources.getColor(R.color.upgrade_purchased, null))
                    } else {
                        count.text = "Disponible"
                        cost.text = formatNumber(offUpgrade.baseCost) + "€"

                        // Update affordability using standard upgrade colors
                        val canAfford = gameData.money >= offUpgrade.baseCost
                        upgradeView.isSelected = canAfford
                        upgradeView.alpha = 1.0f
                        upgradeView.isClickable = true
                        upgradeView.setOnClickListener { handleOfflineUpgradeClick(offUpgrade) }
                    }

                    offlineUpgradesContainer.addView(upgradeView)
                }
            }
        }
    }

    private fun handleOfflineUpgradeClick(upgrade: OfflineUpgrade) {
        val currentCount = gameData.offlineUpgrades[upgrade.id] ?: 0

        // Check if already purchased
        if (currentCount >= 1) {
            showNotification("Ya has comprado esta mejora offline")
            return
        }

        val cost = upgrade.baseCost // Use base cost since it's one-time purchase
        if (gameData.money >= cost) {
            gameData.money -= cost

            gameData.offlineUpgrades[upgrade.id] = 1

            updateDisplay()
            renderOfflineUpgrades() // Re-render to update visual state
            checkAchievements()
            saveGame()

            showNotification("¡${upgrade.name} comprado! +${upgrade.hoursBonus}h offline")
        } else {
            showNotification("No tienes suficiente dinero")
        }
    }

    private fun toggleOfflineUpgrades() {
        offlineUpgradesExpanded = !offlineUpgradesExpanded

        if (offlineUpgradesExpanded) {
            offlineUpgradesContainer.visibility = View.VISIBLE
            offlineUpgradesToggle.text = "▲"
        } else {
            offlineUpgradesContainer.visibility = View.GONE
            offlineUpgradesToggle.text = "▼"
        }
    }

    private fun toggleClickUpgrades() {
        clickUpgradesExpanded = !clickUpgradesExpanded

        if (clickUpgradesExpanded) {
            clickUpgradesContainer.visibility = View.VISIBLE
            clickUpgradesToggle.text = "▲"
        } else {
            clickUpgradesContainer.visibility = View.GONE
            clickUpgradesToggle.text = "▼"
        }
    }

    private fun toggleBusinessUpgrades() {
        businessUpgradesExpanded = !businessUpgradesExpanded

        if (businessUpgradesExpanded) {
            businessUpgradesContainer.visibility = View.VISIBLE
            businessUpgradesToggle.text = "▲"
        } else {
            businessUpgradesContainer.visibility = View.GONE
            businessUpgradesToggle.text = "▼"
        }
    }

    private fun checkAchievements() {
        achievements.forEach { achievement ->
            if (!achievement.unlocked) {
                val shouldUnlock = when (achievement.id) {
                    "first_click" -> gameData.totalClicks >= 1
                    "hundred_euros" -> gameData.totalEarned >= 100
                    "thousand_euros" -> gameData.totalEarned >= 1000
                    "ten_thousand_euros" -> gameData.totalEarned >= 10000
                    "hundred_thousand_euros" -> gameData.totalEarned >= 100000
                    "million_euros" -> gameData.totalEarned >= 1000000
                    "millionaire" -> gameData.totalEarned >= 25000000

                    "click_master" -> gameData.totalClicks >= 1000
                    "click_veteran" -> gameData.totalClicks >= 5000
                    "click_legend" -> gameData.totalClicks >= 10000
                    "click_god" -> gameData.totalClicks >= 50000

                    "first_cursor" -> (gameData.clickUpgrades["cursor"] ?: 0) >= 1
                    "cursor_collector" -> (gameData.clickUpgrades["cursor"] ?: 0) >= 10
                    "cursor_army" -> (gameData.clickUpgrades["cursor"] ?: 0) >= 50
                    "cursor_empire" -> (gameData.clickUpgrades["cursor"] ?: 0) >= 100

                    "pequeno_estanco" -> (gameData.businessUpgrades["mini_estanco"] ?: 0) >= 1
                    "franquicia" -> (gameData.businessUpgrades["cadena"] ?: 0) >= 1
                    "distribuidor" -> (gameData.businessUpgrades["almacen"] ?: 0) >= 1
                    "marca_propia" -> (gameData.businessUpgrades["puros"] ?: 0) >= 1
                    "fabrica" -> (gameData.businessUpgrades["almacen"] ?: 0) >= 1
                    "marketing" -> (gameData.businessUpgrades["publicidad"] ?: 0) >= 1
                    "global" -> (gameData.businessUpgrades["exportador"] ?: 0) >= 1
                    "emperador" -> (gameData.businessUpgrades["dios"] ?: 0) >= 1

                    "passive_income_1" -> gameData.incomePerSecond >= 1.0
                    "passive_income_10" -> gameData.incomePerSecond >= 10.0
                    "passive_income_100" -> gameData.incomePerSecond >= 100.0
                    "passive_income_1000" -> gameData.incomePerSecond >= 1000.0

                    "first_prestige" -> gameData.prestigeLevel >= 1
                    "prestige_master" -> gameData.prestigeLevel >= 5
                    "prestige_legend" -> gameData.prestigeLevel >= 10
                    "prestige_god" -> gameData.prestigeLevel >= 25

                    "patient_player" -> checkPlayTime(3600) // 1 hour
                    "dedicated_player" -> checkPlayTime(18000) // 5 hours
                    "business_mogul" -> checkAllBusinessUpgrades()
                    "completionist" -> checkCompletionist()

                    else -> false
                }

                if (shouldUnlock) {
                    unlockAchievement(achievement.id)
                }
            }
        }
    }

    private fun unlockAchievement(achievementId: String) {
        val achievement = achievements.find { it.id == achievementId }
        achievement?.let {
            it.unlocked = true
            gameData.achievements[achievementId] = true
            showNotification(getString(R.string.achievement_unlocked, it.name))
            renderAchievements()
        }
    }

    private fun checkPlayTime(requiredSeconds: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val playTimeSeconds = (currentTime - gameData.gameStartTime) / 1000
        return playTimeSeconds >= requiredSeconds
    }

    private fun checkAllBusinessUpgrades(): Boolean {
        return businessUpgrades.all { upgrade ->
            (gameData.businessUpgrades[upgrade.id] ?: 0) >= 1
        }
    }

    private fun checkCompletionist(): Boolean {
        val otherAchievements = achievements.filter { it.id != "completionist" }
        return otherAchievements.all { it.unlocked }
    }

    private fun showPrestigeDialog() {
        if (!gameData.canPrestige()) {
            showNotification(getString(R.string.prestige_requirement_not_met))
            return
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.prestige_confirm_title))
            .setMessage(getString(R.string.prestige_confirm_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> doPrestige() }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun doPrestige() {
        if (!gameData.canPrestige()) {
            showNotification(getString(R.string.prestige_requirement_not_met))
            return
        }

        val newPrestigePoints = gameData.calculatePrestigePoints()
        gameData.prestigePoints += newPrestigePoints
        gameData.prestigeLevel++

        val prestigeBonus = gameData.prestigeLevel * 10

        // Reset progress but maintain prestige
        gameData.money = 0
        gameData.incomePerSecond = 0.0
        gameData.totalClicks = 0
        gameData.totalEarned = 0
        gameData.currentBusinessLevel = 1

        // Reset upgrades
        gameData.clickUpgrades.clear()
        gameData.businessUpgrades.clear()

        // Reset some achievements (keep prestige and special ones)
        achievements.forEach { achievement ->
            if (!achievement.id.contains("prestige") && achievement.id != "click_master" &&
                achievement.id != "speed_demon" && achievement.id != "patient_player" &&
                achievement.id != "dedicated_player") {
                achievement.unlocked = false
                gameData.achievements[achievement.id] = false
            }
        }

        // Recalculate money per click with new prestige bonus
        gameData.moneyPerClick = gameData.calculateMoneyPerClick(clickUpgrades)

        updateDisplay()
        renderAchievements()
        saveGame()

        showNotification(getString(R.string.prestige_completed, gameData.prestigeLevel, prestigeBonus, newPrestigePoints))
    }

    private fun showResetDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.reset_confirm_title))
            .setMessage(getString(R.string.reset_confirm_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> showFinalResetDialog() }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun showFinalResetDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.reset_confirm_title))
            .setMessage(getString(R.string.reset_final_confirm))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> resetGame() }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun resetGame() {
        // Reset everything
        gameData = GameData()
        gameData.gameStartTime = System.currentTimeMillis()

        // Reset achievements
        achievements.forEach { achievement ->
            achievement.unlocked = false
        }

        // Clear saved data
        gameDataManager.resetGame()

        updateDisplay()
        renderAchievements()

        showNotification(getString(R.string.game_reset))
    }

    // EXPORT/IMPORT SYSTEM COMMENTED OUT FOR NOW
    /*
    private fun exportGame() {
        try {
            val saveCode = gameDataManager.exportGame(gameData)
            exportCode.text = saveCode

            // Copy to clipboard
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Fumadero Tycoon Save", saveCode)
            clipboard.setPrimaryClip(clip)

            showNotification(getString(R.string.export_success))
        } catch (e: Exception) {
            showNotification(getString(R.string.export_error, e.message))
        }
    }

    private fun importGame() {
        val saveCode = importCode.text.toString().trim()

        if (saveCode.isEmpty()) {
            showNotification(getString(R.string.import_empty_code))
            return
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.import_confirm_title))
            .setMessage(getString(R.string.import_confirm_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> performImport(saveCode) }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun performImport(saveCode: String) {
        try {
            val importedData = gameDataManager.importGame(saveCode)
            gameData = importedData

            // Update achievements
            achievements.forEach { achievement ->
                achievement.unlocked = gameData.achievements[achievement.id] ?: false
            }

            // Recalculate values
            gameData.moneyPerClick = gameData.calculateMoneyPerClick(clickUpgrades)
            gameData.recalculateIncomePerSecond(businessUpgrades)

            // Clear import field
            importCode.text = ""

            updateDisplay()
            renderAchievements()
            saveGame()

            val importDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            showNotification(getString(R.string.import_success, importDate))
        } catch (e: Exception) {
            showNotification(getString(R.string.import_error))
        }
    }

    private fun copyExportCode() {
        val code = exportCode.text.toString()
        if (code.isNotEmpty()) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Fumadero Tycoon Save", code)
            clipboard.setPrimaryClip(clip)
            showNotification(getString(R.string.export_success))
        }
    }
    */

    // SAVE GAME METHOD - REACTIVATED
    private fun saveGame() {
        // Update achievements in game data
        achievements.forEach { achievement ->
            gameData.achievements[achievement.id] = achievement.unlocked
        }
        gameDataManager.saveGame(gameData)
    }

    private fun showNotification(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun formatNumber(number: Number): String {
        val value = number.toDouble()
        return when {
            value >= 1e12 -> String.format("%.2fT", value / 1e12)
            value >= 1e9 -> String.format("%.2fB", value / 1e9)
            value >= 1e6 -> String.format("%.2fM", value / 1e6)
            value >= 1e3 -> String.format("%.2fK", value / 1e3)
            else -> {
                if (value == floor(value)) {
                    value.toLong().toString()
                } else {
                    String.format("%.1f", value)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop game loops
        incomeRunnable?.let { handler.removeCallbacks(it) }
        saveRunnable?.let { handler.removeCallbacks(it) }

        // Save game one last time
        saveGame()
    }

    override fun onPause() {
        super.onPause()
        // Save game when app goes to background
        saveGame()

        // Store last play time for offline earnings
        gameData.lastPlayTime = System.currentTimeMillis()
    }
}