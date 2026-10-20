package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Pantalla de relleno del esqueleto (tarea VC-e): muestra el nombre del frame de Figma
 * y nada mas. Las 33 rutas la usan hasta que cada pantalla se maquete.
 *
 * Quien maquete una pantalla reemplaza esta llamada por su propia Route + Screen, y deja
 * los mismos callbacks. No se toca MainActivity ni el grafo.
 *
 * @param nombre el nombre del frame, de strings.xml (nunca escrito en el codigo).
 * @param onVolver si no es null, dibuja la flecha de volver. Lo usan las que se abren
 *   encima; las 16 con barra no la llevan. Usa el encabezado comun, VoraTopBar.
 * @param onContinuar accion unica provisoria, para que una pantalla del esqueleto no
 *   quede sin salida. Hoy solo la usa SignIn, que si no deja la app encerrada.
 */
@Composable
fun PantallaVacia(
    nombre: String,
    modifier: Modifier = Modifier,
    onVolver: (() -> Unit)? = null,
    onContinuar: (() -> Unit)? = null,
) {
    Column(modifier.fillMaxSize()) {
        if (onVolver != null) {
            VoraTopBar(title = null, onBack = onVolver)
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(VoraTheme.spacing.s24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = nombre,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            if (onContinuar != null) {
                Button(onClick = onContinuar, modifier = Modifier.padding(top = VoraTheme.spacing.s24)) {
                    Text(stringResource(R.string.entrar_demo))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PantallaVaciaPreview() {
    Parcialtp3Theme {
        PantallaVacia(nombre = stringResource(R.string.pant_workout_03), onVolver = {})
    }
}
