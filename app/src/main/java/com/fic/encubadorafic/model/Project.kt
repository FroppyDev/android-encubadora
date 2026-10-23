package com.fic.encubadorafic.model

data class Project(
    val id: Long,
    val user_id: Long,
    val category_id: Long,
    val title: String,
    val slug: String,
    val summary: String,
    val description: String,
    val cover_image: String?,
    val status: String,
    val visibility: String,
    val created_at: String,
    val updated_at: String,
    val deleted_at: String? = null,
    val reactionsCount: Int = 0
)