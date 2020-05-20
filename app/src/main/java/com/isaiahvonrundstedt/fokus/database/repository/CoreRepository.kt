package com.isaiahvonrundstedt.fokus.database.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.isaiahvonrundstedt.fokus.database.AppDatabase
import com.isaiahvonrundstedt.fokus.features.attachments.Attachment
import com.isaiahvonrundstedt.fokus.features.core.Core
import com.isaiahvonrundstedt.fokus.features.task.Task
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CoreRepository (app: Application) {

    private var database = AppDatabase.getInstance(app)
    private var cores = database?.cores()
    private var tasks = database?.tasks()
    private var attachments = database?.attachments()

    fun fetch(): LiveData<List<Core>>? = cores?.fetch()

    fun fetchArchived(): LiveData<List<Core>>? = cores?.fetchArchived()

    fun search(query: String, onSearch:(List<Core>) -> Unit) = GlobalScope.launch {
        onSearch(cores?.search("%$query%") ?: emptyList())
    }

    suspend fun clearArchived() { tasks?.clearArchived() }

    suspend fun insert(task: Task, attachmentList: List<Attachment> = emptyList()) {
        tasks?.insert(task)
        if (attachmentList.isNotEmpty())
            attachmentList.forEach { attachments?.insert(it) }
    }

    suspend fun remove(task: Task) {
        tasks?.remove(task)
    }

    suspend fun update(task: Task, attachmentList: List<Attachment> = emptyList()) {
        tasks?.update(task)
        if (attachmentList.isNotEmpty()) {
            attachments?.removeUsingTaskID(task.taskID)
            attachmentList.forEach { attachments?.insert(it) }
        }
    }
}