package com.artinc.planit

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.artinc.planit.data.TaskDatabase
import com.artinc.planit.data.TaskRepository
import com.artinc.planit.data.TaskViewModel
import com.artinc.planit.data.TaskViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar
import java.util.TimeZone

class NewTaskFragment : BottomSheetDialogFragment() {
    private var selectedPriority: Int = 1 // По умолчанию приоритет 1
    private var selectedColor: String = "blue" // По умолчанию цвет синий
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_task, container, false)

        // Найдите ваш RadioButton
        view.findViewById<RadioButton>(R.id.radioButtonBlue).buttonTintList = ColorStateList.valueOf(Color.rgb(24, 136, 255))
        view.findViewById<RadioButton>(R.id.radioButtonGreen).buttonTintList = ColorStateList.valueOf(Color.rgb(34, 133, 7))
        view.findViewById<RadioButton>(R.id.radioButtonYellow).buttonTintList = ColorStateList.valueOf(Color.rgb(237, 178, 0))
        view.findViewById<RadioButton>(R.id.radioButtonRed).buttonTintList = ColorStateList.valueOf(Color.rgb(225, 7, 7))

        // Инициализация TaskViewModel
        val repository = TaskRepository(TaskDatabase.getDatabase(requireContext()).taskDao())
        val viewModelFactory = TaskViewModelFactory(repository)
        taskViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[TaskViewModel::class.java]

        // Название и описание задачи
        val taskTitle = view.findViewById<EditText>(R.id.edit_text_title)
        val taskDescription = view.findViewById<EditText>(R.id.edit_text_disc)

        // Приоритет задачи через ImageButton
        val priorityButton1 = view.findViewById<ImageButton>(R.id.imageButtonPriority1)
        val priorityButton2 = view.findViewById<ImageButton>(R.id.imageButtonPriority2)
        val priorityButton3 = view.findViewById<ImageButton>(R.id.imageButtonPriority3)

        priorityButton1.setOnClickListener {
            updatePriority(1, priorityButton1, priorityButton2, priorityButton3)
        }
        priorityButton2.setOnClickListener {
            updatePriority(2, priorityButton1, priorityButton2, priorityButton3)
        }
        priorityButton3.setOnClickListener {
            updatePriority(3, priorityButton1, priorityButton2, priorityButton3)
        }

        // Выбор цвета через RadioGroup
        val radioGroupColors = view.findViewById<RadioGroup>(R.id.radioGroupColors)
        radioGroupColors.setOnCheckedChangeListener { _, checkedId ->
            selectedColor = when (checkedId) {
                R.id.radioButtonBlue -> "blue"
                R.id.radioButtonGreen -> "green"
                R.id.radioButtonYellow -> "yellow"
                R.id.radioButtonRed -> "red"
                else -> "blue"
            }
        }

        // Кнопка для создания задачи
        val createTaskButton = view.findViewById<Button>(R.id.createBtn)
        createTaskButton.setOnClickListener {
            val title = taskTitle.text.toString()
            val description = taskDescription.text.toString()

            val calendar = Calendar.getInstance() // Получаем текущую дату и время
            val time = calendar.timeInMillis // Получаем миллисекунды

            if (title.isNotBlank() && description.isNotBlank()) {
                // Создаем новую задачу
                val newTask = Task(
                    title = title,
                    description = description,
                    priority = selectedPriority,
                    color = selectedColor,
                    createdAt = time
                )

                // Сохраняем задачу через ViewModel (например)
                taskViewModel.addTask(newTask)

                selectedColor = "blue"
                selectedPriority = 1
                taskTitle.text = null
                taskDescription.text = null

                // Закрываем BottomSheet
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog
        val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        // Устанавливаем состояние в Expanded
        BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
    }

    // Обновление приоритета и соответствующих звездочек
    private fun updatePriority(priority: Int, button1: ImageButton, button2: ImageButton, button3: ImageButton) {
        selectedPriority = priority
        button1.setBackgroundResource(if (priority >= 1) R.drawable.star else R.drawable.star_outline)
        button2.setBackgroundResource(if (priority >= 2) R.drawable.star else R.drawable.star_outline)
        button3.setBackgroundResource(if (priority >= 3) R.drawable.star else R.drawable.star_outline)
    }
}