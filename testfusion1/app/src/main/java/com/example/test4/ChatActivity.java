package com.example.test4;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerViewMessage;
    TextView noMessageError;
    ProgressBar chargementConv;

    Utilisateur utilisateur;

    String[] s1;
    String[] s2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        this.recyclerViewMessage = findViewById(R.id.recyclerViewConversation);
        this.chargementConv = findViewById(R.id.progressBarConversation);
        this.noMessageError = findViewById(R.id.textViewNoMessage);

        this.utilisateur = new Utilisateur(String.valueOf(getUserID()),getUsername(),getUserMail(),getUserPassword());


        afficheConversation();

    }

    public void afficheConversation()
    {
        requestConversation(res -> {
                    this.chargementConv.setVisibility(View.VISIBLE);
                    if(res.length() != 0)
                    {
                        String[] line = res.split("<br>");

                        int line_length = line.length;

                        this.s1 = new String[line_length];
                        this.s2 = new String[line_length];

                        String[] string;

                        for(int i = 0; i < line_length; i++)
                        {
                            string = line[i].split("\\.");
                            this.s1[i] = string[0];
                            this.s2[i] = string[1];

                            System.out.println(s1[i]+" "+s2[i]);
                        }

                        Adapter adapter = new Adapter(this,s1,s2);
                        recyclerViewMessage.setAdapter(adapter);
                        this.chargementConv.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        noMessageError.setText("Vous n'avez aucune conversation à afficher");
                        this.chargementConv.setVisibility(View.INVISIBLE);
                    }

        });
    }

    // Requête pour récupérer toutes les conversations de l'utilisateur
    public void requestConversation(final map.VolleyCallBack callback)
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

    public void newConversation(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view_popup = getLayoutInflater().inflate(R.layout.popup_new_conv, null);

        builder.setView(view_popup);


        builder.show();
    }


    // Récupère le nom d'utilisateur
    public String getUsername()
    {
        return getIntent().getStringExtra("USER_NAME");
    }

    // Récupère l'id de l'utilisateur
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
