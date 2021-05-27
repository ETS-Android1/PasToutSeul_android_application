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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class map extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener{

    private GoogleMap mMap;
    private map activity;
    private ActivityMapBinding binding;
    private FusedLocationProviderClient mflc;

    HashMap hashMapMarker = new HashMap<String, Marker>();

    boolean mclick;

    ImageButton btnmclick;

    EditText prenom;
    EditText Comm;
    EditText Envie;

    TextView Disprenom;
    TextView Dispenvie;
    TextView Dispcomm;

    Geocoder geocoder;

    List<Address> addresses;

    double lat;
    double lng;

    ProgressBar pgrb;

    RelativeLayout layoutMap;

    RadioGroup rg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.slide_right,R.anim.slide_left);

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
        this.Comm = findViewById(R.id.editTextTextPersonName);
        this.Envie = findViewById(R.id.editTextTextEnvie);
        this.Disprenom = findViewById(R.id.textView2);
        this.Dispenvie = findViewById(R.id.editTextTextEnvie);
        this.Dispcomm = findViewById(R.id.textView14);
        this.mflc = LocationServices.getFusedLocationProviderClient(this);
        this.pgrb = findViewById(R.id.prgb2);
        this.rg = findViewById(R.id.radioGroup);


        //change de place la toolbar google maps
        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).
                getParent()).findViewById(Integer.parseInt("4"));
        // and next place it, for example, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 50, 100);

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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.setMinZoomPreference(14);
        mMap.setMapType(3);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);




        //mMap.setCompasEnabled(true);
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mflc.getLocationAvailability().addOnSuccessListener(new OnSuccessListener<LocationAvailability>() {
                    @Override
                    public void onSuccess(LocationAvailability locationAvailability)
                    {
                        if (locationAvailability.isLocationAvailable()){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            {
                                if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                        == PackageManager.PERMISSION_GRANTED)
                                {
                                    mflc.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>()
                                    {
                                        @Override
                                        public void onSuccess(Location location)
                                        {
                                            // On récupère les coordonnées GPS de l'utilisateur
                                            double latitude = location.getLatitude();
                                            double longitude = location.getLongitude();

                                            // Conversion en LatLng
                                            LatLng here = new LatLng(latitude, longitude);

                                            //Création du marqueur de l'utilisateur
                                            //Marker marker = mMap.addMarker(new MarkerOptions().snippet("userLocation").position(here).title("here").icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_add_location_24)));
                                            //hashMapMarker.put("userLocation", marker);
                                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(here, 17),1000,null);

                                            // Affichage des pins concernant les SDF autour de l'utilisateur
                                            setSDFMarkers();
                                        }
                                    });
                                }
                            }

                        }
                        else
                        {
                            // Pas d"accès à la géolocalisation
                            Toast.makeText(getApplicationContext(),"Veuillez mettre votre geolocalisation",Toast.LENGTH_SHORT).show();
                            LatLng loc = new LatLng(48.82317818259686,2.5644479786118968);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                        }
                    }
                });

            }
            else
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        }

        //Création des marqueurs
        EmmausMarker();
        CHRSMarker();
        toiletteMarker();
        doucheMarker();
        restoMarker();

        pgrb.setVisibility(View.INVISIBLE);


        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {

                if (marker.getSnippet().charAt(0) == '3' || marker.getSnippet().charAt(0) == '0'){
                    String tel = "tel:"+ marker.getSnippet();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(tel));
                    startActivity(intent);
                }

                if (marker.getSnippet().charAt(0) == 'h'){
                    String site = marker.getSnippet();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(site));
                    startActivity(intent);
                }

            }
        });

        // Action lors d'un clic sur un marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //if (marker.getTitle().equals("Sans abri")) {
                    //String markertitle = marker.getTitle();
                    //AlertDialog.Builder popup = new AlertDialog.Builder(activity);
                    //popup.setTitle(markertitle);
                    //LayoutInflater inflater = activity.getLayoutInflater();
                    /*View view = inflater.inflate(R.layout.marker_layout, null);
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

                     */
                //}

                if(marker.getSnippet().equals("emmaus")){
                    AlertDialog.Builder popup = new AlertDialog.Builder(activity);
                    LayoutInflater inflater = activity.getLayoutInflater();
                    View view = inflater.inflate(R.layout.activity_emmaus_marker, null);
                    popup.setView(view);
                }

                return false;
            }
        });

        //Quand on clique sur la fenêtre d'un marqueur
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.getTitle().equals("Sans abri"))
                {
                    // Affichage d'une fenêtre avec des informations supplémentaires
                    AlertDialog.Builder moreInfos = new AlertDialog.Builder(activity,R.style.MyDialogTheme);
                    View customAddMarkerLayout = getLayoutInflater().inflate(R.layout.marker_layout, null);
                    moreInfos.setView(customAddMarkerLayout);
                    moreInfos.setTitle("Plus d'informations");
                    moreInfos.setMessage(marker.getSnippet());

                    moreInfos.setNeutralButton("Signaler une erreur", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Fenêtre pour confirmer son choix
                            AlertDialog.Builder oui_non = new AlertDialog.Builder(activity);

                            oui_non.setTitle("Êtes-vous sûr ?");
                            oui_non.setMessage("Ce pin sera supprimer au bout de 2 signalements");

                            // On annule le signalement.
                            oui_non.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((ViewGroup)customAddMarkerLayout.getParent()).removeView(customAddMarkerLayout);
                                    moreInfos.show();
                                }
                            });

                            // Si on valide, alors on envoie le signalement.
                            oui_non.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((ViewGroup)customAddMarkerLayout.getParent()).removeView(customAddMarkerLayout);
                                    LatLng latLng = marker.getPosition();
                                    requestReport(latLng.latitude, latLng.longitude, new VolleyCallBack() {
                                        @Override
                                        public void onSuccess(String res) {
                                            if(res.equals("delete"))
                                            {
                                                // Actualise les marqueurs
                                                mMap.clear();
                                                EmmausMarker();
                                                CHRSMarker();
                                                toiletteMarker();
                                                doucheMarker();
                                                restoMarker();
                                            }
                                            setSDFMarkers();
                                        }
                                    });

                                    moreInfos.show();
                                }
                            });

                            oui_non.show();
                        }
                    });

                    moreInfos.setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    moreInfos.show();
                }
            }
        });

        // Détection d'un click sur la map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng) {if(mclick)
            {
                // Coordonnées GPS
                lat = latLng.latitude;
                lng = latLng.longitude;


                AlertDialog.Builder areYouSure = new AlertDialog.Builder(activity,R.style.MyDialogTheme);
                areYouSure.setTitle("Vérification");

                View customAddMarkerLayout = getLayoutInflater().inflate(R.layout.addmarker_layout, null);

                areYouSure.setView(customAddMarkerLayout);
                areYouSure.setMessage("Veuillez entrer des informations sur la personne");

                areYouSure.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // si sdf est choisi
                        Date date = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String []string_date = dateFormat.format(date).split(" ");

                        String jour = string_date[0];
                        String heure = string_date[1];

                        //déclaration de l'int pr savoir si le sdf est un homme, une femme ou un handicapé
                        int selectedId = rg.getCheckedRadioButtonId();
                        //string du prénom, commentaire et envie du sdf
                        //prenom.getText();
                        //Comm.getText()
                        //Envie.getText()

                        mMap.addMarker(new MarkerOptions().snippet("Vu la dernière fois par "+getUsername()+" le "+yyyy_mm_ddTodd_mm_yyyy(jour)+" à "+heure+" (...)").position(latLng).title("Sans abri").icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_help__3_)));
                        addPin(lat,lng,getUserID(),jour+" "+heure);
                        mapclick2();
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

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener()
        {
            @Override
            public void onCameraMoveStarted(int reason) {
                // REASON_GESTURE : Mouvement de l'écran via l'utilisateur
                // REASON_DEVELOPER_ANIMATION : Mouvement de l'écran via l'application
                if (reason == REASON_GESTURE || reason == REASON_DEVELOPER_ANIMATION) {
                    setSDFMarkers();
                }
            }
        });

    }

    public void quit()
    {
        AlertDialog.Builder quit = new AlertDialog.Builder(activity);

        quit.setTitle("Êtes-vous sûr de vouloir quitter ?");

        quit.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestDisconnect();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });

        quit.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        quit.show();
    }

    @Override
    public void onBackPressed()
    {
        quit();
    }

    //Bouton quitter
    public void quitBtn(View view)
    {
        quit();
    }

    // Click sur le menu
    public void Click(View v)
    {
        Intent i = new Intent(this, menu.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    
    public void locatezoom(View v)
    {
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
                                            //Marker marker = mMap.addMarker(new MarkerOptions().snippet("userLocation").position(here).title("here").icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_add_location_24)));
                                            //hashMapMarker.put("userLocation", marker);
                                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(here, 17),1000,null);

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
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

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

                // Tri des données
                String[] latlong = c.getContents().split(",");

                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);

                LatLng loc = new LatLng(latitude, longitude);

                String nom = s.getCell(0,i).getContents();
                String site = s.getCell(3,i).getContents();
                String tel = s.getCell(4,i).getContents();

                if (tel == ""){
                    mMap.addMarker(new MarkerOptions().snippet(site).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_emmaus)));
                }
                else {
                    mMap.addMarker(new MarkerOptions().snippet(tel).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_emmaus)));
                }
            }

        } catch (Exception e) {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }

    }

    // Ajout des marqueurs des CHRS.
    public void CHRSMarker() {

        try {
            AssetManager am = getAssets();

            InputStream is = am.open("CHRS.xls");

            Workbook wb = Workbook.getWorkbook(is);

            Sheet s = wb.getSheet(0);

            int row = s.getRows();

            for (int i=1; i<row;i++)
            {
                Cell c = s.getCell(2,i);

                // Tri des données
                String[] latlong = c.getContents().split(",");

                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);

                LatLng loc = new LatLng(latitude, longitude);

                String nom = s.getCell(0,i).getContents();
                String site = s.getCell(3,i).getContents();
                String tel = s.getCell(4,i).getContents();
                if (tel == "") {
                    mMap.addMarker(new MarkerOptions().snippet(site).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_refuge)));
                }
                else {
                    mMap.addMarker(new MarkerOptions().snippet(tel).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_refuge)));
                }
            }

        } catch (Exception e)
        {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }
    }

    // Ajout des marqueurs des toilettes publiques.
    public void toiletteMarker() {

        try {
            AssetManager am = getAssets();

            InputStream is = am.open("toilette.xls");

            Workbook wb = Workbook.getWorkbook(is);

            Sheet s = wb.getSheet(0);

            int row = s.getRows();

            for (int i = 1; i < row; i++) {

                String type = s.getCell(0, i).getContents();

                // Tri des données
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

    // Ajout des marqueurs des douches publiques.
    public void doucheMarker() {

        try {
            AssetManager am = getAssets();

            InputStream is = am.open("douche.xls");

            Workbook wb = Workbook.getWorkbook(is);

            Sheet s = wb.getSheet(0);

            int row = s.getRows();


            for (int i = 1; i < row; i++) {

                String nom = s.getCell(0, i).getContents();

                // Tri des données
                String[] latlong = s.getCell(2, i).getContents().split(",");

                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);

                LatLng loc = new LatLng(latitude, longitude);

                String Site = s.getCell(4, i).getContents();

                mMap.addMarker(new MarkerOptions().snippet(Site).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_douche)));
            }

        }
        catch (Exception e)
        {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }
    }

    // Ajout des marqueur des restos du coeur.
    public void restoMarker() {

        try {
            AssetManager am = getAssets();

            InputStream is = am.open("restoducoeur.xls");

            Workbook wb = Workbook.getWorkbook(is);

            Sheet s = wb.getSheet(0);

            int row = s.getRows();

            for (int i=1; i<row;i++){
                Cell c = s.getCell(2,i);

                // Tri des données
                String[] latlong = c.getContents().split(",");

                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);

                LatLng loc = new LatLng(latitude, longitude);

                String nom = s.getCell(0,i).getContents();
                String site = s.getCell(3,i).getContents();
                String tel = s.getCell(4,i).getContents().replace("33","0");

                if (tel == "") {
                    mMap.addMarker(new MarkerOptions().snippet(site).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_restos_du_coeur_logo)));
                }
                else {
                    mMap.addMarker(new MarkerOptions().snippet(tel).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_restos_du_coeur_logo)));
                }
            }

        } catch (Exception e)
        {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }
    }

    // Récupère les coordonnées GPS à partir du lieu où l'on se trouve
    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);

            // adresss vaut null si la géolocalisation n'est pas activé
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

    // Ajouter un pin dans notre base de données via une requête de type GET
    public void addPin(double latitude, double longitude, long id_user, String date)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        String URL = "https://db-ezpfla.000webhostapp.com/addPin.php?id="+id_user+"&long="+longitude+"&lat="+latitude+"&date="+date;

        StringRequest postRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
        }, error -> Log.e("Réponse", error.toString()));

        // Ajoute la requête dans la file
        queue.add(postRequest);
    }

    // Récupère le nom d'utilisateur
    public String getUsername()
    {
        return getIntent().getStringExtra("USER_NAME");
    }

    // Récupère l'id de l'utilisateur
    public long getUserID()
    {
        String id = getIntent().getStringExtra("USER_ID");
        return Long.valueOf(id).longValue();
    }

    // Ajout d'un marqueur pour chaque coordonnées GPS que l'on reçoit via une requête
    public void setSDFMarkers()
    {
        requestSDFMarkers(res ->
                {
                    if(res.length() != 0){

                        // Tri les informations obtenus ligne par ligne
                        String []line = res.split("<br>");
                        int line_length = line.length;

                        // Informations sur les pins
                        double longitude;
                        double latitude;
                        String date;
                        String heure;
                        String nom_user;

                        String string[] = new String[6];


                        for(int i = 0; i < line_length; i++)
                        {
                            // Tri une nouvelle fois les informations mais en détails
                            string = line[i].split(" ");

                            longitude = Double.parseDouble(string[1]);
                            latitude = Double.parseDouble(string[2]);
                            date = string[4];
                            heure = string[5];
                            nom_user = string[6];

                            // Ajout d'un pin
                            mMap.addMarker(new MarkerOptions().snippet("Vu la dernière fois par "+nom_user+" le "+yyyy_mm_ddTodd_mm_yyyy(date)+" à "+heure+" (...)").position(new LatLng(latitude,longitude)).title("Sans abri").icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_help__3_)));

                        }
                    }

                }
                );
    }

    // Déclaration et envoi d'une requête pour récupérer des données sur les marqueurs dans la BDD.
    public void requestSDFMarkers(final VolleyCallBack callback)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        String URL = "https://db-ezpfla.000webhostapp.com/getPin.php?latitude="+mMap.getCameraPosition().target.latitude+"&longitude="+mMap.getCameraPosition().target.longitude;

        // Envoi de la requête et on redirige la réponse à sa réception
        StringRequest postRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            callback.onSuccess(response.trim());
        }, error -> Log.e("Réponse", error.toString()));

        // Ajout de la requête dans la file
        queue.add(postRequest);
    }

    // Déclaration et envoi d'une requête pour récupérer des données sur les marqueurs dans la BDD.
    public void requestReport(double latitude, double longtitude, VolleyCallBack callBack)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        String URL = "https://db-ezpfla.000webhostapp.com/reportPin.php?username="+getUsername()+"&latitude="+mMap.getCameraPosition().target.latitude+"&longitude="+mMap.getCameraPosition().target.longitude;

        // Envoi de la requête et on redirige la réponse à sa réception
        StringRequest postRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            callBack.onSuccess(response.trim());
        }, error -> Log.e("Réponse", error.toString()));

        // Ajout de la requête dans la file
        queue.add(postRequest);
    }

    public void requestDisconnect()
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        String URL = "https://db-ezpfla.000webhostapp.com/disconnect.php?username="+getUsername();

        // Envoi de la requête et on redirige la réponse à sa réception
        StringRequest postRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            System.out.println(response);
        }, error -> Log.e("Réponse", error.toString()));

        // Ajout de la requête dans la file
        queue.add(postRequest);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }




    public interface VolleyCallBack{
        void onSuccess(String res);
    }

    // Converti une date yyyy-dd-mm en dd-mm-yyyy
    public String yyyy_mm_ddTodd_mm_yyyy(String date)
    {
        String []string = date.split("-");

        return string[2]+"/"+string[1]+"/"+string[0];
    }
}
