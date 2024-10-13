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
        // Загружаем настройки перед установкой макета
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)

        // Устанавливаем тему до вызова setContentView
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val repository = TaskRepository(TaskDatabase.getDatabase(this).taskDao())
        val viewModelFactory = TaskViewModelFactory(repository)
        taskViewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        // Работа со списком
        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Инициализация TaskAdapter
        taskAdapter = TaskAdapter(emptyList(), taskViewModel) { task ->
            // Обработка нажатия на задачу
            openTaskDetails(task)
        }
        recyclerView.adapter = taskAdapter

        // Получение ViewModel
        // Наблюдение за изменениями списка задач
        taskViewModel.getTasksByDate(System.currentTimeMillis()).observe(this) { tasks ->
            taskAdapter.updateTasks(tasks)
            findViewById<LinearLayout>(R.id.start_text).isVisible = tasks.isEmpty()
        }
        // Загрузка всех дат (для боковой панели)
        taskViewModel.getAllTaskDates().observe(this) { dates ->
            // Здесь вы можете обновить боковую панель (например, с помощью NavigationView)
            updateSidePanel(dates)
        }
        //

        // Работа с меню
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        val menuIconBtn = findViewById<ImageButton>(R.id.menuBtn)

        // Обработка кликов по элементам меню
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_stat -> {
                    val statisticsIntent = Intent(this, StatisticsActivity::class.java)
                    startActivity(statisticsIntent)
                }
            }
            drawerLayout.closeDrawers()  // Закрытие меню после выбора
            true
        }
        //

        // Добавление новой задачи
        val addBtn = findViewById<ImageButton>(R.id.addTask)
        addBtn.setOnClickListener {
            if (bottomSheetNewTask == null) {
                bottomSheetNewTask = NewTaskFragment()
            }
            bottomSheetNewTask?.show(supportFragmentManager, bottomSheetNewTask?.tag)
        }
        //

        // Открытие меню
        menuIconBtn.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.openDrawer(navigationView)
            } else {
                drawerLayout.closeDrawer(navigationView)
            }
        }

        // Открытие настроек
        val settingsIconBtn = findViewById<ImageButton>(R.id.settingsBtn)
        settingsIconBtn.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }

    // Обработчик кнопки назад
    override fun onBackPressed() {
        // Проверяем, открыт ли BottomSheetDialogFragment
        if (bottomSheetNewTask?.isVisible == true) {
            bottomSheetNewTask?.dismiss() // Закрываем его
        } else {
            super.onBackPressed() // Иначе вызываем стандартное поведение
        }
    }

    // Открытие BottomSheet с полной информацией о задаче
    private fun openTaskDetails(task: Task) {
        val bottomSheet = TaskFragment.newInstance(task)
        bottomSheet.show(supportFragmentManager, "TaskDetails")
    }

    // Обновление боковой панели с датами
    private fun updateSidePanel(dates: List<String>) {
        // Получаем ссылку на NavigationView
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        val menu = navigationView.menu

        // Удаляем все предыдущие динамические элементы (если они были)
        val dynamicGroupId = 1 // Идентификатор группы для дат (не совпадает с id других элементов)
        menu.removeGroup(dynamicGroupId)

        // Добавляем новые элементы с датами в меню
        dates.forEach { date ->
            menu.add(dynamicGroupId, Menu.NONE, Menu.NONE, date)
                .setOnMenuItemClickListener {
                    // Обрабатываем клик по элементу даты
                    onDateSelected(date)
                    true
                }
        }
    }

    private fun onDateSelected(date: String) {
        // Реализуйте логику для загрузки задач, связанных с выбранной датой
        taskViewModel.getTasksByDate(convertDateToMillis(date)).observe(this) { tasks ->
            findViewById<LinearLayout>(R.id.start_text).isVisible = tasks.isEmpty()
            taskAdapter.updateTasks(tasks)
        }
    }

    private fun convertDateToMillis(dateString: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString) ?: throw IllegalArgumentException("Invalid date format")
        return date.time
    }
}