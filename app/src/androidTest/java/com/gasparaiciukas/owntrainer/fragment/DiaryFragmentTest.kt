package com.gasparaiciukas.owntrainer.fragment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.DiaryEntry
import com.gasparaiciukas.owntrainer.database.DiaryEntryMealCrossRef
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepositoryTest
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.DiaryViewModel
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
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
        launchFragmentInHiltContainer<DiaryFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        )
        onView(withId(R.id.btn_add_food)).perform(click())

        verify(navController).navigate(
            DiaryFragmentDirections.actionDiaryFragmentToAddMealToDiaryFragment()
        )
    }

    @Test
    fun clickOnMeal_navigateToMealItemFragment() {
        launchFragmentInHiltContainer<DiaryFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
            mealAdapter.items = listOf(
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
    fun longClickOnMeal_deleteMeal() = runTest {
        val meal = Meal(
            mealId = 10,
            title = "title",
            instructions = "instructions"
        )
        val date = LocalDate.of(2022, 7, 19)
        val diaryEntry = DiaryEntry(
            diaryEntryId = 1,
            year = date.year,
            dayOfYear = date.dayOfYear,
            dayOfWeek = date.dayOfWeek.value,
            monthOfYear = date.monthValue,
            dayOfMonth = date.dayOfMonth
        )

        diaryRepository.insertDiaryEntry(diaryEntry)
        diaryRepository.insertMeal(meal)
        diaryRepository.insertDiaryEntryMealCrossRef(
            DiaryEntryMealCrossRef(
                diaryEntryId = diaryEntry.diaryEntryId,
                mealId = meal.mealId
            )
        )

        launchFragmentInHiltContainer<DiaryFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            fakeViewModel = viewModel
            mealAdapter.items = listOf(
                MealWithFoodEntries(
                    meal,
                    listOf()
                )
            )
        }

        Espresso.onView(ViewMatchers.withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MealAdapter.MealViewHolder>(
                0,
                ViewActions.longClick()
            )
        )

        Espresso.onView(ViewMatchers.withContentDescription(R.string.delete_meal))
            .inRoot(RootMatchers.isPlatformPopup()).perform(
                ViewActions.click()
            )

        Truth.assertThat(fakeViewModel.diaryEntryWithMeals.first()?.meals)
            .doesNotContain(meal)
    }
}