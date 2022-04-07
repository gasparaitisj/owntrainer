package com.gasparaiciukas.owntrainer.fragment

import android.widget.ImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.*
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepositoryTest
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.DiaryViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify
import java.time.LocalDate
import javax.inject.Inject

@HiltAndroidTest
@MediumTest
@ExperimentalCoroutinesApi
class AddMealToDiaryFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: MainFragmentFactory

    lateinit var navController: NavController
    lateinit var fakeViewModel: DiaryViewModel
    lateinit var userRepository: FakeUserRepositoryTest
    lateinit var diaryRepository: FakeDiaryRepositoryTest

    @Before
    fun setup() {
        hiltRule.inject()

        navController = Mockito.mock(NavController::class.java)
        userRepository = FakeUserRepositoryTest()
        diaryRepository = FakeDiaryRepositoryTest()
        fakeViewModel = DiaryViewModel(userRepository, diaryRepository)
    }

    @Test
    fun clickOnMeal_navigateToDiaryFragment() = runTest {
        launchFragmentInHiltContainer<AddMealToDiaryFragment>(fragmentFactory = fragmentFactory, navController = navController) {
            Navigation.setViewNavController(requireView(), navController)
            sharedViewModel = fakeViewModel
            adapter.items = listOf(
                MealWithFoodEntries(
                    Meal(
                        title = "title",
                        instructions = "instructions"
                    ),
                    listOf()
                )
            )
        }

        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MealAdapter.MealViewHolder>(
                0,
                ViewActions.click()
            )
        )

        verify(navController).navigate(
            AddMealToDiaryFragmentDirections.actionAddMealToDiaryFragmentToDiaryFragment()
        )
    }

    @Test
    fun clickOnNavigationBackButton_navigateToDiaryFragment() = runTest {
        val navController = Mockito.mock(NavController::class.java)

        launchFragmentInHiltContainer<AddMealToDiaryFragment>(fragmentFactory = fragmentFactory, navController = navController) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(
            allOf(
                instanceOf(ImageButton::class.java),
                withParent(withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        verify(navController).navigate(
            AddMealToDiaryFragmentDirections.actionAddMealToDiaryFragmentToDiaryFragment()
        )
    }
}