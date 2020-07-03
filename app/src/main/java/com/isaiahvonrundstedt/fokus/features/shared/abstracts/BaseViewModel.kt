package com.isaiahvonrundstedt.fokus.features.shared.abstracts

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.work.WorkManager

abstract class BaseViewModel(private val app: Application): AndroidViewModel(app) {

    protected val notificationManager by lazy {
        app.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    protected val workManager by lazy {
        WorkManager.getInstance(app)
    }
}