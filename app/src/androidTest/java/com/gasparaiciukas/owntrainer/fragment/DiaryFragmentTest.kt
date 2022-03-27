package com.gasparaiciukas.owntrainer.fragment

import android.widget.ImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentFactory
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
import com.gasparaiciukas.owntrainer.viewmodel.DiaryViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun clickAddMealToDiaryButton_navigateToAddMealToDiaryFragment() {
        val navController = mock(NavController::class.java)
        val fakeViewModel = DiaryViewModel(FakeUserRepositoryTest(), FakeDiaryRepositoryTest())
        fakeViewModel.currentDay = LocalDate.of(2022, 7, 19)
        fakeViewModel.diaryEntryWithMeals = DiaryEntryWithMeals(
            DiaryEntry(
                diaryEntryId = 1,
                year = fakeViewModel.currentDay.year,
                dayOfYear = fakeViewModel.currentDay.dayOfYear,
                dayOfWeek = fakeViewModel.currentDay.dayOfWeek.value,
                monthOfYear = fakeViewModel.currentDay.monthValue,
                dayOfMonth = fakeViewModel.currentDay.dayOfMonth
            ),
            listOf()
        )
        launchFragmentInHiltContainer<DiaryFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
            this.initNavigation()
        }
        onView(withId(R.id.fab)).perform(click())

        verify(navController).navigate(
            DiaryFragmentDirections.actionDiaryFragmentToAddMealToDiaryFragment()
        )
    }

    @Test
    fun clickOnMeal_navigateToMealItemFragment() {
        val navController = mock(NavController::class.java)
        val fakeViewModel = DiaryViewModel(FakeUserRepositoryTest(), FakeDiaryRepositoryTest())
        fakeViewModel.currentDay = LocalDate.of(2022, 7, 19)
        fakeViewModel.diaryEntryWithMeals = DiaryEntryWithMeals(
            DiaryEntry(
                diaryEntryId = 1,
                year = fakeViewModel.currentDay.year,
                dayOfYear = fakeViewModel.currentDay.dayOfYear,
                dayOfWeek = fakeViewModel.currentDay.dayOfWeek.value,
                monthOfYear = fakeViewModel.currentDay.monthValue,
                dayOfMonth = fakeViewModel.currentDay.dayOfMonth
            ),
            listOf()
        )
        fakeViewModel.user = User(
            sex = "Male",
            ageInYears = 25,
            heightInCm = 180,
            weightInKg = 80.0,
            lifestyle = "Lightly active",
            year = fakeViewModel.currentDay.year,
            month = fakeViewModel.currentDay.monthValue,
            dayOfYear = fakeViewModel.currentDay.dayOfYear,
            dayOfMonth = fakeViewModel.currentDay.dayOfMonth,
            dayOfWeek = fakeViewModel.currentDay.dayOfWeek.value
        )
        fakeViewModel.mealsWithFoodEntries = listOf(
            MealWithFoodEntries(
                Meal(
                    mealId = 10,
                    title = "Title",
                    instructions = "Instructions"
                ),
                listOf()
            )
        )
        launchFragmentInHiltContainer<DiaryFragment> {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
            initUi()
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
                diaryEntryId = 1
            )
        )
    }

    @Test
    fun clickDrawerHomeButton_navigateToSelf() {
        val navController = mock(NavController::class.java)
        val fakeViewModel = DiaryViewModel(FakeUserRepositoryTest(), FakeDiaryRepositoryTest())
        fakeViewModel.currentDay = LocalDate.of(2022, 7, 19)
        fakeViewModel.diaryEntryWithMeals = DiaryEntryWithMeals(
            DiaryEntry(
                diaryEntryId = 1,
                year = fakeViewModel.currentDay.year,
                dayOfYear = fakeViewModel.currentDay.dayOfYear,
                dayOfWeek = fakeViewModel.currentDay.dayOfWeek.value,
                monthOfYear = fakeViewModel.currentDay.monthValue,
                dayOfMonth = fakeViewModel.currentDay.dayOfMonth
            ),
            listOf()
        )
        launchFragmentInHiltContainer<DiaryFragment> {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
            this.initNavigation()
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
        val navController = mock(NavController::class.java)
        val fakeViewModel = DiaryViewModel(FakeUserRepositoryTest(), FakeDiaryRepositoryTest())
        fakeViewModel.currentDay = LocalDate.of(2022, 7, 19)
        fakeViewModel.diaryEntryWithMeals = DiaryEntryWithMeals(
            DiaryEntry(
                diaryEntryId = 1,
                year = fakeViewModel.currentDay.year,
                dayOfYear = fakeViewModel.currentDay.dayOfYear,
                dayOfWeek = fakeViewModel.currentDay.dayOfWeek.value,
                monthOfYear = fakeViewModel.currentDay.monthValue,
                dayOfMonth = fakeViewModel.currentDay.dayOfMonth
            ),
            listOf()
        )
        launchFragmentInHiltContainer<DiaryFragment> {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
            this.initNavigation()
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
        val navController = mock(NavController::class.java)
        val fakeViewModel = DiaryViewModel(FakeUserRepositoryTest(), FakeDiaryRepositoryTest())
        fakeViewModel.currentDay = LocalDate.of(2022, 7, 19)
        fakeViewModel.diaryEntryWithMeals = DiaryEntryWithMeals(
            DiaryEntry(
                diaryEntryId = 1,
                year = fakeViewModel.currentDay.year,
                dayOfYear = fakeViewModel.currentDay.dayOfYear,
                dayOfWeek = fakeViewModel.currentDay.dayOfWeek.value,
                monthOfYear = fakeViewModel.currentDay.monthValue,
                dayOfMonth = fakeViewModel.currentDay.dayOfMonth
            ),
            listOf()
        )
        launchFragmentInHiltContainer<DiaryFragment> {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
            this.initNavigation()
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
        val navController = mock(NavController::class.java)
        val fakeViewModel = DiaryViewModel(FakeUserRepositoryTest(), FakeDiaryRepositoryTest())
        fakeViewModel.currentDay = LocalDate.of(2022, 7, 19)
        fakeViewModel.diaryEntryWithMeals = DiaryEntryWithMeals(
            DiaryEntry(
                diaryEntryId = 1,
                year = fakeViewModel.currentDay.year,
                dayOfYear = fakeViewModel.currentDay.dayOfYear,
                dayOfWeek = fakeViewModel.currentDay.dayOfWeek.value,
                monthOfYear = fakeViewModel.currentDay.monthValue,
                dayOfMonth = fakeViewModel.currentDay.dayOfMonth
            ),
            listOf()
        )
        launchFragmentInHiltContainer<DiaryFragment> {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
            this.initNavigation()
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
        val navController = mock(NavController::class.java)
        val fakeViewModel = DiaryViewModel(FakeUserRepositoryTest(), FakeDiaryRepositoryTest())
        fakeViewModel.currentDay = LocalDate.of(2022, 7, 19)
        fakeViewModel.diaryEntryWithMeals = DiaryEntryWithMeals(
            DiaryEntry(
                diaryEntryId = 1,
                year = fakeViewModel.currentDay.year,
                dayOfYear = fakeViewModel.currentDay.dayOfYear,
                dayOfWeek = fakeViewModel.currentDay.dayOfWeek.value,
                monthOfYear = fakeViewModel.currentDay.monthValue,
                dayOfMonth = fakeViewModel.currentDay.dayOfMonth
            ),
            listOf()
        )
        launchFragmentInHiltContainer<DiaryFragment> {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
            this.initNavigation()
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
        val navController = mock(NavController::class.java)
        val fakeViewModel = DiaryViewModel(FakeUserRepositoryTest(), FakeDiaryRepositoryTest())
        fakeViewModel.currentDay = LocalDate.of(2022, 7, 19)
        fakeViewModel.diaryEntryWithMeals = DiaryEntryWithMeals(
            DiaryEntry(
                diaryEntryId = 1,
                year = fakeViewModel.currentDay.year,
                dayOfYear = fakeViewModel.currentDay.dayOfYear,
                dayOfWeek = fakeViewModel.currentDay.dayOfWeek.value,
                monthOfYear = fakeViewModel.currentDay.monthValue,
                dayOfMonth = fakeViewModel.currentDay.dayOfMonth
            ),
            listOf()
        )
        launchFragmentInHiltContainer<DiaryFragment> {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
            this.initNavigation()
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