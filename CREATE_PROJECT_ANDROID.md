# Crear proyecto — demostración Android

En Explorar pulsa **+ Crear proyecto**. Completa título, resumen, categoría y descripción; selecciona tecnologías si lo deseas. La portada ilustrada cambia según la categoría. Los límites de los campos coinciden con el contrato actual: 180, 500 y 20 000 caracteres.

**Publicar proyecto** solicita confirmación, abre el detalle y agrega el proyecto al catálogo de prueba. **Guardar borrador** abre su detalle y lo excluye de Explorar. El formulario confirma el descarte al salir con cambios y conserva los campos durante rotación. Mientras guarda, bloquea nuevas acciones.

Los proyectos se guardan solo en memoria compartida por FakeProjectsRepository, no en el servidor ni en almacenamiento permanente. Desaparecen al terminar el proceso. Esta entrega no incluye edición o bandeja de borradores: el borrador recién creado se consulta en el detalle al guardar. El autor se toma de la sesión y el usuario no escribe IDs, slug, permisos ni fechas.

## Integración futura

- NewProject contiene los campos editables y sus validaciones. DemoProjectOptions concentra los IDs y nombres ficticios de categorías y tecnologías: reemplazarlos por los catálogos de API al integrar.
- ProjectsRepository.crearProyecto es el punto a sustituir por POST /projects. En API, el servidor determina el propietario, slug y fechas; no enviar authorId ni authorName como campos asignables.
- FakeProjectsRepository es singleton para compartir los datos entre creación, catálogo y detalle. El detalle nuevo usa las tecnologías seleccionadas, autor de sesión y cero interacciones/avances.
- CreateProjectViewModel gestiona guardado, errores y prevención de doble envío. CreateProjectScreen recibe estado y callbacks, sin HTTP en la interfaz.
- No hay carga de imágenes todavía; se utiliza ProjectCover con ilustración local. No se modificó backend.

## Pruebas

CreateProjectRepositoryTest cubre persistencia en memoria, correspondencia con detalle, IDs/slugs distintos, exclusión de borradores y validación. CreateProjectScreenTest cubre formulario vacío, confirmación de publicación, campos seleccionados y descarte de cambios.
