package com.gasparaiciukas.owntrainer.fragment

import android.widget.ImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.Reminder
import com.gasparaiciukas.owntrainer.getOrAwaitValue
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.SettingsViewModel
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class CreateReminderFragmentTest {

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
    fun clickBackButton_popBackStack() = runTest {
        launchFragmentInHiltContainer<ReminderFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            sharedViewModel = fakeViewModel
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar)),
                Matchers.not(ViewMatchers.withId(R.id.btn_save))
            )
        ).perform(ViewActions.click())

        Mockito.verify(navController).popBackStack()
    }

    @Test
    fun clickSaveButton_popBackStack() {
        launchFragmentInHiltContainer<CreateReminderFragment> {
            Navigation.setViewNavController(requireView(), navController)
            sharedViewModel = fakeViewModel
        }

        val title = "Random title"
        val hour = 20
        val minute = 0
        val time = "20:00"
        fakeViewModel.ldReminders.getOrAwaitValue()

        // Title
        Espresso.onView(
            ViewMatchers.withId(R.id.et_title)
        ).perform(ViewActions.click())

        Espresso.onView(
            ViewMatchers.withId(R.id.dialog_et_title)
        ).perform(ViewActions.replaceText(title))

        Espresso.onView(
            ViewMatchers.withId(android.R.id.button1)
        ).perform(ViewActions.click())


        // Time
        /* TODO
            replace manually inserted time with actual testing of MaterialTimePicker
            currently, there is insufficient documentation
         */
        Espresso.onView(
            ViewMatchers.withId(R.id.et_time)
        ).perform(replaceText(time))

        fakeViewModel.hour = hour
        fakeViewModel.minute = minute
        fakeViewModel.isTimeCorrect = true

        // Save button
        Espresso.onView(
            ViewMatchers.withId(R.id.btn_save)
        ).perform(ViewActions.click())

        Truth.assertThat(
            userRepository.getReminders().asLiveData().getOrAwaitValue()
        )
            .contains(
                Reminder(
                    title = title,
                    hour = hour,
                    minute = minute
                )
            )

        Mockito.verify(navController).popBackStack()
    }
}