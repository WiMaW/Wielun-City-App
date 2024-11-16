package com.wioletamwrobel.wieluncityapp.ui

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wioletamwrobel.wieluncityapp.beaconConnection.BeaconService
import com.wioletamwrobel.wieluncityapp.data.PlacesDataSource
import com.wioletamwrobel.wieluncityapp.data.PlacesDataSource.placeList
import com.wioletamwrobel.wieluncityapp.data.BeaconsDataSource
import com.wioletamwrobel.wieluncityapp.model.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WielunCityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        WielunCityUiState(
            placesList = placeList,
            currentPlace = PlacesDataSource.defaultPlace
        )
    )

    val uiState: StateFlow<WielunCityUiState> = _uiState

    //update current(selected) place when user click on it in list view; navigation to list and detail view
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

    fun navigateToDialog() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isScannerButtonClicked = true)
            }
        }
    }

    fun navigateFromDialog() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isScannerButtonClicked = false)
            }
        }
    }


    //beacon searching and updating current place to display detail paige
    private val beaconService = BeaconService()
    private var foundedPlace: Place = PlacesDataSource.defaultPlace

    fun scannerLoading() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isScannerLoading = true)
            }
        }
    }

    fun stopLoading() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isScannerLoading = false)
            }
        }
    }

    fun startScanning(context: Context, activity: Activity) {
        beaconService.scanningForBeacon(context, activity)
    }

    fun scannerButtonResponse(context: Context, activity: Activity) {

        for (beacon in beaconService.getScannedBeaconsMacList(context, activity)) {
            if (BeaconsDataSource.placesBeaconList.contains(beacon.mac)) {
                foundedPlace = findPlaceFromBeacon(beacon.mac)
                beaconService.stopScanner()
                stopLoading()
                updateCurrentPlace(foundedPlace)
                navigateFromDialog()
                navigateToDetailPage()
            }
        }
    }

    fun scannerResponseWithoutButton(context: Context, activity: Activity) {
        for (beacon in beaconService.getScannedBeaconsMacList(context, activity)) {
            if (BeaconsDataSource.placesBeaconList.contains(beacon.mac)) {
                foundedPlace = findPlaceFromBeacon(beacon.mac)
                updateCurrentPlace(foundedPlace)
                navigateToDetailPage()

            }
        }
    }

    private fun findPlaceFromBeacon(foundedBeaconMac: String): Place {
        for (place in placeList) {
            if (place.beaconMac == foundedBeaconMac) {
                return place
            }
        }
        return PlacesDataSource.defaultPlace
    }

    fun stopScanning() {
        beaconService.stopScanner()
    }

    fun cleanScannedBeacon() {
        beaconService.clearBeacon()
    }

    data class WielunCityUiState(
        val placesList: List<Place> = emptyList(),
        val currentPlace: Place = PlacesDataSource.defaultPlace,
        val isShowingListPage: Boolean = true,
        val isScannerButtonClicked: Boolean = false,
        val isScannerLoading: Boolean = false,
        val isBeaconScanned: Boolean = false,
    )
}

