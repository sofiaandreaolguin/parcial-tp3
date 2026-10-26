# 009 · Perfil: un ViewModel por pantalla, con el perfil en un repositorio

**Fecha:** 21/10/2026 · **Propuso:** Fiore (tarea VC-c, inventario) · **Estado:** aceptada y aplicada

> Al 26/10 está implementada: las seis pantallas de Profile tienen su propio ViewModel
> (`ProfileDashboardViewModel`, `EditProfileViewModel`, `GoalsViewModel`,
> `NutritionPreferencesViewModel`, `VoiceSettingsViewModel`, `SettingsViewModel`) y el perfil
> vive en `data/repository/ProfileRepository.kt`.
>
> Esta ficha se numeró 008 el 26/10: había nacido como 004, número que ya tenía
> [`004-rutas-object-y-route-screen.md`](004-rutas-object-y-route-screen.md) (del 20/10, y
> citada desde `CONTEXTO.md`). Se renumeró la más nueva para no romper esas referencias.

## Contexto

`specs/PANTALLAS.md` le asignaba `ProfileViewModel` a 5 de las 6 pantallas de Perfil (filas 32 a 36), aplicando el criterio de la sección 2: *"un mismo ViewModel puede servir a varias pantallas del mismo flujo si comparten datos"*.

Pero los 7 `LEEME.md` de `ui/screens/` decían, todos, que cada pantalla lleva cuatro archivos: `XxxRoute.kt`, `XxxScreen.kt`, **`XxxViewModel.kt`** y `XxxUiState.kt`. Ninguno aclaraba que hay flujos donde varias pantallas comparten controlador.

Zaki, maquetando Perfil (VC-q), siguió el LEEME, que es el archivo que está al lado del código. El 21/10 avisó que las filas 32 a 37 pasan a un ViewModel por pantalla.

O sea: el repo se contradecía a sí mismo y el malentendido iba a repetirse en los otros seis flujos.

## Decisión

1. **Las 6 pantallas de Perfil tienen un ViewModel propio** (`ProfileDashboardViewModel`, `EditProfileViewModel`, `GoalsPreferencesViewModel`, `NutritionPreferencesViewModel`, `VoiceAiSettingsViewModel`, `SettingsViewModel`).
2. **El perfil de demostración vive en un `ProfileRepository`**, inyectado por constructor en los ViewModels que lo necesitan.
3. **Los 7 LEEME aclaran** que el controlador de cada pantalla sale de la columna *Controlador* de `specs/PANTALLAS.md`, y que hay flujos donde varias pantallas comparten uno a propósito.

## Por qué (en criollo)

**Las 6 de Perfil no son la misma pantalla en momentos distintos.** Un dashboard, tres formularios, unos ajustes de voz y una lista de settings hacen cosas distintas y muestran datos distintos. Meterlas en un `ProfileViewModel` deja una clase que crece sin freno y que seis pantallas editan a la vez.

**Pero sí comparten un dato: el perfil.** `01` lo muestra; `02`, `03` y `04` lo editan y al guardar vuelven a `01`, que tiene que mostrar el cambio. Si cada ViewModel se guarda su copia, lo que editás en `02` no se ve al volver. Por eso el perfil no puede vivir en un ViewModel: **es un dato, no estado de pantalla, y los datos van en un repositorio.** Eso es justo lo que pide RF06 (la pantalla pasa por el ViewModel, el ViewModel por el repositorio).

**No se generaliza al resto.** Hay dos flujos donde compartir es obligatorio:
- **Voice:** por la decisión 002, `01`, `02`, `03`, `04` y `06` son **una sola ruta** con cinco estados. No hay cinco pantallas que puedan tener cinco ViewModels.
- **Nutrition:** `FoodCaptureViewModel` lo comparten `04` a `07` a propósito, para que el borrador de la comida sobreviva el paso de una pantalla a la siguiente (`PANTALLAS.md`, sección 7).

Por eso la regla no es "uno por pantalla siempre", sino **"el controlador lo dice `PANTALLAS.md`"**, y Perfil es el flujo donde no se comparte.

## Consecuencias

- La zona de Perfil (Zaki) arma los 6 ViewModels y el `ProfileRepository`. Sin el repositorio, el recorrido `01 → 02 → guardar → 01` no muestra el cambio.
- En la devolución hay que poder explicar por qué Perfil no comparte y Nutrition sí. La respuesta corta: **se comparte el ViewModel cuando las pantallas comparten un borrador en curso; se comparte el repositorio cuando comparten un dato guardado.**
- `PANTALLAS.md` queda con el criterio en la sección 2 y el detalle en la sección 9.
