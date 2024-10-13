package com.artinc.planit

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.artinc.planit.data.TaskViewModel

class TaskAdapter(
    private var tasks: List<Task>,
    private val taskViewModel: TaskViewModel,
    private val onTaskClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_card, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
    }

    override fun getItemCount() = tasks.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.isCompl)

        fun bind(task: Task) {
            checkBox.isChecked = task.isCompleted

            // Установите текст, приоритет и цвет
            itemView.findViewById<TextView>(R.id.title).text = task.title
            itemView.findViewById<TextView>(R.id.desc).text = task.description
            setPrior(task.priority)

            val cardView = itemView.findViewById<CardView>(R.id.card)
            cardView.setCardBackgroundColor(getTaskColor(task.color))

            // Установите цвет фона в зависимости от состояния задачи
            if (task.isCompleted) {
                cardView.setCardBackgroundColor(Color.GRAY)
            } else {
                cardView.setCardBackgroundColor(getTaskColor(task.color))
            }

            // Обработка нажатия на чекбокс
            checkBox.setOnCheckedChangeListener(null) // Сначала сбросьте слушателя
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                // Обновляем статус выполнения задачи
                task.isCompleted = isChecked

                // Обновляем задачу в ViewModel
                taskViewModel.updateTask(task)

                // Обновляем текущий элемент после выполнения операций компоновки
                itemView.post {
                    notifyItemChanged(adapterPosition)
                }
            }

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
            itemView.findViewById<ImageView>(R.id.first_prior).setImageResource(if (prior >= 1) R.drawable.star else R.drawable.star_outline)
            itemView.findViewById<ImageView>(R.id.second_prior).setImageResource(if (prior >= 2) R.drawable.star else R.drawable.star_outline)
            itemView.findViewById<ImageView>(R.id.third_prior).setImageResource(if (prior >= 3) R.drawable.star else R.drawable.star_outline)
        }
    }
}

