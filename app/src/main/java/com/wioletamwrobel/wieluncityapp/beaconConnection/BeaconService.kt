package com.wioletamwrobel.wieluncityapp.beaconConnection

import android.Manifest
import android.app.Activity
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
import com.wioletamwrobel.wieluncityapp.data.iBeaconsDataSource


class BeaconService {

    private var beaconManager: KBeaconsMgr? = null
    private var isBeaconFounded = false

    var beaconOnListMac = ""

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
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
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
                    arrayOf(Manifest.permission.BLUETOOTH_SCAN), 3
                );
            }

            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 4
                );
            }
        }
    }

    private fun scanningForBeacon(context: Context) {

        beaconManager?.setScanMode(KBeaconsMgr.SCAN_MODE_BALANCED)

        val scanner = beaconManager?.startScanning()

        when (scanner) {
            0 -> Log.v(TAG, "Scan started");

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

        val beaconDelegate = object : KBeaconMgrDelegate {
            override fun onBeaconDiscovered(beacons: Array<out KBeacon>?) {
                if (beacons != null && !isBeaconFounded) {
                    for (iBeacon in beacons) {
                        for (beaconMac in iBeaconsDataSource.iBeaconList) {
                            if (iBeaconsDataSource.iBeaconList.contains(iBeacon.mac)) {
                                beaconOnListMac = iBeacon.mac
                                isBeaconFounded = true
                            }
                        }
                    }
                }
                beaconManager?.stopScanning()
            }

            override fun onCentralBleStateChang(nNewState: Int) {}
            override fun onScanFailed(errorCode: Int) {

            }
        }
        beaconManager?.delegate = beaconDelegate
    }



    fun getFoundedBeacon(context: Context, activity: Activity): String {
        askingForPermission(context, activity)
        initializeBeaconManager(context)
        scanningForBeacon(context)
        return beaconOnListMac
    }

    fun clearBeacon() {
        beaconManager?.clearBeacons()
        beaconOnListMac = ""
        isBeaconFounded = false
    }
}