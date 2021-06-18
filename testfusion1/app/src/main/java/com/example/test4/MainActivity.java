package com.example.test4;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.text.method.PasswordTransformationMethod;
import android.content.Intent;
import android.widget.Toast;

import org.mindrot.jbcrypt.BCrypt;

public class MainActivity extends AppCompatActivity {

    Context context;
    Requete requete;

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

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }

        //Ajout d'action à effectuer lorsqu'on clique sur les EditText
        createTouchListenerEditText(pwd);
        createTouchListenerEditText(mail);

        // Cache le clavier lorsqu'on clique en dehors des champs de saisie
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

    // Lancement de l'activity de la carte
    public void mapActivity(String username, String user_id)
    {
        Intent intent = new Intent(this, map.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        System.out.println("ID USER = "+user_id+" username : "+username);

        intent.putExtra("USER_NAME", username);
        intent.putExtra("USER_ID", user_id);
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
        this.context = this;
        this.requete = new Requete(context);
        this.mail = findViewById(R.id.editTextMailLogin);
        this.pwd = findViewById(R.id.editTextPasswordLogin);
        this.btnShowHide = findViewById(R.id.imgBtnShowHide);
        this.errMail = findViewById(R.id.txtErrMailLogin);
        this.errPwd = findViewById(R.id.txtErrPwdLogin);
        this.hide = true;
        this.pgrb = findViewById(R.id.prgbLogin);
        this.pgrb.setVisibility(View.INVISIBLE);
        cmode = true;
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

    // Button listener pour le bouton connexion
    // Envoye une requête pour vérifier si le compte existe, si oui connexion
    // Sinon message d'erreur
    public void connexion(View view)
    {
        // Afficher la barre de progression
        pgrb.setVisibility(View.VISIBLE);

        // Vérification des erreurs
        if(!checkError())
        {
            hideKeyboard(view);

            // Création d'un thread afin d'effectuer une requête vers le serveur web sans bloquer l'application.
            Thread thread = new Thread()
            {
                @SuppressLint("SetTextI18n") // Ignore le warning
                public void run()
                {
                    try
                    {
                        // Requête de type POST ( - rapide que la requête GET / + sécure que la requête GET)
                        requete.login(getMail(), res ->
                        {
                            System.out.println(res);
                            if(res.equals("1")) // Erreur(s) concernant le compte de l'utilisateur
                            {
                                errMail.setText("Ce compte n'existe pas");
                                mail.setBackgroundResource(R.drawable.backwithborder_noerror);
                            }
                            else // Aucune erreurs
                            {
                                String []USER_INFO = res.split("\\s+");
                                String hashPass = USER_INFO[2];
                                try
                                {
                                    if(BCrypt.checkpw(getPwd(),hashPass))
                                    {
                                        mapActivity(USER_INFO[0],USER_INFO[1]);
                                        pgrb.setVisibility(View.INVISIBLE);
                                        cmode = true;
                                    }
                                    else
                                    {
                                        errPwd.setText("Mot de passe incorrect");
                                        pwd.setBackgroundResource(R.drawable.backwithborder_error);
                                    }

                                    // Fin du thread
                                    pgrb.setVisibility(View.INVISIBLE);
                                }
                                catch(Exception e)
                                {
                                    errPwd.setText("Mot de passe incorrect");
                                    pwd.setBackgroundResource(R.drawable.backwithborder_error);

                                    // Fin du thread
                                    pgrb.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                    catch (Exception e) // Si erreur(s) de la requête
                    {
                        e.printStackTrace();
                        System.out.println(e.getMessage());

                        // Fin du thread
                        pgrb.setVisibility(View.INVISIBLE);
                    }
                }
            };

            //Lancement du thread
            thread.start();
        }
    }

    /*
     * Fonction : Verifie si il y a des erreurs dans la saisie
     * Return : false si aucune erreur
     *          true sinon
     */
    @SuppressLint("SetTextI18n")
    public boolean checkError()
    {
        boolean hasError = false;

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

        for (char c : banArray) {
            if (username.contains(Character.toString(c))) {
                return true;
            }
        }
        return false;
    }

    // Lancement de l'activity pour le mot de passe oublié
    public void launchForgotPwd(View view)
    {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void createAccountActivity(View view)
    {
        Intent intent = new Intent(this,inscription.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        // Animation
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void guestMode(View view)
    {
        cmode = false;
        Intent intent = new Intent(this,map.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        intent.putExtra("USER_NAME", "INVITE");
        intent.putExtra("USER_ID", "1");
        intent.putExtra("USER_MAIL", "INVITE@INVITE.com");
        intent.putExtra("USER_PASSWORD", "INVITE");

        startActivity(intent);

        // Animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


}