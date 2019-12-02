package com.example.krono

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.regions.Regions


const val EXTRA_MESSAGE = "com.example.krono.MESSAGE"

class MainActivity : AppCompatActivity() {

    private var mAWSAppSyncClient: AWSAppSyncClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var mAWSAppSyncClient = AWSAppSyncClient.builder().context(applicationContext).region(
            Regions.DEFAULT_REGION).awsConfiguration(AWSConfiguration(applicationContext)).build()
    }

    /*Called when the user taps the Search button*/
    fun searchActivity(view: View) {
        // Look up database based on activity_search field
        val activitySearch = findViewById<EditText>(R.id.activity_search)
        val message = activitySearch.text.toString()
        val intent = Intent(this, DisplayActivityActivity::class.java ).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)
    }
}
