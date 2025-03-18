package com.example.skillcinema.ui.onboarding

import OnboardingPagerAdapter
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private var currentPage = 0
    private val totalPages = 4 // Установите общее количество страниц

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Устанавливаем адаптер для ViewPager2
        binding.viewPager.adapter = OnboardingPagerAdapter(requireActivity())

        // Подключаем TabLayoutMediator для синхронизации с ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            // Настройка вкладок, если нужно
        }.attach()

        // Обработка смены страницы
        binding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position

                if (currentPage == totalPages - 1) { // Проверка на последнюю страницу (layout_onboarding_loader)
                    Handler(Looper.getMainLooper()).postDelayed({
                        navigateToHomepage()
                    }, 3000) // Задержка в 3 секунды
                }
            }
        })

        // Обработка кнопки "Пропустить"
        binding.skipButton.setOnClickListener {
            navigateToHomepage()
        }
    }

    private fun navigateToHomepage() {
        // Сохраняем в SharedPreferences, что онбординг был показан
        val sharedPref = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("is_first_launch", false).apply()

        // Навигация на главный экран
        findNavController().navigate(R.id.action_onboardingFragment_to_homepageFragment)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}