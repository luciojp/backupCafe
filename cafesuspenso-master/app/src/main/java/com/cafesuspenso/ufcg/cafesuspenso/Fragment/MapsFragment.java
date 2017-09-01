package com.cafesuspenso.ufcg.cafesuspenso.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.cafesuspenso.ufcg.cafesuspenso.Activity.MainActivity;
import com.cafesuspenso.ufcg.cafesuspenso.Model.Cafeteria;
import com.cafesuspenso.ufcg.cafesuspenso.Model.Product;
import com.cafesuspenso.ufcg.cafesuspenso.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MapsFragment extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng myLocation;
    private boolean permissionAsked;
    private static final int LOCATION_REQUEST_CODE = 12;

    private LocationManager mLocationManager;
    private MainActivity main;
    private List<Cafeteria> cafeterias;
    Cafeteria selected;
    private Product product1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (MainActivity) getActivity();
        main.moveBottomBarDown();
        getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        loadMarkers(mMap);

        myLocation = getMyLocation();
        if(myLocation != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f));
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        addComplaintInUserLocation(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                selected = null;
                main.setCafeteriaSelected(null);
                for(Cafeteria c: cafeterias){
                    if(c.getLocation().equals(marker.getPosition())) {
                        main.setCafeteriaSelected(c);
                        selected = c;
                        break;
                    }
                }

                if(selected != null)
                    main.moveBottomBarUp(marker.getTitle(), selected.getAvailableCoffee());
                return true;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {


            @Override
            public void onMapClick(LatLng latLng) {
                main.moveBottomBarDown();
            }
        });
    }

    private void loadMarkers(GoogleMap mMap) {
        cafeterias = new ArrayList<>();
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String JSONMarkers = sharedPref.getString("cafeterias", null);

        if(JSONMarkers != null){
            Random random = new Random();
            try {
                JSONArray responsePost = new JSONArray(JSONMarkers);
                for (int i = 0; i < responsePost.length(); i++) {
                    JSONObject marked = responsePost.getJSONObject(i);

                    String name = marked.getString("name");
                    JSONObject location = marked.getJSONObject("location");
                    LatLng latLng = new LatLng(location.getDouble("lat"),location.getDouble("lng"));
                    String imagem = marked.getString("imagem");
                    int qntdCafe = marked.getInt("numberProduct");

                    JSONObject product = marked.getJSONObject("product");
                    product1 = new Product(product.getInt("id"), product.getDouble("price"),product.getString("image"), product.getString("description"), product.getBoolean("accepted"), product.getString("name"));
                    cafeterias.add(new Cafeteria(name,latLng,imagem,qntdCafe, product1));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        Bitmap coffeeBig = BitmapFactory.decodeResource(getResources(), R.drawable.coffee);
        Bitmap resized = Bitmap.createScaledBitmap(coffeeBig, 50, 50, true);
        BitmapDescriptor coffee = BitmapDescriptorFactory.fromBitmap(resized);

        Bitmap coffeegrayBig = BitmapFactory.decodeResource(getResources(), R.drawable.coffeegray);
        Bitmap resized2 = Bitmap.createScaledBitmap(coffeegrayBig, 50, 50, true);
        BitmapDescriptor coffeegray = BitmapDescriptorFactory.fromBitmap(resized2);
        for(Cafeteria c : cafeterias){
            if(c.getAvailableCoffee() > 0){
                mMap.addMarker(new MarkerOptions().position(c.getLocation()).title(c.getPlacename()).icon(coffee));
            } else {
                mMap.addMarker(new MarkerOptions().position(c.getLocation()).title(c.getPlacename()).icon(coffeegray));
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.content_main, viewGroup, false);

        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    private void addComplaintInUserLocation(boolean isFromMap) {
        myLocation = getMyLocation();
        try {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f));

            if (myLocation == null)
                openMapPermissionDialog();
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Your gps is off, please turn on and try again", Toast.LENGTH_SHORT).show();
        }
    }

    private LatLng getMyLocation() {
        Location first = getLastBestLocation();
        if (first != null)
            return new LatLng(first.getLatitude(), first.getLongitude());

        Location second = getLastKnownLocation();
        if (second != null)
            return new LatLng(second.getLatitude(), second.getLongitude());

        return null;
    }

    private Location getLastKnownLocation() {
        if (mLocationManager == null) return null;
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    private Location getLastBestLocation() {

        Location response = null;


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            long GPSLocationTime = 0;
            if (locationGPS != null) {
                GPSLocationTime = locationGPS.getTime();
            }

            long NetLocationTime = 0;

            if (locationNet != null) {
                NetLocationTime = locationNet.getTime();
            }

            if (0 < GPSLocationTime - NetLocationTime) {
                response = locationGPS;
            } else {
                response = locationNet;
            }
        }
        return response;
    }

    private void verifyGpsState() {
        boolean locationPermission = ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        if (locationPermission) {

            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            }
        } else if (!permissionAsked) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);

            permissionAsked = true;

        }


        /**
         mLocationFAB.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        addComplaintInUserLocation(true);
        }
        });
         */
    }

    @Override
    public void onResume() {
        super.onResume();

        verifyGpsState();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.message_gps_off))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void openMapPermissionDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage(getString(R.string.map_permission_error));
        builder1.setTitle(getString(R.string.map_permission_error_title));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
