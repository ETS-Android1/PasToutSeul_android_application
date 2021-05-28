package com.example.test4;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerViewMessage;
    TextView noMessageError;

    Utilisateur utilisateur;

    String[] s1;
    String[] s2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        this.recyclerViewMessage = findViewById(R.id.recyclerViewConversation);
        this.utilisateur = new Utilisateur(String.valueOf(getUserID()),getUsername(),getUserMail(),getUserPassword());
        this.noMessageError = findViewById(R.id.textViewNoMessage);

        s1 = new String[1];
        s2 = new String[1];

        if(s1.length == 0)
        {
            noMessageError.setText("Vous n'avez aucune conversation à afficher");
        }

        s1[0] = utilisateur.username;
        s2[0] = utilisateur.email;

        Adapter adapter = new Adapter(this,s1,s2);
        recyclerViewMessage.setAdapter(adapter);

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
