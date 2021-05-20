package com.example.test4;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.test4.databinding.ActivityMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private map activity;
    private ActivityMapBinding binding;
    private FusedLocationProviderClient mflc;
    HashMap hashMapMarker = new HashMap<String, Marker>();
    boolean mclick;
    ImageButton btnmclick;
    EditText prenom;
    EditText Nom;
    EditText Comm;
    TextView Disprenom;
    TextView Dispnom;
    TextView Dispcomm;
    Geocoder geocoder;
    List<Address> addresses;
    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.activity = this;
        this.mclick = false;
        this.btnmclick = findViewById(R.id.btnmapclick);
        this.prenom = findViewById(R.id.editText2);
        this.Nom = findViewById(R.id.editText3);
        this.Comm = findViewById(R.id.editTextTextPersonName);
        this.Disprenom = findViewById(R.id.textView2);
        this.Dispnom = findViewById(R.id.textView13);
        this.Dispcomm = findViewById(R.id.textView14);
        this.mflc = LocationServices.getFusedLocationProviderClient(this);

        //listView = findViewById(R.id.sp)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.setMapType(3);
        //mMap.setCompasEnabled(true);
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mflc.getLocationAvailability().addOnSuccessListener(new OnSuccessListener<LocationAvailability>() {
                    @Override
                    public void onSuccess(LocationAvailability locationAvailability) {
                        if (locationAvailability.isLocationAvailable()){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                        == PackageManager.PERMISSION_GRANTED) {
                            mflc.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {

                                    double lat = location.getLatitude();
                                    double lon = location.getLongitude();
                                    LatLng here = new LatLng(lat, lon);
                                    Marker marker = mMap.addMarker(new MarkerOptions().snippet("userLocation").position(here).title("here").icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_add_location_24)));
                                    hashMapMarker.put("userLocation", marker);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 15));

                                }
                            });}}

                        } else{Toast.makeText(getApplicationContext(),"veuillez mettre votre geolocalisation",Toast.LENGTH_SHORT).show();}
                    }
                });


            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        }


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getSnippet().equals("sdf")) {
                    String markertitle = marker.getTitle();
                    AlertDialog.Builder popup = new AlertDialog.Builder(activity);
                    popup.setTitle(markertitle);
                    LayoutInflater inflater = activity.getLayoutInflater();
                    View view = inflater.inflate(R.layout.marker_layout, null);
                    popup.setView(view);
                    popup.setMessage("les coordonnées sont " + marker.getPosition());
                    popup.setPositiveButton("envoyer un message", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    popup.setNegativeButton("Enlever le marker", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            marker.remove();
                        }
                    });
                    popup.show();
                }
                return false;
            }
        });


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {if(mclick){

                lat = latLng.latitude;
                lng = latLng.longitude;

                AlertDialog.Builder areYouSure = new AlertDialog.Builder(activity);
                areYouSure.setTitle("vérification");
                View customAddMarkerLayout = getLayoutInflater().inflate(R.layout.addmarker_layout, null);
                areYouSure.setView(customAddMarkerLayout);
                areYouSure.setMessage("veuillez entrer des informations sur la personne");
                //areYouSure.setIcon()
                areYouSure.setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // si sdf est choisi
                        mMap.addMarker(new MarkerOptions().snippet("sdf").position(latLng).title("sdf").icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_help__3_)));
                        mapclick2();
                        geocoder = new Geocoder(activity, Locale.getDefault());
                        try {
                            addresses = geocoder.getFromLocation(lat,lng,1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (addresses.size() > 0)
                        {

                            //addresses.get(0).getLocality()   avoir le nom de la ville
                            //addresses.get(0).getPostalCode() avoir le code postale
                            //Log.d("location", addresses.get(0).getPostalCode());
                        }
                        else { }

                    }
                });
                areYouSure.setNegativeButton("non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                areYouSure.show();

            }}
        }
        );



    }


    public void Click(View v) {
        Intent i = new Intent(this, menu.class);
        startActivity(i);

    }

    public void locatezoom(View v){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mflc.getLocationAvailability().addOnSuccessListener(new OnSuccessListener<LocationAvailability>() {
                    @Override
                    public void onSuccess(LocationAvailability locationAvailability) {
                        if (locationAvailability.isLocationAvailable()){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    mflc.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {

                                            double lat = location.getLatitude();
                                            double lon = location.getLongitude();
                                            LatLng here = new LatLng(lat, lon);
                                            Marker marker = mMap.addMarker(new MarkerOptions().snippet("userLocation").position(here).title("here").icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_add_location_24)));
                                            hashMapMarker.put("userLocation", marker);
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 15));

                                        }
                                    });}}

                        } else{Toast.makeText(getApplicationContext(),"veuillez mettre votre geolocalisation",Toast.LENGTH_SHORT).show();}
                    }
                });


            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        }

    }

    public void mapclick(View v)
    {

        if(mclick)
        {
            btnmclick.setBackgroundResource(R.drawable.helpadd);
            mclick = false;
        }
        else
        {
            btnmclick.setBackgroundResource(R.drawable.mapclickno);
            mclick = true;
            Toast.makeText(getApplicationContext(), "Veuillez placer un marqueur",Toast.LENGTH_SHORT).show();
        }
    }

    public  void mapclick2(){
        btnmclick.setBackgroundResource(R.drawable.helpadd);
        mclick = false;
    }



    public void chat(View v) {
        Intent i = new Intent(this, chat.class);
        startActivity(i);

    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}