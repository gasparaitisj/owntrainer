package com.gasparaiciukas.owntrainer.fragment

import android.view.View
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
import com.gasparaiciukas.owntrainer.network.Food
import com.gasparaiciukas.owntrainer.network.FoodNutrient
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.NetworkFoodItemViewModel
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import javax.inject.Inject


@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class NetworkFoodItemFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: MainFragmentFactory

    lateinit var navController: NavController
    lateinit var fakeViewModel: NetworkFoodItemViewModel
    lateinit var userRepository: FakeUserRepositoryTest
    lateinit var savedStateHandle: SavedStateHandle
    private val food = createFood()

    @Before
    fun setup() {
        hiltRule.inject()

        savedStateHandle = SavedStateHandle().apply {
            set("food", food)
        }
        navController = Mockito.mock(NavController::class.java)
        userRepository = FakeUserRepositoryTest()
        fakeViewModel = NetworkFoodItemViewModel(userRepository, savedStateHandle)
    }

    @Test
    fun clickOnNavigationBackButton_navigateToFoodFragment() {
        launchFragmentInHiltContainer<NetworkFoodItemFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            Navigation.setViewNavController(requireView(), navController)
            viewModel = fakeViewModel
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Mockito.verify(navController).navigate(
            NetworkFoodItemFragmentDirections.actionNetworkFoodItemFragmentToFoodFragment()
        )
    }

    @Test
    fun clickAddNavigationButton_navigateToAddFoodToMealFragment() = runTest {
        launchFragmentInHiltContainer<NetworkFoodItemFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            viewModel.foodItem = food
            viewModel.loadData()
        }

        Espresso.onView(
            ViewMatchers.withId(R.id.et_weight)
        ).perform(
            ViewActions.replaceText("200"),
        )
        Espresso.onView(
            ViewMatchers.withId(R.id.btn_add_to_meal)
        ).perform(
            ViewActions.click()
        )

        Mockito.verify(navController).navigate(
            NetworkFoodItemFragmentDirections.actionNetworkFoodItemFragmentToAddFoodToMealFragment(
                food,
                200
            )
        )
    }

    private fun createFood(): Food {
        return Food(
            fdcId = 454004,
            description = "APPLE",
            lowercaseDescription = "apple",
            dataType = "Branded",
            gtinUpc = "867824000001",
            publishedDate = "2019-04-01",
            brandOwner = "TREECRISP 2 GO",
            ingredients = "CRISP APPLE.",
            marketCountry = "United States",
            foodCategory = "Pre-Packaged Fruit & Vegetables",
            allHighlightFields = "<b>Ingredients</b>: CRISP <em>APPLE</em>.",
            score = 932.4247,
            foodNutrients = listOf(
                FoodNutrient(
                    nutrientId = 1003,
                    nutrientName = "Protein",
                    nutrientNumber = "203",
                    unitName = "G",
                    derivationCode = "LCCS",
                    derivationDescription = "Calculated from value per serving size measure",
                    value = 0.0
                ),
                FoodNutrient(
                    nutrientId = 1005,
                    nutrientName = "Carbohydrate, by difference",
                    nutrientNumber = "205",
                    unitName = "G",
                    derivationCode = "LCCS",
                    derivationDescription = "Calculated from value per serving size measure",
                    value = 14.3
                ),
                FoodNutrient(
                    nutrientId = 1008,
                    nutrientName = "Energy",
                    nutrientNumber = "208",
                    unitName = "G",
                    derivationCode = "LCCS",
                    derivationDescription = "Calculated from value per serving size measure",
                    value = 52.0
                ),
                FoodNutrient(
                    nutrientId = 1004,
                    nutrientName = "Total lipid (fat)",
                    nutrientNumber = "204",
                    unitName = "G",
                    derivationCode = "LCSL",
                    derivationDescription = "Calculated from a less than value per serving size measure",
                    value = 0.65
                ),
            )
        )
    }
}