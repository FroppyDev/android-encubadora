package com.fic.encubadorafic.model

data class ProjectUpdate(val id: Long, val title: String, val body: String, val date: String)
data class ProjectDetail(
    val project: Project,
    val authorName: String,
    val categoryName: String,
    val technologies: List<String> = emptyList(),
    val updates: List<ProjectUpdate> = emptyList(),
    val supports: Int = 0,
    val supported: Boolean = false,
    val collaborationPending: Boolean = false,
    val demo: Boolean = false,
)
