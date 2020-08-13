package com.chibufirst.scampassesment

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openStatsActivity(view: View) {
        startActivity(Intent(this, StatsActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun createPhoneIntent(view: View) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:080097000010")
        startActivity(intent)
    }

    fun createMessageIntent(view: View) {
        val messageIntent = Intent(Intent.ACTION_SENDTO)
        messageIntent.type = "vnd.android-dir/mms-sms"
        messageIntent.data = Uri.parse("smsto" +
                ":" + Uri.encode("080097000010"))
        startActivity(messageIntent)
    }
}
