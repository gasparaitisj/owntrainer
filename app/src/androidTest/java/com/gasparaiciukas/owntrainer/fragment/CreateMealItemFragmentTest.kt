package com.gasparaiciukas.owntrainer.fragment

import android.content.Context
import android.widget.ImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.getOrAwaitValue
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.MealViewModel
import com.google.common.truth.Truth
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

    lateinit var navController: NavController
    lateinit var fakeViewModel: MealViewModel
    lateinit var diaryRepository: FakeDiaryRepositoryTest
    private val testContext: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setup() {
        hiltRule.inject()

        navController = Mockito.mock(NavController::class.java)
        diaryRepository = FakeDiaryRepositoryTest()
        fakeViewModel = MealViewModel(diaryRepository)
    }

    @Test
    fun clickSaveButton_popBackStack() {
        launchFragmentInHiltContainer<CreateMealItemFragment> {
            Navigation.setViewNavController(requireView(), navController)
            sharedViewModel = fakeViewModel
        }

        val title = "Random title"
        val instructions = "Instructions"
        fakeViewModel.ldMeals.getOrAwaitValue()

        // Title
        Espresso.onView(
            ViewMatchers.withId(R.id.et_title)
        ).perform(click())

        Espresso.onView(
            ViewMatchers.withId(R.id.dialog_et_title)
        ).perform(replaceText(title))

        Espresso.onView(
            ViewMatchers.withId(android.R.id.button1)
        ).perform(click())


        // Instructions
        Espresso.onView(
            ViewMatchers.withId(R.id.et_instructions)
        ).perform(click())

        Espresso.onView(
            ViewMatchers.withId(R.id.dialog_et_instructions)
        ).perform(replaceText(instructions))

        Espresso.onView(
            ViewMatchers.withId(android.R.id.button1)
        ).perform(click())

        // Save button
        onView(
            withId(R.id.btn_save)
        ).perform(click())

        Truth.assertThat(
            diaryRepository.getAllMealsWithFoodEntries().asLiveData().getOrAwaitValue()
        )
            .contains(
                MealWithFoodEntries(
                    Meal(
                        title = title,
                        instructions = instructions
                    ),
                    listOf()
                )
            )

        Mockito.verify(navController).popBackStack()
    }

    @Test
    fun clickOnNavigationBackButton_popBackStack() {
        launchFragmentInHiltContainer<CreateMealItemFragment> {
            Navigation.setViewNavController(requireView(), navController)
            sharedViewModel = fakeViewModel
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
}