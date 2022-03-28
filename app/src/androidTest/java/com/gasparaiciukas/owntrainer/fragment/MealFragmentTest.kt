package com.gasparaiciukas.owntrainer.fragment

import android.view.View
import android.widget.ImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.DiaryEntry
import com.gasparaiciukas.owntrainer.database.DiaryEntryMealCrossRef
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.getOrAwaitValue
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.FoodViewModel
import com.gasparaiciukas.owntrainer.viewmodel.MealViewModel
import com.google.android.material.tabs.TabLayout
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


@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class MealFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: MainFragmentFactory

    lateinit var navController: NavController
    lateinit var fakeViewModel: MealViewModel
    lateinit var diaryRepository: FakeDiaryRepositoryTest

    @Before
    fun setup() {
        hiltRule.inject()

        navController = Mockito.mock(NavController::class.java)
        diaryRepository = FakeDiaryRepositoryTest()
        fakeViewModel = MealViewModel(diaryRepository)
    }

    @Test
    fun singleClickOnMeal_navigateToMealItemFragment() = runTest {
        launchFragmentInHiltContainer<MealFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            fakeViewModel = viewModel
        }

        fakeViewModel.diaryRepository.insertMeal(
            Meal(
                mealId = 10,
                title = "title",
                instructions = "instructions"
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MealAdapter.MealViewHolder>(
                0,
                ViewActions.click()
            )
        )

        Mockito.verify(navController).navigate(
            MealFragmentDirections.actionMealFragmentToMealItemFragment(
                mealId = 10,
                diaryEntryId = -1
            )
        )
    }

    @Test
    fun longClickOnMeal_deleteMeal() = runTest {
        launchFragmentInHiltContainer<MealFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            fakeViewModel = viewModel
        }

        val meal = Meal(
            mealId = 10,
            title = "title",
            instructions = "instructions"
        )

        fakeViewModel.diaryRepository.insertMeal(meal)

        Espresso.onView(ViewMatchers.withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MealAdapter.MealViewHolder>(
                0,
                ViewActions.longClick()
            )
        )

        Espresso.onView(ViewMatchers.withContentDescription(R.string.delete_meal)).inRoot(isPlatformPopup()).perform(
            ViewActions.click()
        )

        assertThat(fakeViewModel.ldMeals.getOrAwaitValue()).doesNotContain(meal)
    }


    @Test
    fun clickFoodsTab_navigateToFoodFragment() {
        launchFragmentInHiltContainer<MealFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        Espresso.onView(ViewMatchers.withId(R.id.layout_tab)).perform(selectTabAtPosition(0))

        Mockito.verify(navController).navigate(
            MealFragmentDirections.actionMealFragmentToFoodFragment()
        )
    }

    private fun selectTabAtPosition(tabIndex: Int): ViewAction {
        return object : ViewAction {
            override fun getDescription() = "with tab at index $tabIndex"

            override fun getConstraints() =
                Matchers.allOf(
                    ViewMatchers.isDisplayed(),
                    ViewMatchers.isAssignableFrom(TabLayout::class.java)
                )

            override fun perform(uiController: UiController, view: View) {
                val tabLayout = view as TabLayout
                val tabAtIndex: TabLayout.Tab = tabLayout.getTabAt(tabIndex)
                    ?: throw PerformException.Builder()
                        .withCause(Throwable("No tab at index $tabIndex"))
                        .build()

                tabAtIndex.select()
            }
        }
    }

    @Test
    fun clickDrawerHomeButton_navigateToDiaryFragment() {
        launchFragmentInHiltContainer<MealFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.home)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            MealFragmentDirections.actionMealFragmentToDiaryFragment()
        )
    }

    @Test
    fun clickDrawerFoodsButton_navigateToFoodFragment() {
        launchFragmentInHiltContainer<MealFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.foods)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            MealFragmentDirections.actionMealFragmentToFoodFragment()
        )
    }

    @Test
    fun clickDrawerMealsButton_navigateToMealFragment() {
        launchFragmentInHiltContainer<MealFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.meals)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            MealFragmentDirections.actionMealFragmentSelf()
        )
    }

    @Test
    fun clickDrawerProgressButton_navigateToProgressFragment() {
        launchFragmentInHiltContainer<MealFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.progress)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            MealFragmentDirections.actionMealFragmentToProgressFragment()
        )
    }

    @Test
    fun clickDrawerProfileButton_navigateToProfileFragment() {
        launchFragmentInHiltContainer<MealFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.profile)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            MealFragmentDirections.actionMealFragmentToProfileFragment()
        )
    }

    @Test
    fun clickDrawerSettingsButton_navigateToSettingsFragment() {
        launchFragmentInHiltContainer<MealFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.settings)).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            MealFragmentDirections.actionMealFragmentToSettingsFragment()
        )
    }

}