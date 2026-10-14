package com.fic.encubadorafic.data.repository

import com.fic.encubadorafic.model.Project
import javax.inject.Inject

class FakeProjectsRepository @Inject constructor() : ProjectsRepository {

    override suspend fun obtenerProjectos(): List<Project> {

        return listOf<Project>(
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