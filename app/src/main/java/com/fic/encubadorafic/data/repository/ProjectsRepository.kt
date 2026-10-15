package com.fic.encubadorafic.data.repository

import com.fic.encubadorafic.model.Project

interface ProjectsRepository {

    suspend fun crearProyecto(input: com.fic.encubadorafic.model.NewProject, published: Boolean,
        authorId: Long, authorName: String): com.fic.encubadorafic.model.ProjectDetail

    suspend fun obtenerProjectos(): List<Project>

    suspend fun obtenerDetalle(id: Long): com.fic.encubadorafic.model.ProjectDetail?


}