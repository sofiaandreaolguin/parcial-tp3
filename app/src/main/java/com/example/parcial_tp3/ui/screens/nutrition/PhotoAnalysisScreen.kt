package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraTag
import com.example.parcial_tp3.ui.components.VoraTagTone
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Nutrition / 06 — Photo Analysis. La barra inferior la pone el Scaffold de MainActivity y el
 * scroll cubre pantallas más bajas (UI03). La foto es de demostración (NAV05): no se abre la cámara.
 * "Retake Photo" muestra el aviso de modo demostración.
 */
@Composable
fun PhotoAnalysisScreen(
    draft: MealDraft,
    onBack: () -> Unit,
    onReview: () -> Unit,
    onRetake: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(
                start = VoraTheme.spacing.screen,
                end = VoraTheme.spacing.screen,
                top = VoraTheme.spacing.s12,
                bottom = VoraTheme.spacing.s24,
            ),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s20),
    ) {
        NutritionHeader(title = R.string.nutrition_photo_title, onBack = onBack)
        MealPhoto()
        EstimateCard(draft)
        Text(
            text = stringResource(R.string.nutrition_photo_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10)) {
            VoraButton(
                text = stringResource(R.string.nutrition_photo_review),
                onClick = onReview,
                modifier = Modifier.fillMaxWidth(),
            )
            VoraButton(
                text = stringResource(R.string.nutrition_photo_retake),
                onClick = onRetake,
                modifier = Modifier.fillMaxWidth(),
                style = VoraButtonStyle.Outlined,
            )
        }
    }
}

// Decorativa: las etiquetas y la tarjeta de abajo ya dicen lo que hay en el plato.
@Composable
private fun MealPhoto() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(PHOTO_RATIO)
            .clip(RoundedCornerShape(VoraTheme.radius.r20)),
    ) {
        Image(
            painter = painterResource(R.drawable.meal_photo_demo),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
        )
        PhotoTag(R.string.nutrition_photo_tag_chicken, CHICKEN_TAG_X, CHICKEN_TAG_Y)
        PhotoTag(R.string.nutrition_photo_tag_rice, RICE_TAG_X, RICE_TAG_Y)
    }
}

/** Posiciona la etiqueta por su borde izquierdo y su centro, en proporción a la foto. */
@Composable
private fun PhotoTag(text: Int, x: Float, y: Float) {
    VoraTag(
        text = stringResource(text),
        tone = VoraTagTone.Dark,
        modifier = Modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
            layout(constraints.maxWidth, constraints.maxHeight) {
                placeable.place(
                    x = (constraints.maxWidth * x).toInt(),
                    y = (constraints.maxHeight * y).toInt() - placeable.height / 2,
                )
            }
        },
    )
}

@Composable
private fun EstimateCard(draft: MealDraft) {
    VoraCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // El frame lo dibuja de 13, más grande que la etiqueta de 10 de VoraSectionLabel.
                Text(
                    text = stringResource(R.string.nutrition_photo_estimate),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.nutrition_photo_estimate_note),
                    style = VoraTheme.text.labelPlain,
                    color = VoraTheme.colors.textMuted,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.nutrition_photo_kcal, draft.kcal),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
                    VoraTag(stringResource(R.string.nutrition_photo_protein, draft.proteinG))
                    VoraTag(stringResource(R.string.nutrition_photo_carbs, draft.carbsG))
                    VoraTag(stringResource(R.string.nutrition_photo_fat, draft.fatG))
                }
            }
        }
    }
}

// Foto del frame: 700 x 440 px a 2x (nodo "meal photo container").
private const val PHOTO_RATIO = 700f / 440f

// Etiquetas sobre la foto: borde izquierdo y centro vertical, medidos en el frame como fracción de la foto.
private const val CHICKEN_TAG_X = 0.114f
private const val CHICKEN_TAG_Y = 0.323f
private const val RICE_TAG_X = 0.514f
private const val RICE_TAG_Y = 0.55f

@Preview(showBackground = true, widthDp = 393, heightDp = 770)
@Composable
private fun PhotoAnalysisScreenPreview() {
    Parcialtp3Theme {
        PhotoAnalysisScreen(draft = PhotoMealDraft, onBack = {}, onReview = {}, onRetake = {})
    }
}
