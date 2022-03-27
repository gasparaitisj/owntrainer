package com.gasparaiciukas.owntrainer.fragment

import android.widget.ImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.CreateMealItemViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito


@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class CreateMealItemFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun clickSaveButton_popBackStack() {

    }

    @Test
    fun clickOnNavigationBackButton_popBackStack() {
        val navController = Mockito.mock(NavController::class.java)
        val fakeViewModel = CreateMealItemViewModel(FakeDiaryRepositoryTest())
        launchFragmentInHiltContainer<CreateMealItemFragment> {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
            this.initUi()
        }

        onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(withId(R.id.top_app_bar)),
                not(withId(R.id.btn_save))
            )
        ).perform(click())

        Mockito.verify(navController).popBackStack()
    }

    @Test
    fun clickOnNavigationSaveButton_popBackStack() {
        val navController = Mockito.mock(NavController::class.java)
        val fakeViewModel = CreateMealItemViewModel(FakeDiaryRepositoryTest())
        launchFragmentInHiltContainer<CreateMealItemFragment> {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
            this.initUi()
        }

        onView(withId(R.id.btn_save)).perform(click())

        Mockito.verify(navController).popBackStack()
    }
}