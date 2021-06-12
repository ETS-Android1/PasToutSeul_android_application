package com.example.test4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class donate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
    }

    public void call1(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.emmaus-solidarite.org/faire-un-don/"));
        startActivity(intent);
    }
    public void call2(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.secourspopulaire.fr/faire-un-don"));
        startActivity(intent);
    }
    public void call3(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.secours-catholique.org/comment-donner"));
        startActivity(intent);
    }
    public void call4(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.croix-rouge.fr/Je-donne/Faire-un-don"));
        startActivity(intent);
    }
    public void call5(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.restosducoeur.org/faire-un-don-financier/"));
        startActivity(intent);
    }
    public void call6(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://anrs.asso.fr/"));
        startActivity(intent);
    }
}