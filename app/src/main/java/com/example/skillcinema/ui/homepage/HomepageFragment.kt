package com.example.skillcinema.ui.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillcinema.data.Movie
import com.example.skillcinema.databinding.FragmentHomepageBinding

class HomepageFragment : Fragment() {

    private var _binding: FragmentHomepageBinding? = null
    private val binding get() = _binding!!
    private val movieAdapter = MovieAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }

        // Загрузите данные (пример)
        val sampleMovies = listOf(
            Movie(1, "Movie 1", "2024", "https://example.com/image1.jpg"),
            Movie(2, "Movie 2", "2023", "https://example.com/image2.jpg")
        )
        movieAdapter.submitList(sampleMovies)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}