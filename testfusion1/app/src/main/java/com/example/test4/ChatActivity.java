package com.example.test4;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity
{
    Context context;
    Requete requete;

    // Infos sur l'utilisateur
    String titre, id_user, id_group, username, email, password;

    TextView titreView, msgError;

    EditText addTextView,editTextMessage;

    Adapter adapter;
    RecyclerView recyclerMessage, recyclerParticipants;

    Date lastUpdate;

    // Liste des messages avec leurs informations
    ArrayList<String> nom = new ArrayList<>();
    ArrayList<String> message = new ArrayList<>();
    ArrayList<String> temps = new ArrayList<>();

    // Création d'un service qui mettra à jour les messages toutes les x secondes
    ScheduledExecutorService update;

    // Fenêtre popup pour ajouter un utilisateur
    View view_popup_add, view_popup_leave, view_popup_participant;
    AlertDialog.Builder builder_add, builder_leave, builder_participant;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.context = this;
        this.requete = new Requete(context);

        // Initialisation des variables pour la fenêtre popup
        initPopupDialog();

        // Initialisation des variables transmis par le père
        initIntentString();

        // Initialisation des identifiants des textView, EditText, etc...
        initViewID();

        // Initialisation de la recyclerView permettant d'afficher les messages du chat
        initRecyclerViewChat();

        progressBar.setVisibility(View.INVISIBLE);

        // Configuration et initialisation du service s'éxecutant tous les x secondes
        initScheduledService();
    }

    // Configuration et initialisation du service s'éxecutant tous les x secondes
    public void initScheduledService()
    {
        // Date de la dernière mise à jour
        this.lastUpdate = new Date();

        // Mise à jour des messages toutes les 5 secondes
        this.update = Executors.newScheduledThreadPool(1);
        this.update.scheduleAtFixedRate(this::updateMessages, 5, 5, TimeUnit.SECONDS);
    }

    // Initialisation des identifiants des textView, EditText, etc...
    public void initViewID()
    {
        this.titreView = findViewById(R.id.textViewTitleChat);
        this.titreView.setText(this.titre);
        this.editTextMessage = findViewById(R.id.editTextChat);
        this.recyclerMessage = findViewById(R.id.recyclerViewChat);
        this.recyclerParticipants= view_popup_participant.findViewById(R.id.recyclerViewParticipants);
        this.addTextView = this.view_popup_add.findViewById(R.id.editTextNomUser);
        this.msgError = this.view_popup_add.findViewById(R.id.textViewAddError);
        this.progressBar = findViewById(R.id.progressBarChat);
    }

    // Initialisation des variables pour la fenêtre popup
    public void initPopupDialog()
    {
        this.view_popup_add = getLayoutInflater().inflate(R.layout.popup_add_people, null);
        this.builder_add = new AlertDialog.Builder(this,R.style.MyDialogTheme).setView(this.view_popup_add);

        this.view_popup_leave = getLayoutInflater().inflate(R.layout.popup_leave_conv, null);
        this.builder_leave = new AlertDialog.Builder(this,R.style.MyDialogTheme).setView(this.view_popup_leave);

        this.view_popup_participant = getLayoutInflater().inflate(R.layout.popup_participant, null);
        this.builder_participant = new AlertDialog.Builder(this,R.style.MyDialogTheme).setView(this.view_popup_participant);
    }

    // Initialisation des variables transmis par le père
    public void initIntentString()
    {
        this.titre = getIntent().getStringExtra("TITRE");
        this.id_user = getIntent().getStringExtra("USER_ID");
        this.username = getIntent().getStringExtra("USER_NAME");
        this.id_group = getIntent().getStringExtra("GROUP_ID");
        this.email = getIntent().getStringExtra("USER_MAIL");
        this.password = getIntent().getStringExtra("USER_PASSWORD");
    }

    // Initialisation de la recyclerView permettant d'afficher les messages du chat
    public void initRecyclerViewChat()
    {
        this.progressBar.setVisibility(View.VISIBLE);

        // Récupération de tous les messages dans une conversation
        requete.getMessages(id_group,res ->
                {
                    // Affiche les messages si il y en a
                    // Liste des messages reçues sous la formes :
                    // (NOM D'UTILISATEUR)!§!(IDENTIFIANT UTILISATEUR)!§!(DATE : yyyy-MM-dd HH-mm-ss)!§!(IDENTIFIANT GROUPE)!§!(MESSAGE)<!§!>
                    // 0 : NOM D'UTILISATEUR
                    // 1 : IDENTIFIANT UTILISATEUR
                    // 2 : DATE : yyyy-MM-dd HH-mm-ss
                    // 3 : IDENTIFIANT GROUPE
                    // 4 : MESSAGE
                    if(res.trim().length() != 0)
                    {
                        // Tri de la liste d'informations reçues
                        String[] lines = res.split("<!§!>");
                        String[] element;

                        for (String line : lines) {
                            element = line.split("!§!");
                            nom.add(element[0]);
                            message.add(element[4]);
                            temps.add(element[2]);
                        }
                    }

                    // Intialisation des datas dans notre recycler view
                    adapter = new Adapter(this, nom, message, temps,"chat");

                    // Affichage commencant à partir du bas
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerMessage.setLayoutManager(linearLayoutManager);

                    // Création du recycler view
                    recyclerMessage.setAdapter(adapter);
                    recyclerMessage.scrollToPosition(adapter.getItemCount()-1);
                    this.progressBar.setVisibility(View.INVISIBLE);
                });
    }

    // Ajoute dans la recyclerView ce que l'utilisateur à tapé sur le clavier si c'est pas vide.
    public void send(View view)
    {
        // Récupération du contenu du champ de saisie
        String inputMessage = editTextMessage.getText().toString();

        if(!inputMessage.equals(""))
        {
            // Récupère la date actuelle
            Date date = new Date();
            SimpleDateFormat dateFormatUS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

            // Ajout du message dans la recyclerView
            this.adapter.addItem(this.username,inputMessage,dateFormatUS.format(date));

            // Scroll vers le dernier message
            this.recyclerMessage.scrollToPosition(adapter.getItemCount()-1);

            // Envoi du message
            System.out.println("ID USER "+id_user);
            requete.sendMessage(id_group,id_user,inputMessage,dateFormatUS.format(date));

            editTextMessage.setText("");
        }
    }

    // Mis à jour des nouveaux messages sur l'affichage
    public void updateMessages()
    {
        // Récupération de la date pour récupérer seulement les nouveaux messages
        SimpleDateFormat dateFormatUS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

        // Récupère les nouveaux messages
        requete.getNewMessages(id_group,id_user,dateFormatUS.format(lastUpdate),res -> {
            if(res.trim().length() != 0)
            {
                // Date de la dernière mise à jour
                lastUpdate = new Date();

                // Tri des informations reçues de type :
                // (NOM D'UTILISATEUR)!§!(IDENTIFIANT UTILISATEUR)!§!(DATE : yyyy-MM-dd HH-mm-ss)!§!(IDENTIFIANT GROUPE)!§!(MESSAGE)<!§!>
                // 0 : NOM D'UTILISATEUR
                // 1 : IDENTIFIANT UTILISATEUR
                // 2 : DATE : yyyy-MM-dd HH-mm-ss
                // 3 : IDENTIFIANT GROUPE
                // 4 : MESSAGE
                String[] lines = res.split("<!§!>");
                String[] element;

                for (String line : lines) {
                    element = line.split("!§!");
                    nom.add(element[0]);
                    message.add(element[4]);
                    temps.add(element[2]);


                    // Mise à jour de la recyclerView
                    this.adapter.update();

                    this.recyclerMessage.scrollToPosition(adapter.getItemCount()-1);
                }

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        // Arrêt des requêtes
        this.update.shutdown();

        // Fermeture de l'activité
        this.finish();

        // Lancement de l'activité ConversationActivity
        Intent conv = new Intent(this,ConversationActivity.class);

        conv.putExtra("USER_NAME", username);
        conv.putExtra("USER_ID", id_user);
        conv.putExtra("USER_MAIL",email);
        conv.putExtra("USER_PASSWORD",password);

        startActivity(conv);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // Affichage de la fenêtre popup pour ajouter des participants
    @SuppressLint("SetTextI18n")
    public void popupAdd(View view)
    {
        AlertDialog popup = builder_add.show();

        // Bloque les clicks en dehors de la fenêtre popup
        popup.setCanceledOnTouchOutside(false);

        // Récupère les identifiants des boutons de la fenêtre popup
        Button quitter = this.view_popup_add.findViewById(R.id.btnCancelAdd);
        Button add = this.view_popup_add.findViewById(R.id.btnAddPeople);

        // Bouton retour
        popup.setOnKeyListener((arg0, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK)
            {
                ((ViewGroup)view_popup_add.getParent()).removeView(view_popup_add);
                popup.dismiss();
            }
            return true;
        });

        // Bouton quitter
        quitter.setOnClickListener(v -> {
            ((ViewGroup)view_popup_add.getParent()).removeView(view_popup_add);
            popup.dismiss();
        });

        // Bouton ajouter
        add.setOnClickListener(v -> {
            msgError.setText("");
            addTextView.setBackgroundResource(R.drawable.backwithborder_noerror);
            if(!addTextView.getText().toString().equals(""))
            {
                requete.addPerson(addTextView.getText().toString(),id_group, res ->
                {
                    String response = res.trim();
                    System.out.println(response);
                    if(response.equals("error_username"))
                    {
                        msgError.setText("Cet utilisateur n'existe pas.");
                        addTextView.setBackgroundResource(R.drawable.backwithborder_error);
                    }
                    else if(response.equals("exist"))
                    {
                        msgError.setText("Cet utilisateur à déja été ajouté.");
                        addTextView.setBackgroundResource(R.drawable.backwithborder_error);
                    }
                    else // Utilisateur ajouté
                    {
                        msgError.setText("");
                        addTextView.setBackgroundResource(R.drawable.backwithborder_noerror);
                        addTextView.setText("");
                        Toast.makeText(ChatActivity.this, res, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                addTextView.setBackgroundResource(R.drawable.backwithborder_error);
                msgError.setText("Veuillez mettre un nom d'utilisateur.");
            }
        });
    }

    // Création d'un popup pour quitter la conversation
    public void leaveConversationPopup(View view)
    {
        AlertDialog popup = builder_leave.show();

        // Desactive les clicks en dehors de la fenêtre popup
        popup.setCanceledOnTouchOutside(false);

        // Les boutons sur les popups
        Button cancel = this.view_popup_leave.findViewById(R.id.btnCancelLeave);
        Button leave = this.view_popup_leave.findViewById(R.id.btnLeaveConv);

        // Bouton retour
        popup.setOnKeyListener((arg0, keyCode, event) -> {
            // Click sur le bouton retour
            if (keyCode == KeyEvent.KEYCODE_BACK)
            {
                ((ViewGroup)view_popup_leave.getParent()).removeView(view_popup_leave);
                // Fermeture de la fenêtre popup
                popup.dismiss();
            }
            return true;
        });

        // Bouton annuler
        cancel.setOnClickListener(v -> {
            ((ViewGroup)view_popup_leave.getParent()).removeView(view_popup_leave);
            // Fermeture de la fenêtre popup
            popup.dismiss();
        });

        // Bouton quitter la conversation
        leave.setOnClickListener(v -> {
            ((ViewGroup)view_popup_leave.getParent()).removeView(view_popup_leave);

            // Fermeture de la fenêtre popup
            popup.dismiss();

            // Quitte la conversation
            requete.leave(id_user,id_group,res ->
            {
                // Fermeture du chat
                onBackPressed();
            });
        });
    }

    // Affichage de la fenêtre popup affichant la liste des participants
    @SuppressLint("SetTextI18n")
    public void participantPopup(View view)
    {
        AlertDialog popup = builder_participant.show();

        // Desactive les clicks en dehors de la fenêtre popup
        popup.setCanceledOnTouchOutside(false);

        // Bouton retour
        popup.setOnKeyListener((arg0, keyCode, event) -> {
            // Click sur le bouton retour
            if (keyCode == KeyEvent.KEYCODE_BACK)
            {
                ((ViewGroup)view_popup_participant.getParent()).removeView(view_popup_participant);
                popup.dismiss();
            }
            return true;
        });

        // Bouton de la fenêtre
        Button leave = view_popup_participant.findViewById(R.id.btnLeaveParticipant);

        // Bouton quitter la fenêtre
        leave.setOnClickListener(v -> {
            ((ViewGroup)view_popup_participant.getParent()).removeView(view_popup_participant);

            // Fermeture de la fenêtre popup
            popup.dismiss();
        });

        // Récupère la liste des participants dans la conversation
        requete.getParticipants(this.id_group, res ->
        {
            // Tri des infos reçues de type :
            //(IDENTIFIANT UTILISATEUR)<!!>(NOM D'UTILISATEUR)<br>
            String[] line = res.split("<br>");

            // Nombre de participant(s)
            int nParticipant = line.length;

            ArrayList<String> nom_utilisateur = new ArrayList<>();
            String[] string;

            for (String s : line) {
                // 0 : IDENTIFIANT UTILISATEUR
                // 1 : NOM D'UTILISATEUR
                string = s.split("<!!>");

                if (string[1].equals(username)) {
                    string[1] = string[1] + " (Vous)";
                }

                nom_utilisateur.add(string[1]);
            }

            // Affichage de la fenêtre popup
            TextView txtVParticipants = view_popup_participant.findViewById(R.id.txtVNombreParticipants);
            txtVParticipants.setText(nParticipant+" participant(s)");

            // Adapter pour la recyclerView
            Adapter adapter = new Adapter(view_popup_participant.getContext(), nom_utilisateur,new ArrayList<>(),new ArrayList<>(),"participantPopup");

            recyclerParticipants.setLayoutManager(new LinearLayoutManager(this));
            recyclerParticipants.setAdapter(adapter);
        });
    }
}
