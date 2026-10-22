package com.example.parcial_tp3.ui.screens.profile

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

/** Foto de perfil de Profile / 01 y 02. Decorativa: el nombre ya está al lado. */
@Composable
fun ProfileAvatar(modifier: Modifier = Modifier, size: Dp = VoraTheme.sizes.avatar) {
    Image(
        painter = painterResource(R.drawable.avatar),
        contentDescription = null,
        modifier = modifier
            .size(size)
            .clip(CircleShape),
    )
}
