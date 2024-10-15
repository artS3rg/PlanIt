package com.artinc.planit

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artinc.planit.data.TaskDatabase
import com.artinc.planit.data.TaskRepository
import com.artinc.planit.data.TaskViewModel
import com.artinc.planit.data.TaskViewModelFactory
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private var bottomSheetNewTask: NewTaskFragment? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskViewModel: TaskViewModel
    private var currentDateInMillis: Long = System.currentTimeMillis()
    private var currentTaskObserver: Observer<List<Task>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppTheme()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setupWindowInsets()
        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupNavigationDrawer()
        setupAddTaskButton()
    }

    private fun setAppTheme() {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViewModel() {
        val repository = TaskRepository(TaskDatabase.getDatabase(this).taskDao())
        val viewModelFactory = TaskViewModelFactory(repository)
        taskViewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(emptyList(), taskViewModel) { task -> openTaskDetails(task) }
        recyclerView.adapter = taskAdapter
    }

    private fun setupObservers() {
        observeTasks(currentDateInMillis)
        taskViewModel.getAllTaskDates().observe(this) { dates -> updateSidePanel(dates) }
    }

    private fun setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_stat -> startActivity(Intent(this, StatisticsActivity::class.java))
                R.id.nav_sett -> startActivity(Intent(this, SettingsActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        }

        findViewById<ImageButton>(R.id.menuBtn).setOnClickListener {
            if (!drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.openDrawer(navigationView)
            } else {
                drawerLayout.closeDrawer(navigationView)
            }
        }
    }

    private fun setupAddTaskButton() {
        findViewById<ImageButton>(R.id.addTask).setOnClickListener {
            if (bottomSheetNewTask == null) {
                bottomSheetNewTask = NewTaskFragment()
            }
            bottomSheetNewTask?.show(supportFragmentManager, bottomSheetNewTask?.tag)
        }
    }

    override fun onBackPressed() {
        if (bottomSheetNewTask?.isVisible == true) {
            bottomSheetNewTask?.dismiss()
        } else {
            super.onBackPressed()
        }
    }

    private fun openTaskDetails(task: Task) {
        val bottomSheet = TaskFragment.newInstance(task)
        bottomSheet.show(supportFragmentManager, "TaskDetails")
    }

    private fun updateSidePanel(dates: List<String>) {
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        val menu = navigationView.menu
        val dynamicGroupId = 1

        menu.removeGroup(dynamicGroupId)

        val inputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        dates.forEach { date ->
            // Парсим дату в формате dd-MM-yyyy
            val parsedDate = inputDateFormat.parse(date)
            calendar.time = parsedDate

            // Получаем название месяца из strings.xml на основе локализации
            val monthNumber = calendar.get(Calendar.MONTH) // от 0 до 11
            val monthName = when (monthNumber) {
                0 -> getString(R.string.month_january)
                1 -> getString(R.string.month_february)
                2 -> getString(R.string.month_march)
                3 -> getString(R.string.month_april)
                4 -> getString(R.string.month_may)
                5 -> getString(R.string.month_june)
                6 -> getString(R.string.month_july)
                7 -> getString(R.string.month_august)
                8 -> getString(R.string.month_september)
                9 -> getString(R.string.month_october)
                10 -> getString(R.string.month_november)
                11 -> getString(R.string.month_december)
                else -> ""
            }

            // Получаем день месяца
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val year = calendar.get(Calendar.YEAR)
            // Форматируем строку как "день месяц"
            val formattedDate = "$dayOfMonth $monthName $year"

            // Добавляем отформатированную дату в боковую панель
            menu.add(dynamicGroupId, Menu.NONE, Menu.NONE, formattedDate).setOnMenuItemClickListener {
                onDateSelected(date)  // Передаем исходную дату для логики
                true
            }
        }
    }

    private fun onDateSelected(date: String) {
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        drawerLayout.closeDrawer(navigationView)
        val dateInMillis = convertDateToMillis(date)
        if (dateInMillis != currentDateInMillis) {
            observeTasks(dateInMillis) // Наблюдаем за задачами для выбранной даты
        }
    }

    private fun convertDateToMillis(dateString: String): Long {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.parse(dateString)?.time ?: throw IllegalArgumentException("Invalid date format")
    }

    private fun observeTasks(dateInMillis: Long) {

        currentTaskObserver?.let {
            taskViewModel.getTasksByDate(currentDateInMillis).removeObserver(it)
        }

        currentDateInMillis = dateInMillis

        val observer = Observer<List<Task>> { tasks ->
            taskAdapter.updateTasks(tasks)
            findViewById<LinearLayout>(R.id.start_text).isVisible = tasks.isEmpty()
        }

        currentTaskObserver = observer
        taskViewModel.getTasksByDate(dateInMillis).observe(this, observer)
    }
}

