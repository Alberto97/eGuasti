package net.albertopedron.eguasti.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import net.albertopedron.eguasti.data.GeocodingRepository
import net.albertopedron.eguasti.data.model.GeocodingLocation
import net.albertopedron.eguasti.data.model.GeocodingSuggestion

class SearchViewModel(
    private val repository: GeocodingRepository = GeocodingRepository(),
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _suggestions = MutableStateFlow<List<GeocodingSuggestion>>(emptyList())
    val suggestions: StateFlow<List<GeocodingSuggestion>> = _suggestions.asStateFlow()

    private val _location = MutableSharedFlow<GeocodingLocation>()
    val location: SharedFlow<GeocodingLocation> = _location.asSharedFlow()

    @OptIn(FlowPreview::class)
    private val querySubscription = viewModelScope.launch {
        _query
            .debounce(SEARCH_DEBOUNCE_MS)
            .distinctUntilChanged()
            .collectLatest { value ->
                if (value.length < MIN_QUERY_LENGTH) {
                    _suggestions.value = emptyList()
                    return@collectLatest
                }
                val result = repository.getSuggestions(value)
                _suggestions.value = result.getOrDefault(emptyList())
            }
    }

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun onSuggestionClick(suggestion: GeocodingSuggestion) {
        viewModelScope.launch {
            val result = repository.getLocation(suggestion.key)
            val location = result.getOrNull() ?: return@launch
            _location.emit(location)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L
        private const val MIN_QUERY_LENGTH = 3
    }
}
