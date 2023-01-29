package com.gasparaiciukas.owntrainer.utils.adapter

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gasparaiciukas.owntrainer.utils.fragment.FoodFragment
import com.gasparaiciukas.owntrainer.utils.fragment.MealFragment

class FoodAndMealAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = TAB_COUNT

    override fun createFragment(position: Int) = when (position) {
        0 -> FoodFragment()
        else -> MealFragment()
    }

    companion object {
        private const val TAB_COUNT = 2
    }
}
