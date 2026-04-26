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
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eguasti.composeapp.generated.resources.Res
import eguasti.composeapp.generated.resources.app_name
import eguasti.composeapp.generated.resources.nav_alerts
import eguasti.composeapp.generated.resources.nav_map
import eguasti.composeapp.generated.resources.nav_search
import eguasti.composeapp.generated.resources.search_hint
import eguasti.composeapp.generated.resources.search_recent_title
import eguasti.composeapp.generated.resources.zilla_slab_bold
import net.albertopedron.eguasti.ui.theme.EGuastiTheme
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource

private enum class SearchTab { Map, Search, Alerts }

@Composable
fun SearchScreen(
    recentSearches: List<RecentSearch> = sampleRecentSearches,
    onRecentSearchClick: (RecentSearch) -> Unit = {},
    onSearch: (String) -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToAlerts: () -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableStateOf(SearchTab.Search) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        topBar = { SearchTopBar() },
        bottomBar = {
            SearchBottomBar(
                selected = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        SearchTab.Map -> onNavigateToMap()
                        SearchTab.Search -> Unit
                        SearchTab.Alerts -> onNavigateToAlerts()
                    }
                },
            )
        },
    ) { innerPadding ->
        SearchContent(
            recentSearches = recentSearches,
            onRecentSearchClick = onRecentSearchClick,
            onSearch = onSearch,
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
    recentSearches: List<RecentSearch>,
    onRecentSearchClick: (RecentSearch) -> Unit,
    onSearch: (String) -> Unit,
    contentPadding: PaddingValues,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

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
                    onQueryChange = { query = it },
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text(stringResource(Res.string.search_hint)) },
                    //trailingIcon = { if (query.isNotEmpty()) ClearIcon(onClear) },
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

        if (recentSearches.isNotEmpty()) {
            Text(
                text = stringResource(Res.string.search_recent_title).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(16.dp),
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items = recentSearches, key = { it.id }) { search ->
                    RecentSearchItem(
                        search = search,
                        onClick = { onRecentSearchClick(search) },
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentSearchList(
    recentSearches: List<RecentSearch>,
    onRecentSearchClick: (RecentSearch) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = recentSearches, key = { it.id }) { search ->
            RecentSearchItem(
                search = search,
                onClick = { onRecentSearchClick(search) },
            )
        }
    }
}

@Composable
private fun RecentSearchItem(
    search: RecentSearch,
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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = search.primary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = search.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SearchBottomBar(
    selected: SearchTab,
    onTabSelected: (SearchTab) -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = selected == SearchTab.Map,
            onClick = { onTabSelected(SearchTab.Map) },
            icon = { Icon(imageVector = Icons.Filled.Map, contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_map)) },
        )
        NavigationBarItem(
            selected = selected == SearchTab.Search,
            onClick = { onTabSelected(SearchTab.Search) },
            icon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_search)) },
        )
        NavigationBarItem(
            selected = selected == SearchTab.Alerts,
            onClick = { onTabSelected(SearchTab.Alerts) },
            icon = { Icon(imageVector = Icons.Filled.Notifications, contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_alerts)) },
        )
    }
}

private val sampleRecentSearches = listOf(
    RecentSearch(
        id = "1",
        primary = "Via Roma, Comacchio",
        secondary = "Ferrara, Emilia-Romagna",
    ),
    RecentSearch(
        id = "2",
        primary = "Piazza Duomo, Milano",
        secondary = "Milano, Lombardia",
    ),
    RecentSearch(
        id = "3",
        primary = "Via dei Condotti, Roma",
        secondary = "Roma, Lazio",
    ),
)

@Preview
@Composable
private fun SearchScreenPreview() {
    EGuastiTheme {
        SearchScreen()
    }
}
