package com.example.test4;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ConversationActivity extends AppCompatActivity {

    RecyclerView recyclerViewMessage;
    TextView textViewMessageError;
    ProgressBar prgbConversation;
    EditText editTextNomGrp;

    FloatingActionButton floatingActionButton;
    Utilisateur utilisateur;

    View view_popup_add;
    AlertDialog.Builder builder;

    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        this.view_popup_add = getLayoutInflater().inflate(R.layout.popup_new_conv, null);
        this.builder = new AlertDialog.Builder(this,R.style.MyDialogTheme).setView(this.view_popup_add);

        initView();

        this.utilisateur = new Utilisateur(String.valueOf(getUserID()),getUsername(),getUserMail(),getUserPassword());

    }

    // Lancement de l'activité ou quand on revient sur l'activité
    @Override
    public void onResume()
    {
        super.onResume();
        afficheConversation();
    }

    public void initView()
    {
        this.recyclerViewMessage = findViewById(R.id.recyclerViewConversation);
        this.prgbConversation = findViewById(R.id.progressBarConversation);
        this.textViewMessageError = findViewById(R.id.textViewNoMessage);
        this.floatingActionButton = findViewById(R.id.floatingActionButton2);
        this.editTextNomGrp = this.view_popup_add.findViewById(R.id.editTextNomGroupe);
    }

    // Requête pour récupérer toutes les conversations de l'utilisateur
    public void requestAfficheConversation(final map.VolleyCallBack callback)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        String URL = "https://db-ezpfla.000webhostapp.com/afficheConv.php?id_user="+utilisateur.id_user;

        StringRequest postRequest = new StringRequest(Request.Method.GET, URL, response ->
        {
            Log.i("Réponse", response);
            callback.onSuccess(response);
        }, error -> Log.e("Réponse", error.toString()));

        // Ajoute la requête dans la file
        queue.add(postRequest);
    }

    // Traitement du résultat de la requête "requestAfficheConversation"
    public void afficheConversation()
    {
        // Affichage lors du lancement de la requête
        this.prgbConversation.setVisibility(View.VISIBLE);
        textViewMessageError.setText("");

        // Lancement de la requête
        requestAfficheConversation(res ->
        {
            ArrayList<String> titre = new ArrayList<>();
            ArrayList<String> lastMessage = new ArrayList<>();
            ArrayList<String> time = new ArrayList<>();
            ArrayList<String> id_group = new ArrayList<>();

                    if(res.trim().length() != 0)
                    {
                        // Séparation de chaque lignes de données
                        String[] line = res.split("<br>");

                        int nline= line.length;

                        //String[] id_group = new String[nline];
                        //String[] titre = new String[nline];
                        //String[] lastMessage = new String[nline];
                        String[] element;

                        for(int i = 0; i < nline; i++) {
                            element = line[i].split("\\.");
                            System.out.println(element[0]);
                            // Problème : Il y a un saut de ligne dans la réponse du serveur ce qui impact l'affichage
                            // Solution temporaire : Pour l'instant, on supprime les saut de lignes le temps de trouver la src du problème
                            //titre[i] = string[0].replace("\n","");
                            titre.add(element[0].replace("\n",""));
                            id_group.add(element[1]);
                            lastMessage.add(element[2]);
                            time.add(element[3]);
                            //System.out.println(titre[i]+" "+lastMessage[i]);
                        }
                        System.out.println("Titre avant lancement du chat : "+titre.toString());
                        adapter = new Adapter(this, titre, lastMessage, time,"conversation");

                        // Ajout des données des items dans notre recyclerView
                        recyclerViewMessage.setAdapter(adapter);
                        // Ajout d'un listener pour les items dans la recyclerView
                        recyclerViewMessage.addOnItemTouchListener(
                                new RecyclerItemClickListener(this, recyclerViewMessage ,new RecyclerItemClickListener.OnItemClickListener()
                                {
                                    @Override public void onItemClick(View view, int position)
                                    {
                                        System.out.println("Titre au lancement du chat : "+titre.toString());
                                        String title  = titre.get(position);
                                        String idGrp = id_group.get(position);
                                        launchChat(title,idGrp);
                                    }

                                    @Override
                                    public void onLongItemClick(View view, int position)
                                    {
                                            // Rien à faire
                                    }
                                }));
                    }
                    else
                    {
                        textViewMessageError.setText(R.string.pas_de_conversation);
                    }
            this.prgbConversation.setVisibility(View.INVISIBLE);
        });
    }

    // Requête permettant de créer un groupe de conversation
    public void requestCreateConversation(String nom_groupe, String date, final map.VolleyCallBack callBack)
    {
        // Initisalisation de la reqûete
        RequestQueue queue = Volley.newRequestQueue(this);

        String URL = "https://db-ezpfla.000webhostapp.com/createConv.php?id_user="+utilisateur.id_user+"&nom_groupe="+nom_groupe+"&date="+date;

        StringRequest postRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            callBack.onSuccess(response);
        }, error -> Log.e("Réponse", error.toString()));

        // Ajoute la requête dans la file
        queue.add(postRequest);
    }

    // Traitement du résultat de la requête "requestCreateConversation"
    public void createConversation(String nom_groupe)
    {
        // Affichage
        this.prgbConversation.setVisibility(View.VISIBLE);

        Date date = new Date();
        SimpleDateFormat dateFormatUS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Lancement de la requête
        requestCreateConversation(nom_groupe,dateFormatUS.format(date), res-> {
            //System.out.println(res);
            afficheConversation();
        });
    }

    // Création d'un popup afin de créer un groupe de conversation
    public void newConversationPopup(View view)
    {
        AlertDialog popup = builder.show();

        // Bloque les clicks en dehors de la fenêtre pop-up
        popup.setCanceledOnTouchOutside(false);

        // Les boutons sur les popups
        Button cancel = this.view_popup_add.findViewById(R.id.btnCancelGrp);
        Button create = this.view_popup_add.findViewById(R.id.btnCreateGroup);

        // Bouton retour
        popup.setOnKeyListener((arg0, keyCode, event) -> {
            // Click sur le bouton retour
            if (keyCode == KeyEvent.KEYCODE_BACK)
            {
                ((ViewGroup)view_popup_add.getParent()).removeView(view_popup_add);
                popup.dismiss();
            }
            return true;
        });

        // Bouton annuler
        cancel.setOnClickListener(v -> {
            ((ViewGroup)view_popup_add.getParent()).removeView(view_popup_add);
            popup.dismiss();
        });

        // Bouton créer
        create.setOnClickListener(v -> {
            ((ViewGroup)view_popup_add.getParent()).removeView(view_popup_add);

            String nom_groupe = editTextNomGrp.getText().toString();

            if(nom_groupe.trim().equals(""))
            {
                // Nom de groupe initiale lorsqu'il y a aucun nom renseigné
                nom_groupe = "(Pas de nom)";
            }

            createConversation(nom_groupe);

            popup.dismiss();
        });

    }


    // Lancement du chat
    public void launchChat(String titre, String id_groupe)
    {
        Intent chatActivity = new Intent(this, ChatActivity.class);

        // Envoi des variables vers chatActivity
        chatActivity.putExtra("TITRE", titre);
        chatActivity.putExtra("USER_ID", utilisateur.id_user);
        chatActivity.putExtra("USER_NAME", utilisateur.username);
        chatActivity.putExtra("USER_MAIL",utilisateur.email);
        chatActivity.putExtra("USER_PASSWORD",utilisateur.password);
        chatActivity.putExtra("GROUP_ID", id_groupe);

        // Lancement du chat
        startActivity(chatActivity);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        this.finish();
    }

    public String getUsername()
    {
        return getIntent().getStringExtra("USER_NAME");
    }

    public String getUserID()
    {
        return getIntent().getStringExtra("USER_ID");
    }

    public String getUserMail()
    {
        return getIntent().getStringExtra("USER_MAIL");
    }

    public String getUserPassword()
    {
        return getIntent().getStringExtra("USER_PASSWORD");
    }
}
