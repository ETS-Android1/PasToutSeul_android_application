package com.example.test4;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.test4.databinding.ActivityMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
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

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class map extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    Context context;
    Requete requete;

    private GoogleMap mMap;
    private map activity;
    private FusedLocationProviderClient mflc;

    TextView tw;

    HashMap hashMapMarker = new HashMap<String, Marker>();

    boolean mclick;

    ImageButton btnmclick;

    double lat;
    double lng;

    ProgressBar pgrb;

    RadioButton rb1,rb2,rb3;

    int icone;

    Utilisateur utilisateur;

    View view_popup;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        com.example.test4.databinding.ActivityMapBinding binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.context = this;
        this.requete = new Requete(context);

        this.mclick = false;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Fenêtre popup pour ajouter un marqueur
        this.view_popup = getLayoutInflater().inflate(R.layout.addmarker_layout, null);
        this.builder = new AlertDialog.Builder(this,R.style.MyDialogTheme).setView(this.view_popup);

        // Informations sur l'utilisateur
        utilisateur = new Utilisateur(String.valueOf(getUserID()),getUsername(),getUserMail(),getUserPassword());

        this.activity = this;

        //pastoutsuel sur la toolbar en montserrat
        this.tw = findViewById(R.id.textView);
        Typeface typeface = ResourcesCompat.getFont(activity, R.font.montserrat);
        tw.setTypeface(typeface);

        this.btnmclick = findViewById(R.id.btnmapclick);

        this.mflc = LocationServices.getFusedLocationProviderClient(this);
        this.pgrb = findViewById(R.id.progressBarMap);


        this.rb1 = this.view_popup.findViewById(R.id.radioButton);
        this.rb2 = this.view_popup.findViewById(R.id.radioButton2);
        this.rb3 = this.view_popup.findViewById(R.id.radioButton3);
        this.icone = 0;
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

        Toast.makeText(this,"Bienvenue, "+utilisateur.username, Toast.LENGTH_LONG).show();

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
    @SuppressLint({"MissingPermission", "SetTextI18n"})
    @Override
    public void onMapReady(@NotNull GoogleMap googleMap)
    {
        pgrb.setVisibility(View.VISIBLE);

        mMap = googleMap;

        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.setMinZoomPreference(13);
        mMap.setMapType(3);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        // pour stylisée les textview qui prennent des infos de la base de donée
        Typeface typeface = ResourcesCompat.getFont(activity, R.font.montserrat);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mflc.getLocationAvailability().addOnSuccessListener(locationAvailability -> {
                    if (locationAvailability.isLocationAvailable()){
                        if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED)
                        {
                            mflc.getLastLocation().addOnSuccessListener(location ->
                            {
                                // On récupère les coordonnées GPS de l'utilisateur
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                // Conversion en LatLng
                                LatLng here = new LatLng(latitude, longitude);

                                //Création du marqueur de l'utilisateur
                                //Marker marker = mMap.addMarker(new MarkerOptions().snippet("userLocation").position(here).title("here").icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_add_location_24)));
                                //hashMapMarker.put("userLocation", marker);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 17));

                                // Affichage des pins concernant les SDF autour de l'utilisateur
                                setSDFMarkers();
                            });
                        }
                    }

                    else
                    {
                        // Pas d"accès à la géolocalisation
                        Toast.makeText(getApplicationContext(),"Veuillez mettre votre geolocalisation",Toast.LENGTH_SHORT).show();
                        LatLng loc = new LatLng(48.82317818259686,2.5644479786118968);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));

                    }
                    pgrb.setVisibility(View.INVISIBLE);
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
        banque();
        Captif();
        stVincentPaul();
        secoursIslamique();
        secoursCatholique();
        secoursPopulaire();
        croixrouge();
        ANRS();

        mMap.setOnInfoWindowClickListener(marker ->
        {
            if (marker.getSnippet().charAt(0) == '3' || marker.getSnippet().charAt(0) == '0'){
                String tel = "tel:"+ marker.getSnippet();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(tel));
                startActivity(intent);
            }

            else if (marker.getSnippet().charAt(0) == 'h'){
                String site = marker.getSnippet();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(site));
                startActivity(intent);
            }

        });


        //Quand on clique sur la fenêtre d'un marqueur
        mMap.setOnInfoWindowClickListener(marker ->
        {
            if (marker.getTitle().contains("Sans abri"))
            {
                pgrb.setVisibility(View.VISIBLE);
                String[] infos = marker.getTitle().split(" ");
                String id_pin = infos[2];

                // Affichage d'une fenêtre avec des informations supplémentaires
                AlertDialog.Builder moreInfos = new AlertDialog.Builder(activity,R.style.MyDialogTheme2);

                View customAddMarkerLayout = getLayoutInflater().inflate(R.layout.marker_layout, null);

                // Affichage des infos
                TextView prenom = customAddMarkerLayout.findViewById(R.id.textViewPrenom);
                TextView commentaire = customAddMarkerLayout.findViewById(R.id.textViewCommentaire);
                TextView envie = customAddMarkerLayout.findViewById(R.id.textViewEnvie);

                moreInfos.setView(customAddMarkerLayout);
                moreInfos.setTitle("Plus d'informations");
                moreInfos.setMessage(marker.getSnippet());

                moreInfos.setNeutralButton("Signaler une erreur", (dialog, which) ->
                {
                    //Fenêtre pour confirmer son choix
                    AlertDialog.Builder oui_non = new AlertDialog.Builder(activity,R.style.MyDialogTheme2);

                    oui_non.setTitle("Êtes-vous sûr ?");
                    oui_non.setMessage("Ce pin sera supprimer au bout de 2 signalements");

                    // On annule le signalement.
                    oui_non.setNegativeButton("Non", (dialog1, which1) ->
                    {
                        ((ViewGroup)customAddMarkerLayout.getParent()).removeView(customAddMarkerLayout);
                        moreInfos.show();
                    });

                    // Si on valide, alors on envoie le signalement.
                    oui_non.setPositiveButton("Oui", (dialog12, which12) ->
                    {
                        ((ViewGroup)customAddMarkerLayout.getParent()).removeView(customAddMarkerLayout);

                        requete.reportPin(getUsername(),id_pin, res -> {
                            if(res.equals("delete"))
                            {
                                // Actualise les marqueurs
                                mMap.clear();
                                EmmausMarker();
                                CHRSMarker();
                                toiletteMarker();
                                doucheMarker();
                                restoMarker();
                                banque();
                                Captif();
                                stVincentPaul();
                                secoursIslamique();
                                secoursCatholique();
                                secoursPopulaire();
                                croixrouge();
                                ANRS();
                            }
                            setSDFMarkers();
                        });
                    });
                    oui_non.show();
                });

                moreInfos.setPositiveButton("Fermer", (dialog, which) -> dialog.cancel());

                requete.sdfInfos(Integer.parseInt(id_pin), res ->
                {
                    String[] strings = res.split("<!§!>");

                    prenom.setText(strings[0]);
                    prenom.setTypeface(typeface);
                    commentaire.setText("Envie : "+strings[2]);
                    commentaire.setTypeface(typeface);
                    envie.setText("Commentaire : "+strings[1]);
                    envie.setTypeface(typeface);
                    moreInfos.show();

                    pgrb.setVisibility(View.INVISIBLE);
                });
            }
            else if (marker.getSnippet().charAt(0) == '3' || marker.getSnippet().charAt(0) == '0'){
                String tel = "tel:"+ marker.getSnippet();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(tel));
                startActivity(intent);
            }
            else if (marker.getSnippet().charAt(0) == 'h'){
                String site = marker.getSnippet();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(site));
                startActivity(intent);
            }
        });

        // Détection d'un click sur la map
        mMap.setOnMapClickListener(latLng ->
        {
            if(mclick)
            {
                // Coordonnées GPS
                lat = latLng.latitude;
                lng = latLng.longitude;

                AlertDialog popup = builder.show();

                // Desactive les click en dehors de la fenêtre
                popup.setCanceledOnTouchOutside(false);

                // Champ de saisie de la fenêtre popup
                EditText editTxtPrenom = view_popup.findViewById(R.id.editText2);
                EditText editTxtComment = view_popup.findViewById(R.id.editTextTextPersonName);
                EditText editTxtEnvie = view_popup.findViewById(R.id.editTextTextEnvie);

                // Bouton de la fenêtre popup
                Button valider = view_popup.findViewById(R.id.btnMarkerValider);
                Button annuler = view_popup.findViewById(R.id.btnMarkerAnnuler);

                // Bouton retour
                popup.setOnKeyListener((arg0, keyCode, event) -> {
                    // Click sur le bouton retour
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        ((ViewGroup) view_popup.getParent()).removeView(view_popup);
                        popup.dismiss();
                    }
                    return true;
                });

                // Bouton annuler
                annuler.setOnClickListener(v -> {
                    ((ViewGroup) view_popup.getParent()).removeView(view_popup);

                    editTxtPrenom.setText("");
                    editTxtComment.setText("");
                    editTxtEnvie.setText("");

                    popup.dismiss();
                });

                // Bouton valider
                valider.setOnClickListener(v -> {
                    pgrb.setVisibility(View.VISIBLE);

                    ((ViewGroup) view_popup.getParent()).removeView(view_popup);

                    // Récupération de la saisie de l'utilisateur
                    String stringPrenom = editTxtPrenom.getText().toString();
                    String stringComment = editTxtComment.getText().toString();
                    String stringEnvie = editTxtEnvie.getText().toString();

                    // Récupération de la date
                    Date date = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

                    // Récupère le jour et l'heure
                    String[] string_date = dateFormat.format(date).split(" ");
                    String jour = string_date[0];
                    String heure = string_date[1];

                    requete.addPin(lat, lng, Long.parseLong(utilisateur.id_user), jour, heure, icone, stringPrenom, stringComment, stringEnvie, res ->
                    {
                        setSDFMarkers();
                        pgrb.setVisibility(View.INVISIBLE);
                    });

                    mapclick2();

                    editTxtPrenom.setText("");
                    editTxtComment.setText("");
                    editTxtEnvie.setText("");

                    popup.dismiss();
                });
            }
        });

        // Action à réaliser lorsque le caméra a bougé
        mMap.setOnCameraMoveStartedListener(reason -> {
            // REASON_GESTURE : Mouvement de l'écran via l'utilisateur
            // REASON_DEVELOPER_ANIMATION : Mouvement de l'écran via l'application
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE || reason == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION)
            {
                setSDFMarkers();
            }
        });
    }

    public void quit()
    {
        AlertDialog.Builder quit = new AlertDialog.Builder(activity,R.style.MyDialogTheme2);

        quit.setTitle("Êtes-vous sûr de vouloir quitter ?");

        quit.setPositiveButton("Oui", (dialog, which) ->
        {
            // Déconnexion
            requete.disconnect(getUsername());

            Context context = getApplicationContext();
            Intent intent = new Intent(context,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        });

        quit.setNegativeButton("Non", (dialog, which) -> dialog.cancel());

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

    public void checkRB1(View view)
    {
        icone = 0;
    }

    public void checkRB2(View view)
    {
        icone = 1;
    }

    public void checkRB3(View view)
    {
        icone = 2;
    }

    public void locatezoom(View v)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mflc.getLocationAvailability().addOnSuccessListener(locationAvailability ->
                {
                    if (locationAvailability.isLocationAvailable()){
                        if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            mflc.getLastLocation().addOnSuccessListener(location ->
                            {
                                double lat = location.getLatitude();
                                double lon = location.getLongitude();
                                LatLng here = new LatLng(lat, lon);

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(here, 17),1000,null);
                            });}

                    } else{Toast.makeText(getApplicationContext(),"Veuillez mettre votre geolocalisation",Toast.LENGTH_SHORT).show();}
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

    // Lance la page contenant la liste des conversations
    public void conversation(View v) {

        Intent conv = new Intent(this,ConversationActivity.class);

        conv.putExtra("USER_NAME", utilisateur.username);
        conv.putExtra("USER_ID", utilisateur.id_user);
        conv.putExtra("USER_MAIL", utilisateur.email);
        conv.putExtra("USER_PASSWORD",utilisateur.password);
        startActivity(conv);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

                if (tel.equals("")){
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
                if (tel.equals("")) {
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
                String tel = s.getCell(4,i).getContents();

                if (tel.equals("")) {
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


    // Ajout des marqueur du secours populaire.
    public void secoursPopulaire() {

        try {
            AssetManager am = getAssets();

            InputStream is = am.open("SecourPopulaire.xls");

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

                if (tel.equals("")) {
                    mMap.addMarker(new MarkerOptions().snippet(site).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_secours_populaire_logo)));
                }
                else {
                    mMap.addMarker(new MarkerOptions().snippet(tel).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_secours_populaire_logo)));
                }
            }

        } catch (Exception e)
        {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }
    }


    // Ajout des marqueur du secours catholique.
    public void secoursCatholique(){

        try {
            AssetManager am = getAssets();

            InputStream is = am.open("SecourCatholique.xls");

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

                if (tel.equals("")) {
                    mMap.addMarker(new MarkerOptions().snippet(site).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_catholique)));
                }
                else {
                    mMap.addMarker(new MarkerOptions().snippet(tel).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_catholique)));
                }
            }

        } catch (Exception e)
        {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }
    }

    // Ajout des marqueur du secours islamique.
    public void secoursIslamique() {

        try {
            AssetManager am = getAssets();

            InputStream is = am.open("SecourIslamique.xls");

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

                if (tel.equals("")) {
                    mMap.addMarker(new MarkerOptions().snippet(site).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_islamique)));
                }
                else {
                    mMap.addMarker(new MarkerOptions().snippet(tel).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_islamique)));
                }
            }

        } catch (Exception e)
        {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }
    }


    // Ajout des marqueur de saint viencent de paul.
    public void stVincentPaul() {

        try {
            AssetManager am = getAssets();

            InputStream is = am.open("SaintVincentDePaul.xls");

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

                if (tel.equals("")) {
                    mMap.addMarker(new MarkerOptions().snippet(site).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_vincent)));
                }
                else {
                    mMap.addMarker(new MarkerOptions().snippet(tel).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_vincent)));
                }
            }

        } catch (Exception e)
        {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }
    }


    // Ajout des marqueur de captif la libération.
    public void Captif() {

        try {
            AssetManager am = getAssets();

            InputStream is = am.open("CaptifLaLiberation.xls");

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

                if (tel.equals("")) {
                    mMap.addMarker(new MarkerOptions().snippet(site).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_captif)));
                }
                else {
                    mMap.addMarker(new MarkerOptions().snippet(tel).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_captif)));
                }
            }

        } catch (Exception e)
        {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }
    }


    // Ajout des marqueur des banques alimentaires.
    public void banque() {

        try {
            AssetManager am = getAssets();

            InputStream is = am.open("banqueAlimentaire.xls");

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

                if (tel.equals("")) {
                    mMap.addMarker(new MarkerOptions().snippet(site).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_banquealimentaire)));
                }
                else {
                    mMap.addMarker(new MarkerOptions().snippet(tel).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_banquealimentaire)));
                }
            }

        } catch (Exception e)
        {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }
    }

    // Ajout des marqueur des croix rouges
    public void croixrouge() {

        try {
            AssetManager am = getAssets();

            InputStream is = am.open("CroixRouge.xls");

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

                if (tel.equals("")) {
                    mMap.addMarker(new MarkerOptions().snippet(site).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_croixrouge)));
                }
                else {
                    mMap.addMarker(new MarkerOptions().snippet(tel).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_croixrouge)));
                }
            }

        } catch (Exception e)
        {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }
    }

    public void ANRS() {

        try {
            AssetManager am = getAssets();

            InputStream is = am.open("ANRS.xls");

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

                if (tel.equals("")) {
                    mMap.addMarker(new MarkerOptions().snippet(site).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_anrs)));
                }
                else {
                    mMap.addMarker(new MarkerOptions().snippet(tel).position(loc).title(nom).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_anrs)));
                }
            }

        } catch (Exception e)
        {
            Log.d("binks", e.getMessage());
            e.printStackTrace();
        }
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
        return Long.parseLong(id);
    }

    public String getUserMail()
    {
        return getIntent().getStringExtra("USER_MAIL");
    }

    public String getUserPassword()
    {
        return getIntent().getStringExtra("USER_PASSWORD");
    }
    // Ajout d'un marqueur pour chaque coordonnées GPS que l'on reçoit via une requête
    public void setSDFMarkers()
    {
        requete.getSDFMarkers(mMap.getCameraPosition().target.latitude,mMap.getCameraPosition().target.longitude,res ->
        {
            if(res.length() != 0){

                // Tri les informations obtenus ligne par ligne
                String []line = res.split("<br>");

                // Informations sur les pins
                String id_user;
                double longitude;
                double latitude;
                String date;
                String heure;
                String nom_user;
                String icone;
                String[] string;

                for (String s : line) {
                    // Tri une nouvelle fois les informations mais en détails
                    string = s.split(" ");

                    id_user = string[0];
                    longitude = Double.parseDouble(string[1]);
                    latitude = Double.parseDouble(string[2]);
                    date = string[4];
                    heure = string[5];
                    nom_user = string[6];
                    icone = string[7];

                    switch (icone) {
                        case "1":
                            // Ajout d'un pin
                            mMap.addMarker(new MarkerOptions().snippet("Vu la dernière fois par " + nom_user + " le " + yyyy_mm_ddTodd_mm_yyyy(date) + " à " + heure).position(new LatLng(latitude, longitude)).title("Sans abri " + id_user).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_personne_1)));
                            break;
                        case "2":
                            // Ajout d'un pin
                            mMap.addMarker(new MarkerOptions().snippet("Vu la dernière fois par " + nom_user + " le " + yyyy_mm_ddTodd_mm_yyyy(date) + " à " + heure).position(new LatLng(latitude, longitude)).title("Sans abri " + id_user).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_personne_2)));
                            break;
                        default:
                            // Ajout d'un pin
                            mMap.addMarker(new MarkerOptions().snippet("Vu la dernière fois par " + nom_user + " le " + yyyy_mm_ddTodd_mm_yyyy(date) + " à " + heure).position(new LatLng(latitude, longitude)).title("Sans abri " + id_user).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_personne_0)));
                            break;
                    }
                }
            }
        });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    // Converti une date yyyy-dd-mm en dd-mm-yyyy
    public String yyyy_mm_ddTodd_mm_yyyy(String date)
    {
        String []string = date.split("-");

        return string[2]+"/"+string[1]+"/"+string[0];
    }
    //affiche les numéros d'urgence
    public void sos(View v){
        Intent i = new Intent(this, sos.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void don(View v){
        Intent i = new Intent(this, donate.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
