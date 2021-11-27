package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.FragmentAddMealToDiaryBinding
import com.gasparaiciukas.owntrainer.viewmodel.AddMealToDiaryViewModel
import com.gasparaiciukas.owntrainer.viewmodel.BundleViewModelFactory
import timber.log.Timber

class AddMealToDiaryFragment : Fragment() {
    private var _binding: FragmentAddMealToDiaryBinding? = null
    private val binding get() = _binding!!

    private val args: AddMealToDiaryFragmentArgs by navArgs()

    private lateinit var viewModel: AddMealToDiaryViewModel
    private lateinit var viewModelFactory: BundleViewModelFactory

    private lateinit var adapter: MealAdapter
    private val listener: (meal: Meal, position: Int) -> Unit = { meal: Meal, _: Int ->
        viewModel.addMealToDiary(meal)
        findNavController().popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMealToDiaryBinding.inflate(inflater, container, false)
        val bundle = Bundle().apply {
            putString("primaryKey", args.primaryKey)
        }
        viewModelFactory = BundleViewModelFactory(bundle)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(AddMealToDiaryViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val passLambda: (_: Int) -> Unit = { _: Int -> }
        adapter = MealAdapter(viewModel.meals, listener, passLambda)
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}