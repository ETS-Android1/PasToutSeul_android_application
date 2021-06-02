package com.example.test4;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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



public class ConversationActivity extends AppCompatActivity {

    RecyclerView recyclerViewMessage;
    TextView noMessageError;
    ProgressBar chargementConv;
    EditText nomGrp;
    View view_popup;
    FloatingActionButton floatingActionButton;
    Utilisateur utilisateur;

    String[] titre;
    String[] id_group;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        this.view_popup = getLayoutInflater().inflate(R.layout.popup_new_conv, null);
        this.builder = new AlertDialog.Builder(this).setView(this.view_popup);

        initView();

        this.utilisateur = new Utilisateur(String.valueOf(getUserID()),getUsername(),getUserMail(),getUserPassword());

        afficheConversation();

    }

    public void initView()
    {
        this.recyclerViewMessage = findViewById(R.id.recyclerViewConversation);
        this.chargementConv = findViewById(R.id.progressBarConversation);
        this.noMessageError = findViewById(R.id.textViewNoMessage);
        this.nomGrp = this.view_popup.findViewById(R.id.editTextNomGroupe);
        this.floatingActionButton = findViewById(R.id.floatingActionButton2);
    }

    // Requête pour récupérer toutes les conversations de l'utilisateur
    public void requestAfficheConversation(final map.VolleyCallBack callback)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        String URL = "https://db-ezpfla.000webhostapp.com/afficheConv.php?id_user="+utilisateur.id_user;

        StringRequest postRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            callback.onSuccess(response.trim());
        }, error -> Log.e("Réponse", error.toString()));

        // Ajoute la requête dans la file
        queue.add(postRequest);
    }

    // Traitement du résultat de la requête "requestAfficheConversation"
    public void afficheConversation()
    {
        this.chargementConv.setVisibility(View.VISIBLE);

        requestAfficheConversation(res -> {
                    if(res.length() != 0) {
                        String[] line = res.split("<br>");

                        int line_length = line.length;

                        this.titre = new String[line_length];
                        this.id_group = new String[line_length];

                        String[] string;

                        for (int i = 0; i < line_length; i++) {
                            string = line[i].split("\\.");
                            this.titre[i] = string[0];
                            this.id_group[i] = string[1];

                            System.out.println(titre[i] + " " + id_group[i]);
                        }

                        Adapter adapter = new Adapter(this, titre, id_group);
                        recyclerViewMessage.setAdapter(adapter);

                        // Ajout d'un listener pour les items dans la recyclerView
                        recyclerViewMessage.addOnItemTouchListener(
                                new RecyclerItemClickListener(this, recyclerViewMessage ,new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override public void onItemClick(View view, int position) {
                                        System.out.println(titre[position]+" "+id_group[position]);
                                        launchChat(titre[position],id_group[position]);
                                    }

                                    @Override public void onLongItemClick(View view, int position) {
                                        System.out.println("long click");
                                    }
                                })
                        );
                        this.chargementConv.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        noMessageError.setText("Vous n'avez aucune conversation à afficher");
                        this.chargementConv.setVisibility(View.INVISIBLE);
                    }

        });
    }

    // Requête permettant de créer un groupe de conversation
    public void requestCreateConversation(String nom_groupe, final map.VolleyCallBack callBack)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        String URL = "https://db-ezpfla.000webhostapp.com/createConv.php?id_user="+utilisateur.id_user+"&nom_groupe="+nom_groupe;

        StringRequest postRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            callBack.onSuccess(response);
        }, error -> Log.e("Réponse", error.toString()));

        // Ajoute la requête dans la file
        queue.add(postRequest);
    }

    // Traitement du résultat de la requête "requestCreateConversation"
    public void createConversation(View view, String nom_groupe)
    {
        this.chargementConv.setVisibility(View.VISIBLE);

        requestCreateConversation(nom_groupe, res-> {
            System.out.println(res);
            afficheConversation();
        });
    }

    // Création d'un popup afin de créer un groupe de conversation
    public void newConversationPopup(View view)
    {
        AlertDialog popup = builder.show();

        popup.setCanceledOnTouchOutside(false);

        Button cancel = this.view_popup.findViewById(R.id.btnCancelGrp);
        Button create = this.view_popup.findViewById(R.id.btnCreateGroup);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup)view_popup.getParent()).removeView(view_popup);
                popup.dismiss();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup)view_popup.getParent()).removeView(view_popup);

                String nom_groupe = nomGrp.getText().toString();
                createConversation(v,nom_groupe);

                popup.dismiss();
            }
        });

    }

    // Lancement du chat
    public void launchChat(String titre, String id_groupe)
    {
        Intent chatActivity = new Intent(this, ChatActivity.class);

        chatActivity.putExtra("TITRE", titre);
        chatActivity.putExtra("USER_ID", utilisateur.id_user);
        chatActivity.putExtra("USER_NAME", utilisateur.username);
        chatActivity.putExtra("GROUP_ID", id_groupe);

        startActivity(chatActivity);
    }

    public String getUsername()
    {
        return getIntent().getStringExtra("USER_NAME");
    }

    public String getUserID()
    {
        String id = getIntent().getStringExtra("USER_ID");
        return id;
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
