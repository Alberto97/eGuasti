package net.albertopedron.eguasti.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import eguasti.composeapp.generated.resources.Res
import eguasti.composeapp.generated.resources.app_name
import eguasti.composeapp.generated.resources.search_hint
import eguasti.composeapp.generated.resources.zilla_slab_bold
import net.albertopedron.eguasti.data.model.GeocodingSuggestion
import net.albertopedron.eguasti.ui.components.AppBottomBar
import net.albertopedron.eguasti.ui.components.BottomBarTab
import net.albertopedron.eguasti.ui.theme.EGuastiTheme
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel { SearchViewModel() },
    onNavigateToMap: () -> Unit = {},
    onNavigateToMapAt: (latitude: Double, longitude: Double) -> Unit = { _, _ -> },
    onNavigateToAlerts: () -> Unit = {},
) {
    val query by viewModel.query.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.location.collect { location ->
            onNavigateToMapAt(location.latitude, location.longitude)
        }
    }

    SearchScreen(
        query = query,
        onQueryChange = viewModel::onQueryChange,
        suggestions = suggestions,
        onSuggestionClick = viewModel::onSuggestionClick,
        onNavigateToMap = onNavigateToMap,
        onNavigateToAlerts = onNavigateToAlerts,
    )
}

@Composable
private fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    suggestions: List<GeocodingSuggestion>,
    onSuggestionClick: (GeocodingSuggestion) -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToAlerts: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        topBar = { SearchTopBar() },
        bottomBar = {
            AppBottomBar(
                selected = BottomBarTab.Search,
                onTabSelected = { tab ->
                    when (tab) {
                        BottomBarTab.Map -> onNavigateToMap()
                        BottomBarTab.Search -> Unit
                        BottomBarTab.Alerts -> onNavigateToAlerts()
                    }
                },
            )
        },
    ) { innerPadding ->
        SearchContent(
            query = query,
            onQueryChange = onQueryChange,
            suggestions = suggestions,
            onSuggestionClick = onSuggestionClick,
            contentPadding = innerPadding,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar() {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Bolt,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = stringResource(Res.string.app_name),
                    fontFamily = FontFamily(Font(Res.font.zilla_slab_bold)),
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchContent(
    query: String,
    onQueryChange: (String) -> Unit,
    suggestions: List<GeocodingSuggestion>,
    onSuggestionClick: (GeocodingSuggestion) -> Unit,
    contentPadding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp),
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text(stringResource(Res.string.search_hint)) },
                )
            },
            expanded = false,
            onExpandedChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            windowInsets = WindowInsets(0, 0, 0, 0),
            content = {},
        )

        if (suggestions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 16.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items = suggestions, key = { it.key }) { suggestion ->
                    SuggestionItem(
                        suggestion = suggestion,
                        onClick = { onSuggestionClick(suggestion) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    suggestion: GeocodingSuggestion,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = CircleShape,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp),
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = suggestion.text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview
@Composable
private fun SearchScreenPreview() {
    EGuastiTheme {
        SearchScreen(
            query = "Roma",
            onQueryChange = {},
            suggestions = listOf(
                GeocodingSuggestion(text = "Via Roma, Comacchio, Ferrara", key = "k1"),
                GeocodingSuggestion(text = "Piazza Duomo, Milano", key = "k2"),
                GeocodingSuggestion(text = "Via dei Condotti, Roma", key = "k3"),
            ),
            onSuggestionClick = {},
            onNavigateToMap = {},
            onNavigateToAlerts = {},
        )
    }
}
