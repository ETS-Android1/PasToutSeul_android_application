package com.example.test4;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.*;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.PasswordTransformationMethod;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    boolean hide;
    static boolean cmode;
    EditText pwd;
    EditText mail;
    TextView errMail;
    TextView errPwd;
    ImageButton btnShowHide;
    ProgressBar pgrb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAttribute();

        //Ajout d'action à effectuer lorsqu'on clique sur les EditText
        pwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    pwd.setTextColor(getResources().getColor(R.color.black));
                    pwd.setHintTextColor(getResources().getColor(R.color.gris));
                }

                return false; // return is important...
            }
        });

        mail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    mail.setTextColor(getResources().getColor(R.color.black));
                    mail.setHintTextColor(getResources().getColor(R.color.gris));
                }

                return false; // return is important...
            }
        });

        findViewById(R.id.layoutActivityMain).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    hideKeyboard(v);
                }

                return false; // return is important...
            }
        });
    }

    /*
     * Méthode : Initialisation des attributs
     */
    public void initAttribute()
    {
        this.mail = findViewById(R.id.editTextMailLogin);
        this.pwd = findViewById(R.id.editTextPasswordLogin);
        this.btnShowHide = findViewById(R.id.imgBtnShowHide);
        this.errMail = findViewById(R.id.txtErrMailLogin);
        this.errPwd = findViewById(R.id.txtErrPwdLogin);
        this.hide = true;
        this.pgrb = findViewById(R.id.prgbLogin);
        this.pgrb.setVisibility(View.INVISIBLE);
        this.cmode = true;
    }

    /*
     * Fonction : Récupère le mail
     * Return Type : String
     */
    public String getMail()
    {
        return mail.getText().toString();
    }

    /*
     * Fonction : Récupère le mot de passe
     * Return Type : String
     */
    public String getPwd()
    {
        return pwd.getText().toString();
    }


    public void connexion(View view)
    {
        if(!checkError())
        {
            hideKeyboard(view);

            //Afficher la barre de progression
            pgrb.setVisibility(view.VISIBLE);

            // Création d'un thread afin d'effectuer une requête vers le serveur web sans bloquer l'application.
            Thread thread = new Thread()
            {
                @SuppressLint("SetTextI18n")
                public void run()
                {
                    try
                    {
                        postLogin(getMail(), getPwd(), res -> {
                            if(res.equals("1"))
                            {
                                errMail.setText("Ce compte n'existe pas");
                                mail.setBackgroundResource(R.drawable.backwithborder_noerror);
                                pgrb.setVisibility(view.GONE);
                            }
                            else if(res.equals("2"))
                            {
                                errPwd.setText("Mot de passe incorrect");
                                pwd.setBackgroundResource(R.drawable.backwithborder_error);
                                pgrb.setVisibility(view.GONE);
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this,"Bienvenue, "+res, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, map.class);
                                cmode = true;
                                startActivity(intent);

                            }
                        });
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                        pgrb.setVisibility(view.GONE);
                    }
                }
            };

            //Lancement du thread
            thread.start();
        }
    }

    public void postLogin(String mail, String password,final VolleyCallBack callback)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String URL = "https://db-ezpfla.000webhostapp.com/login.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("Réponse", response);
            callback.onSuccess(response.trim()); // trim() pour enlever les espaces car la réponse contient des espaces.
        }, error -> Log.e("Réponse", error.toString()))
        {
            @Override
            protected  Map<String,String> getParams()
            {
                Map<String,String> logs = new HashMap<>();
                //Ajout des arguments
                logs.put("mail",mail);
                logs.put("password",password);

                return logs;
            }
        };

        queue.add(postRequest);
    }

    public interface VolleyCallBack{
        void onSuccess(String res);
    }

    /*
     * Fonction : Verifie si il y a des erreurs dans la saisie
     * Return : false si aucune erreur
     *          true sinon
     */
    @SuppressLint("SetTextI18n")
    public boolean checkError()
    {
        Boolean hasError = false;

        String email = getMail();
        String password = getPwd();

        //Reinitialisation de l'affichage
        errPwd.setText("");
        errMail.setText("");

        mail.setBackgroundResource(R.drawable.backwithborder_noerror);
        mail.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));

        pwd.setBackgroundResource(R.drawable.backwithborder_noerror);
        pwd.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));

        //Détection du bon format d'un mail : https://www.javatpoint.com/java-email-validation
        //Pattern d'un mail "@ ... "
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if(!matcher.matches())
        {
            hasError = true;
            errMail.setText("Veuillez saisir un email valide");
            mail.setBackgroundResource(R.drawable.backwithborder_error);
            mail.setTextColor(this.getResources().getColor(R.color.rouge_clair));
            mail.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
        }
        if(password.equals(""))
        {
            hasError = true;
            errPwd.setText("Veuillez saisir un mot de passe");
            pwd.setBackgroundResource(R.drawable.backwithborder_error);
            pwd.setTextColor(this.getResources().getColor(R.color.rouge_clair));
            pwd.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
        }

        return hasError;
    }


    public void forgotPwd(View view)
    {
        Toast.makeText(getApplicationContext(), "Mot de passe oublié ?",Toast.LENGTH_SHORT).show();
    }

    public void show_hide(View view)
    {

        if(hide)
        {
            btnShowHide.setBackgroundResource(R.drawable.affiche);
            pwd.setTransformationMethod(null); //Affiche le mdp
            hide = false;
        }
        else
        {
            btnShowHide.setBackgroundResource(R.drawable.cache);
            pwd.setTransformationMethod(new PasswordTransformationMethod()); //Cache le mdp
            hide = true;
        }
    }

    public void hideKeyboard(View view)
    {
        // Cacher le clavier
        if (this.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void createAccountActivity(View view)
    {
        Intent intent = new Intent(this,inscription.class);
        startActivity(intent);
    }

    public void guestMode(View view)
    {
        cmode = false;
        Intent intent = new Intent(this,map.class);
        startActivity(intent);
    }


}