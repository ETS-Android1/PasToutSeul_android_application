package com.example.test4;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
    EditText Prenom;
    EditText Nom;
    EditText Username;
    TextView errMail;
    TextView errPwd1;
    TextView errPwd2;
    TextView errPrenom;
    TextView errNom;
    TextView errUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        initAttributes();
    }

    public void loginActivity(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void initAttributes()
    {
        this.mail = findViewById(R.id.editTextMailRegister);
        this.pwd = findViewById(R.id.editTextPasswordRegister);
        this.pwd2 = findViewById(R.id.editTextPasswordRegister2);
        this.Prenom = findViewById(R.id.editTextTextPrenom);
        this.Nom = findViewById(R.id.editTextNom);
        this.Username = findViewById(R.id.editTextUsernameRegister);
        this.errMail = findViewById(R.id.txtErrMailRegister);
        this.errPwd1 = findViewById(R.id.txtErrPwd1);
        this.errPwd2 = findViewById(R.id.txtErrPwd2);
        this.errNom = findViewById(R.id.txtErrNom);
        this.errPrenom = findViewById(R.id.txtErrPrenom);
        this.errUsername = findViewById(R.id.txtErrUsername);

    }

    /*
     * Fonction : Récupère le nom
     * Return Type : String
     */
    public String getNom()
    {
        return Nom.getText().toString();
    }



    /*
     * Fonction : Récupère le prénom
     * Return Type : String
     */
    public String getPrenom() { return Prenom.getText().toString(); }



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
            return true;
        }
        else
        {
            errMail.setText("");
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
            errPwd2.setText("");
            return true;
        }
        if(!password2.equals(password1))
        {
            errPwd1.setText("");
            errPwd2.setText("Mots de passe non identiques");
            return true;
        }
        errPwd2.setText("");
        errPwd1.setText("");
        return false;
    }

    public boolean checkErrorNom()
    {
        String nom = getNom();

        if(nom.equals(""))
        {
            errNom.setText("Veuillez saisir un nom valide");
            return true;
        }
        errNom.setText("");
        return false;
    }


    public boolean checkErrorPrenom()
    {
        String prenom = getPrenom();

        if(prenom.equals(""))
        {
            errPrenom.setText("Veuillez saisir un prénom valide");
            return true;
        }
        errPrenom.setText("");
        return false;
    }

    public boolean checkErrorUsername()
    {
        String nom_user = getUsername();

        if(nom_user.equals(""))
        {
            errUsername.setText("Veuillez saisir un nom d'utilisateur valide");
            return true;
        }
        errUsername.setText("");
        return false;
    }
    public void createAccount(View view)
    {


        if (!checkErrorPrenom()  && !checkErrorNom() && !checkErrorUsername() && !checkErrorMail() && !checkErrorPassword() )
        {
            Thread thread = new Thread()
            {
                public void run()
                {
                    try{
                        postCreateAccount(getNom(), getPrenom(), getUsername(), getMail(), getPwd(), new MainActivity.VolleyCallBack() {
                            @Override
                            public void onSuccess(String res) {
                                if(res.equals("user_error"))
                                {
                                    errUsername.setText("Le nom d'utilisateur est déja pris");
                                }
                                else if(res.equals("mail_error"))
                                {
                                    errMail.setText("Ce mail est déja associé à un compte");
                                }
                                else
                                {
                                    Toast.makeText(inscription.this,"Création de compte réussi ! ", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(inscription.this, MainActivity.class);
                                    startActivity(intent);
                                }

                            }
                        });
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                }
            };
            thread.start();
        }

    }


    public void postCreateAccount(String nom, String prenom, String username, String mail, String password,final MainActivity.VolleyCallBack callback)
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

                logs.put("name",nom);
                logs.put("first_name",prenom);
                logs.put("username",username);
                logs.put("mail",mail);
                logs.put("password",password);

                return logs;
            }
        };

        queue.add(postRequest);
    }
}
