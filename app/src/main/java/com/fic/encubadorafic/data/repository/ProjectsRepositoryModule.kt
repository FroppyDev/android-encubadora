package com.fic.encubadorafic.data.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProjectsRepositoryModule {
    @Binds
    abstract fun bindProjectsRepository(repository: FakeProjectsRepository): ProjectsRepository
}