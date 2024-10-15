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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artinc.planit.data.TaskDatabase
import com.artinc.planit.data.TaskRepository
import com.artinc.planit.data.TaskViewModel
import com.artinc.planit.data.TaskViewModelFactory
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private var bottomSheetNewTask: NewTaskFragment? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Установка темы приложения
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
        taskViewModel.getTasksByDate(System.currentTimeMillis()).observe(this) { tasks ->
            taskAdapter.updateTasks(tasks)
            findViewById<LinearLayout>(R.id.start_text).isVisible = tasks.isEmpty()
        }

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
        dates.forEach { date ->
            menu.add(dynamicGroupId, Menu.NONE, Menu.NONE, date).setOnMenuItemClickListener {
                onDateSelected(date)
                true
            }
        }
    }

    private fun onDateSelected(date: String) {
        taskViewModel.getTasksByDate(convertDateToMillis(date)).observe(this) { tasks ->
            findViewById<LinearLayout>(R.id.start_text).isVisible = tasks.isEmpty()
            taskAdapter.updateTasks(tasks)
        }
    }

    private fun convertDateToMillis(dateString: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.parse(dateString)?.time ?: throw IllegalArgumentException("Invalid date format")
    }
}
