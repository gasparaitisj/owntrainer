package com.gasparaiciukas.owntrainer.utils.fragment

import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat.getColor
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.viewpager2.widget.ViewPager2
import com.gasparaiciukas.owntrainer.NavigationMainDirections
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.utils.NutrientValueFormatter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlin.math.roundToInt

fun Fragment.setPieChart(
    carbsPercentage: Float,
    fatPercentage: Float,
    proteinPercentage: Float,
    calories: Float,
    pieChart: PieChart,
    context: Context
) {
    val colors = listOf(
        getColor(context, R.color.colorCarbs),
        getColor(context, R.color.colorFat),
        getColor(context, R.color.colorProtein)
    )

    val entries = listOf(
        PieEntry(carbsPercentage, getString(R.string.carbohydrates)),
        PieEntry(fatPercentage, getString(R.string.fat)),
        PieEntry(proteinPercentage, getString(R.string.protein))
    )

    val pieDataSet = PieDataSet(entries, "Data").apply {
        this.colors = colors
    }

    val pieData = PieData(pieDataSet).apply {
        setValueFormatter(NutrientValueFormatter())
        setValueTextSize(12f)
    }

    pieChart.apply {
        data = pieData
        centerText = context.getString(
            R.string.pie_chart_center_text,
            calories.roundToInt().toString()
        )
        setCenterTextSize(14f)
        setCenterTextColor(
            getColor(context, R.color.colorWhite)
        )
        centerTextRadiusPercent = 100f
        setHoleColor(getColor(context, R.color.colorCalories))
        holeRadius = 30f
        transparentCircleRadius = 0f
        legend.isEnabled = false
        description.isEnabled = false
        setTouchEnabled(false)
        invalidate()
    }
}

fun Fragment.setupBottomNavigation(
    bottomNavigation: BottomNavigationView,
    navController: NavController,
    @IdRes checkedItemId: Int
) {
    bottomNavigation.selectedItemId = checkedItemId
    bottomNavigation.setOnItemSelectedListener {
        when (it.itemId) {
            R.id.diaryFragment -> {
                navController.navigate(
                    R.id.action_global_diaryFragment
                )
                true
            }
            R.id.mealFragment -> {
                navController.navigate(
                    NavigationMainDirections.actionGlobalFoodAndMealFragment(index = 1)
                )
                true
            }
            R.id.progressFragment -> {
                navController.navigate(
                    R.id.action_global_progressFragment
                )
                true
            }
            else -> false
        }
    }
}

fun Fragment.setupNavigationView(
    viewPager: ViewPager2? = null,
    navigationView: NavigationView?,
    drawerLayout: DrawerLayout?,
    navController: NavController,
    @IdRes checkedItem: Int
) {
    var selectedMenuItem: MenuItem? = null
    drawerLayout?.addDrawerListener(object : DrawerLayout.DrawerListener {
        override fun onDrawerClosed(drawerView: View) {
            selectedMenuItem?.let { menuItem ->
                navigateToNavigationViewMenuItem(
                    viewPager = viewPager,
                    menuItem = menuItem,
                    navController = navController
                )
            }
        }
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
        override fun onDrawerOpened(drawerView: View) {}
        override fun onDrawerStateChanged(newState: Int) {}
    })
    navigationView?.setCheckedItem(checkedItem)
    navigationView?.setNavigationItemSelectedListener { menuItem ->
        selectedMenuItem = menuItem
        drawerLayout?.close()
        true
    }
}

private fun navigateToNavigationViewMenuItem(
    viewPager: ViewPager2? = null,
    menuItem: MenuItem,
    navController: NavController
): Boolean {
    when (menuItem.itemId) {
        R.id.diaryFragment -> {
            navController.navigate(
                R.id.action_global_diaryFragment
            )
            return true
        }
        R.id.foodFragment -> {
            val index = 0
            if (viewPager != null) {
                viewPager.setCurrentItem(index, true)
            } else {
                navController.navigate(
                    NavigationMainDirections.actionGlobalFoodAndMealFragment(index = index)
                )
            }
            return true
        }
        R.id.mealFragment -> {
            val index = 1
            if (viewPager != null) {
                viewPager.setCurrentItem(index, true)
            } else {
                navController.navigate(
                    NavigationMainDirections.actionGlobalFoodAndMealFragment(index = index)
                )
            }
            return true
        }
        R.id.progressFragment -> {
            navController.navigate(
                R.id.action_global_progressFragment
            )
            return true
        }
        R.id.settingsFragment -> {
            navController.navigate(
                R.id.action_global_settingsFragment
            )
            return true
        }
        else -> {
            return false
        }
    }
}
