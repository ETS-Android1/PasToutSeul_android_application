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

    private menu activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        this.activity = this;



    }


    public void ClickCompte(View v) {

        Intent i = new Intent(this, Compte.class);
        startActivity(i);
    }

}