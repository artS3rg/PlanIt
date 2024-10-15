package com.artinc.planit

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.artinc.planit.data.TaskDatabase
import com.artinc.planit.data.TaskRepository
import com.artinc.planit.data.TaskViewModel
import com.artinc.planit.data.TaskViewModelFactory
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class StatisticsActivity : AppCompatActivity() {
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_statistics)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val repository = TaskRepository(TaskDatabase.getDatabase(this).taskDao())
        val viewModelFactory = TaskViewModelFactory(repository)
        taskViewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        val colorChart = findViewById<PieChart>(R.id.colors_piechart)
        taskViewModel.getTaskCountByColor().observe(this) { list ->
            // Создаем карту для связи строки цвета с ресурсами цветов
            val colorMap = mapOf(
                "blue" to Pair(getString(R.string.blue_btn), getColor(R.color.blue)),
                "green" to Pair(getString(R.string.green_btn), getColor(R.color.green)),
                "yellow" to Pair(getString(R.string.yellow_btn), getColor(R.color.yellow)),
                "red" to Pair(getString(R.string.red_btn), getColor(R.color.red))
            )

            // Итерируемся по списку ColorCount и вставляем в диаграмму
            list.forEach { colorCount ->
                val (label, color) = colorMap[colorCount.color] ?: return@forEach // Пропускаем, если цвет не найден
                colorChart.addPieSlice(
                    PieModel(
                        label, colorCount.count.toFloat(), color
                    )
                )
            }
        }
        colorChart.startAnimation()

        val taskChart = findViewById<PieChart>(R.id.tasks_piechart)
        taskViewModel.getCompletedTaskCount().observe(this) { count ->
            if (count != 0) {
                taskChart.addPieSlice(
                    PieModel(
                        getString(R.string.completed_text), count.toFloat(),
                        getColor(R.color.purple)
                    )
                )
            }
        }
        taskViewModel.getIncompleteTaskCount().observe(this) { count ->
            if (count != 0) {
                taskChart.addPieSlice(
                    PieModel(
                        getString(R.string.unfulfilled_text), count.toFloat(),
                        getColor(R.color.orange)
                    )
                )
            }
        }
        taskChart.startAnimation()

        taskViewModel.getBusiestDay().observe(this) { timestamp ->
            timestamp?.let {
                // Инициализируем объект Calendar и устанавливаем время
                val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }

                // Получаем день, месяц и год
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val monthNumber = calendar.get(Calendar.MONTH) // от 0 до 11
                val year = calendar.get(Calendar.YEAR)

                // Получаем название месяца из strings.xml на основе локализации
                val monthName = when (monthNumber) {
                    Calendar.JANUARY -> getString(R.string.month_january)
                    Calendar.FEBRUARY -> getString(R.string.month_february)
                    Calendar.MARCH -> getString(R.string.month_march)
                    Calendar.APRIL -> getString(R.string.month_april)
                    Calendar.MAY -> getString(R.string.month_may)
                    Calendar.JUNE -> getString(R.string.month_june)
                    Calendar.JULY -> getString(R.string.month_july)
                    Calendar.AUGUST -> getString(R.string.month_august)
                    Calendar.SEPTEMBER -> getString(R.string.month_september)
                    Calendar.OCTOBER -> getString(R.string.month_october)
                    Calendar.NOVEMBER -> getString(R.string.month_november)
                    Calendar.DECEMBER -> getString(R.string.month_december)
                    else -> ""
                }

                // Форматируем строку как "день месяц год"
                val formattedDate = "$dayOfMonth $monthName $year"

                // Устанавливаем отформатированную дату в TextView
                findViewById<TextView>(R.id.highestTaskDay).text = formattedDate
            }
        }

        taskViewModel.getFirstPriorCount().observe(this) { count ->
            if (count != null) {
                findViewById<TextView>(R.id.first_prior).text = count.toString()
            }
        }

        taskViewModel.getSecondPriorCount().observe(this) { count ->
            if (count != null) {
                findViewById<TextView>(R.id.second_prior).text = count.toString()
            }
        }

        taskViewModel.getThirdPriorCount().observe(this) { count ->
            if (count != null) {
                findViewById<TextView>(R.id.third_prior).text = count.toString()
            }
        }
    }
}