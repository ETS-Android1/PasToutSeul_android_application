package com.example.test4;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    String titre;
    String id_user;
    String id_group;

    TextView titreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.titre = getIntent().getStringExtra("TITRE");
        this.id_user = getIntent().getStringExtra("USER_ID");
        this.id_group = getIntent().getStringExtra("GROUP_ID");

        this.titreView = findViewById(R.id.textViewTitleChat);
        this.titreView.setText(this.titre);

    }
}
