package com.gasparaiciukas.owntrainer.fragment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.Reminder
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.SettingsViewModel
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
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class ReminderFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: MainFragmentFactory

    lateinit var navController: NavController
    lateinit var fakeViewModel: SettingsViewModel
    lateinit var userRepository: FakeUserRepositoryTest

    @Before
    fun setup() {
        hiltRule.inject()

        navController = Mockito.mock(NavController::class.java)
        userRepository = FakeUserRepositoryTest()
        fakeViewModel = SettingsViewModel(userRepository)
    }

    @Test
    fun clickAddReminderButton_navigateToCreateReminderFragment() = runTest {
        launchFragmentInHiltContainer<ReminderFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        )
        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            ReminderFragmentDirections.actionReminderFragmentToCreateReminderFragment()
        )
    }

    @Test
    fun longClickOnReminder_deleteReminder() = runTest {
        val reminder = Reminder(
            reminderId = 1,
            title = "Breakfast",
            hour = 7,
            minute = 30
        )
        launchFragmentInHiltContainer<ReminderFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            sharedViewModel = fakeViewModel
            reminderAdapter.items = listOf(
                reminder
            )
        }
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MealAdapter.MealViewHolder>(
                0,
                ViewActions.longClick()
            )
        )

        Espresso.onView(ViewMatchers.withContentDescription(R.string.delete_reminder))
            .inRoot(RootMatchers.isPlatformPopup()).perform(
                ViewActions.click()
            )

        Truth.assertThat(fakeViewModel.reminders.first()).doesNotContain(reminder)
    }
}