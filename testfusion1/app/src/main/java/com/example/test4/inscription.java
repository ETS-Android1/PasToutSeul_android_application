package com.example.test4;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class inscription extends AppCompatActivity {

    EditText mail;
    EditText pwd;
    EditText pwd2;
    EditText Username;
    TextView errMail;
    TextView errPwd1;
    TextView errPwd2;
    TextView errUsername;
    ProgressBar pgrb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inscription);
        initAttributes();

        findViewById(R.id.layoutActivityInscription).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    hideKeyboard(v);
                }

                return false;
            }
        });

        createTouchListenerEditText(Username);
        createTouchListenerEditText(mail);
        createTouchListenerEditText(pwd);
        createTouchListenerEditText(pwd2);
    }

    @Override
    public void onBackPressed()
    {
        startLoginActivity();
    }

    /*
    * Procédure : Création des touch listener pour les editText (Détails visuels pour les editText après une erreur)
    * */
    private void createTouchListenerEditText(EditText editText)
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

    // Lancement de la page de connexion
    public void startLoginActivity()
    {
        Intent intent = new Intent(inscription.this, MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // Button listener
    public void loginActivityBtn(View view)
    {
        startLoginActivity();
    }

    // Initialisation des vues
    private void initAttributes()
    {
        this.mail = findViewById(R.id.editTextMailRegister);
        this.pwd = findViewById(R.id.editTextPasswordRegister);
        this.pwd2 = findViewById(R.id.editTextPasswordRegister2);
        this.Username = findViewById(R.id.editTextUsernameRegister);
        this.errMail = findViewById(R.id.txtErrMailRegister);
        this.errPwd1 = findViewById(R.id.txtErrPwd1);
        this.errPwd2 = findViewById(R.id.txtErrPwd2);
        this.errUsername = findViewById(R.id.txtErrUsername);
        this.pgrb = findViewById(R.id.pgrbInscription);
        pgrb.setVisibility(View.INVISIBLE);
    }

    /*
     * Fonction : Récupère le mail
     * Return Type : String
     */
    private String getMail()
    {
        return mail.getText().toString();
    }

    /*
     * Fonction : Récupère le mot de passe
     * Return Type : String
     */
    private String getPwd()
    {
        return pwd.getText().toString();
    }

    /*
     * Fonction : Récupère le mot de passe à répéter
     * Return Type : String
     */
    private String getPwd2(){return pwd2.getText().toString();}


    /*
     * Fonction : Récupère le mot de passe à répéter
     * Return Type : String
     */
    private String getUsername(){return Username.getText().toString();}


    private boolean checkErrorMail()
    {
        String email = getMail();

        if(!isValidEmail(email))
        {
            errMail.setText("Veuillez saisir un email valide");
            affichageErrorMail();
            return true;
        }
        else
        {
            errMail.setText("");
            resetAffichageErrorMail();

            return false;
        }

    }

    //https://stackoverflow.com/questions/36040154/email-validation-on-edittext-android
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    /*
    * Fonction : Vérifie si le nom d'utilisateur est valide
    * Return : false si non valide
    *          true sinon
    * */
    private boolean isValidUsername(String username)
    {
        char [] banArray = {'!','"','#','$','%','&','\'', '(',')','*','+',',','-','.','/',':',';','<','=','>','?','@','[','\\',']','^','_','`','{','|','}','~'};

        for(int i = 0; i < banArray.length; i++)
        {
            if(username.contains(Character.toString(banArray[i])))
            {
                return false;
            }
        }
        return true;
    }

    /*
     * Fonction : Vérifie si le nom d'utilisateur est valide
     * Return : false si non valide
     *          true sinon
     * */
    private boolean isValidPassword(String mdp)
    {
        char [] banArray = {'!','"','#','$','%','&','\'', '(',')','*','+',',','-','.','/',':',';','<','=','>','?','@','[','\\',']','^','_','`','{','|','}','~'};

        for(int i = 0; i < banArray.length; i++)
        {
            if(mdp.contains(Character.toString(banArray[i])))
            {
                return false;
            }
        }

        return true;
    }

    private boolean checkErrorPassword()
    {
        String password1 = getPwd();
        String password2 = getPwd2();

        int len = password1.length();
        if(password1.equals(""))
        {
            errPwd1.setText("Veuillez saisir un mot de passe valide");
            affichageErrorPass1();

            errPwd2.setText("");
            resetAffichageErrorPass2();
            return true;
        }

        if(len < 8 || len > 16)
        {
            errPwd1.setText("Votre mot de passe doit contenir entre 8 et 16 caractères");
            affichageErrorPass1();

            errPwd2.setText("");
            resetAffichageErrorPass2();
            return true;
        }

        if(!isValidPassword(password1))
        {
            errPwd1.setText("Le mot de passe ne doit pas contenir ces caractères :  !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~");
            affichageErrorPass1();

            errPwd2.setText("");
            resetAffichageErrorPass2();
            return true;
        }

        if(!password2.equals(password1))
        {
            errPwd1.setText("");
            resetAffichageErrorPass1();

            errPwd2.setText("Mots de passe non identiques");
            affichageErrorPass2();
            return true;
        }

        errPwd1.setText("");
        resetAffichageErrorPass1();

        errPwd2.setText("");
        resetAffichageErrorPass2();

        return false;
    }

    private boolean checkErrorUsername()
    {
        String nom_user = getUsername();

        if(nom_user.equals(""))
        {
            errUsername.setText("Veuillez saisir un nom d'utilisateur valide");
            affichageErrorUsername();
            return true;
        }

        if(!isValidUsername(nom_user))
        {
            System.out.println("aezae");
            errUsername.setText("Le nom d'utilisateur ne doit pas contenir ces caractères : !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~");
            affichageErrorUsername();
            return true;
        }

        errUsername.setText("");
        resetAffichageErrorUsername();

        return false;
    }

    public void createAccount(View view)
    {
        if (!checkErrorUsername() && !checkErrorMail() && !checkErrorPassword()) // Vérification des erreurs possibles
        {
            pgrb.setVisibility(view.VISIBLE);
            pgrb.bringToFront();
            hideKeyboard(view);
            Thread thread = new Thread()
            {
                public void run()
                {
                    try{
                        postCreateAccount(getUsername(), getMail(), getPwd(), new MainActivity.VolleyCallBack() {
                            @Override
                            public void onSuccess(String res) {
                                if(res.equals("user_error"))
                                {
                                    errUsername.setText("Ce nom d'utilisateur est déja pris");
                                    affichageErrorUsername();
                                    pgrb.setVisibility(view.GONE);
                                }
                                else if(res.equals("mail_error"))
                                {
                                    errMail.setText("Ce mail est déja associé à un compte");
                                    affichageErrorMail();
                                    pgrb.setVisibility(view.GONE);
                                }
                                else
                                {
                                    Toast.makeText(inscription.this,"Création de compte réussi ! ", Toast.LENGTH_LONG).show();
                                    pgrb.setVisibility(view.GONE);
                                    startLoginActivity();

                                }

                            }
                        });
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                        pgrb.setVisibility(view.GONE);
                    }
                }
            };
            thread.start();
        }

    }

    private void postCreateAccount(String username, String mail, String password,final MainActivity.VolleyCallBack callback)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String URL = "https://db-ezpfla.000webhostapp.com/createAccount.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("Réponse", response);
            try {
                callback.onSuccess(response.trim()); // trim() pour enlever les espaces car la réponse contient des espaces.
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("Réponse", error.toString()))
        {
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> logs = new HashMap<String,String>();

                logs.put("username",username);
                logs.put("mail",mail);
                logs.put("password",password);

                return logs;
            }
        };

        queue.add(postRequest);
    }

    private void affichageErrorMail()
    {
        mail.setBackgroundResource(R.drawable.backwithborder_error);
        mail.setTextColor(this.getResources().getColor(R.color.rouge_clair));
        mail.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
    }

    private void affichageErrorUsername()
    {
        Username.setBackgroundResource(R.drawable.backwithborder_error);
        Username.setTextColor(this.getResources().getColor(R.color.rouge_clair));
        Username.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
    }

    private void affichageErrorPass1()
    {
        pwd2.setBackgroundResource(R.drawable.backwithborder_error);
        pwd2.setTextColor(this.getResources().getColor(R.color.rouge_clair));
        pwd2.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
    }

    private void affichageErrorPass2()
    {
        pwd.setBackgroundResource(R.drawable.backwithborder_error);
        pwd.setTextColor(this.getResources().getColor(R.color.rouge_clair));
        pwd.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
    }

    private void resetAffichageErrorMail()
    {
        mail.setBackgroundResource(R.drawable.backwithborder_noerror);
        mail.setTextColor(this.getResources().getColor(R.color.black));
        mail.setHintTextColor(this.getResources().getColor(R.color.gris));
    }

    private void resetAffichageErrorUsername()
    {
        Username.setBackgroundResource(R.drawable.backwithborder_noerror);
        Username.setTextColor(this.getResources().getColor(R.color.black));
        Username.setHintTextColor(this.getResources().getColor(R.color.gris));
    }

    private void resetAffichageErrorPass1()
    {
        pwd2.setBackgroundResource(R.drawable.backwithborder_noerror);
        pwd2.setTextColor(this.getResources().getColor(R.color.black));
        pwd2.setHintTextColor(this.getResources().getColor(R.color.gris));
    }

    private void resetAffichageErrorPass2()
    {
        pwd.setBackgroundResource(R.drawable.backwithborder_noerror);
        pwd.setTextColor(this.getResources().getColor(R.color.black));
        pwd.setHintTextColor(this.getResources().getColor(R.color.gris));
    }

    private void hideKeyboard(View view)
    {
        // Cacher le clavier
        if (this.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
