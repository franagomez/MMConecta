package cl.grupo11.mmconecta.ui.home

import cl.grupo11.mmconecta.model.LearningContent
import cl.grupo11.mmconecta.viewmodel.HomeUiState
import cl.grupo11.mmconecta.viewmodel.NotificationsUiState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(state: HomeUiState, onCategorySelected: (String) -> Unit, onContentSelected: (String) -> Unit, onProfileSelected: () -> Unit,
    onFavoriteSelected: (String) -> Unit,
    onFavoritesFilterChanged: (Boolean) -> Unit,
    onRetryFavorites: () -> Unit,
    onRetryContents: () -> Unit,
    notificationsState: NotificationsUiState,
    onSendNotification: () -> Unit,
    isWideScreen: Boolean = false
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("MMConecta", style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary)
                Text("Aprende, crea y conecta", style = MaterialTheme.typography.titleMedium)
                OutlinedButton(onClick = onProfileSelected) { Text("Mi perfil") }
            }
            item {
                Text("Conocimiento para tu negocio", style = MaterialTheme.typography.headlineSmall)
                Text("Explora contenidos de panadería, pastelería y chocolatería.")
            }
            if (state.contentsLoading) {
                item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            }
            if (!state.contentsLoading && state.error != null && state.contents.isEmpty()) {
                item {
                    Text(state.error, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = onRetryContents) { Text("Reintentar catálogo") }
                }
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.categories) { category ->
                        FilterChip(
                            selected = category == state.selectedCategory,
                            onClick = { onCategorySelected(category) },
                            label = { Text(category) }
                        )
                    }
                }
            }
            item {
                FilterChip(selected = state.onlyFavorites,
                    onClick = { onFavoritesFilterChanged(!state.onlyFavorites) },
                    label = { Text("Mis favoritos") })
                if (state.favoritesBusy) LinearProgressIndicator(Modifier.fillMaxWidth())
                if (!state.contentsLoading) {
                    state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
                if (!state.favoritesLoaded && !state.favoritesBusy) {
                    TextButton(onClick = onRetryFavorites) { Text("Reintentar carga") }
                }
                if (state.contents.isEmpty() && state.favoritesLoaded && !state.contentsLoading) {
                    Text(if (state.onlyFavorites) "No tienes favoritos en esta categoría. Puedes agregarlos desde el catálogo."
                        else "No hay contenidos en esta categoría.")
                }
            }
            if (isWideScreen) {
                // Pantallas medianas/expandidas (tablet, landscape): 2 columnas para aprovechar el ancho.
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        state.contents.chunked(2).forEach { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                pair.forEach { content ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        ContentCard(content, state, onContentSelected, onFavoriteSelected)
                                    }
                                }
                                if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                // Pantallas compactas (celular en vertical): 1 columna, como antes.
                items(state.contents, key = { it.id }) { content ->
                    ContentCard(content, state, onContentSelected, onFavoriteSelected)
                }
            }
            item {
                HorizontalDivider()
                Text("Notificaciones", style = MaterialTheme.typography.titleMedium)
                Text("Recibe un aviso de prueba cuando haya contenido nuevo.")
                OutlinedButton(enabled = !notificationsState.busy, onClick = onSendNotification) {
                    Text("Probar notificación de contenido nuevo")
                }
                if (notificationsState.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
                notificationsState.message?.let {
                    Text(it, color = if (notificationsState.isError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary)
                }
            }
            item {
                Text("Prototipo académico · Grupo 11\nContenidos de demostración",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ContentCard(
    content: LearningContent,
    state: HomeUiState,
    onContentSelected: (String) -> Unit,
    onFavoriteSelected: (String) -> Unit
) {
    Card(
        onClick = { onContentSelected(content.id) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp, pressedElevation = 1.dp)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        content.category,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                val isFavorite = content.id in state.favoriteIds
                val favoriteColor by animateColorAsState(
                    targetValue = if (isFavorite) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline,
                    label = "favorite-color"
                )
                val favoriteScale by animateFloatAsState(
                    targetValue = if (isFavorite) 1.15f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "favorite-scale"
                )
                IconButton(
                    enabled = state.favoritesLoaded && !state.favoritesBusy,
                    onClick = { onFavoriteSelected(content.id) },
                    modifier = Modifier.graphicsLayer {
                        scaleX = favoriteScale
                        scaleY = favoriteScale
                    }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar de favoritos" else "Guardar en favoritos",
                        tint = favoriteColor
                    )
                }
            }
            Text(content.title, style = MaterialTheme.typography.titleLarge)
            Text(content.description, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
