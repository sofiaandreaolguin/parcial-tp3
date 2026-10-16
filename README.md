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
| OkHttp logging-interceptor | 4.12.0 |
| Coroutines | 1.11.0 |
| Room | 2.8.5 |
| Hilt / hilt-navigation-compose | 2.60.1 / 1.4.0 |

`compileSdk` 37, `minSdk` 29.

## URL del servidor

`BASE_URL` sale de `api.baseUrl` en `local.properties` (no se sube a Git). Por defecto es `http://10.0.2.2:8000/` (mock local desde el emulador). Para el servidor publicado: `api.baseUrl=https://tp3.martinrivas.ar/`.
