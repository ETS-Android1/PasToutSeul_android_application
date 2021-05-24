package com.example.test4;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
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

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

                return false; // return is important...
            }
        });

        createTouchListenerEditText(Username);
        createTouchListenerEditText(mail);
        createTouchListenerEditText(pwd);
        createTouchListenerEditText(pwd2);
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

    public void loginActivity(View view)
    {
        Intent intent = new Intent(inscription.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void initAttributes()
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

    /*
     * Fonction : Récupère le mot de passe à répéter
     * Return Type : String
     */
    public String getPwd2(){return pwd2.getText().toString();}


    /*
     * Fonction : Récupère le mot de passe à répéter
     * Return Type : String
     */
    public String getUsername(){return Username.getText().toString();}


    public boolean checkErrorMail()
    {
        String email = getMail();

        //Détection du bon format d'un mail : https://www.javatpoint.com/java-email-validation
        //Pattern d'un mail "@ ... "
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if(!matcher.matches())
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

    public boolean checkErrorPassword()
    {
        String password1 = getPwd();
        String password2 = getPwd2();

        if(password1.equals(""))
        {
            errPwd1.setText("Veuillez saisir un mot de passe valide");
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

    public boolean checkErrorUsername()
    {
        String nom_user = getUsername();

        if(nom_user.equals(""))
        {
            errUsername.setText("Veuillez saisir un nom d'utilisateur valide");
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
                                    loginActivity(view);

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

    public void postCreateAccount(String username, String mail, String password,final MainActivity.VolleyCallBack callback)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String URL = "https://db-ezpfla.000webhostapp.com/createAccount.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("Réponse", response);
            callback.onSuccess(response.trim()); // trim() pour enlever les espaces car la réponse contient des espaces.
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

    public void affichageErrorMail()
    {
        mail.setBackgroundResource(R.drawable.backwithborder_error);
        mail.setTextColor(this.getResources().getColor(R.color.rouge_clair));
        mail.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
    }

    public void affichageErrorUsername()
    {
        Username.setBackgroundResource(R.drawable.backwithborder_error);
        Username.setTextColor(this.getResources().getColor(R.color.rouge_clair));
        Username.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
    }

    public void affichageErrorPass1()
    {
        pwd2.setBackgroundResource(R.drawable.backwithborder_error);
        pwd2.setTextColor(this.getResources().getColor(R.color.rouge_clair));
        pwd2.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
    }

    public void affichageErrorPass2()
    {
        pwd.setBackgroundResource(R.drawable.backwithborder_error);
        pwd.setTextColor(this.getResources().getColor(R.color.rouge_clair));
        pwd.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
    }

    public void resetAffichageErrorMail()
    {
        mail.setBackgroundResource(R.drawable.backwithborder_noerror);
        mail.setTextColor(this.getResources().getColor(R.color.black));
        mail.setHintTextColor(this.getResources().getColor(R.color.gris));
    }

    public void resetAffichageErrorUsername()
    {
        Username.setBackgroundResource(R.drawable.backwithborder_noerror);
        Username.setTextColor(this.getResources().getColor(R.color.black));
        Username.setHintTextColor(this.getResources().getColor(R.color.gris));
    }

    public void resetAffichageErrorPass1()
    {
        pwd2.setBackgroundResource(R.drawable.backwithborder_noerror);
        pwd2.setTextColor(this.getResources().getColor(R.color.black));
        pwd2.setHintTextColor(this.getResources().getColor(R.color.gris));
    }

    public void resetAffichageErrorPass2()
    {
        pwd.setBackgroundResource(R.drawable.backwithborder_noerror);
        pwd.setTextColor(this.getResources().getColor(R.color.black));
        pwd.setHintTextColor(this.getResources().getColor(R.color.gris));
    }

    public void hideKeyboard(View view)
    {
        // Cacher le clavier
        if (this.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
