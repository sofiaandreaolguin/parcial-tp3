package com.example.parcial_tp3.ui.screens.nutrition

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Flecha plana y título de pantalla de Nutrition / 03, 06, 07 y 08.
 *
 * @param trailing texto gris a la derecha ("13:12" de 08).
 */
@Composable
internal fun NutritionHeader(@StringRes title: Int, onBack: () -> Unit, trailing: String? = null) =
    NutritionHeader(stringResource(title), onBack, trailing)

/** Con el título ya resuelto: el de 08 depende del tipo de comida, que llega con los datos. */
@Composable
internal fun NutritionHeader(title: String, onBack: () -> Unit, trailing: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
    ) {
        Box(Modifier.size(VoraTheme.sizes.icon).wrapContentSize(unbounded = true)) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = stringResource(R.string.volver),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .weight(1f)
                .semantics { heading() },
        )
        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.bodyMedium,
                color = VoraTheme.colors.textMuted,
            )
        }
    }
}
