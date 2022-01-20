package co.edu.unal.reto_9;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import co.edu.unal.reto_9.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private boolean locationPermissionGranted;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;

    private LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private float DEFAULT_ZOOM = 16.13570929f;

    private Double latitud;
    private Double longitud;
    private Double radio;

    EditText vLatitud;
    EditText vLongitud;
    EditText vRadio;
    Button actualizar;
    Button actual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        vLatitud = (EditText) findViewById(R.id.my_latitud);
        vLongitud = (EditText) findViewById(R.id.my_longitud);
        vRadio = (EditText) findViewById(R.id.my_radio);
        actualizar = (Button) findViewById(R.id.actualizar);
        actual = (Button) findViewById(R.id.actual);



        //Messi
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCoords();
            }
        });

        actual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
        getDeviceLocation();


    }
    public void updateCoords(){
        if(vLatitud.getText().length()>0 && vLongitud.getText().length()>0 && vRadio.getText().length()>0) {
            latitud = Double.parseDouble(String.valueOf(vLatitud.getText()));
            longitud = Double.parseDouble(String.valueOf(vLongitud.getText()));
            radio = Double.parseDouble(String.valueOf(vRadio.getText()));

            double zoom = Math.log(18000 / (radio / 2)) / Math.log(2);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitud, longitud), (float) zoom));
            LatLng ubicacion = new LatLng(latitud, longitud);
            mMap.addMarker(new MarkerOptions().position(ubicacion).title("actual"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));

        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                latitud = lastKnownLocation.getLatitude();
                                longitud = lastKnownLocation.getLongitude();

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitud, longitud), DEFAULT_ZOOM));
                                LatLng ubicacion = new LatLng(latitud, longitud);
                                mMap.addMarker(new MarkerOptions().position(ubicacion).title("actual"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));

                                vLatitud.setText(String.valueOf(latitud));
                                vLongitud.setText(String.valueOf(longitud));
                                vRadio.setText(String.valueOf(0.5));

                            }
                        } else {
                            latitud = defaultLocation.latitude;
                            longitud = defaultLocation.longitude;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  { }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
    }


}



