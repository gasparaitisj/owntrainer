package com.gasparaiciukas.owntrainer.fragment

import android.widget.ImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.getOrAwaitValue
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepositoryTest
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.FoodViewModel
import com.google.common.truth.Truth.assertThat
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

@HiltAndroidTest
@MediumTest
@ExperimentalCoroutinesApi
class AddFoodToMealFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: MainFragmentFactory

    lateinit var navController: NavController
    lateinit var fakeViewModel: FoodViewModel
    lateinit var userRepository: FakeUserRepositoryTest
    lateinit var diaryRepository: FakeDiaryRepositoryTest

    @Before
    fun setup() {
        hiltRule.inject()

        navController = Mockito.mock(NavController::class.java)
        userRepository = FakeUserRepositoryTest()
        diaryRepository = FakeDiaryRepositoryTest()

        fakeViewModel = FoodViewModel(diaryRepository, userRepository)
    }

    @Test
    fun clickOnMeal_popBackStack() = runTest {
        val meal = Meal(
            title = "title",
            instructions = "instructions"
        )
        launchFragmentInHiltContainer<AddFoodToMealFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            Navigation.setViewNavController(requireView(), navController)
            fakeViewModel = sharedViewModel
        }

        fakeViewModel.diaryRepository.insertMeal(meal)

        Espresso.onView(ViewMatchers.withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MealAdapter.MealViewHolder>(
                0,
                ViewActions.click()
            )
        )

        assertThat(fakeViewModel.ldMeals.getOrAwaitValue()).contains(
            MealWithFoodEntries(
                meal,
                listOf()
            )
        )
        Mockito.verify(navController).popBackStack()
    }

    @Test
    fun clickOnNavigationBackButton_popBackStack() = runTest {
        launchFragmentInHiltContainer<AddFoodToMealFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Mockito.verify(navController).popBackStack()
    }

}