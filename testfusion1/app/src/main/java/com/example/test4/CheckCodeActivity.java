package com.example.test4;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CheckCodeActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_check_code);

        // Cache le clavier lorsqu'on clique en dehors des champs de saisie
        hideKeyboardOnClick();
    }

    public void postCheckCode(String mail, MainActivity.VolleyCallBack callback)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        // Récupère une date
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String string_date = dateFormat.format(date);

        // Génération d'un code au hasard à envoyer sur le mail de l'utilisateur
        int code = new Random().nextInt(999999);
        String string_code = String.format("%06d", code);

        // URL du serveur web
        String URL = "https://db-ezpfla.000webhostapp.com/checkCode.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("Réponse", response);
            callback.onSuccess(response.trim());
            // Appel d'une fonction à éxecuter si succès de la requête
        }, error -> Log.e("Réponse", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> logs = new HashMap<>();
                //Ajout des arguments
                logs.put("email", mail);
                logs.put("code", string_code);
                logs.put("date", string_date);
                return logs;
            }
        };

        queue.add(postRequest);
    }


    // Cache le clavier lorsqu'on clique en dehors des champs de saisie
    public void hideKeyboardOnClick()
    {
        findViewById(R.id.layoutAcitivtyCheckCode).setOnTouchListener((view, event) -> {
            if(MotionEvent.ACTION_UP == event.getAction()) {
                hideKeyboard(view);
            }
            return false;
        });
    }

    // Cacher le clavier
    public void hideKeyboard(View view)
    {
        if (this.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
