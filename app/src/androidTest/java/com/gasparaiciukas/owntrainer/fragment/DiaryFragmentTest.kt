package com.gasparaiciukas.owntrainer.fragment

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.DiaryEntry
import com.gasparaiciukas.owntrainer.database.DiaryEntryWithMeals
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepositoryTest
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.DiaryViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.LocalDate

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class DiaryFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

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
        launchFragmentInHiltContainer<DiaryFragment> {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
            this.initNavigation()
        }
        onView(withId(R.id.fab)).perform(click())

        verify(navController).navigate(
            DiaryFragmentDirections.actionDiaryFragmentToAddMealToDiaryFragment(
                fakeViewModel.diaryEntryWithMeals.diaryEntry.diaryEntryId
            )
        )
    }


}