package com.example.test4;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity
{

    String titre, id_user, id_group, username;

    TextView titreView;
    EditText editTextMessage;

    Adapter adapter;
    RecyclerView recyclerMessage;

    ArrayList<String> nom = new ArrayList<String>();
    ArrayList<String> message = new ArrayList<String>();
    ArrayList<String> temps = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialisation des variables transmis par le père
        this.titre = getIntent().getStringExtra("TITRE");
        this.id_user = getIntent().getStringExtra("USER_ID");
        this.username = getIntent().getStringExtra("USER_NAME");
        this.id_group = getIntent().getStringExtra("GROUP_ID");

        initViewID();

        initRecyclerView();
    }

    public void initViewID()
    {
        this.titreView = findViewById(R.id.textViewTitleChat);
        this.titreView.setText(this.titre);
        this.editTextMessage = findViewById(R.id.editTextChat);
        this.recyclerMessage = findViewById(R.id.recyclerViewChat);
    }

    public void initRecyclerView()
    {
        getMessages((res ->
                {
                    if(res.trim().length() != 0)
                    {
                        String[] lines = res.split("<!§!>");
                        String[] element;
                        int n = lines.length;

                        for(int i = 0; i < n; i++)
                        {
                            System.out.println(lines[i]);
                            element = lines[i].split("!§!");
                            nom.add(element[0]);
                            message.add(element[4]);
                            temps.add(element[2]);
                        }
                    }

                    // Intialisation des datas dans notre recycler view
                    adapter = new Adapter(this, nom, message, temps);

                    // Affichage commencant à partir du bas
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerMessage.setLayoutManager(linearLayoutManager);

                    // Création du recycler view
                    recyclerMessage.setAdapter(adapter);
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

            adapter.addItem(this.username,inputMessage,dateFormatUS.format(date));
            sendMessage(inputMessage,dateFormatUS.format(date));
            editTextMessage.setText("");
        }
    }

    // Envoie d'un message via une requête https
    public void sendMessage(String message,String date)
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


}
