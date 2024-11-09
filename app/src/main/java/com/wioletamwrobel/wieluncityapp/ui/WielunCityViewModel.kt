package com.wioletamwrobel.wieluncityapp.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wioletamwrobel.wieluncityapp.beaconConnection.BeaconService
import com.wioletamwrobel.wieluncityapp.data.PlacesDataSource
import com.wioletamwrobel.wieluncityapp.data.PlacesDataSource.placeList
import com.wioletamwrobel.wieluncityapp.model.Place
import kotlinx.coroutines.async
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
//        viewModelScope.launch {
        _uiState.update {
            it.copy(currentPlace = selectedPlace)
        }
//        }
    }

    fun navigateToListPage() {
//        viewModelScope.launch {
        _uiState.update {
            it.copy(isShowingListPage = true)
        }
//        }
    }

    fun navigateToDetailPage() {
//        viewModelScope.launch {
        _uiState.update {
            it.copy(isShowingListPage = false)
        }
//        }
    }

    fun navigateToDialog() {
//        viewModelScope.launch {
        _uiState.update {
            it.copy(isScannerButtonClicked = true)
        }
//        }
    }

    fun navigateFromDialog() {
//        viewModelScope.launch {
        _uiState.update {
            it.copy(isScannerButtonClicked = false)
        }
//        }
    }

    //beacon searching and updating current place to display detail paige
    private val beaconService = BeaconService()
    private var foundedBeacon: String = ""

    fun scanToFindBeacon(context: Context, activity: Activity) {
        viewModelScope.launch {
            foundedBeacon = beaconService.getFoundedBeacon(context, activity)
        }
    }

    fun findPlaceFromBeacon(): Place {
        for (place in placeList) {
            Log.d(
                "Debug",
                "checking: ${place.beaconMac} against foundedbeacon: ${foundedBeacon}"
            )
            if (place.beaconMac == foundedBeacon) {
                return place
            }
        }
        return PlacesDataSource.defaultPlace
    }

    fun setBeaconScanned() {
//        viewModelScope.launch {

        _uiState.update {
            it.copy(isBeaconScanned = true)
        }
//        }
    }

    fun unsetBeaconScanned() {
//        viewModelScope.launch {
        _uiState.update {
            it.copy(isBeaconScanned = false)
        }
      //  beaconService.clearBeacon()
    }

    fun displayToastForUserAboutFoundedPlace(context: Context) {
        when (foundedBeacon) {
            "BC:57:29:0E:46:7B" -> {
                Toast.makeText(context, "City Hall found", Toast.LENGTH_SHORT)
                    .show()
            }

            "BC:57:29:0E:46:78" -> Toast.makeText(
                context,
                "Eternal Love Statue found",
                Toast.LENGTH_SHORT
            ).show()

            "BC:57:29:03:77:02" -> Toast.makeText(
                context,
                "Cinema/Theater Syrena found",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    data class MyBeautifulCityUiState(
        val placesList: List<Place> = emptyList(),
        val currentPlace: Place = PlacesDataSource.defaultPlace,
        val isShowingListPage: Boolean = true,
        val isScannerButtonClicked: Boolean = false,
        val isBeaconScanned: Boolean = false,
    )
}

