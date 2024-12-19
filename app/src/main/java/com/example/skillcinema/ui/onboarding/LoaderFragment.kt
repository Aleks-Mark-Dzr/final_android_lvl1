package com.example.skillcinema.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.skillcinema.databinding.FragmentLoaderBinding
import com.example.skillcinema.databinding.FragmentOnboarding2Binding
import com.example.skillcinema.databinding.FragmentOnboarding3Binding

class LoaderFragment : Fragment() {

    private var _binding: FragmentLoaderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
