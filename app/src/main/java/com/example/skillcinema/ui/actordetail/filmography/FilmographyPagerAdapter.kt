package com.example.skillcinema.ui.actordetail.filmography

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.skillcinema.domain.models.Profession

/**
 * По одной вкладке на каждую профессию, для которой у актёра есть фильмы.
 * Хостом выступает сам [FilmographyListFragment], чтобы вкладки жили в его childFragmentManager
 * и могли читать общую [FilmographyViewModel].
 */
class FilmographyPagerAdapter(
    hostFragment: Fragment,
    private val professions: List<Profession>
) : FragmentStateAdapter(hostFragment) {

    override fun getItemCount(): Int = professions.size

    override fun createFragment(position: Int): Fragment =
        FilmographyTabFragment.newInstance(professions[position])
}
