# Detalle de proyecto (Android)

Desde Explorar, tocar una tarjeta abre Routes.ProjectDetail(id). El listado de prueba solo muestra proyectos publicados y públicos; el borrador de Aula Inteligente permanece en FakeProjectsRepository para futuros flujos del autor.

La pantalla contiene una portada ilustrada local por categoría, título, resumen, creador, descripción, tecnologías, fechas y pestaña de avances. El alcance mantiene una portada: no agrega campañas, pagos, galería ni correo privado del creador.

La leyenda de demostración indica que el apoyo y la solicitud de colaboración son locales. Se permite alternar el apoyo; la solicitud se confirma y pasa a pendiente. El autor no puede solicitar colaborar en su propio proyecto. El estado de demostración se mantiene en memoria del ViewModel por proyecto, y no se envía al backend ni se persiste al cerrar el proceso.

## Integración posterior

- ProjectsRepository.obtenerDetalle(id) es el punto de carga del detalle. Actualmente FakeProjectsRepository compone ProjectDetail a partir del Project existente y relaciones de ejemplo.
- ProjectDetail separa autor público, categoría, tecnologías, avances e interacciones del modelo base de la tabla. No se expone el correo del creador.
- Sustituir el binding de ProjectsRepository por la implementación de API; mapear GET /projects/{id} y GET /projects/{id}/updates. Mantener autorización en servidor.
- ProjectDetailViewModel concentra carga, error, reintento y callbacks de interacción. Al integrar, reemplazar las mutaciones de demostración por operaciones del repositorio para reacciones y solicitudes y gestionar errores/duplicados. Los callbacks actuales no simulan éxito si demo=false: los controles permanecen deshabilitados hasta conectarlos.
- ProjectCover usa una ilustración local porque las rutas de portada actuales son ficticias. Agregar un cargador de imágenes al disponer de URLs reales, preservando el fallback y las restricciones de visibilidad.
- No se modificó el backend ni se conectaron operaciones reales de proyectos.

## Verificación

Compilar con :app:assembleDebug y ejecutar :app:testDebugUnitTest. ProjectDetailScreenTest valida las interacciones locales, pestaña de avances, exclusión de solicitud propia y reintento cuando no existe el proyecto. La captura de la prueba se guarda en externalFilesDir/project-detail.png del dispositivo.
