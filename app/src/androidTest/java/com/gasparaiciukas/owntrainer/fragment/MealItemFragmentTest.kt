package com.gasparaiciukas.owntrainer.fragment

import android.widget.ImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.DatabaseFoodAdapter
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.getOrAwaitValue
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepositoryTest
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable
import com.gasparaiciukas.owntrainer.viewmodel.MealItemViewModel
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
class MealItemFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: MainFragmentFactory

    lateinit var navController: NavController
    lateinit var fakeViewModel: MealItemViewModel
    lateinit var userRepository: FakeUserRepositoryTest
    lateinit var diaryRepository: FakeDiaryRepositoryTest
    lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setup() {
        hiltRule.inject()

        navController = Mockito.mock(NavController::class.java)
        userRepository = FakeUserRepositoryTest()
        diaryRepository = FakeDiaryRepositoryTest()
        savedStateHandle = SavedStateHandle().apply {
            set("diaryEntryId", 0)
            set("mealId", 0)
        }
        fakeViewModel = MealItemViewModel(userRepository, diaryRepository, savedStateHandle)
    }

    @Test
    fun clickOnFoodEntry_navigateToDatabaseFoodItemFragment() = runTest {
        launchFragmentInHiltContainer<MealItemFragment>(fragmentFactory = fragmentFactory, navController = navController) {
            Navigation.setViewNavController(requireView(), navController)
            fakeViewModel = viewModel
        }
        fakeViewModel.diaryRepository.insertMeal(
            Meal(
                mealId = 0,
                title = "title",
                instructions = "instructions"
            )
        )
        fakeViewModel.diaryRepository.insertFood(
            FoodEntry(
                foodEntryId = 1,
                mealId = 0,
                title = "Banana",
                caloriesPer100G = 89.0,
                carbsPer100G = 23.0,
                fatPer100G = 0.3,
                proteinPer100G = 1.1,
                quantityInG = 120.0
            )
        )
        fakeViewModel.diaryRepository.insertFood(
            FoodEntry(
                foodEntryId = 2,
                mealId = 0,
                title = "Egg",
                caloriesPer100G = 155.0,
                carbsPer100G = 1.1,
                fatPer100G = 11.0,
                proteinPer100G = 13.0,
                quantityInG = 60.0
            )
        )
        fakeViewModel.loadData(fakeViewModel.ldUser.getOrAwaitValue())

        Espresso.onView(ViewMatchers.withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<DatabaseFoodAdapter.DatabaseFoodViewHolder>(
                0,
                ViewActions.click()
            )
        )

        Mockito.verify(navController).navigate(
            MealItemFragmentDirections.actionMealItemFragmentToDatabaseFoodItemFragment(
                food = FoodEntryParcelable(
                    title = "Banana",
                    caloriesPer100G = 89.0,
                    carbsPer100G = 23.0,
                    fatPer100G = 0.3,
                    proteinPer100G = 1.1,
                    quantityInG = 120.0
                )
            )
        )
    }

    @Test
    fun clickOnNavigationBackButton_popBackStack() = runTest {
        launchFragmentInHiltContainer<MealItemFragment>(fragmentFactory = fragmentFactory, navController = navController) {
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