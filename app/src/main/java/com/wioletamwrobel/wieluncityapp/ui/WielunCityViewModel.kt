package com.wioletamwrobel.wieluncityapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wioletamwrobel.wieluncityapp.data.PlacesDataSource
import com.wioletamwrobel.wieluncityapp.model.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyBeautifulCityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        MyBeautifulCityUiState(
            placesList = PlacesDataSource.placeList,
            currentPlace = PlacesDataSource.placeList.getOrElse(0) {
                PlacesDataSource.defaultPlace }
        ))

    val uiState: StateFlow<MyBeautifulCityUiState> = _uiState

    fun updateCurrentPlace(selectedPlace: Place) {
        viewModelScope.launch {
                _uiState.update {
                    it.copy(currentPlace = selectedPlace)
                }
        }
    }

    fun navigateToListPage() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isShowingListPage = true)
            }
        }
    }

    fun navigateToDetailPage() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isShowingListPage = false)
            }
        }
    }

    data class MyBeautifulCityUiState(
        val placesList: List<Place> = emptyList(),
        val currentPlace: Place = PlacesDataSource.defaultPlace,
        val isShowingListPage: Boolean = true,
    )
}

