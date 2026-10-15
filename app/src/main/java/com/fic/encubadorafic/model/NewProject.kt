package com.fic.encubadorafic.model

data class ProjectOption(val id: Long, val name: String)
object DemoProjectOptions {
    val categories = listOf(
        ProjectOption(1, "Arte"),
        ProjectOption(2, "Multimedia"),
        ProjectOption(3, "Educacion"),
        ProjectOption(4, "Entretenimiento"),
        ProjectOption(5, "Videojuegos")
    )
    val technologies = listOf(
        ProjectOption(1, "Kotlin"),
        ProjectOption(2, "Laravel"),
        ProjectOption(3, "PostgreSQL"),
        ProjectOption(4, "Arduino"),
        ProjectOption(5, "Python"),
        ProjectOption(6, "React")
    )
}

data class NewProject(
    val title: String, val summary: String, val description: String,
    val categoryId: Long?, val technologyIds: List<Long> = emptyList(),
) {
    fun errors(): Map<String, String> = buildMap {
        if (title.trim().isEmpty() || title.trim().length > 180) put(
            "title",
            "Escribe un título de 1 a 180 caracteres."
        )
        if (summary.trim().isEmpty() || summary.trim().length > 500) put(
            "summary",
            "Escribe un resumen de 1 a 500 caracteres."
        )
        if (description.trim().isEmpty() || description.trim().length > 20000) put(
            "description",
            "Describe tu proyecto (máximo 20 000 caracteres)."
        )
        if (DemoProjectOptions.categories.none { it.id == categoryId }) put(
            "category",
            "Selecciona una categoría."
        )
        if (technologyIds.distinct().size != technologyIds.size || technologyIds.any { id -> DemoProjectOptions.technologies.none { it.id == id } }) put(
            "technologies",
            "Selecciona tecnologías válidas."
        )
    }
}
