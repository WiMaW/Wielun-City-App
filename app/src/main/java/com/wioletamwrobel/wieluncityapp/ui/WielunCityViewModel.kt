package com.wioletamwrobel.wieluncityapp.ui

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.wioletamwrobel.wieluncityapp.beaconConnection.BeaconService
import com.wioletamwrobel.wieluncityapp.data.PlacesDataSource
import com.wioletamwrobel.wieluncityapp.data.PlacesDataSource.placeList
import com.wioletamwrobel.wieluncityapp.model.Place
import com.wioletamwrobel.wieluncityapp.utils.ScannerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyBeautifulCityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        MyBeautifulCityUiState(
            placesList = placeList,
            currentPlace = PlacesDataSource.defaultPlace
        )
    )

    val uiState: StateFlow<MyBeautifulCityUiState> = _uiState

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

    fun scannerLoading() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isScannerLoading = true)
            }
        }
    }

    fun scannerStop() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isScannerLoading = false)
            }
        }
    }


    //beacon searching and updating current place to display detail paige
    private val beaconService = BeaconService()
    private var foundedPlace: Place = PlacesDataSource.defaultPlace

    fun scannerButtonResponse(context: Context, activity: Activity) {






        foundedPlace = beaconService.getfoundedPlace(context, activity)

        when (beaconService.scannerState) {

            ScannerState.LOADING -> {
                Log.d(TAG, "Scanner State Loading")
                scannerLoading()
                foundedPlace = beaconService.getfoundedPlace(context, activity)
            }

            ScannerState.SUCCESS -> {
                Log.d(TAG, "Scanner State Success")
                scannerStop()
                updateCurrentPlace(foundedPlace)
                navigateFromDialog()
                navigateToDetailPage()
            }

            ScannerState.ERROR -> {}

            ScannerState.UNKNOWN -> {
                foundedPlace = beaconService.getfoundedPlace(context, activity)
                scannerLoading()
                Log.d(TAG, "Scanner State Unknown")
            }
        }
    }

    fun cleanScannedBeacon() {
//        beaconService.clearBeacon()
    }

    data class MyBeautifulCityUiState(
        val placesList: List<Place> = emptyList(),
        val currentPlace: Place = PlacesDataSource.defaultPlace,
        val isShowingListPage: Boolean = true,
        val isScannerButtonClicked: Boolean = false,
        val isScannerLoading: Boolean = false,
        val isBeaconScanned: Boolean = false,
    )
}

