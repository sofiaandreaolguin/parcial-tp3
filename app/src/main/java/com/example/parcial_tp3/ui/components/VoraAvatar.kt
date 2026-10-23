package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Foto de perfil del usuario de demostracion: Profile / 01 (72), Profile / 02 (80) y el saludo
 * de Home / 01 (40). Decorativa: no suma nada que la pantalla no diga.
 */
@Composable
fun VoraAvatar(modifier: Modifier = Modifier, size: Dp = VoraTheme.sizes.avatar) {
    Image(
        painter = painterResource(R.drawable.avatar),
        contentDescription = null,
        modifier = modifier
            .size(size)
            .clip(CircleShape),
    )
}
