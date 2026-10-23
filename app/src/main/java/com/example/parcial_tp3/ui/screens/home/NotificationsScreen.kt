package com.example.parcial_tp3.ui.screens.home

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Home / 02 — Notifications. Con barra inferior (la pone el Scaffold) y sin flecha: se vuelve
 * con el atras del sistema o la pestana Home. Cada notificacion lleva a donde dice
 * specs/PANTALLAS.md; la de la racha no tiene pantalla y no se puede tocar.
 */
@Composable
fun NotificationsScreen(
    state: NotificationsUiState,
    onMarkAllAsRead: () -> Unit,
    onOpen: (NotificationItem) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(
                start = VoraTheme.spacing.screen,
                end = VoraTheme.spacing.screen,
                top = VoraTheme.spacing.s12,
                bottom = VoraTheme.spacing.s24,
            ),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s20),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.notifications_title),
                style = MaterialTheme.typography.headlineSmall,
                color = colors.onSurface,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.notifications_mark_all),
                style = MaterialTheme.typography.labelMedium,
                color = if (state.hasUnread) colors.primary else VoraTheme.colors.textMuted,
                modifier = Modifier.clickable(enabled = state.hasUnread, role = Role.Button, onClick = onMarkAllAsRead),
            )
        }
        Section(R.string.notifications_today, state.today, onOpen)
        Section(R.string.notifications_yesterday, state.yesterday, onOpen)
    }
}

/** "TODAY" o "YESTERDAY" y sus tarjetas, separadas 10. Sin notificaciones no se dibuja. */
@Composable
private fun Section(@StringRes label: Int, items: List<NotificationItem>, onOpen: (NotificationItem) -> Unit) {
    if (items.isEmpty()) return
    Column {
        Text(text = stringResource(label), style = VoraTheme.text.notificationLabel, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(VoraTheme.spacing.s12))
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10)) {
            items.forEach { NotificationCard(it, onOpen) }
        }
    }
}

/** Icono, remitente y hace cuanto, titulo y texto; el punto verde si no se leyo. */
@Composable
private fun NotificationCard(item: NotificationItem, onOpen: (NotificationItem) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val sinLeer = stringResource(R.string.notifications_unread)
    Box {
        VoraCard(
            // "Unread" es un estado de la tarjeta: TalkBack lo lee junto con la notificacion
            modifier = Modifier
                .fillMaxWidth()
                .semantics { if (item.unread) stateDescription = sinLeer },
            padding = VoraTheme.spacing.s14,
            onClick = if (item.destination == NotificationDestination.None) null else ({ onOpen(item) }),
        ) {
            Row {
                Box(Modifier.size(VoraTheme.sizes.notificationIconBox), contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(VoraTheme.sizes.iconXSmall),
                    )
                }
                Spacer(Modifier.width(VoraTheme.spacing.s12))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(item.sender),
                            style = VoraTheme.text.notificationLabel,
                            color = colors.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                        Text(text = ageLabel(item.ageMinutes), style = MaterialTheme.typography.bodySmall, color = VoraTheme.colors.textMuted)
                    }
                    // en el nodo son 4 y 4, pero la fila del remitente ocupa mas en Compose: con 2 el
                    // titulo y el texto caen donde en el frame y la tarjeta mide 107 (medido)
                    Spacer(Modifier.height(VoraTheme.spacing.s2))
                    Text(text = stringResource(item.title), style = VoraTheme.text.notificationTitle, color = colors.onSurface)
                    Spacer(Modifier.height(VoraTheme.spacing.s4))
                    Text(text = stringResource(item.body), style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
                }
            }
        }
        if (item.unread) {
            // el punto del nodo esta a 12 del borde de arriba y a 12 del de la derecha
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = VoraTheme.spacing.s12, end = VoraTheme.spacing.s12)
                    .size(VoraTheme.sizes.unreadDot)
                    .background(colors.primary, CircleShape),
            )
        }
    }
}

/** "20m ago", "2h ago" o "1d ago": minutos hasta una hora, horas hasta un dia, despues dias. */
@Composable
private fun ageLabel(ageMinutes: Int): String = when {
    ageMinutes < NotificationsUiState.MINUTES_PER_HOUR -> stringResource(R.string.notifications_minutes_ago, ageMinutes)
    ageMinutes < NotificationsUiState.MINUTES_PER_DAY ->
        stringResource(R.string.notifications_hours_ago, ageMinutes / NotificationsUiState.MINUTES_PER_HOUR)
    else -> stringResource(R.string.notifications_days_ago, ageMinutes / NotificationsUiState.MINUTES_PER_DAY)
}

@Preview(showBackground = true, widthDp = 393, heightDp = 844)
@Composable
private fun NotificationsScreenPreview() {
    Parcialtp3Theme {
        NotificationsScreen(
            state = NotificationsUiState(NotificationsDemoData.NOTIFICATIONS),
            onMarkAllAsRead = {},
            onOpen = {},
        )
    }
}
