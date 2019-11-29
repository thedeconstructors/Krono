package com.example.krono

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class DisplayActivityActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_activity)

        val message = intent.getStringExtra(EXTRA_MESSAGE)

        val textView = findViewById<TextView>(R.id.DatabaseView).apply {
            text = message
        }
    }
}
