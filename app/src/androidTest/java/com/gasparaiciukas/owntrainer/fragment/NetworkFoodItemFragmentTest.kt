package com.gasparaiciukas.owntrainer.fragment

import android.content.Context
import android.widget.ImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.launchFragmentInHiltContainer
import com.gasparaiciukas.owntrainer.network.Food
import com.gasparaiciukas.owntrainer.network.FoodNutrient
import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepositoryTest
import com.gasparaiciukas.owntrainer.repository.FakeUserRepositoryTest
import com.gasparaiciukas.owntrainer.viewmodel.FoodViewModel
import com.gasparaiciukas.owntrainer.viewmodel.NetworkFoodItemUiState
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
    lateinit var fakeViewModel: FoodViewModel
    lateinit var userRepository: FakeUserRepositoryTest
    lateinit var diaryRepository: FakeDiaryRepositoryTest
    private val testContext: Context = ApplicationProvider.getApplicationContext()

    private val food = createFood()

    @Before
    fun setup() {
        hiltRule.inject()

        navController = Mockito.mock(NavController::class.java)
        userRepository = FakeUserRepositoryTest()
        diaryRepository = FakeDiaryRepositoryTest()
        fakeViewModel = FoodViewModel(diaryRepository, userRepository)
    }

    @Test
    fun clickOnNavigationBackButton_popBackStack() {
        launchFragmentInHiltContainer<NetworkFoodItemFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            Navigation.setViewNavController(requireView(), navController)
            sharedViewModel = fakeViewModel
        }

        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(ImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(R.id.top_app_bar))
            )
        ).perform(ViewActions.click())

        Mockito.verify(navController).popBackStack()
    }

    @Test
    fun clickAddNavigationButton_navigateToAddFoodToMealFragment() = runTest {
        val uiState = NetworkFoodItemUiState(
            user = fakeViewModel.user.first(),
            foodItem = food,
            title = food.description.toString(),
            carbs = 14.3f,
            carbsPercentage = 14.3f,
            calories = 52.0f,
            fat = 0.65f,
            fatPercentage = 0.65f,
            protein = 0.0f,
            proteinPercentage = 0.0f
        )
        launchFragmentInHiltContainer<NetworkFoodItemFragment>(
            fragmentFactory = fragmentFactory,
            navController = navController
        ) {
            fakeViewModel = sharedViewModel
            refreshUi(uiState)
        }

        val quantity = 200

        // Quantity
        Espresso.onView(
            ViewMatchers.withId(R.id.et_quantity)
        ).perform(ViewActions.click())

        Espresso.onView(
            ViewMatchers.withId(R.id.dialog_et_quantity)
        ).perform(ViewActions.replaceText(quantity.toString()))

        Espresso.onView(
            ViewMatchers.withId(android.R.id.button1)
        ).perform(ViewActions.click())

        // Save button
        Espresso.onView(
            ViewMatchers.withId(R.id.btn_add_to_meal)
        ).perform(ViewActions.click())


        Mockito.verify(navController).navigate(
            NetworkFoodItemFragmentDirections.actionNetworkFoodItemFragmentToAddFoodToMealFragment()
        )
        Truth.assertThat(fakeViewModel.quantity).isEqualTo(quantity)
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