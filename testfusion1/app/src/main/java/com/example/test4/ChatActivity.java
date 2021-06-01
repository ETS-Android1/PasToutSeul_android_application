package com.example.test4;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    String titre, id_user, id_group, username;

    TextView titreView;
    EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.titre = getIntent().getStringExtra("TITRE");
        this.id_user = getIntent().getStringExtra("USER_ID");
        this.username = getIntent().getStringExtra("USER_NAME");
        this.id_group = getIntent().getStringExtra("GROUP_ID");

        this.titreView = findViewById(R.id.textViewTitleChat);
        this.titreView.setText(this.titre);
        this.message = findViewById(R.id.editTextChat);

    }

    public void send(View view)
    {
        String mess = message.getText().toString();
        System.out.println(mess);
        String []nom;
        String []message;
        String []temps;
        Adapter adapter = new Adapter(this, nom, message, temps);
        recyclerViewMessage.setAdapter(adapter);
    }
}
