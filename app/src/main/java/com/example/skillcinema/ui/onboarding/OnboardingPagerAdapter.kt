import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.skillcinema.R
//import com.example.skillcinema.ui.onboarding.OnboardingPageFragment

class OnboardingPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 4 // Количество экранов

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingPageFragment.newInstance(R.layout.layout_onboarding_page_1)
            1 -> OnboardingPageFragment.newInstance(R.layout.layout_onboarding_page_2)
            2 -> OnboardingPageFragment.newInstance(R.layout.layout_onboarding_page_3)
            3 -> OnboardingPageFragment.newInstance(R.layout.layout_onboarding_loader)
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}