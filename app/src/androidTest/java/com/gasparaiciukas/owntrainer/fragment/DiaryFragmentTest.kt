package com.gasparaiciukas.owntrainer.fragment

import android.widget.ImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.*
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepositoryTest
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.DatabaseFoodItemViewModel
import com.gasparaiciukas.owntrainer.viewmodel.DiaryViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.LocalDate
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class DiaryFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: MainFragmentFactory

    lateinit var navController: NavController
    lateinit var fakeViewModel: DiaryViewModel
    lateinit var userRepository: FakeUserRepositoryTest
    lateinit var diaryRepository: FakeDiaryRepositoryTest

    @Before
    fun setup() {
        hiltRule.inject()

        navController = Mockito.mock(NavController::class.java)
        userRepository = FakeUserRepositoryTest()
        diaryRepository = FakeDiaryRepositoryTest()
        fakeViewModel = DiaryViewModel(userRepository, diaryRepository)
    }

    @Test
    fun clickAddMealToDiaryButton_navigateToAddMealToDiaryFragment() = runTest {
        launchFragmentInHiltContainer<DiaryFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
        }
        onView(withId(R.id.fab)).perform(click())

        verify(navController).navigate(
            DiaryFragmentDirections.actionDiaryFragmentToAddMealToDiaryFragment()
        )
    }

    @Test
    fun clickOnMeal_navigateToMealItemFragment() {
        launchFragmentInHiltContainer<DiaryFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
            adapter.items = listOf(
                MealWithFoodEntries(
                    Meal(
                        mealId = 10,
                        title = "title",
                        instructions = "instructions"
                    ),
                    listOf()
                )
            )
        }
        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MealAdapter.MealViewHolder>(
                0,
                click()
            )
        )

        verify(navController).navigate(
            DiaryFragmentDirections.actionDiaryFragmentToMealItemFragment(
                mealId = 10,
                diaryEntryId = 0
            )
        )
    }

    @Test
    fun clickDrawerHomeButton_navigateToSelf() {
        launchFragmentInHiltContainer<DiaryFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        onView(
            allOf(instanceOf(ImageButton::class.java), withParent(withId(R.id.top_app_bar)))
        ).perform(click())

        onView(withId(R.id.home)).perform(click())

        verify(navController).navigate(
            DiaryFragmentDirections.actionDiaryFragmentSelf()
        )
    }

    @Test
    fun clickDrawerFoodsButton_navigateToFoodFragment() {
        launchFragmentInHiltContainer<DiaryFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        onView(
            allOf(instanceOf(ImageButton::class.java), withParent(withId(R.id.top_app_bar)))
        ).perform(click())

        onView(withId(R.id.foods)).perform(click())

        verify(navController).navigate(
            DiaryFragmentDirections.actionDiaryFragmentToFoodFragment()
        )
    }

    @Test
    fun clickDrawerMealsButton_navigateToMealFragment() {
        launchFragmentInHiltContainer<DiaryFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        onView(
            allOf(instanceOf(ImageButton::class.java), withParent(withId(R.id.top_app_bar)))
        ).perform(click())

        onView(withId(R.id.meals)).perform(click())

        verify(navController).navigate(
            DiaryFragmentDirections.actionDiaryFragmentToMealFragment()
        )
    }

    @Test
    fun clickDrawerProgressButton_navigateToProgressFragment() {
        launchFragmentInHiltContainer<DiaryFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        onView(
            allOf(instanceOf(ImageButton::class.java), withParent(withId(R.id.top_app_bar)))
        ).perform(click())

        onView(withId(R.id.progress)).perform(click())

        verify(navController).navigate(
            DiaryFragmentDirections.actionDiaryFragmentToProgressFragment()
        )
    }

    @Test
    fun clickDrawerProfileButton_navigateToProfileFragment() {
        launchFragmentInHiltContainer<DiaryFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        onView(
            allOf(instanceOf(ImageButton::class.java), withParent(withId(R.id.top_app_bar)))
        ).perform(click())

        onView(withId(R.id.profile)).perform(click())

        verify(navController).navigate(
            DiaryFragmentDirections.actionDiaryFragmentToProfileFragment()
        )
    }

    @Test
    fun clickDrawerSettingsButton_navigateToSettingsFragment() {
        launchFragmentInHiltContainer<DiaryFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        onView(
            allOf(instanceOf(ImageButton::class.java), withParent(withId(R.id.top_app_bar)))
        ).perform(click())

        onView(withId(R.id.settings)).perform(click())

        verify(navController).navigate(
            DiaryFragmentDirections.actionDiaryFragmentToSettingsFragment()
        )
    }
}