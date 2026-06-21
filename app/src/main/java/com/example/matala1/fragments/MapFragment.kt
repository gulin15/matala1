package com.example.matala1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.matala1.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_FRM_google_map) as? SupportMapFragment

        // load map when ready
        mapFragment?.getMapAsync(this)

        return v
    }

    // automatically used when map is ready to use
    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map

        // starting spot on map
        val defaultLocation = LatLng(32.1133, 34.8180)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
    }

    fun zoomToLocation(latitude: Double, longitude: Double, title: String = "Score Location") {
        val location = LatLng(latitude, longitude)

        googleMap?.let { map ->
            // clear former markers
            map.clear()

            map.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(title)
            )

            // zoom animation
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }
}