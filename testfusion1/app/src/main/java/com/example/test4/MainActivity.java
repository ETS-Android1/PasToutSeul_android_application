package com.example.test4;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.*;
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
        this.pgrb = findViewById(R.id.prgb);
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
        int err = checkError();
        switch (err){
            case 1 :
                errMail.setText("Veuillez saisir un email valide");
                errPwd.setText("");
                break;
            case 2 :
                errPwd.setText("Veuillez saisir un mot de passe valide");
                errMail.setText("");
                break;
            case 3 :
                errMail.setText("Veuillez saisir un email valide");
                errPwd.setText("Veuillez saisir un mot de passe");
                break;
            default :

                pgrb.setVisibility(view.VISIBLE);
                errMail.setText("");
                errPwd.setText("");
                Thread thread = new Thread()
                {
                    public void run()
                    {
                        try {
                            postLogin(getMail(), getPwd(), new VolleyCallBack() {
                                @Override
                                public void onSuccess(String res)
                                {
                                    if(res.equals("1"))
                                    {
                                        errMail.setText("Le compte n'existe pas");
                                        pgrb.setVisibility(view.GONE);
                                    }
                                    else if(res.equals("2"))
                                    {
                                        errPwd.setText("Mot de passe incorrect");
                                        pgrb.setVisibility(view.GONE);
                                    }
                                    else
                                    {
                                        Toast.makeText(MainActivity.this,"Bienvenue, "+res, Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(MainActivity.this, map.class);
                                        cmode = true;
                                        startActivity(intent);

                                    }
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

                thread.start();
                break;
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
                Map<String,String> logs = new HashMap<String,String>();
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
     * Return : Un entier qui correspond à une erreur.
     *          0 : Pas d'erreur
     *          1 : Le format de l'email est incorrect seulement
     *          2 : L'utilisateur n'a pas saisie de mot de passe seulement
     *          3 : Erreur 1 et 2
     */
    public int checkError()
    {
        String email = getMail();
        String password = getPwd();

        Boolean isMail = true;
        Boolean isPass = true;

        int res = 0;

        //Détection du bon format d'un mail : https://www.javatpoint.com/java-email-validation
        //Pattern d'un mail "@ ... "
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if(!matcher.matches())
        {
            isMail = false;
        }
        if(password.equals(""))
        {
            isPass = false;
        }

        if(!isMail && isPass)
        {
            res = 1;
        }
        if(isMail && !isPass)
        {
            res = 2;
        }
        if(!isMail && !isPass)
        {
            res = 3;
        }

        return res;
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

    public void createAccountActivity(View view)
    {
        Intent intent = new Intent(this,inscription.class);
        startActivity(intent);
    }

    public void guestmode(View view)
    {
        cmode = false;
        Intent intent = new Intent(this,map.class);
        startActivity(intent);
    }


}