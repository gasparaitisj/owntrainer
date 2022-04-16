package com.gasparaiciukas.owntrainer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.Reminder
import com.gasparaiciukas.owntrainer.databinding.RowReminderBinding

class ReminderAdapter : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    class ReminderViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val binding = RowReminderBinding.bind(view)

        init {
            this.itemView.isLongClickable = true
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(
            oldItem: Reminder,
            newItem: Reminder
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Reminder,
            newItem: Reminder
        ): Boolean {
            return oldItem.reminderId == newItem.reminderId
        }
    }

    private var singleClickListener: ((Reminder) -> Unit)? = null
    private var longClickListener: ((Reminder) -> Unit)? = null
    private val differ = AsyncListDiffer(this, diffCallback)

    var items: List<Reminder>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    fun setOnClickListeners(
        singleClickListener: ((reminder: Reminder) -> Unit)? = null,
        longClickListener: ((reminder: Reminder) -> Unit)? = null
    ) {
        this.singleClickListener = singleClickListener
        this.longClickListener = longClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        // Get information of each row
        val title = items[position].title
        val hour = items[position].hour
        val minute = items[position].minute

        // Display information of each row
        holder.binding.tvTitle.text = title
        holder.binding.tvTime.text = String.format("Alarm set at %02d:%02d", hour, minute)
        holder.binding.layoutItem.setOnClickListener {
            singleClickListener?.let { click ->
                click(items[position])
            }
        }
        holder.binding.layoutItem.setOnLongClickListener {
            val popup = PopupMenu(holder.binding.layoutItem.context, holder.binding.layoutItem)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.reminder_menu, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener {
                longClickListener?.let { click ->
                    click(items[position])
                }
                true
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}