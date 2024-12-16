package com.example.skillcinema.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.skillcinema.databinding.FragmentOnboardingBinding

class OnboardingActivityFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Обработка кнопки Skip
        binding.onboardingSkipButton.setOnClickListener {
            // Здесь вы можете заменить текущий фрагмент или закрыть приложение
            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, AnotherFragment()) // Замените AnotherFragment на нужный
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Очищаем binding для предотвращения утечек памяти
    }
}