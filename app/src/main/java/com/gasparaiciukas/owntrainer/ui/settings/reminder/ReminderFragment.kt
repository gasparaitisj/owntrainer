package com.gasparaiciukas.owntrainer.ui.settings.reminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.FragmentReminderBinding
import com.gasparaiciukas.owntrainer.settings.SettingsUiState
import com.gasparaiciukas.owntrainer.settings.SettingsViewModel
import com.gasparaiciukas.owntrainer.utils.Constants
import com.gasparaiciukas.owntrainer.utils.adapter.ReminderAdapter
import com.gasparaiciukas.owntrainer.utils.database.Reminder
import com.gasparaiciukas.owntrainer.utils.notif.ReminderNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ReminderFragment : Fragment(R.layout.fragment_reminder) {
    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!

    lateinit var reminderAdapter: ReminderAdapter

    lateinit var sharedViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SettingsViewModel::class.java]

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.uiState.collectLatest {
                    refreshUi(it)
                }
            }
        }

        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUi() {
        initNavigation()
        initRecyclerView()
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun refreshUi(uiState: SettingsUiState) {
        reminderAdapter.items = uiState.reminders
    }

    private fun initRecyclerView() {
        reminderAdapter = ReminderAdapter().apply {
            setFormatStrings(
                ReminderAdapter.ReminderAdapterFormatStrings(
                    time = getString(R.string.row_reminder_alarm_text)
                )
            )
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reminderAdapter
        }
        reminderAdapter.setOnClickListeners(
            longClickListener = { reminder: Reminder ->
                sharedViewModel.deleteReminder(reminder.reminderId)
                cancelNotification()
            }
        )
    }

    private fun initNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.fab.setOnClickListener {
            findNavController().navigate(
                ReminderFragmentDirections.actionReminderFragmentToCreateReminderFragment()
            )
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelNotification() {
        val intent = Intent(requireActivity().applicationContext, ReminderNotification::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                requireActivity().applicationContext,
                Constants.NOTIFICATION_REMINDER_ID,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getBroadcast(
                requireActivity().applicationContext,
                Constants.NOTIFICATION_REMINDER_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}
