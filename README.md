# parcial-tp3

## Versiones usadas

| Herramienta | Versión |
|---|---|
| Android Gradle Plugin | 9.4.1 |
| Kotlin | 2.2.10 |
| KSP | 2.3.12 |
| Compose BOM | 2026.02.01 |
| Navigation Compose | 2.10.2 |
| Lifecycle (ViewModel / runtime Compose) | 2.11.0 |
| Retrofit + converter-gson | 3.0.0 |
| OkHttp logging-interceptor / mockwebserver (tests) | 4.12.0 |
| Coroutines (y coroutines-test) | 1.11.0 |
| Room | 2.8.5 |
| Hilt / hilt-navigation-compose | 2.60.1 / 1.4.0 |

`compileSdk` 37, `minSdk` 29.

## URL del servidor

`BASE_URL` sale de `api.baseUrl` en `local.properties` (no se sube a Git). Por defecto es `http://10.0.2.2:8000/` (mock local desde el emulador). Para el servidor publicado: `api.baseUrl=https://tp3.martinrivas.ar/`.

Después de cambiar `local.properties`, hacé **Sync Project with Gradle Files** para que se regenere `BuildConfig`. La URL siempre termina en `/`: si no, Retrofit no arranca (lo controla `BaseUrlTest`).

| Servidor | `api.baseUrl` | Cuándo |
|---|---|---|
| Mock local desde el emulador (por defecto) | `http://10.0.2.2:8000/` | Para probar la caché (RF02): se puede detener el servidor. Correr antes `python3 mock/server.py` |
| Publicado por el docente | `https://tp3.martinrivas.ar/` | Sin levantar nada. Es el que hay que usar en la versión release |
| Teléfono físico | `http://<IP-de-la-compu>:8000/` | Con `python3 mock/server.py --host 0.0.0.0` |

HTTP sin cifrar está habilitado **solo en debug** (`app/src/debug/res/xml/network_security_config.xml`). La release exige HTTPS, así que con el mock local no conecta.

### Escenarios de prueba del mock (RF05)

En debug, un interceptor de OkHttp (`MockScenarioInterceptor`) agrega los headers de prueba a cada petición. Se configuran en `local.properties` (y Sync):

```properties
# empty | error | malformed (sin la línea = respuesta normal)
api.mockScenario=empty
# demora en milisegundos, para ver el estado de carga
api.mockDelayMs=3000
```

Para volver a la respuesta normal, borrá las dos líneas. En release el interceptor no se agrega.

## Inyección de dependencias (RF08)

Hilt (construido sobre Dagger) arma el grafo al compilar:

- `VoraApplication` (`@HiltAndroidApp`) crea el contenedor de toda la app (`SingletonComponent`).
- `MainActivity` (`@AndroidEntryPoint`) puede recibir dependencias y da acceso a los `@HiltViewModel`.
- `di/NetworkModule`: provee la URL, los interceptores, `OkHttpClient`, `Retrofit` y `VoraApi`, una sola instancia cada uno (`@Singleton`).
- `di/DatabaseModule`: provee la única base Room (`VoraDatabase`, `@Singleton`) y sus DAO.
- Los repositorios piden por constructor (`@Inject constructor`) la API o el DAO que necesitan, y Hilt resuelve la cadena.
