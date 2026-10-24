package com.example.parcial_tp3.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraCardOnDark
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.components.VoraStatusDot
import com.example.parcial_tp3.ui.components.VoraTopBarLarge
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Progress / 06 — Weekly Review (frame 1:2287). Se abre encima, sin barra, verde hasta el
 * borde (está en RUTAS_A_SANGRE): deja el lugar de las barras del sistema con
 * statusBarsPadding y navigationBarsPadding. Se llega desde "See weekly review →" de 02.
 */
@Composable
fun WeeklyReviewScreen(
    state: WeeklyReview,
    onBack: () -> Unit,
    onAskVora: () -> Unit,
    onWeeklyPlan: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.primary)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
    ) {
        VoraTopBarLarge(
            title = stringResource(R.string.progress_review_title),
            subtitle = stringResource(R.string.progress_review_subtitle),
            onBack = onBack,
            onDark = true,
            actions = { VoraOrb(size = VoraTheme.sizes.orbInsight, onDark = true) },
        )
        Column(
            // con subtitulo, el frame deja unos 18 entre el subtitulo y el contenido (nodo 1:2296);
            // VoraTopBarLarge deja 12
            modifier = Modifier.padding(
                start = VoraTheme.spacing.screen,
                top = VoraTheme.spacing.s6,
                end = VoraTheme.spacing.screen,
                bottom = VoraTheme.spacing.s24,
            ),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s20),
        ) {
            SummaryCard(state)
            Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
                SectionTitle(stringResource(R.string.progress_review_went_well), colors.secondary)
                state.wins.forEach { WinRow(it) }
            }
            Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
                SectionTitle(stringResource(R.string.progress_review_to_improve), VoraTheme.colors.cta)
                ImproveCard(state.improvement, onDinnerIdeas = onAskVora)
            }
            RecommendationsCard(state.recommendations, onWeeklyPlan)
            Column(
                modifier = Modifier.padding(top = VoraTheme.spacing.s8),
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
            ) {
                VoraButton(
                    text = stringResource(R.string.progress_review_ask_vora),
                    onClick = onAskVora,
                    modifier = Modifier.fillMaxWidth(),
                    style = VoraButtonStyle.Cta,
                    icon = painterResource(R.drawable.ic_mic),
                )
                VoraButton(
                    text = stringResource(R.string.progress_review_done),
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    style = VoraButtonStyle.OutlinedOnDark,
                )
            }
        }
    }
}

/** "You had a strong week." en lima y los tres datos en blanco, separados por divisores. */
@Composable
private fun SummaryCard(state: WeeklyReview) {
    VoraCardOnDark(
        modifier = Modifier.fillMaxWidth(),
        radius = VoraTheme.radius.r24,
        spacing = VoraTheme.spacing.s14,
    ) {
        Text(
            text = stringResource(state.headline),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = VoraTheme.colors.cta,
        )
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
        ) {
            state.summary.forEachIndexed { i, metric ->
                if (i > 0) {
                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight(),
                        thickness = VoraTheme.sizes.border,
                        color = VoraTheme.colors.onDarkSubtle,
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .semantics(mergeDescendants = true) {},
                    verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2),
                ) {
                    Text(
                        text = stringResource(metric.label),
                        style = MaterialTheme.typography.bodySmall,
                        color = VoraTheme.colors.onDarkMuted,
                    )
                    Text(
                        text = stringResource(metric.value),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

/** "WHAT WENT WELL" en menta, "WHAT TO IMPROVE" en lima: 13 Bold en mayúsculas. */
@Composable
private fun SectionTitle(text: String, color: Color) {
    Text(text = text, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color)
}

/** Un logro: check en un círculo menta, título blanco y el detalle en blanco al 70 %. */
@Composable
private fun WinRow(win: ReviewPoint) {
    Row(
        modifier = Modifier.semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
    ) {
        Box(
            modifier = Modifier
                .size(VoraTheme.sizes.winCheck)
                .background(VoraTheme.colors.mintTranslucent, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(VoraTheme.sizes.chevronSmall),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2),
        ) {
            Text(
                text = stringResource(win.title),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Text(
                text = stringResource(win.body),
                style = MaterialTheme.typography.bodyMedium,
                color = VoraTheme.colors.onDarkMuted,
            )
        }
    }
}

/** "Dinner consistency": alerta en lima, título, detalle y "Get dinner ideas →". */
@Composable
private fun ImproveCard(point: ReviewPoint, onDinnerIdeas: () -> Unit) {
    VoraCardOnDark(modifier = Modifier.fillMaxWidth(), spacing = VoraTheme.spacing.s12) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_alert_circle),
                contentDescription = null,
                tint = VoraTheme.colors.cta,
                modifier = Modifier.size(VoraTheme.sizes.iconXSmall),
            )
            Text(
                text = stringResource(point.title),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Text(
            text = stringResource(point.body),
            style = MaterialTheme.typography.bodyMedium,
            color = VoraTheme.colors.onDarkMuted,
        )
        ArrowLink(
            text = stringResource(R.string.progress_review_dinner_ideas),
            color = VoraTheme.colors.cta,
            onClick = onDinnerIdeas,
            modifier = Modifier.padding(top = VoraTheme.spacing.s4),
        )
    }
}

/** Tarjeta blanca: tres viñetas, un divisor y "View next week's plan →". */
@Composable
private fun RecommendationsCard(recommendations: List<Int>, onWeeklyPlan: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    VoraCard(modifier = Modifier.fillMaxWidth(), radius = VoraTheme.radius.r20) {
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
            Text(
                text = stringResource(R.string.progress_review_next_week),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = VoraTheme.colors.textMuted,
            )
            Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10)) {
                recommendations.forEach { rec ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10),
                    ) {
                        VoraStatusDot(colors.primary)
                        Text(text = stringResource(rec), style = MaterialTheme.typography.bodyMedium, color = colors.onSurface)
                    }
                }
            }
            HorizontalDivider(thickness = VoraTheme.sizes.border, color = colors.outline)
            ArrowLink(
                text = stringResource(R.string.progress_review_view_plan),
                color = colors.primary,
                onClick = onWeeklyPlan,
            )
        }
    }
}

/** Texto de 13 SemiBold con la flecha de 14 a 6, todo tocable. */
@Composable
private fun ArrowLink(text: String, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.clickable(role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s6),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = color)
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(VoraTheme.sizes.chevronSmall),
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1100)
@Composable
private fun WeeklyReviewScreenPreview() {
    Parcialtp3Theme {
        WeeklyReviewScreen(state = progressDemoState().review, onBack = {}, onAskVora = {}, onWeeklyPlan = {})
    }
}
