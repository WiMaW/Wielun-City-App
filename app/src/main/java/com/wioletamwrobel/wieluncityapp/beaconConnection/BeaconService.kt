package com.wioletamwrobel.wieluncityapp.beaconConnection

import android.Manifest
import android.app.Activity
import android.bluetooth.le.ScanSettings
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kkmcn.kbeaconlib2.KBeacon
import com.kkmcn.kbeaconlib2.KBeaconsMgr
import com.kkmcn.kbeaconlib2.KBeaconsMgr.KBeaconMgrDelegate
import com.wioletamwrobel.wieluncityapp.R

class BeaconService {

    private var beaconManager: KBeaconsMgr? = null
    var beaconsListMac: MutableSet<KBeacon> = mutableSetOf()

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
            )
        }
        // for android10, the app need fine location permission for BLE scanning //
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0
            )
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
                )
            }

            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 0
                )
            }
        }
    }

    fun scanningForBeacon(context: Context, activity: Activity) {

        askingForPermission(context, activity)
        initializeBeaconManager(context)

        beaconManager?.setScanMode(KBeaconsMgr.SCAN_MODE_LOW_LATENCY)

        val scanner = beaconManager?.startScanning()

        beaconManager?.setScanSetting(ScanSettings.Builder().apply {

        })


        when (scanner) {
            0 -> {
                beaconManager?.delegate = object : KBeaconMgrDelegate {
                    override fun onBeaconDiscovered(beacons: Array<out KBeacon>?) {
                        if (beacons != null) {
                            for (beacon in beacons) {
                                Log.d(TAG, "BeaconMac: ${beacon.mac}, beaconRSSI: ${beacon.rssi}")
                                beaconsListMac.add(beacon)
                            }
                            Log.d(TAG, "BeaconList: ${beaconsListMac}")
                        }
                    }

                    override fun onCentralBleStateChang(nNewState: Int) {}

                    override fun onScanFailed(errorCode: Int) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.on_Scan_Failed_massage),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            KBeaconsMgr.SCAN_ERROR_BLE_NOT_ENABLE -> Toast.makeText(
                context,
                context.getString(R.string.ble_not_enable_massage),
                Toast.LENGTH_SHORT
            ).show()

            KBeaconsMgr.SCAN_ERROR_UNKNOWN -> Toast.makeText(
                context,
                context.getString(R.string.scan_error_unknown_massage),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun stopScanner() {
        beaconManager?.stopScanning()
    }

    fun getScannedBeaconsMacList(context: Context, activity: Activity): MutableSet<KBeacon> {
        scanningForBeacon(context, activity)
        return beaconsListMac
    }

    fun clearBeacon() {
        beaconManager?.clearBeacons()
        beaconsListMac.clear()
        beaconManager = null
    }
}