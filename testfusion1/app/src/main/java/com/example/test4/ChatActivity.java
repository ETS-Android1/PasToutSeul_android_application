package com.example.test4;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity
{

    String titre, id_user, id_group, username, email, password;

    TextView titreView, msgError;

    EditText addTextView,editTextMessage;

    Adapter adapter;
    RecyclerView recyclerMessage, recyclerParticipants;

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

        initPopupDialog();

        initIntentString();

        initViewID();

        initRecyclerViewChat();

        progressBar.setVisibility(View.INVISIBLE);

        // Mise à jour des messages toutes les 5 secondes
        this.update = Executors.newScheduledThreadPool(1);
        update.scheduleAtFixedRate(this::updateMessages, 5, 5, TimeUnit.SECONDS);
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
        getMessages((res ->
                {
                    if(res.trim().length() != 0)
                    {
                        String[] lines = res.split("<!§!>");
                        String[] element;
                        int n = lines.length;

                        for(int i = 0; i < n; i++)
                        {
                            //System.out.println(lines[i]);
                            element = lines[i].split("!§!");
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
                    this.progressBar.setVisibility(View.INVISIBLE);
                }));
    }

    // Ajoute dans la recyclerView ce que l'utilisateur à tapé sur le clavier si c'est pas vide.
    public void send(View view)
    {
        String inputMessage = editTextMessage.getText().toString();

        if(!inputMessage.equals(""))
        {
            Date date = new Date();
            SimpleDateFormat dateFormatUS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            this.adapter.addItem(this.username,inputMessage,dateFormatUS.format(date));
            sendMessageRequest(inputMessage,dateFormatUS.format(date));
            editTextMessage.setText("");
        }
    }

    // Envoie d'un message via une requête https
    public void sendMessageRequest(String message,String date)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        String URL = "https://db-ezpfla.000webhostapp.com/sendMessage.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("Réponse", response);
        }, error -> Log.e("Réponse", error.toString())){
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> logs = new HashMap<>();

                //Ajout des arguments
                logs.put("id_grp",id_group);
                logs.put("id_usr",id_user);
                logs.put("date",date);
                logs.put("msg",message);

                return logs;
            }
        };

        //Gérer les timeout
        postRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Ajout de la requête dans la file d'attente
        queue.add(postRequest);

    }

    // Récupération de tous les messages
    public void getMessages(final map.VolleyCallBack callback)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        String URL = "https://db-ezpfla.000webhostapp.com/getMessages.php?id_grp="+this.id_group;

        StringRequest getRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            callback.onSuccess(response);
        }, error -> Log.e("Réponse", error.toString()));

        queue.add(getRequest);
    }

    // Mis à jour des nouveaux messages sur l'affichage
    public void updateMessages()
    {
        Date date = new Date();
        SimpleDateFormat dateFormatUS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        getNewMessages(dateFormatUS.format(date),res -> {
            if(res.trim().length() != 0)
            {
                String[] lines = res.split("<!§!>");
                String[] element;
                int n = lines.length;

                for(int i = 0; i < n; i++)
                {
                    element = lines[i].split("!§!");
                    nom.add(element[0]);
                    message.add(element[4]);
                    temps.add(element[2]);

                    this.adapter.addItem(element[0],element[4],element[2]);
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        this.update.shutdown();
        this.finish();

        Intent conv = new Intent(this,ConversationActivity.class);

        conv.putExtra("USER_NAME", username);
        conv.putExtra("USER_ID", id_user);
        conv.putExtra("USER_MAIL",email);
        conv.putExtra("USER_PASSWORD",password);

        startActivity(conv);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // Récupère les nouveaux messages côté serveur
    public void getNewMessages(String timeLastUpdate, final map.VolleyCallBack callback)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        String URL = "https://db-ezpfla.000webhostapp.com/getNewMessages.php?id_grp="+this.id_group+"&id_usr="+this.username+"&time="+timeLastUpdate;

        StringRequest getRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            callback.onSuccess(response);
        }, error -> Log.e("Réponse", error.toString()));

        queue.add(getRequest);
    }

    public void getParticipants(String id_groupe, final map.VolleyCallBack callBack)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        String URL = "https://db-ezpfla.000webhostapp.com/getParticipants.php?id_groupe="+id_groupe;

        StringRequest getRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            callBack.onSuccess(response);
        }, error -> Log.e("Réponse", error.toString()));

        queue.add(getRequest);
    }

    // Affichage de la fenêtre popup pour ajouter des participants
    public void popupAdd(View view)
    {
        AlertDialog popup = builder_add.show();

        popup.setCanceledOnTouchOutside(false);

        Button quitter = this.view_popup_add.findViewById(R.id.btnCancelAdd);
        Button add = this.view_popup_add.findViewById(R.id.btnAddPeople);

        // Bouton retour
        popup.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    ((ViewGroup)view_popup_add.getParent()).removeView(view_popup_add);
                    popup.dismiss();
                }
                return true;
            }
        });

        quitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup)view_popup_add.getParent()).removeView(view_popup_add);
                popup.dismiss();
            }
        });

        add.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v)
            {
                msgError.setText("");
                addTextView.setBackgroundResource(R.drawable.backwithborder_noerror);
                if(!addTextView.getText().toString().equals(""))
                {
                    addPerson(addTextView.getText().toString(),id_group, res ->
                    {
                        String response = res.trim();
                        System.out.println(response);
                        if(response.equals("error_username"))
                        {
                            msgError.setText("Cet utilisateur n'existe pas.");
                            addTextView.setBackgroundResource(R.drawable.backwithborder_error);
                            //addTextView.setTextColor(R.color.rouge_fonce);
                        }
                        else if(response.equals("exist"))
                        {
                            msgError.setText("Cet utilisateur à déja été ajouté.");
                            addTextView.setBackgroundResource(R.drawable.backwithborder_error);
                            //addTextView.setTextColor(R.color.rouge_fonce);
                        }
                        else
                        {
                            msgError.setText("");
                            addTextView.setBackgroundResource(R.drawable.backwithborder_noerror);
                            addTextView.setText("");
                            //addTextView.setTextColor(R.color.black);
                            Toast.makeText(ChatActivity.this, res, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    addTextView.setBackgroundResource(R.drawable.backwithborder_error);
                    msgError.setText("Veuillez mettre un nom d'utilisateur.");
                    //addTextView.setHintTextColor(R.color.rouge_clair);
                }
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
        popup.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // Click sur le bouton retour
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    ((ViewGroup)view_popup_leave.getParent()).removeView(view_popup_leave);
                    // Fermeture de la fenêtre popup
                    popup.dismiss();
                }
                return true;
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup)view_popup_leave.getParent()).removeView(view_popup_leave);
                // Fermeture de la fenêtre popup
                popup.dismiss();
            }
        });

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup)view_popup_leave.getParent()).removeView(view_popup_leave);

                // Fermeture de la fenêtre popup
                popup.dismiss();

                // Quitte la conversation
                leave(res ->{
                    // Fermeture du chat
                    ChatActivity.super.finish();
                });
            }
        });
    }

    public void participantPopup(View view)
    {
        getParticipants(this.id_group, res ->
        {
            String[] line = res.split("<br>");

            int nParticipant = line.length;

            ArrayList<String> nom_utilisateur = new ArrayList<>();
            String[] string;

            for(int i = 0; i < nParticipant; i++)
            {
                string = line[i].split("<!!>");

                if(string[1].equals(username))
                {
                    string[1] = string[1]+" (Vous)";
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

            AlertDialog popup = builder_participant.show();

            // Desactive les clicks en dehors de la fenêtre popup
            popup.setCanceledOnTouchOutside(false);

            // Bouton de la fenêtre
            Button leave = view_popup_participant.findViewById(R.id.btnLeaveParticipant);

            leave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ViewGroup)view_popup_participant.getParent()).removeView(view_popup_participant);

                    // Fermeture de la fenêtre popup
                    popup.dismiss();
                }
            });
        });


    }

    public void addPerson(String name, String id_grp, final map.VolleyCallBack callback)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        String URL = "https://db-ezpfla.000webhostapp.com/addPeople.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("Réponse", response);
            callback.onSuccess(response);
        }, error -> Log.e("Réponse", error.toString())){
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> logs = new HashMap<>();

                //Ajout des arguments
                logs.put("nom_user",name);
                logs.put("id_groupe",id_grp);

                return logs;
            }
        };

        //Gérer les timeout
        postRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Ajout de la requête dans la file d'attente
        queue.add(postRequest);
    }


    public void leave(final map.VolleyCallBack callBack)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        String URL = "https://db-ezpfla.000webhostapp.com/leaveConv.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("Réponse", response);
            callBack.onSuccess(response);
        }, error -> Log.e("Réponse", error.toString())){
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> logs = new HashMap<>();

                //Ajout des arguments
                logs.put("id_user",id_user);
                logs.put("id_group",id_group);

                return logs;
            }
        };

        //Gérer les timeout
        postRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Ajout de la requête dans la file d'attente
        queue.add(postRequest);
    }
}
