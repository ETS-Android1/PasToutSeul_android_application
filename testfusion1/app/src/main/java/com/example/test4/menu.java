package com.example.test4;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class menu extends AppCompatActivity {

    boolean cmode;
    private menu activity;
    TextView prenom;
    TextView nom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        this.activity = this;
        cmode = MainActivity.cmode;
        prenom = findViewById(R.id.textView3);
        nom = findViewById(R.id.textView4);

        if (!cmode){
            prenom.setText("Mode visiteur");
            nom.setText("");
        }


    }


    public void ClickCompte(View v) {
        if(cmode){
        Intent i = new Intent(this, Compte.class);
        startActivity(i);}
    }

}