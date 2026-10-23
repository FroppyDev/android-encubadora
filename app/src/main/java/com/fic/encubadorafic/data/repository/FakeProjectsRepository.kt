package com.fic.encubadorafic.data.repository

import com.fic.encubadorafic.model.Project
import javax.inject.Inject

@javax.inject.Singleton
class FakeProjectsRepository @Inject constructor() : ProjectsRepository {

    private val created = linkedMapOf<Long, com.fic.encubadorafic.model.ProjectDetail>()
    private var nextId = 100L

    override suspend fun crearProyecto(
        input: com.fic.encubadorafic.model.NewProject, published: Boolean,
        authorId: Long, authorName: String,
    ): com.fic.encubadorafic.model.ProjectDetail {

        require(input.errors().isEmpty()) { "Revisa los datos del proyecto." }
        require(authorId > 0 && authorName.isNotBlank()) { "Inicia sesión para crear el proyecto." }

        return synchronized(created) {
            val id = nextId++
            val date = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.ROOT)
                .apply {
                    timeZone = java.util.TimeZone.getTimeZone("UTC")
                }.format(java.util.Date())
            val slug = java.text.Normalizer.normalize(
                input.title.trim().lowercase(java.util.Locale.ROOT),
                java.text.Normalizer.Form.NFD
            )
                .replace(Regex("\\p{M}+"), "").replace(Regex("[^a-z0-9]+"), "-").trim('-')
                .ifBlank { "proyecto" } + "-$id"
            val project = Project(
                id,
                authorId,
                requireNotNull(input.categoryId),
                input.title.trim(),
                slug,
                input.summary.trim(),
                input.description.trim(),
                null,
                if (published) "published" else "draft",
                if (published) "public" else "private",
                date,
                date
            )
            com.fic.encubadorafic.model.ProjectDetail(
                project, authorName,
                input.categoryName.ifBlank { "Categoría no disponible" },
                technologies = com.fic.encubadorafic.model.DemoProjectOptions.technologies.filter { it.id in input.technologyIds }
                    .map { it.name },
                demo = true
            ).also { created[id] = it }
        }
    }

    override suspend fun obtenerDetalle(id: Long): com.fic.encubadorafic.model.ProjectDetail? {
        synchronized(created) { created[id] }?.let { return it }
        val project = obtenerProjectos().firstOrNull { it.id == id } ?: return null
        val category = when (project.category_id) {
            2L -> "Sustentabilidad"
            3L -> "Educación"
            4L -> "Internet de las cosas"
            else -> "Desarrollo de software"
        }
        return com.fic.encubadorafic.model.ProjectDetail(
            project = project,
            authorName = when (project.user_id) {
                1L -> "Ana López"; 2L -> "Diego Ramírez"; 3L -> "Sofía García"; else -> "Luis Hernández"
            },
            categoryName = category,
            technologies = when (project.id) {
                2L -> listOf("Kotlin", "Mapas"); 4L -> listOf(
                    "Arduino",
                    "Sensores"
                ); else -> listOf("Kotlin", "Laravel", "PostgreSQL")
            },
            supports = (project.id * 7 + 5).toInt(),
            updates = if (project.id == 3L) emptyList() else listOf(
                com.fic.encubadorafic.model.ProjectUpdate(
                    1,
                    "Primer prototipo",
                    "Definimos las pantallas principales y estamos reuniendo comentarios de estudiantes para mejorar la propuesta.",
                    "2026-10-12"
                ),
                com.fic.encubadorafic.model.ProjectUpdate(
                    2,
                    "Así comenzó la idea",
                    "Identificamos la necesidad y organizamos las primeras actividades del proyecto.",
                    "2026-10-08"
                )
            ),
            demo = true,
        )
    }

    override suspend fun obtenerProjectos(): List<Project> {

        return synchronized(created) {
            created.values.map { it.project }.reversed()
        } + listOf<Project>(
            Project(
                id = 1L,
                user_id = 1L,
                category_id = 1L,
                title = "FIC Conecta",
                slug = "fic-conecta",
                summary = "Conecta estudiantes que buscan colaboradores para sus proyectos.",
                description = "Permite publicar ideas, indicar las habilidades requeridas " +
                        "y recibir solicitudes de estudiantes interesados para formar equipos " +
                        "multidisciplinarios dentro de la facultad.",
                cover_image = "projects/fic-conecta.jpg",
                status = "published",
                visibility = "public",
                created_at = "2026-09-21T10:00:00Z",
                updated_at = "2026-10-01T14:30:00Z"
            ),
            Project(
                id = 2L,
                user_id = 2L,
                category_id = 2L,
                title = "EcoCampus",
                slug = "ecocampus",
                summary = "Fomenta el reciclaje y localiza puntos de recolección en el campus.",
                description = "Los estudiantes pueden consultar puntos de reciclaje, " +
                        "aprender a separar residuos e inscribirse en jornadas de limpieza. " +
                        "Incluye un registro de materiales recolectados por campaña.",
                cover_image = "projects/ecocampus.jpg",
                status = "published",
                visibility = "public",
                created_at = "2026-09-23T16:00:00Z",
                updated_at = "2026-10-04T11:15:00Z"
            ),
            Project(
                id = 3L,
                user_id = 3L,
                category_id = 3L,
                title = "Tutorías entre Compañeros",
                slug = "tutorias-entre-companeros",
                summary = "Encuentra asesorías académicas impartidas por otros estudiantes.",
                description = "Conecta a estudiantes que necesitan apoyo en programación, " +
                        "matemáticas y bases de datos con compañeros que dominan esas materias. " +
                        "Ofrece búsqueda por asignatura, horarios y solicitudes de asesoría.",
                cover_image = "projects/tutorias-entre-companeros.jpg",
                status = "published",
                visibility = "public",
                created_at = "2026-09-28T09:00:00Z",
                updated_at = "2026-10-06T17:45:00Z"
            ),
            Project(
                id = 4L,
                user_id = 1L,
                category_id = 4L,
                title = "Aula Inteligente",
                slug = "aula-inteligente",
                summary = "Monitorea las condiciones ambientales de los salones con sensores.",
                description = "Utiliza dispositivos IoT para medir temperatura, humedad " +
                        "e iluminación. Los datos permiten identificar condiciones que afectan " +
                        "la comodidad y proponer mejoras en el consumo energético.",
                cover_image = "projects/aula-inteligente.jpg",
                status = "draft",
                visibility = "private",
                created_at = "2026-10-05T12:00:00Z",
                updated_at = "2026-10-10T10:20:00Z"
            ),
            Project(
                id = 5L,
                user_id = 4L,
                category_id = 1L,
                title = "Turno Fácil",
                slug = "turno-facil",
                summary = "Reduce las filas en servicios escolares mediante turnos digitales.",
                description = "Permite solicitar un turno desde el teléfono y consultar " +
                        "el avance de la fila. El personal puede llamar al siguiente estudiante, " +
                        "organizar la atención por trámite y consultar tiempos de espera.",
                cover_image = "projects/turno-facil.jpg",
                status = "published",
                visibility = "public",
                created_at = "2026-10-07T08:30:00Z",
                updated_at = "2026-10-12T15:00:00Z"
            )
        )
    }
}