package com.gasparaiciukas.owntrainer.fragment

import android.content.Context
import android.widget.ImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.Lifestyle
import com.gasparaiciukas.owntrainer.database.Sex
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.SettingsViewModel
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
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
    lateinit var fakeViewModel: SettingsViewModel
    lateinit var userRepository: FakeUserRepositoryTest
    private val testContext: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setup() {
        hiltRule.inject()

        navController = mock(NavController::class.java)
        userRepository = FakeUserRepositoryTest()
        fakeViewModel = SettingsViewModel(userRepository)
    }

    @Test
    fun editProfile_popBackStack() = runTest {
        launchFragmentInHiltContainer<ProfileFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            Navigation.setViewNavController(requireView(), navController)
            sharedViewModel = fakeViewModel
        }

        val user = userRepository.user.first().copy()
        user.sex = Sex.FEMALE.ordinal
        user.ageInYears = 30
        user.heightInCm = 170
        user.weightInKg = 71.6
        user.lifestyle = Lifestyle.MODERATELY_ACTIVE.ordinal

        // Sex
        Espresso.onView(
            ViewMatchers.withId(R.id.et_sex)
        ).perform(click())

        Espresso.onView(
            ViewMatchers.withText(testContext.getString(R.string.female))
        ).perform(click())

        // android.R.id.button1 is the positive button id
        Espresso.onView(
            ViewMatchers.withId(android.R.id.button1)
        ).perform(click())

        // Age
        Espresso.onView(
            ViewMatchers.withId(R.id.et_age)
        ).perform(click())

        Espresso.onView(
            ViewMatchers.withId(R.id.dialog_et_age)
        ).perform(replaceText(user.ageInYears.toString()))

        Espresso.onView(
            ViewMatchers.withId(android.R.id.button1)
        ).perform(click())

        // Height
        Espresso.onView(
            ViewMatchers.withId(R.id.et_height)
        ).perform(click())

        Espresso.onView(
            ViewMatchers.withId(R.id.dialog_et_height)
        ).perform(replaceText(user.heightInCm.toString()))

        Espresso.onView(
            ViewMatchers.withId(android.R.id.button1)
        ).perform(click())

        // Weight
        Espresso.onView(
            ViewMatchers.withId(R.id.et_weight)
        ).perform(click())

        Espresso.onView(
            ViewMatchers.withId(R.id.dialog_et_weight)
        ).perform(replaceText(user.weightInKg.toString()))

        Espresso.onView(
            ViewMatchers.withId(android.R.id.button1)
        ).perform(click())

        // Lifestyle
        Espresso.onView(
            ViewMatchers.withId(R.id.et_lifestyle)
        ).perform(click())

        Espresso.onView(
            ViewMatchers.withText(testContext.getString(R.string.moderately_active))
        ).perform(click())

        Espresso.onView(
            ViewMatchers.withId(android.R.id.button1)
        ).perform(click())

        // Back button
        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(click())

        Truth.assertThat(userRepository.user.first()).isEqualTo(user)
        Mockito.verify(navController).popBackStack()
    }
}