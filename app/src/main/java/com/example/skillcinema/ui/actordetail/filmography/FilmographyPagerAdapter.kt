package com.example.skillcinema.ui.actordetail.filmography

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.skillcinema.domain.models.Profession

class FilmographyPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val actorId: Int,
    private val professions: Map<Profession, Int>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = professions.size

    override fun createFragment(position: Int): Fragment {
        val profession = when(position) {
            0 -> Profession.ACTOR
            1 -> Profession.DIRECTOR
            2 -> Profession.WRITER
            3 -> Profession.PRODUCER
            else -> Profession.OTHER
        }

        return FilmographyTabFragment.newInstance(
            actorId = actorId,
            profession = profession,
            count = professions[profession] ?: 0
        )
    }

    fun getPageTitle(position: Int): String {
        val profession = when(position) {
            0 -> Profession.ACTOR
            1 -> Profession.DIRECTOR
            2 -> Profession.WRITER
            3 -> Profession.PRODUCER
            else -> Profession.OTHER
        }

        return "${profession.title} (${professions[profession] ?: 0})"
    }
}