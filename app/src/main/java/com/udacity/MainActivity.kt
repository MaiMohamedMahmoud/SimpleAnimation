package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.notification.NotificationHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var nothingSelectedText: String
    lateinit var downloadManager: DownloadManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        nothingSelectedText = getString(R.string.txt_notSelected);

        custom_button.setOnClickListener {
            checkSelectedRadioButton()
        }

    }

    private fun checkSelectedRadioButton() {
        //call download fun with the specified url and title
        if (radio_group.checkedRadioButtonId == -1) {
            //show toast message.
            showToastMessage()
        } else {
            when (radio_group.checkedRadioButtonId) {
                radio_glide.id -> download(DownloadingLinks.GLIDE.title, DownloadingLinks.GLIDE.url)
                radio_loadApp.id -> download(
                    DownloadingLinks.LoadApp.title,
                    DownloadingLinks.LoadApp.url
                )
                radio_retrofit.id -> download(
                    DownloadingLinks.RETROFIT.title,
                    DownloadingLinks.RETROFIT.url
                )
            }
        }
    }

    private fun showToastMessage() {
        Toast.makeText(this, "$nothingSelectedText", Toast.LENGTH_LONG).show()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID == id) {

                val query = DownloadManager.Query()
                query.setFilterById(id);
                val cursor: Cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status: Int =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val downloadTitle =
                        cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                    val downloadDescription =
                        cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))

                    val isSuccess = status == DownloadManager.STATUS_SUCCESSFUL
                    Toast.makeText(this@MainActivity, downloadTitle, Toast.LENGTH_LONG).show()
                    NotificationHelper.createNotificationChannel(
                        context,
                        downloadTitle,
                        isSuccess, downloadDescription
                    )

                }
                cursor.close()

            }

        }
    }

    private fun download(title: Int, url: String) {
        custom_button.downloadStartLoading()

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(title))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        const val TITLE = "TITLE"
        const val SUCCESS = "success"
        const val DESCRIPTION = "description"
    }

}
