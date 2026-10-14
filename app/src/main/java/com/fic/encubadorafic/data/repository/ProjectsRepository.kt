package com.fic.encubadorafic.data.repository

import com.fic.encubadorafic.model.Project

interface ProjectsRepository {

    suspend fun obtenerProjectos(): List<Project>

}