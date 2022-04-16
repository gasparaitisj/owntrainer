package com.gasparaiciukas.owntrainer.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlarmManager.INTERVAL_DAY
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.DialogCreateReminderTitleBinding
import com.gasparaiciukas.owntrainer.databinding.FragmentCreateReminderBinding
import com.gasparaiciukas.owntrainer.notif.ReminderNotification
import com.gasparaiciukas.owntrainer.utils.Constants.NOTIFICATION_REMINDER_ID
import com.gasparaiciukas.owntrainer.utils.Constants.NOTIFICATION_REMINDER_TITLE_EXTRA
import com.gasparaiciukas.owntrainer.viewmodel.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class CreateReminderFragment : Fragment(R.layout.fragment_create_reminder) {
    private var _binding: FragmentCreateReminderBinding? = null
    private val binding get() = _binding!!

    lateinit var sharedViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SettingsViewModel::class.java]
        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUi() {
        initNavigation()
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun initNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.topAppBar.menu.findItem(R.id.btn_save).setOnMenuItemClickListener {
            if (sharedViewModel.isTitleCorrect &&
                sharedViewModel.isTimeCorrect
            ) {
                sharedViewModel.createReminder(
                    binding.etTitle.text.toString(),
                    sharedViewModel.hour,
                    sharedViewModel.minute
                )
                scheduleNotification()
                findNavController().popBackStack()
            } else {
                if (!sharedViewModel.isTitleCorrect) {
                    binding.layoutEtTitle.error = getString(R.string.title_must_not_be_empty)
                }
                if (!sharedViewModel.isTimeCorrect) {
                    binding.layoutEtTime.error = getString(R.string.time_must_not_be_empty)
                }
                false
            }
        }
        binding.etTitle.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_create_reminder_title, null)
            val dialogBinding = DialogCreateReminderTitleBinding.bind(view)
            var titleTemp = ""
            dialogBinding.dialogEtTitle.setText("")
            dialogBinding.dialogEtTitle.doOnTextChanged { text, _, _, _ ->
                titleTemp = text.toString()
                val validation = isTitleCorrect(titleTemp)
                if (validation == null) {
                    dialogBinding.dialogLayoutEtTitle.error = null
                } else {
                    dialogBinding.dialogLayoutEtTitle.error = validation
                }
            }
            MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setTitle(getString(R.string.title))
                .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    if (isTitleCorrect(titleTemp) == null) {
                        sharedViewModel.title = titleTemp
                        binding.etTitle.setText(sharedViewModel.title)
                        sharedViewModel.isTitleCorrect = true
                        binding.layoutEtTitle.error = null
                    }
                }
                .show()
        }
        binding.etTime.setOnClickListener {
            showTimePicker()
        }
    }

    private fun isTitleCorrect(text: String): String? {
        if (text.isBlank()) {
            return getString(R.string.title_must_not_be_empty)
        }
        sharedViewModel.ldReminders.value?.let { reminders ->
            println(reminders)
            val titleExists = reminders.any { reminder -> reminder.title == text }
            if (titleExists) {
                return getString(R.string.reminder_with_this_title_already_exists)
            } else {
                return null
            }
        }
        return getString(R.string.title_must_be_valid)
    }

    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText(getString(R.string.select_reminder_time))
            .build()
        picker.addOnPositiveButtonClickListener {
            sharedViewModel.ldReminders.value?.let { reminders ->
                if (reminders.any { reminder -> reminder.hour == picker.hour && reminder.minute == picker.minute }) {
                    showIncorrectTimeDialog()
                } else {
                    sharedViewModel.hour = picker.hour
                    sharedViewModel.minute = picker.minute
                    binding.etTime.setText(
                        String.format("%02d:%02d", sharedViewModel.hour, sharedViewModel.minute)
                    )
                    sharedViewModel.isTimeCorrect = true
                    binding.layoutEtTime.error = null
                }
            }
        }
        picker.show(parentFragmentManager, "TIME_PICKER")
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun scheduleNotification() {
        val intent = Intent(requireActivity().applicationContext, ReminderNotification::class.java)
        val title = binding.etTitle.text.toString()
        intent.putExtra(NOTIFICATION_REMINDER_TITLE_EXTRA, title)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                requireActivity().applicationContext,
                NOTIFICATION_REMINDER_ID,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getBroadcast(
                requireActivity().applicationContext,
                NOTIFICATION_REMINDER_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            getTime(),
            INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun getTime(): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, sharedViewModel.hour)
            set(Calendar.MINUTE, sharedViewModel.minute)
            set(Calendar.SECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun showIncorrectTimeDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.error))
            .setMessage(getString(R.string.reminder_with_this_time_already_exists))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                // do nothing
            }
            .show()
    }
}