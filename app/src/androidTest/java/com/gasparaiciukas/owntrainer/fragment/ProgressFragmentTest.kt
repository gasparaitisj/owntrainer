package com.gasparaiciukas.owntrainer.fragment

import android.widget.ImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepositoryTest
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.ProfileViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import javax.inject.Inject


@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class ProgressFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: MainFragmentFactory

    lateinit var navController: NavController

    @Before
    fun setup() {
        hiltRule.inject()
        navController = Mockito.mock(NavController::class.java)
    }

    @Test
    fun clickDrawerHomeButton_navigateToDiaryFragment() {
        launchFragmentInHiltContainer<ProgressFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.home)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            ProgressFragmentDirections.actionProgressFragmentToDiaryFragment()
        )
    }

    @Test
    fun clickDrawerFoodsButton_navigateToFoodFragment() {
        launchFragmentInHiltContainer<ProgressFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.foods)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            ProgressFragmentDirections.actionProgressFragmentToFoodFragment()
        )
    }

    @Test
    fun clickDrawerMealsButton_navigateToMealFragment() {
        launchFragmentInHiltContainer<ProgressFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.meals)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            ProgressFragmentDirections.actionProgressFragmentToMealFragment()
        )
    }

    @Test
    fun clickDrawerProgressButton_navigateSelf() {
        launchFragmentInHiltContainer<ProgressFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.progress)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            ProgressFragmentDirections.actionProgressFragmentSelf()
        )
    }

    @Test
    fun clickDrawerProfileButton_navigateToProfileFragment() {
        launchFragmentInHiltContainer<ProgressFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.profile)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            ProgressFragmentDirections.actionProgressFragmentToProfileFragment()
        )
    }

    @Test
    fun clickDrawerSettingsButton_navigateToSettingsFragment() {
        launchFragmentInHiltContainer<ProgressFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.settings)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            ProgressFragmentDirections.actionProgressFragmentToSettingsFragment()
        )
    }
}