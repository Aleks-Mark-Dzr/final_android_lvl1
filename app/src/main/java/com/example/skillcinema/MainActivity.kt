package com.example.skillcinema

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

        // Отображаем OnboardingFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OnboardingActivityFragment())
                .commit()
        }
    }
}