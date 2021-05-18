package com.example.test4;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Compte extends AppCompatActivity {

    private Compte activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compte);
        this.activity = this;
    }


    public void ClickChange(View v){

        AlertDialog.Builder change = new AlertDialog.Builder(activity);
        View changeLayout = getLayoutInflater().inflate(R.layout.dialog_profile_change, null);
        change.setView(changeLayout);
        change.show();
    }


}