import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.skillcinema.ui.onboarding.LoaderFragment
import com.example.skillcinema.ui.onboarding.Onboarding2Fragment
import com.example.skillcinema.ui.onboarding.Onboarding3Fragment
import com.example.skillcinema.ui.onboarding.OnboardingActivityFragment

class OnboardingPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf(
        OnboardingActivityFragment(),  // Первый экран
        Onboarding2Fragment(),         // Второй экран
        Onboarding3Fragment(),         // Третий экран
        LoaderFragment()               // Экран загрузки
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}