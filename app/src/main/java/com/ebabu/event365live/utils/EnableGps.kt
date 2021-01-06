package com.ebabu.event365live.utils

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.widget.Toast
import com.ebabu.event365live.httprequest.Constants
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

class EnableGps(var activity: Activity) {

    //lateinit var enableInstance: EnableGps;

    public fun enablegps() {
        val mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(1000)

        val settingsBuilder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
        settingsBuilder.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(activity).checkLocationSettings(settingsBuilder.build())
        result.addOnCompleteListener { task ->
            //getting the status code from exception
            try {
                task.getResult(ApiException::class.java)
            } catch (ex: ApiException) {

                when (ex.statusCode) {

                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {

                          Toast.makeText(activity,"GPS IS OFF",Toast.LENGTH_SHORT).show()

                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        val resolvableApiException = ex as ResolvableApiException
                        resolvableApiException.startResolutionForResult(activity, Constants.REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        // Toast.makeText(this,"PendingIntent unable to execute request.",Toast.LENGTH_SHORT).show()

                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

//                        Toast.makeText(
//                                this,
//                                "Something is wrong in your GPS",
//                                Toast.LENGTH_SHORT
//                        ).show()
//
//                    }


                    }
                }


            }


        }
    }
}