package com.artinc.planit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TasksAdapter(private val tasks: List<Task>) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        val descTextView: TextView = itemView.findViewById(R.id.desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_card, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.titleTextView.text = task.taskTitle
        holder.descTextView.text = task.taskDesc
    }

    override fun getItemCount() = tasks.size
}
