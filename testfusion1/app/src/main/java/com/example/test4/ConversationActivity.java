package com.example.test4;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ConversationActivity extends AppCompatActivity
{
    Context context;
    Requete requete;

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

        this.context = this;
        this.requete = new Requete(context);

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

    // Traitement du résultat de la requête "requestAfficheConversation"
    public void afficheConversation()
    {
        // Affichage lors du lancement de la requête
        this.prgbConversation.setVisibility(View.VISIBLE);
        textViewMessageError.setText("");

        // Lancement de la requête
        requete.getConversation(utilisateur.id_user,res ->
        {
            ArrayList<String> titre = new ArrayList<>();
            ArrayList<String> lastMessage = new ArrayList<>();
            ArrayList<String> time = new ArrayList<>();
            ArrayList<String> id_group = new ArrayList<>();

                    if(res.trim().length() != 0)
                    {
                        // Tri des infos reçues de type :
                        // (NOM CONVERSATION).(IDENTIFIANT CONVERSATION).(DERNIER MESSAGE ENVOYE).(DATE DERNIER MESSAGE)<br>
                        // 0 : NOM CONVERSATION
                        // 1 : IDENTIFIANT CONVERSATION
                        // 2 : DERNIER MESSAGE ENVOYE
                        // 3 : DATE DERNIER MESSAGE
                        String[] line = res.split("<br>");

                        String[] element;

                        for (String s : line) {
                            element = s.split("\\.");

                            // Problème : Il y a un saut de ligne dans la réponse du serveur ce qui impact l'affichage
                            // Solution temporaire : Pour l'instant, on supprime les saut de lignes le temps de trouver la src du problème
                            titre.add(element[0].replace("\n", ""));
                            id_group.add(element[1]);
                            lastMessage.add(element[2]);
                            time.add(element[3]);
                        }

                        // Création d'une recyclerView avec les infos
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

    // Traitement du résultat de la requête "requestCreateConversation"
    public void createConversation(String nom_groupe)
    {
        // Chargement
        this.prgbConversation.setVisibility(View.VISIBLE);

        Date date = new Date();
        SimpleDateFormat dateFormatUS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

        // Lancement de la requête
        requete.newConversation(utilisateur.id_user,nom_groupe,dateFormatUS.format(date), res->
        {
            Intent conv = new Intent(this,ConversationActivity.class);

            conv.putExtra("USER_NAME", getUsername());
            conv.putExtra("USER_ID", getUserID());
            conv.putExtra("USER_MAIL",getUserMail());
            conv.putExtra("USER_PASSWORD",getUserPassword());

            startActivity(conv);
            this.finish();
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
