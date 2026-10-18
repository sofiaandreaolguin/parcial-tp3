#!/usr/bin/env bash
# Control automático: corre solo al final de cada turno de la IA (hook Stop de .claude/settings.json).
# Si algo falla devuelve 2, y la IA no puede dar la tarea por terminada: tiene que corregir.
cd "$(dirname "$0")/.." || exit 0

# Sesión que escribe tests en rojo (TDD): los tests TIENEN que fallar, así que solo se controlan las reglas.
# Se activa abriendo la sesión con TDD_ROJO=1 claude, o creando el archivo vacío .tdd-rojo en la raíz.
if [ "$TDD_ROJO" = "1" ] || [ -f .tdd-rojo ]; then
  ./scripts/reglas.sh || exit 2
  exit 0
fi

# Si en este turno no cambió nada dentro de app/, no hace falta correr Gradle.
if command -v git >/dev/null 2>&1 && git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  if [ -z "$(git --no-optional-locks status --porcelain -- app)" ]; then
    exit 0
  fi
fi

./gradlew -q testDebugUnitTest >&2 || {
  echo "Fallan los tests unitarios. Corregí el código (no los tests) antes de terminar." >&2
  exit 2
}
./scripts/reglas.sh || exit 2
exit 0
