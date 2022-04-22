package com.gasparaiciukas.owntrainer.fragment

import android.view.View
import android.widget.ImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.MainCoroutineRuleTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.DatabaseFoodAdapter
import com.gasparaiciukas.owntrainer.database.FoodEntry
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.database.MealWithFoodEntries
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepositoryTest
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.utils.FoodEntryParcelable
import com.gasparaiciukas.owntrainer.viewmodel.MealItemUiState
import com.gasparaiciukas.owntrainer.viewmodel.MealItemViewModel
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.core.AnyOf.anyOf
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

    @get:Rule
    val mainCoroutineRule = MainCoroutineRuleTest()

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
    fun singleClickOnFoodEntry_navigateToDatabaseFoodItemFragment() = runTest {
        val foodEntry = FoodEntry(
            mealId = 0,
            title = "Banana",
            caloriesPer100G = 89.0,
            carbsPer100G = 23.0,
            fatPer100G = 0.3,
            proteinPer100G = 1.1,
            quantityInG = 120.0
        )
        val mealWithFoodEntries = MealWithFoodEntries(
            Meal(
                title = "title",
                instructions = "instructions"
            ),
            listOf(
                foodEntry
            )
        )
        val uiState = MealItemUiState(
            user = userRepository.user.first(),
            mealWithFoodEntries = mealWithFoodEntries,
            carbsPercentage = 0f,
            fatPercentage = 0f,
            proteinPercentage = 0f,
            carbsDailyIntakePercentage = 0f,
            fatDailyIntakePercentage = 0f,
            proteinDailyIntakePercentage = 0f,
            caloriesDailyIntakePercentage = 0f
        )
        launchFragmentInHiltContainer<MealItemFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
            refreshUi(uiState)
        }

        Espresso.onView(ViewMatchers.withId(R.id.recycler_view)).perform(
            ScrollToAction(),
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
                    calories = mealWithFoodEntries.calories,
                    carbs = mealWithFoodEntries.carbs,
                    fat = mealWithFoodEntries.fat,
                    protein = mealWithFoodEntries.protein,
                    quantityInG = 120.0
                )
            )
        )
    }

    class ScrollToAction(
        private val original: androidx.test.espresso.action.ScrollToAction = androidx.test.espresso.action.ScrollToAction()
    ) : ViewAction by original {

        override fun getConstraints(): Matcher<View> = anyOf(
            allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                isDescendantOfA(isAssignableFrom(NestedScrollView::class.java))),
            original.constraints
        )
    }

    @Test
    fun clickOnNavigationBackButton_popBackStack() = runTest {
        launchFragmentInHiltContainer<MealItemFragment>(
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