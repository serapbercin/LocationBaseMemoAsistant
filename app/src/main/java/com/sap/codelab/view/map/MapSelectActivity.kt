package com.sap.codelab.view.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sap.codelab.R

class MapSelectActivity : AppCompatActivity() {

    private var map: GoogleMap? = null
    private var selected: LatLng? = null

    private val locPerms =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { enableMyLocationIfGranted() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_select)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync { gMap ->
            map = gMap
            gMap.uiSettings.isZoomControlsEnabled = true
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(41.0082, 28.9784), 12f)) // Istanbul example

            gMap.setOnMapClickListener { latLng ->
                selected = latLng
                gMap.clear()
                gMap.addMarker(MarkerOptions().position(latLng).title("Selected"))
            }

            requestMyLocationIfNeeded()
        }

        findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            val point = selected ?: return@setOnClickListener
            setResult(RESULT_OK, Intent().apply {
                putExtra(EXTRA_LAT, point.latitude)
                putExtra(EXTRA_LNG, point.longitude)
            })
            finish()
        }
    }

    private fun requestMyLocationIfNeeded() {
        val fine = Manifest.permission.ACCESS_FINE_LOCATION
        val coarse = Manifest.permission.ACCESS_COARSE_LOCATION
        val need =
            ContextCompat.checkSelfPermission(this, fine) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, coarse) != PackageManager.PERMISSION_GRANTED

        if (need) {
            locPerms.launch(arrayOf(fine, coarse))
        } else {
            enableMyLocationIfGranted()
        }
    }

    private fun enableMyLocationIfGranted() {
        val granted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (granted) try { map?.isMyLocationEnabled = true } catch (_: SecurityException) {}
    }

    companion object {
        const val EXTRA_LAT = "extra_lat"
        const val EXTRA_LNG = "extra_lng"
    }
}
