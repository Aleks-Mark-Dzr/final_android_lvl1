package com.example.skillcinema

import OnboardingPagerAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.skillcinema.databinding.ActivityMainBinding
import com.example.skillcinema.ui.onboarding.OnboardingActivityFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка ViewPager2
        val onboardingAdapter = OnboardingPagerAdapter(this)
        binding.viewPager.adapter = onboardingAdapter
    }
}