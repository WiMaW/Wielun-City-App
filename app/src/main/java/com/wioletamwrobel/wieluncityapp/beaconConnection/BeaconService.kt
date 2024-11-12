package com.wioletamwrobel.wieluncityapp.beaconConnection

import android.Manifest
import android.app.Activity
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.CALLBACK_TYPE_ALL_MATCHES
import android.bluetooth.le.ScanSettings.CALLBACK_TYPE_ALL_MATCHES_AUTO_BATCH
import android.bluetooth.le.ScanSettings.CALLBACK_TYPE_FIRST_MATCH
import android.bluetooth.le.ScanSettings.CALLBACK_TYPE_MATCH_LOST
import android.bluetooth.le.ScanSettings.MATCH_MODE_AGGRESSIVE
import android.bluetooth.le.ScanSettings.MATCH_MODE_STICKY
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kkmcn.kbeaconlib2.KBeacon
import com.kkmcn.kbeaconlib2.KBeaconsMgr
import com.kkmcn.kbeaconlib2.KBeaconsMgr.KBeaconMgrDelegate
import com.wioletamwrobel.wieluncityapp.data.PlacesDataSource
import com.wioletamwrobel.wieluncityapp.data.PlacesDataSource.placeList
import com.wioletamwrobel.wieluncityapp.data.iBeaconsDataSource
import com.wioletamwrobel.wieluncityapp.model.Place
import com.wioletamwrobel.wieluncityapp.utils.ScannerState


class BeaconService {

    private var beaconManager: KBeaconsMgr? = null
    private var foundedPlace by mutableStateOf(PlacesDataSource.defaultPlace)
    var scannerState by mutableStateOf(ScannerState.UNKNOWN)
    var beaconOnListMac: MutableList<String> = mutableListOf()

    private fun initializeBeaconManager(context: Context) {
        beaconManager = KBeaconsMgr.sharedBeaconManager(context)
    }

    private fun askingForPermission(context: Context, activity: Activity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0
            );
        }
        //for android10, the app need fine location permission for BLE scanning
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0
            );
        }
        //for android 12, the app need declare follow permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.BLUETOOTH_SCAN), 0
                );
            }

            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 0
                );
            }
        }
    }

    fun scanningForBeacon(context: Context, activity: Activity) {

        askingForPermission(context, activity)
        initializeBeaconManager(context)

        beaconManager?.setScanMode(KBeaconsMgr.SCAN_MODE_LOW_LATENCY)

        val scanner = beaconManager?.startScanning()

        when (scanner) {
            0 -> {
              //  ScannerState.LOADING
                beaconManager?.delegate = object : KBeaconMgrDelegate {
                    override fun onBeaconDiscovered(beacons: Array<out KBeacon>?) {
                        if (beacons != null) {
                            for (iBeacon in beacons) {
                                beaconOnListMac.add(iBeacon.mac)
                                Log.d(TAG, iBeacon.mac)
                            }
                        }
                        Log.d(TAG, beaconOnListMac.toString())
                    }

                    override fun onCentralBleStateChang(nNewState: Int) {}

                    override fun onScanFailed(errorCode: Int) {
                //        scannerState = ScannerState.ERROR
                    }
                }
            }

            KBeaconsMgr.SCAN_ERROR_BLE_NOT_ENABLE -> Toast.makeText(
                context,
                "Bluetooth function is not enable",
                Toast.LENGTH_SHORT
            ).show()

            KBeaconsMgr.SCAN_ERROR_UNKNOWN -> Toast.makeText(
                context,
                "Please make sure the app has Bluetooth scan permission",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun stopScanner() {
        beaconManager?.stopScanning()
    }


    fun getScannedBeaconsMacList(context: Context, activity: Activity): MutableList<String> {
        scanningForBeacon(context, activity)
        return beaconOnListMac
    }

    fun clearBeacon() {
        beaconManager?.clearBeacons()
        beaconOnListMac.clear()
    }
}