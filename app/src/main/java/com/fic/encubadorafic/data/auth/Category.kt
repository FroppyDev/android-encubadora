package com.fic.encubadorafic.data.auth

data class Category(val id: Long, val name: String, val description: String?, val active: Boolean)
data class CategoryPage(val items: List<Category>, val lastPage: Int)
