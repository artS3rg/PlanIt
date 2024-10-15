package com.artinc.planit

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.artinc.planit.data.TaskViewModel

class TaskAdapter(
    private var tasks: List<Task>,
    private val taskViewModel: TaskViewModel,
    private val onTaskClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    fun updateTasks(newTasks: List<Task>) {
        val diffCallback = TaskDiffCallback(tasks, newTasks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        tasks = newTasks
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_card, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount() = tasks.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.isCompl)
        private val cardView: CardView = itemView.findViewById(R.id.card)
        private val titleView: TextView = itemView.findViewById(R.id.title)
        private val descView: TextView = itemView.findViewById(R.id.desc)
        private val firstPrior: ImageView = itemView.findViewById(R.id.first_prior)
        private val secondPrior: ImageView = itemView.findViewById(R.id.second_prior)
        private val thirdPrior: ImageView = itemView.findViewById(R.id.third_prior)

        fun bind(task: Task) {
            checkBox.setOnCheckedChangeListener(null) // Удаляем слушатель
            checkBox.isChecked = task.isCompleted

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                task.isCompleted = isChecked
                taskViewModel.updateTask(task)
                // Обновляем только текущий элемент
                notifyItemChanged(adapterPosition)
            }

            titleView.text = task.title
            descView.text = task.description
            setPrior(task.priority)

            cardView.setCardBackgroundColor(if (task.isCompleted) Color.GRAY else getTaskColor(task.color))

            // Обрабатываем нажатие на весь элемент
            itemView.setOnClickListener { onTaskClick(task) }
        }

        private fun getTaskColor(color: String): Int {
            return when (color) {
                "blue" -> Color.rgb(24, 136, 255)
                "green" -> Color.rgb(34, 133, 7)
                "yellow" -> Color.rgb(237, 178, 0)
                "red" -> Color.rgb(225, 7, 7)
                else -> Color.rgb(24, 136, 255)
            }
        }

        private fun setPrior(prior: Int) {
            firstPrior.setImageResource(if (prior >= 1) R.drawable.star else R.drawable.star_outline)
            secondPrior.setImageResource(if (prior >= 2) R.drawable.star else R.drawable.star_outline)
            thirdPrior.setImageResource(if (prior >= 3) R.drawable.star else R.drawable.star_outline)
        }
    }
}
