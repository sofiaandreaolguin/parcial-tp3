# 008 · Firebase reemplaza al acceso de demostración

Fecha: 2026-10-25 · Tarea: extra aa · Spec: `specs/funcionales/firebase-auth-perfil.md`

## Qué se decidió
SignIn y SignUp usan Firebase Authentication y el perfil vive en Firestore (`users/{uid}`). No se conserva el acceso de demostración. La caché de Firestore sigue activa: una escritura sin red queda en cola.

## Por qué
- Firebase es un extra de hasta 10 puntos (Auth 4, Firestore 4, reglas 2). Un modo demo convivente duplicaba la ruta de acceso y no suma puntos.
- Cada persona que corre el proyecto necesita su propio `google-services.json`: no se sube, y el repo trae `app/google-services.json.example` para que el clon compile (la CI lo copia).

## Riesgo aceptado
Sin `google-services.json` real o sin cuenta no se entra a Home. La consigna pide un "acceso de demostración válido sin Firebase" como cobertura mínima de Acceso; el auditor de la spec lo marcó como crítico. Si el grupo prefiere bajar el riesgo, se puede volver a una implementación demo elegida por configuración.

## Alternativas descartadas
- Modo demo junto a Firebase: más código y un atajo que no está en el Figma.
- Desactivar la persistencia de Firestore para esperar la confirmación del servidor: el guardado sería verificable, pero se pierde la caché y la pantalla queda esperando sin red.
- Campos del perfil vacíos al registrarse: `Profile` no admite vacíos (fecha, medidas y enums) sin tocar seis pantallas ajenas.

## Qué no cubre
Goals, Nutrition Preferences y Voice & AI se leen del perfil pero sus cambios siguen en memoria. Las comidas y los entrenamientos siguen en Room y no se separan por cuenta.
