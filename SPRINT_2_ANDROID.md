# Android: alcance hasta sprint 2

Implementación y verificación local: 14 de octubre de 2026. Referencia: HU01–HU06 de `docs/HISTORIAS_Y_SPRINTS.md` del backend. Las fechas del calendario del plan no representan fechas reales de entrega.

## Entregado en Android

| Historia | Implementación |
|---|---|
| HU01 Registro | Nombre, correo institucional, contraseña y confirmación; validaciones por campo; registro sin simular una sesión. |
| HU02 Verificación | Pantalla pendiente, reenvío y actualización del usuario; el contenido de la app requiere sesión verificada. |
| HU03 Sesión | Login real, token cifrado con Keystore, restauración y cierre con revocación; 401 devuelve al login. |
| HU04 Recuperación | Login → Olvidé mi contraseña → Abrir formulario de recuperación. Abre `/forgot-password` del mismo servidor en el navegador. Regresar al login después del cambio. |
| HU05 Perfil | Menú → Mi perfil. Consulta GET `/user`, edita solo nombre y biografía mediante PATCH `/user`, muestra validaciones y confirmación; permite borrar biografía y confirma descartar cambios sin guardar. El nombre actualizado aparece en el menú. |
| HU06 Categorías (cliente Android) | Menú → Categorías. Consulta todas las páginas de GET `/categories`, carga/error/reintento/vacío y descarta categorías inactivas. No contiene administración. |

El catálogo de proyectos sigue usando el repositorio de ejemplos del estudiante. Publicación, búsqueda real y demás historias de sprints posteriores quedan fuera de esta entrega.

## Contratos y dependencias para el equipo

- Perfil: nombre requerido, máximo 100 caracteres; biografía nullable, máximo 2000. La app envía exclusivamente `name` y `bio`. Correo, permisos y verificación son de solo lectura.
- Categorías: se interpreta `is_active` (booleano, 0/1) y se admite `active` como alias. La API actual no devuelve ese campo: temporalmente, las categorías sin indicador se consideran activas. Confirmar `is_active` con backend antes de integrar la desactivación. Las selecciones nuevas de proyectos, previstas en sprint 3, deberán reutilizar la lista filtrada.
- HU06 completa requiere administración web, estado activo/inactivo en backend y bloqueo de nuevas asignaciones a categorías inactivas en servidor. Android no puede garantizar esas restricciones por sí solo.
- HU02 completa requiere proteger también las consultas comunitarias del backend. Actualmente sus rutas de consulta son públicas aunque Android exige verificación para navegar al contenido.
- Recuperación usa el flujo web existente. El equipo web/backend debe validar entrega de correo, caducidad/reutilización del enlace y revocación de tokens tras restablecer. Esta entrega comprobó acceso al formulario, no una nueva implementación del restablecimiento.
- Staging HTTPS, correo de pruebas compartido y aceptación entre web/Android siguen siendo requisitos de cierre del equipo; la validación de esta entrega es local.

## Ejecutar

1. Backend: `docker compose up -d`.
2. Teléfono por USB: `adb reverse tcp:8000 tcp:8000` tras reconectarlo.
3. En `local.properties` (ignorado por Git): `api.baseUrl=http://127.0.0.1:8000/api/v1/` para USB, o `http://10.0.2.2:8000/api/v1/` para emulador. `-PAPI_BASE_URL` tiene prioridad. Release requiere configurar una URL HTTPS.
4. Mailpit en computadora: `http://localhost:8025`. Abrir allí los enlaces de correo locales.
5. Compilar con el JDK configurado por el proyecto (se validó usando el JDK 25 de Android Studio).

## Evidencia de validación

- `:app:assembleDebug`: correcto; APK instalado en Samsung SM-A556E.
- `:app:testDebugUnitTest`: 15 pruebas, cero fallos (7 de autenticación, 7 de sprint 2 y 1 ejemplo existente).
- `AuthIntegrationTest`: 10 pruebas instrumentadas aprobadas; HTTP, PATCH con campos permitidos, biografía nula, categorías, errores y Keystore.
- `SprintTwoScreensTest`: 4 pruebas Compose aprobadas; guardado, descarte, categorías vacías y navegación de recuperación.
- `SprintTwoApiSmokeTest`: 1 prueba aprobada contra Laravel/Mailpit; registro sintético, verificación, persistencia y limpieza de biografía, consulta de categorías y formulario de recuperación HTTP 200. El token de prueba se revoca al terminar y no usa el almacenamiento de sesión de la app. Permanece la cuenta sintética en la base local.
- El flujo global `AuthFlowSmokeTest` se interrumpió porque el dispositivo abrió una sesión existente y la prueba requiere comenzar sin sesión. No se cuenta como aprobado en esta entrega; no se borraron los datos del usuario. Para repetirlo, cerrar sesión voluntariamente primero.

Las pruebas instrumentadas se ejecutaron con APKs compilados e instalados y `adb shell am instrument`, evitando la dependencia UTP que anteriormente no podía descargarse por certificados. No se desactivó TLS.

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest --offline
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w -r -e class com.fic.encubadorafic.data.auth.AuthIntegrationTest,com.fic.encubadorafic.SprintTwoScreensTest com.fic.encubadorafic.test/androidx.test.runner.AndroidJUnitRunner

# Opcional: requiere backend local y Mailpit; crea una cuenta sintética.
adb reverse tcp:8025 tcp:8025
adb shell am instrument -w -r -e class com.fic.encubadorafic.SprintTwoApiSmokeTest -e runSprintSmoke true com.fic.encubadorafic.test/androidx.test.runner.AndroidJUnitRunner
```

## Aceptación manual restante del equipo

En staging: recuperación completa con enlace válido/vencido/reutilizado; volver a ingresar con la contraseña nueva y comprobar revocación en otro dispositivo; editar perfil desde web y refrescar Android; desactivar categoría desde el panel web y comprobar su exclusión y el rechazo del servidor al asignarla. Repetir con falta de red y validar los mensajes de reintento.
