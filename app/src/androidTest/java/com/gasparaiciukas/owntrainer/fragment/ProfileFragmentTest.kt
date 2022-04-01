package com.gasparaiciukas.owntrainer.fragment

import android.widget.ImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.getOrAwaitValue
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepositoryTest
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.DiaryViewModel
import com.gasparaiciukas.owntrainer.viewmodel.ProfileViewModel
import com.google.common.truth.Truth
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
class ProfileFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: MainFragmentFactory

    lateinit var navController: NavController
    lateinit var fakeViewModel: ProfileViewModel
    lateinit var userRepository: FakeUserRepositoryTest
    lateinit var diaryRepository: FakeDiaryRepositoryTest

    @Before
    fun setup() {
        hiltRule.inject()

        navController = Mockito.mock(NavController::class.java)
        userRepository = FakeUserRepositoryTest()
        fakeViewModel = ProfileViewModel(userRepository)
    }

    @Test
    fun clickSaveButton_shouldUpdateUserCorrectly() {
        launchFragmentInHiltContainer<ProfileFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            fakeViewModel = viewModel
        }

        val user = fakeViewModel.ldUser.getOrAwaitValue().copy()
        user.sex = "Female"
        user.ageInYears = 20
        user.heightInCm = 160
        user.weightInKg = 45.0
        user.lifestyle = "Sedentary"

        Espresso.onView(ViewMatchers.withId(R.id.et_sex))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Female"))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.et_age))
            .perform(ViewActions.replaceText("20"))

        Espresso.onView(ViewMatchers.withId(R.id.et_height))
            .perform(ViewActions.replaceText("160"))

        Espresso.onView(ViewMatchers.withId(R.id.et_weight))
            .perform(ViewActions.replaceText("45.0"))

        Espresso.onView(ViewMatchers.withId(R.id.et_lifestyle))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Sedentary"))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.btn_save)).perform(ViewActions.click())

        Truth.assertThat(fakeViewModel.ldUser.getOrAwaitValue()).isEqualTo(user)
    }

    @Test
    fun clickDrawerHomeButton_navigateToDiaryFragment() {
        launchFragmentInHiltContainer<ProfileFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            fakeViewModel = viewModel
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.home)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            ProfileFragmentDirections.actionProfileFragmentToDiaryFragment()
        )
    }

    @Test
    fun clickDrawerFoodsButton_navigateToFoodFragment() {
        launchFragmentInHiltContainer<ProfileFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.foods)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            ProfileFragmentDirections.actionProfileFragmentToFoodFragment()
        )
    }

    @Test
    fun clickDrawerMealsButton_navigateToMealFragment() {
        launchFragmentInHiltContainer<ProfileFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.meals)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            ProfileFragmentDirections.actionProfileFragmentToMealFragment()
        )
    }

    @Test
    fun clickDrawerProgressButton_navigateToProgressFragment() {
        launchFragmentInHiltContainer<ProfileFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.progress)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            ProfileFragmentDirections.actionProfileFragmentToProgressFragment()
        )
    }

    @Test
    fun clickDrawerProfileButton_navigateToProfileFragment() {
        launchFragmentInHiltContainer<ProfileFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.profile)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            ProfileFragmentDirections.actionProfileFragmentSelf()
        )
    }

    @Test
    fun clickDrawerSettingsButton_navigateToSettingsFragment() {
        launchFragmentInHiltContainer<ProfileFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.settings)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            ProfileFragmentDirections.actionProfileFragmentToSettingsFragment()
        )
    }
}