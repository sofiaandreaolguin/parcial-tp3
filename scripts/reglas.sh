#!/usr/bin/env bash
# Reglas del grupo que el compilador no ve. Devuelve 2 si alguna se rompe.
# Las carpetas son las de specs/CONTEXTO.md (sección 4): si cambian, se ajustan acá.
cd "$(dirname "$0")/.." || exit 0
fallas=0

UI=$(find app/src/main -path '*/ui/*' -name '*.kt' 2>/dev/null)
if [ -n "$UI" ]; then
  # RF06: la UI no habla con Retrofit ni con la capa de datos
  if grep -nE '^import (retrofit2|.*\.data\.(remote|local)\.)' $UI >&2; then
    echo "RF06: una pantalla importa Retrofit o la capa de datos. Tiene que pasar por el ViewModel y el repositorio." >&2
    fallas=1
  fi
  # C30: la UI no importa Firebase; usa AuthRepository y ProfileRepository
  if grep -nE '^import com\.google\.firebase' $UI >&2; then
    echo "C30: una pantalla importa Firebase. Tiene que pasar por el ViewModel y el repositorio." >&2
    fallas=1
  fi
  # Idiomas: nada de texto escrito directo en Text("...")
  if grep -nE 'Text\(\s*(text\s*=\s*)?"' $UI >&2; then
    echo "Idiomas: hay texto escrito directo en una pantalla. Va en strings.xml y se usa con stringResource()." >&2
    fallas=1
  fi
fi

# C32: los repositorios no exponen tipos de Firebase en su API publica
REPOS=$(find app/src/main -path '*/data/repository/*' -name '*.kt' 2>/dev/null)
if [ -n "$REPOS" ] && grep -nE '^\s*(override |open |abstract )*(suspend )?(fun|val|var) [^{=]*(FirebaseUser|DocumentSnapshot)' $REPOS >&2; then
  echo "C32: un repositorio expone FirebaseUser o DocumentSnapshot en su API publica. Solo modelos de dominio." >&2
  fallas=1
fi

# NAV05: sin permisos de micrófono ni cámara
if grep -nE 'android\.permission\.(RECORD_AUDIO|CAMERA)' app/src/main/AndroidManifest.xml >&2 2>/dev/null; then
  echo "NAV05: el manifiesto pide micrófono o cámara. La consigna pide no pedirlos si no se usan de verdad." >&2
  fallas=1
fi

# NAV05: tampoco se pide el permiso desde el código, aunque no esté en el manifiesto
KT=$(find app/src/main -name '*.kt' 2>/dev/null)
if [ -n "$KT" ] && grep -nE 'RequestPermission|checkSelfPermission|shouldShowRequestPermissionRationale' $KT >&2; then
  echo "NAV05: hay código que pide un permiso del sistema. El coach y el análisis de comida son simulados." >&2
  fallas=1
fi

[ $fallas -eq 0 ] || exit 2
exit 0
