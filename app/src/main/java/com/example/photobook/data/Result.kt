package com.example.photobook.data

import com.google.android.gms.tasks.Task

/**
 * Result - Result of remotes tasks.
 *
 * @id: Id of the document that has been used
 * @task: A google task that will give idea of
 * result of the task.
 */
data class Result(
    var id: String,
    var task: Task<Void>
)
