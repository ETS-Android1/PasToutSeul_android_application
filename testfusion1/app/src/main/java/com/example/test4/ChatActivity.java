package com.example.test4;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    String titre, id_user, id_group, username;

    TextView titreView;
    EditText editTextMessage;

    Adapter adapter;
    RecyclerView recyclerMessage;

    ArrayList<String> nom;
    ArrayList<String>message;
    ArrayList<String>temps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialisation des variables transmis par le père
        this.titre = getIntent().getStringExtra("TITRE");
        this.id_user = getIntent().getStringExtra("USER_ID");
        this.username = getIntent().getStringExtra("USER_NAME");
        this.id_group = getIntent().getStringExtra("GROUP_ID");


        initViewID();

        initArrayList();

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
        // Intialisation des datas dans notre recycler view
        adapter = new Adapter(this, nom, message, temps);

        // Affichage commencant à partir du bas
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerMessage.setLayoutManager(linearLayoutManager);

        // Création du recycler view
        recyclerMessage.setAdapter(adapter);

    }

    public void initArrayList()
    {
        this.nom = new ArrayList<String>();
        this.message = new ArrayList<String>();
        this.temps = new ArrayList<String>();
    }

    // Ajoute dans la recyclerView ce que l'utilisateur à tapé sur le clavier si c'est pas vide.
    public void send(View view)
    {
        String inputMessage = editTextMessage.getText().toString();

        if(!inputMessage.equals(""))
        {
            adapter.addItem(this.username,inputMessage,"heure");
            editTextMessage.setText("");
        }
    }
}
