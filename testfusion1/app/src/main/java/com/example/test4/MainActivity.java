package com.example.test4;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        createTouchListenerEditText(pwd);
        createTouchListenerEditText(mail);

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
     * Procédure : Création des touch listener pour les editText (Détails visuels pour les editText après une erreur)
     * */
    public void createTouchListenerEditText(EditText editText)
    {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    editText.setTextColor(getResources().getColor(R.color.black));
                    editText.setHintTextColor(getResources().getColor(R.color.gris));
                }

                return false;
            }
        });
    }

    public void mapActivity(View view, String user_info)
    {
        Intent intent = new Intent(this, map.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        String []USER_INFO = user_info.split("-");

        intent.putExtra("USER_NAME", USER_INFO[0]);
        intent.putExtra("USER_ID", USER_INFO[1]);
        intent.putExtra("USER_MAIL", getMail());
        intent.putExtra("USER_PASSWORD",getPwd());

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

        // Vérification des erreurs
        if(!checkError())
        {
            hideKeyboard(view);

            // Afficher la barre de progression
            pgrb.setVisibility(view.VISIBLE);

            // Création d'un thread afin d'effectuer une requête vers le serveur web sans bloquer l'application.
            Thread thread = new Thread()
            {
                @SuppressLint("SetTextI18n") // Ignore le warning
                public void run()
                {
                    try
                    {
                        // Requête de type POST ( - rapide que la requête GET / + sécure que la requête GET)
                        postLogin(getMail(), getPwd(), res -> {
                            if(res.equals("1")) // Erreur(s) concernant le compte de l'utilisateur
                            {
                                errMail.setText("Ce compte n'existe pas");
                                mail.setBackgroundResource(R.drawable.backwithborder_noerror);
                                pgrb.setVisibility(view.GONE);
                            }
                            else if(res.equals("2")) // Erreur(s) concernant le mot de passe
                            {
                                errPwd.setText("Mot de passe incorrect");
                                pwd.setBackgroundResource(R.drawable.backwithborder_error);
                                pgrb.setVisibility(view.GONE);
                            }
                            else // Aucunes erreurs
                            {
                                mapActivity(view,res);
                                pgrb.setVisibility(view.GONE);
                                cmode = true;
                            }
                        });
                    }
                    catch (Exception e) // Si erreur(s) de la requête
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

    /*
    * Procédure : Envoi d'une requête de type POST pour se connecter
    * */
    public void postLogin(String mail, String password,final VolleyCallBack callback)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        // URL du serveur web
        String URL = "https://db-ezpfla.000webhostapp.com/login.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("Réponse", response);
            // Appel d'une fonction à éxecuter si succès de la requête
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

        //Gérer les timeout
        postRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Lance la requête
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

        if(!isValidEmail(email))
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

    //https://stackoverflow.com/questions/36040154/email-validation-on-edittext-android
    private static boolean isValidEmail(String email)
    {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidUsername(String username)
    {
        char [] banArray = {'!','"','#','$','%','&','\'', '(',')','*','+',',','-','.','/',':',';','<','=','>','?','@','[','\\',']','^','_','`','{','|','}','~'};

        for(int i = 0; i < banArray.length; i++)
        {
            if(username.contains(Character.toString(banArray[i])))
            {
                return true;
            }
        }
        return false;
    }

    public void forgotPwd(View view)
    {
        Toast.makeText(getApplicationContext(), "Mot de passe oublié ?",Toast.LENGTH_SHORT).show();
    }

    /*
    * Procédure : Affiche/Cache le mot de passe
    */
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

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void guestMode(View view)
    {
        cmode = false;
        Intent intent = new Intent(this,map.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        intent.putExtra("USER_NAME", "INVITE");
        intent.putExtra("USER_ID", "1");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


}