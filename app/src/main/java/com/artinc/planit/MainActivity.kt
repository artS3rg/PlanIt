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

        val createBtn = findViewById<ImageButton>(R.id.createTask)
        val menuIconBtn = findViewById<ImageButton>(R.id.menuBtn)
        val settingsIconBtn = findViewById<ImageButton>(R.id.settingsBtn)

        createBtn.setOnClickListener {
            Toast
                .makeText(this@MainActivity, "Создание новой задачи", Toast.LENGTH_SHORT)
                .show()
        }

        menuIconBtn.setOnClickListener {
            Toast
                .makeText(this@MainActivity, "Открытие меню", Toast.LENGTH_SHORT)
                .show()
        }

        settingsIconBtn.setOnClickListener {
            Toast
                .makeText(this@MainActivity, "Найстройки", Toast.LENGTH_SHORT)
                .show()
        }
    }
}