package com.cborum.traverse;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cborum.traverse.backend.Place;
import com.cborum.traverse.backend.TraverseApiWrapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PlacesMapTab extends Fragment implements OnMapReadyCallback {
    private final String TAG = "PlacesMapTab";

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private TraverseApiWrapper taw;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.places_map_tab, container, false);
        taw = TraverseApiWrapper.Companion.getInstance();
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // todo set onInfoWindowClickListener to go to details for the attraction, like in the gridview
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        mMap = googleMap;
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.style_json));
            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }
//        mMap.getUiSettings().setAllGesturesEnabled(false);
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        updateMapPlaces();
    }

    private void updateMapPlaces() {
        // todo if places aren't different don't update
        if (taw.getPlaces() != null) {
            for (Place p : taw.getPlaces()) {
                // todo markers with image and onclick to detailactivity
                mMap.addMarker(new MarkerOptions().position(
                        new LatLng(p.getGeometry().getLocation().getLat(),
                                p.getGeometry().getLocation().getLng())).title(p.getName()));
            }
            LatLng ll = new LatLng(taw.getLastKnownLocation().getLatitude(), taw.getLastKnownLocation().getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 12));
        }
    }

    @Subscribe
    public void onLocationEvent(Location mLocation) {
        updateMapPlaces();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        Log.d(TAG, "onStart");
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
        Log.d(TAG, "onStop");
    }
}
