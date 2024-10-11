package com.artinc.planit

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artinc.planit.data.TaskViewModel
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    private var bottomSheet: NewTaskFragment? = null

    //private lateinit var taskViewModel: TaskViewModel
    //private lateinit var recyclerView: RecyclerView
    //private lateinit var adapter: TasksAdapter

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

        // Работа со списком
//        recyclerView = findViewById(R.id.recyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
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
            if (bottomSheet == null) {
                bottomSheet = NewTaskFragment()
            }
            bottomSheet?.show(supportFragmentManager, bottomSheet?.tag)
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
        if (bottomSheet?.isVisible == true) {
            bottomSheet?.dismiss() // Закрываем его
        } else {
            super.onBackPressed() // Иначе вызываем стандартное поведение
        }
    }
}