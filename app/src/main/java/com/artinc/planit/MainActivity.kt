package com.artinc.planit

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val createBtn = findViewById<Button>(R.id.createTask)
        val menuIconBtn = findViewById<ImageButton>(R.id.menuBtn)
        val gridIconBtn = findViewById<ImageButton>(R.id.gridBtn)
        val columnIconBtn = findViewById<ImageButton>(R.id.columnBtn)

        createBtn.setOnClickListener {
            Toast
                .makeText(this@MainActivity, "Создание новой задачи", Toast.LENGTH_LONG)
                .show()
        }

        menuIconBtn.setOnClickListener {
            Toast
                .makeText(this@MainActivity, "Открытие меню", Toast.LENGTH_LONG)
                .show()
        }

        gridIconBtn.setOnClickListener {
            Toast
                .makeText(this@MainActivity, "Изменение вида на сетку", Toast.LENGTH_LONG)
                .show()
        }

        columnIconBtn.setOnClickListener {
            Toast
                .makeText(this@MainActivity, "Изменение вида на колонку", Toast.LENGTH_LONG)
                .show()
        }
    }
}