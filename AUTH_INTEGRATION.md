# Login y registro con Laravel

La API se consume mediante HttpURLConnection fuera del hilo principal. AuthViewModel conserva el estado durante rotaciones; AuthRepository centraliza la sesión. Se conserva el diseño de las pantallas existentes.

## Desarrollo

1. Levantar el backend con `docker compose up -d` desde su repositorio (instalar dependencias y migrar según su README si es un entorno nuevo).
2. Compilar Android en variante **debug**. Por defecto utiliza `http://10.0.2.2:8000/api/v1/` para el emulador.
3. Registrar un correo `@info.uas.edu.mx`, contraseña de al menos 10 caracteres y confirmación.
4. Abrir Mailpit en la computadora: `http://localhost:8025`. Abrir allí el enlace de verificación, cuya firma usa la URL del backend.
5. Iniciar sesión en Android y pulsar **Ya verifiqué mi correo** si aún aparece la pantalla de verificación.

Registro no devuelve token: muestra confirmación y vuelve al login. Un login válido guarda el token cifrado con AES/GCM y una clave Android Keystore. El almacenamiento se excluye del backup y se vincula a la URL del servidor. Al abrir la app se valida con GET /user. Un 401 elimina la sesión; un fallo de red ofrece reintentar sin destruirla.

Para dispositivo USB: ejecutar `adb reverse tcp:8000 tcp:8000` y compilar con `-PAPI_BASE_URL=http://127.0.0.1:8000/api/v1/`. También puede definirse API_BASE_URL en `~/.gradle/gradle.properties` para Android Studio. Para un servidor remoto usar la URL HTTPS del backend, no la URL Git del repositorio. Release no tiene servidor predeterminado: configurar `-PAPI_BASE_URL=https://tu-servidor/api/v1/`. HTTP solo se permite en debug para los hosts locales indicados en network_security_config.xml; nunca se deshabilita la validación TLS.

Cerrar sesión desde el menú principal o verificación revoca el token actual antes de eliminarlo. Si falla la red se muestra el error y se mantiene la sesión para reintentar la revocación.

## Endpoints

- POST /register: name, email, password, password_confirmation.
- POST /login: email, password, device_name.
- GET /user: Authorization Bearer.
- POST /email/verification-notification: Authorization Bearer.
- POST /logout: Authorization Bearer; 204.

Se manejan validaciones 422, sesión inválida 401, permisos 403, límite 429, fallos 5xx y red. No se registran cuerpos de solicitudes, contraseñas o tokens. Las pantallas principales conservan sus datos de ejemplo: esta integración solo conecta autenticación, nombre del usuario y cierre de sesión.

## Verificación

`./gradlew :app:testDebugUnitTest :app:assembleDebug`

Las pruebas de repositorio cubren registro sin token, validación institucional, login correcto/incorrecto, restauración, expiración y logout con reintento. Para prueba manual: credenciales inválidas no navegan; registro no permite duplicados; cuenta sin verificar muestra verificación; reinicio de app restaura sesión válida; logout no permite volver al inicio con Atrás; comprobar también desconexión y rotación.

Referencias: [red del emulador](https://developer.android.com/studio/run/emulator-networking-address), [seguridad de red](https://developer.android.com/privacy-and-security/security-config), [Android Keystore](https://developer.android.com/privacy-and-security/keystore).

La configuracion local de Android Studio puede establecerse con api.baseUrl=http://127.0.0.1:8000/api/v1/ en local.properties para telefono USB; para emulador usar http://10.0.2.2:8000/api/v1/. Solo afecta debug y -PAPI_BASE_URL tiene prioridad. Ejecutar adb reverse tcp:8000 tcp:8000 al reconectar el telefono.
