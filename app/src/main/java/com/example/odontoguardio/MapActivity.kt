package com.example.odontoguardio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load osmdroid configuration
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))

        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapview)
        mapView.setTileSource(TileSourceFactory.MAPNIK) // Set tile source here
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)

        // Set the map center to a specific location (e.g., New York City)
        val startPoint = GeoPoint(-7.218672728437808, -35.87547948914417)
        mapView.controller.setCenter(startPoint)

        // Add a marker
        val marker = Marker(mapView)
        marker.position = startPoint
        marker.title = "Casa dos conselhos"
        mapView.overlays.add(marker)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}