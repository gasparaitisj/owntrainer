package com.gasparaiciukas.owntrainer.fragment

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.AppearanceMode
import com.gasparaiciukas.owntrainer.viewmodel.SettingsViewModel
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import javax.inject.Inject


@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class SettingsFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: MainFragmentFactory

    lateinit var navController: NavController
    lateinit var fakeViewModel: SettingsViewModel
    lateinit var userRepository: FakeUserRepositoryTest

    private val testContext: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setup() {
        hiltRule.inject()
        navController = Mockito.mock(NavController::class.java)

        userRepository = FakeUserRepositoryTest()
        fakeViewModel = SettingsViewModel(userRepository)
    }

    @Test
    @Ignore(
        "Test crashes at this code snippet:\n'binding.navigationView.setupWithNavController(findNavController())'" +
                "with error:\n View androidx.drawerlayout.widget.DrawerLayout{17d6f28 VFE...... ......I. 0,0-0,0 #7f0900fb" +
                "app:id/drawer_layout} does not have a NavController set"
    )
    fun changeAppearanceMode_shouldChangeAppearanceMode() {
        launchFragmentInHiltContainer<SettingsFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            fakeViewModel = viewModel
        }

        Espresso.onView(
            ViewMatchers.withId(R.id.tv_appearance)
        ).perform(ViewActions.click())

        Espresso.onView(
            ViewMatchers.withText(testContext.getString(R.string.light_mode))
        ).perform(ViewActions.click())

        // android.R.id.button1 is the positive button id
        Espresso.onView(
            ViewMatchers.withId(android.R.id.button1)
        ).perform(ViewActions.click())

        Truth.assertThat(fakeViewModel.ldAppearanceMode.value).isEqualTo(AppearanceMode.DAY.ordinal)
        Truth.assertThat(AppCompatDelegate.getDefaultNightMode())
            .isEqualTo(AppCompatDelegate.MODE_NIGHT_NO)
    }

    @Test
    fun clickEditProfile_navigateToProfileFragment() {
        launchFragmentInHiltContainer<SettingsFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            fakeViewModel = viewModel
        }

        Espresso.onView(
            ViewMatchers.withId(R.id.tv_profile)
        ).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            SettingsFragmentDirections.actionSettingsFragmentToProfileFragment()
        )
    }

    @Test
    fun clickReminders_navigateToReminderFragment() {
        launchFragmentInHiltContainer<SettingsFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            fakeViewModel = viewModel
        }

        Espresso.onView(
            ViewMatchers.withId(R.id.tv_reminders)
        ).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            SettingsFragmentDirections.actionSettingsFragmentToReminderFragment()
        )
    }
}