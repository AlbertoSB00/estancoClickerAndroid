package com.appberto.estancoclickerandroid

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        val logo = findViewById<ImageView>(R.id.splashLogo)
        val title = findViewById<TextView>(R.id.splashTitle)
        val subtitle = findViewById<TextView>(R.id.splashSubtitle)
        
        // Inicializar elementos invisibles
        logo.alpha = 0f
        title.alpha = 0f
        subtitle.alpha = 0f
        logo.scaleX = 0.5f
        logo.scaleY = 0.5f
        
        // Animación del logo
        val logoFadeIn = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f)
        val logoScaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0.5f, 1.2f, 1f)
        val logoScaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0.5f, 1.2f, 1f)
        
        val logoAnimSet = AnimatorSet()
        logoAnimSet.playTogether(logoFadeIn, logoScaleX, logoScaleY)
        logoAnimSet.duration = 800
        logoAnimSet.interpolator = AccelerateDecelerateInterpolator()
        
        // Animación del título
        val titleFadeIn = ObjectAnimator.ofFloat(title, "alpha", 0f, 1f)
        val titleSlideUp = ObjectAnimator.ofFloat(title, "translationY", 50f, 0f)
        
        val titleAnimSet = AnimatorSet()
        titleAnimSet.playTogether(titleFadeIn, titleSlideUp)
        titleAnimSet.duration = 600
        titleAnimSet.interpolator = AccelerateDecelerateInterpolator()
        
        // Animación del subtítulo
        val subtitleFadeIn = ObjectAnimator.ofFloat(subtitle, "alpha", 0f, 1f)
        val subtitleSlideUp = ObjectAnimator.ofFloat(subtitle, "translationY", 30f, 0f)
        
        val subtitleAnimSet = AnimatorSet()
        subtitleAnimSet.playTogether(subtitleFadeIn, subtitleSlideUp)
        subtitleAnimSet.duration = 500
        subtitleAnimSet.interpolator = AccelerateDecelerateInterpolator()
        
        // Secuencia de animaciones
        val mainAnimSet = AnimatorSet()
        mainAnimSet.playSequentially(logoAnimSet, titleAnimSet, subtitleAnimSet)
        
        // Iniciar animaciones
        Handler(Looper.getMainLooper()).postDelayed({
            mainAnimSet.start()
        }, 200)
        
        // Ir a MainActivity después de las animaciones
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 2500)
    }
}
