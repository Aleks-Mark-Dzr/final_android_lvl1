package com.example.skillcinema.ui.actordetail.filmography

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.skillcinema.domain.models.Profession

class FilmographyTabFragment : Fragment() {

    companion object {
        private const val ARG_ACTOR_ID = "actor_id"
        private const val ARG_PROFESSION = "profession"
        private const val ARG_COUNT = "count"

        fun newInstance(actorId: Int, profession: Profession, count: Int) =
            FilmographyTabFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ACTOR_ID, actorId)
                    putSerializable(ARG_PROFESSION, profession)
                    putInt(ARG_COUNT, count)
                }
            }
    }

    // Реализация фрагмента
}