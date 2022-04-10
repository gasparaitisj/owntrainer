package com.gasparaiciukas.owntrainer.fragment

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.getOrAwaitValue
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.FoodViewModel
import com.google.android.material.internal.CheckableImageButton
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class FoodFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: MainFragmentFactory

    lateinit var navController: NavController
    lateinit var fakeViewModel: FoodViewModel
    lateinit var diaryRepository: FakeDiaryRepositoryTest

    @Before
    fun setup() {
        hiltRule.inject()

        navController = Mockito.mock(NavController::class.java)
        diaryRepository = FakeDiaryRepositoryTest()
        fakeViewModel = FoodViewModel(diaryRepository)
    }

    @Test
    fun clickImeButton_navigateToNetworkFoodItemFragment() {
        launchFragmentInHiltContainer<FoodFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            Navigation.setViewNavController(requireView(), navController)
            fakeViewModel = viewModel
        }

        onView(withId(R.id.et_search))
            .perform(
                ViewActions.replaceText("Title")
            )

        onView(withId(R.id.et_search))
            .perform(
                ViewActions.pressImeActionButton()
            )

        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MealAdapter.MealViewHolder>(
                0,
                ViewActions.click()
            )
        )

        Mockito.verify(navController).navigate(
            FoodFragmentDirections.actionFoodFragmentToNetworkFoodItemFragment(
                position = 0,
                foodItem = fakeViewModel.ldResponse.getOrAwaitValue().data?.foods?.get(0)
            )
        )
    }

    @Test
    fun clickSearchIcon_navigateToNetworkFoodItemFragment() {
        launchFragmentInHiltContainer<FoodFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            Navigation.setViewNavController(requireView(), navController)
            fakeViewModel = viewModel
        }

        onView(withId(R.id.et_search))
            .perform(
                ViewActions.replaceText("Title")
            )

        onView(
            Matchers.allOf(
                Matchers.instanceOf(CheckableImageButton::class.java),
                ViewMatchers.withContentDescription("Search")
            )
        ).perform(click())

        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MealAdapter.MealViewHolder>(
                0,
                ViewActions.click()
            )
        )

        Mockito.verify(navController).navigate(
            FoodFragmentDirections.actionFoodFragmentToNetworkFoodItemFragment(
                position = 0,
                foodItem = fakeViewModel.ldResponse.getOrAwaitValue().data?.foods?.get(0)
            )
        )
    }

    @Test
    fun clickMealsTab_navigateToMealFragment() {
        launchFragmentInHiltContainer<FoodFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        onView(withId(R.id.layout_tab)).perform(selectTabAtPosition(1))

        Mockito.verify(navController).navigate(
            FoodFragmentDirections.actionFoodFragmentToMealFragment()
        )
    }

    private fun selectTabAtPosition(tabIndex: Int): ViewAction {
        return object : ViewAction {
            override fun getDescription() = "with tab at index $tabIndex"

            override fun getConstraints() =
                allOf(isDisplayed(), isAssignableFrom(TabLayout::class.java))

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

}