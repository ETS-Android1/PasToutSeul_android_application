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
    EditText tv_idch;
    EditText tv_namech;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        this.activity = this;



    }


    public void Click2(View v) {

        tv_idch =  findViewById(R.id.idchange);
        tv_namech = findViewById(R.id.namechange);
        TextView tv3 = findViewById(R.id.textView3);
        TextView tv4 = findViewById(R.id.textView4);
        AlertDialog.Builder popup = new AlertDialog.Builder(activity);
        popup.setTitle("Changements");
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_profile_change, null);
        popup.setView(customLayout);
        popup.show();
    }
}