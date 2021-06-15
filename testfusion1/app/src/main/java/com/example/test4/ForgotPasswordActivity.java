package com.example.test4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity
{
    EditText editTextEmail;
    TextView txtVMailErrorForgotPassword;
    ProgressBar prgb;
    Button btnSendMail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mdp_oublie);

        this.editTextEmail = findViewById(R.id.editTextMailForgotPassword);
        this.txtVMailErrorForgotPassword = findViewById(R.id.txtVMailErrorForgotPassword);
        this.prgb = findViewById(R.id.progressBarForgotPwd);
        this.btnSendMail = findViewById(R.id.btnSendMail);

        // Cache le clavier lorsqu'on clique en dehors des champs de saisie
        hideKeyboardOnClick();

    }

    public void sendCode(View view)
    {
        btnSendMail.setClickable(false);
        String email = getMail();
        prgb.setVisibility(View.VISIBLE);
        hideKeyboard(view);

        if(!hasError(email))
        {
            postForgotPassword(email, res -> {
                if(!res.equals("error"))
                {
                    System.out.println("Pas d'erreur");
                    emailSender mail = new emailSender();

                    // Aucune erreur => Envoi d'un mail
                    try
                    {
                        mail.sendMail("PasToutSeul : Mot de passe oublié","Ce code sera valide pendant 5 minutes: "+res, email);
                        Toast.makeText(this, "Un code vient d'être envoyé sur cette adresse suivante : "+email, Toast.LENGTH_LONG).show();
                        launchCheckCode(email);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
                else
                {
                    editTextEmail.setTextColor(this.getResources().getColor(R.color.rouge_fonce));
                    editTextEmail.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
                    txtVMailErrorForgotPassword.setText("L'email rensigné n'existe pas.");
                    System.out.println("Erreur");
                }
                prgb.setVisibility(View.INVISIBLE);
                btnSendMail.setClickable(true);
            });
        }
        else
        {
            prgb.setVisibility(View.INVISIBLE);
            btnSendMail.setClickable(true);
        }
    }



    public boolean hasError(String email)
    {

        editTextEmail.setTextColor(this.getResources().getColor(R.color.black));
        editTextEmail.setBackgroundResource(R.drawable.backwithborder_noerror);
        editTextEmail.setHintTextColor(this.getResources().getColor(R.color.gris));
        txtVMailErrorForgotPassword.setText("");

        if(!isValidEmail(email))
        {
            editTextEmail.setTextColor(this.getResources().getColor(R.color.rouge_fonce));
            editTextEmail.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
            editTextEmail.setBackgroundResource(R.drawable.backwithborder_error);
            txtVMailErrorForgotPassword.setText("Cet email n'est pas valide.");
            return true;
        }

        return false;
    }

    public String getMail()
    {
        return this.editTextEmail.getText().toString();
    }

    //https://stackoverflow.com/questions/36040154/email-validation-on-edittext-android
    private static boolean isValidEmail(String email)
    {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void postForgotPassword(String mail, MainActivity.VolleyCallBack callback)
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
        String URL = "https://db-ezpfla.000webhostapp.com/forgotPassword.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("Réponse", response);
            try {
                callback.onSuccess(response.trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }
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

    public void launchCheckCode(String email)
    {
        Intent intent = new Intent(this, CheckCodeActivity.class);
        intent.putExtra("USER_MAIL",email);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // Cache le clavier lorsqu'on clique en dehors des champs de saisie
    public void hideKeyboardOnClick()
    {
        findViewById(R.id.layoutAcitivtyForgotPassword).setOnTouchListener((view, event) -> {
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
