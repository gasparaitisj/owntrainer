package com.gasparaiciukas.owntrainer.fragment

import android.widget.ImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable
import com.gasparaiciukas.owntrainer.viewmodel.DatabaseFoodItemViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import javax.inject.Inject


@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class DatabaseFoodItemFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: MainFragmentFactory

    lateinit var navController: NavController
    lateinit var fakeViewModel: DatabaseFoodItemViewModel
    lateinit var userRepository: FakeUserRepositoryTest
    lateinit var savedStateHandle: SavedStateHandle
    private val foodEntry = createFoodEntry()

    @Before
    fun setup() {
        hiltRule.inject()

        savedStateHandle = SavedStateHandle().apply {
            set("food", foodEntry)
        }
        navController = Mockito.mock(NavController::class.java)
        userRepository = FakeUserRepositoryTest()
        fakeViewModel = DatabaseFoodItemViewModel(userRepository, savedStateHandle)
    }

    @Test
    fun clickOnNavigationBackButton_popBackStack() {
        launchFragmentInHiltContainer<DatabaseFoodItemFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
            viewModel.food = foodEntry
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Mockito.verify(navController).popBackStack()
    }

    private fun createFoodEntry(): FoodEntryParcelable {
        return FoodEntryParcelable(
            title = "Egg",
            caloriesPer100G = 155.0,
            calories = 155.0 * 60.0 / 100.0,
            carbsPer100G = 1.1,
            carbs = 1.1 * 60.0 / 100.0,
            fatPer100G = 11.0,
            fat = 11.0 * 60.0 / 100.0,
            proteinPer100G = 13.0,
            protein = 13.0 * 60.0 / 100.0,
            quantityInG = 60.0
        )
    }
}