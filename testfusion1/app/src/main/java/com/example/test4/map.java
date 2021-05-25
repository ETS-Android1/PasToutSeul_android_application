package com.example.test4;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.test4.databinding.ActivityMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

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
    ProgressBar pgrb;


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
        this.pgrb = findViewById(R.id.prgb2);

        Toast.makeText(this,"Bienvenue, "+getUsername(), Toast.LENGTH_LONG).show();


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
        mMap.setMinZoomPreference(14);
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
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 17));
                                    screenLatLng();
                                    setSDFMarkers();
                                }
                            });}}

                        } else{Toast.makeText(getApplicationContext(),"veuillez mettre votre geolocalisation",Toast.LENGTH_SHORT).show();
                        LatLng loc = new LatLng(48.82317818259686,2.5644479786118968);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                        }
                    }
                });


            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        }


        EmmausMarker();
        CHRSMarker();
        toiletteMarker();
        doucheMarker();
        restoMarker();

        pgrb.setVisibility(View.INVISIBLE);

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

                if(marker.getSnippet().equals("emmaus")){
                    AlertDialog.Builder popup = new AlertDialog.Builder(activity);
                    LayoutInflater inflater = activity.getLayoutInflater();
                    View view = inflater.inflate(R.layout.activity_emmaus_marker, null);
                    popup.setView(view);
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
                            // Récupère l'adresse
                            addresses = geocoder.getFromLocation(lat,lng,1);
                            // Ajout du pin dans la bdd
                            addPin(lat,lng,getUserID());
                            System.out.println("Je suis la");
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
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 17));

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

    public void EmmausMarker() {

        try {
            AssetManager am = getAssets();
            InputStream is = am.open("emmaus.xls");
            Workbook wb = Workbook.getWorkbook(is);
            Sheet s = wb.getSheet(0);
            int row = s.getRows();


            for (int i=1; i<row;i++){
                Cell c = s.getCell(2,i);
                String[] latlong = c.getContents().split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                LatLng loc = new LatLng(latitude, longitude);
                String nom = s.getCell(0,i).getContents();
                String tel = s.getCell(4,i).getContents().replace("33","0");

                mMap.addMarker(new MarkerOptions().snippet(tel).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_emmaus)));
            }

        } catch (Exception e) {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }

    }

    public void CHRSMarker() {

        try {
            AssetManager am = getAssets();
            InputStream is = am.open("CHRS.xls");
            Workbook wb = Workbook.getWorkbook(is);
            Sheet s = wb.getSheet(0);
            int row = s.getRows();


            for (int i=1; i<row;i++){
                Cell c = s.getCell(2,i);
                String[] latlong = c.getContents().split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                LatLng loc = new LatLng(latitude, longitude);
                String nom = s.getCell(0,i).getContents();
                String tel = s.getCell(4,i).getContents().replace("33","0");

                mMap.addMarker(new MarkerOptions().snippet(tel).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_refuge)));
            }

        } catch (Exception e) {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }



    }

    public void toiletteMarker() {

        try {
            AssetManager am = getAssets();
            InputStream is = am.open("toilette.xls");
            Workbook wb = Workbook.getWorkbook(is);
            Sheet s = wb.getSheet(0);
            int row = s.getRows();


            for (int i = 1; i < row; i++) {

                String type = s.getCell(0, i).getContents();
                String[] latlong = s.getCell(9, i).getContents().split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                LatLng loc = new LatLng(latitude, longitude);
                String horraire = s.getCell(4, i).getContents();


                mMap.addMarker(new MarkerOptions().snippet(horraire).position(loc).title(type).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_toilettes)));
            }

        } catch (Exception e) {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }
    }


    public void doucheMarker() {

        try {
            AssetManager am = getAssets();
            InputStream is = am.open("douche.xls");
            Workbook wb = Workbook.getWorkbook(is);
            Sheet s = wb.getSheet(0);
            int row = s.getRows();


            for (int i = 1; i < row; i++) {

                String nom = s.getCell(0, i).getContents();
                String[] latlong = s.getCell(2, i).getContents().split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                LatLng loc = new LatLng(latitude, longitude);
                String Site = s.getCell(4, i).getContents();


                mMap.addMarker(new MarkerOptions().snippet(Site).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_douche)));
            }

        } catch (Exception e) {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }
    }


    public void restoMarker() {

        try {
            AssetManager am = getAssets();
            InputStream is = am.open("restoducoeur.xls");
            Workbook wb = Workbook.getWorkbook(is);
            Sheet s = wb.getSheet(0);
            int row = s.getRows();


            for (int i=1; i<row;i++){
                Cell c = s.getCell(2,i);
                String[] latlong = c.getContents().split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                LatLng loc = new LatLng(latitude, longitude);
                String nom = s.getCell(0,i).getContents();
                String tel = s.getCell(4,i).getContents().replace("33","0");

                mMap.addMarker(new MarkerOptions().snippet(tel).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_restos_du_coeur_logo)));
            }

        } catch (Exception e) {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }
    }


    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    /*
    * Procédure : Ajouter un pin dans notre base de données avec une requête de type GET
    */
    public void addPin(double latitude, double longitude, long id_user)
    {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date_bis = dateFormat.format(date);

        RequestQueue queue = Volley.newRequestQueue(this);

        // URL du serveur web
        String URL = "https://db-ezpfla.000webhostapp.com/addPin.php?id="+id_user+"&long="+longitude+"&lat="+latitude+"&date="+date_bis;

        StringRequest postRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            // Appel d'une fonction à éxecuter si succès de la requête
        }, error -> Log.e("Réponse", error.toString()));

        //Lance la requête
        queue.add(postRequest);
    }

    // Récupère le nom d'utilisateur que MainActivity a envoyé vers map
    public String getUsername()
    {
        return getIntent().getStringExtra("USER_NAME");
    }

    // Récupère l'id de l'utilisateur que MainActivity a envoyé vers map
    public long getUserID()
    {
        String id = getIntent().getStringExtra("USER_ID");
        return Long.valueOf(id).longValue();
    }

    public void screenLatLng()
    {
        Double lat = mMap.getCameraPosition().target.latitude;
        Double lng = mMap.getCameraPosition().target.longitude;
        System.out.println("Latitude : "+lat);
        System.out.println("Longitude : "+lng);
    }

    public void setSDFMarkers()
    {

        requestSDFMarkers(res ->
                {
                    if(res.length() == 0){return;}
                    String []line = res.split("<br>");

                    int line_length = line.length;



                    long []id_pin = new long[line_length];
                    double []longitude = new double[line_length];
                    double []latitude = new double[line_length];
                    long []id_user = new long[line_length];
                    String []date = new String[line_length];

                    LatLng latLng;
                    for(int i = 0; i <line_length; i++)
                    {
                        longitude[i] = Double.valueOf(line[i].split("'")[1]);
                        latitude[i] = Double.valueOf(line[i].split("'")[2]);

                        latLng = new LatLng(latitude[i],longitude[i]);
                        mMap.addMarker(new MarkerOptions().snippet("sdf").position(latLng).title("sdf").icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_help__3_)));

                        // A décommenter si nécessaire pour manipuler ces données
                        /*
                        id_pin[i] = Long.valueOf(line[i].split("'")[0]).longValue();
                        id_user[i] = Long.valueOf(line[i].split("'")[3]).longValue();
                        date[i] = line[i].split("'")[4];*/
                    }

                }
                );
    }


    public void requestSDFMarkers(final VolleyCallBack callback)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        // URL du serveur web
        String URL = "https://db-ezpfla.000webhostapp.com/getPin.php?latitude="+mMap.getCameraPosition().target.latitude+"&longitude="+mMap.getCameraPosition().target.longitude;

        StringRequest postRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            callback.onSuccess(response);
            // Appel d'une fonction à éxecuter si succès de la requête
        }, error -> Log.e("Réponse", error.toString()));

        //Lance la requête
        queue.add(postRequest);
    }

    public interface VolleyCallBack{
        void onSuccess(String res);
    }
}
