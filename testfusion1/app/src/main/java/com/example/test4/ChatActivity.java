package com.example.test4;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    EditText nomGrp;
    View view_popup;

    Utilisateur utilisateur;

    String[] s1;
    String[] s2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        this.view_popup = getLayoutInflater().inflate(R.layout.popup_new_conv, null);

        this.recyclerViewMessage = findViewById(R.id.recyclerViewConversation);
        this.chargementConv = findViewById(R.id.progressBarConversation);
        this.noMessageError = findViewById(R.id.textViewNoMessage);
        this.nomGrp = this.view_popup.findViewById(R.id.editTextNomGroupe);

        this.utilisateur = new Utilisateur(String.valueOf(getUserID()),getUsername(),getUserMail(),getUserPassword());

        afficheConversation();

    }

    public void afficheConversation()
    {
        System.out.println("aezaeazeaze");
        this.chargementConv.setVisibility(View.VISIBLE);
        requestAfficheConversation(res -> {
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

    public void newConversationPopup(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(this.view_popup);

        final AlertDialog popup = builder.show();

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

    public void createConversation(View view, String nom_groupe)
    {
        this.chargementConv.setVisibility(View.VISIBLE);

        requestCreateConversation(nom_groupe, res-> {
            System.out.println(res);
            afficheConversation();
        });
    }

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
