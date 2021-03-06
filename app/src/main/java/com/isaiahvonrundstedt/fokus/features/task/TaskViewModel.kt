package com.isaiahvonrundstedt.fokus.features.task

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequest
import com.isaiahvonrundstedt.fokus.database.repository.TaskRepository
import com.isaiahvonrundstedt.fokus.features.attachments.Attachment
import com.isaiahvonrundstedt.fokus.features.core.work.task.TaskNotificationWorker
import com.isaiahvonrundstedt.fokus.features.shared.abstracts.BaseViewModel
import com.isaiahvonrundstedt.fokus.features.shared.abstracts.BaseWorker
import com.isaiahvonrundstedt.fokus.features.widget.task.TaskWidgetProvider
import kotlinx.coroutines.launch

class TaskViewModel(private var app: Application) : BaseViewModel(app) {

    private var repository = TaskRepository.getInstance(app)
    private var items: LiveData<List<TaskResource>>? = repository.fetch()

    fun fetch(): LiveData<List<TaskResource>>? = items

    fun insert(task: Task, attachmentList: List<Attachment> = emptyList()) = viewModelScope.launch {
        repository.insert(task, attachmentList)

        // Check if notifications for tasks are turned on and check if
        // the task is not finished, then schedule a notification
        if (preferenceManager.taskReminder && !task.isFinished && task.isDueDateInFuture()) {

            val data = BaseWorker.convertTaskToData(task)
            val request = OneTimeWorkRequest.Builder(TaskNotificationWorker::class.java)
                .setInputData(data)
                .build()
            workManager.enqueue(request)
        }

        TaskWidgetProvider.triggerRefresh(app)
    }

    fun remove(task: Task) = viewModelScope.launch {
        repository.remove(task)

        if (task.isImportant)
            notificationManager?.cancel(task.taskID, BaseWorker.NOTIFICATION_ID_TASK)

        workManager.cancelUniqueWork(task.taskID)

        TaskWidgetProvider.triggerRefresh(app)
    }

    fun update(task: Task, attachmentList: List<Attachment> = emptyList()) = viewModelScope.launch {
        repository.update(task, attachmentList)

        // If we have a persistent notification,
        // we should dismiss it when the user updates
        // the task to finish
        if (task.isFinished || !task.isImportant || task.dueDate?.isBeforeNow == true)
            notificationManager?.cancel(task.taskID, BaseWorker.NOTIFICATION_ID_TASK)

        // Check if notifications for tasks is turned on and if the task
        // is not finished then reschedule the notification from
        // WorkManager
        if (preferenceManager.taskReminder && !task.isFinished && task.isDueDateInFuture()) {
            workManager.cancelUniqueWork(task.taskID)
            val data = BaseWorker.convertTaskToData(task)
            val request = OneTimeWorkRequest.Builder(TaskNotificationWorker::class.java)
                .setInputData(data)
                .build()
            workManager.enqueue(request)
        }

        TaskWidgetProvider.triggerRefresh(app)
    }
}