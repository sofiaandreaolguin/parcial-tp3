package com.example.parcial_tp3.ui.screens.profile

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.parcial_tp3.R

/** Aviso de las opciones de Profile que no tienen pantalla (NAV03). */
@Composable
fun rememberDemoToast(): () -> Unit {
    val context = LocalContext.current
    return remember(context) { { Toast.makeText(context, R.string.aviso_demo, Toast.LENGTH_SHORT).show() } }
}
