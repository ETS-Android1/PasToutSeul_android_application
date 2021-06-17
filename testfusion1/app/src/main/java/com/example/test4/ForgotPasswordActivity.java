package com.example.test4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class ForgotPasswordActivity extends AppCompatActivity
{
    Context context;
    Requete requete;

    EditText editTextEmail;
    TextView txtVMailErrorForgotPassword;
    ProgressBar prgb;
    Button btnSendMail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mdp_oublie);

        this.context = this;
        this.requete = new Requete(context);

        this.editTextEmail = findViewById(R.id.editTextMailForgotPassword);
        this.txtVMailErrorForgotPassword = findViewById(R.id.txtVMailErrorForgotPassword);
        this.prgb = findViewById(R.id.progressBarForgotPwd);
        this.btnSendMail = findViewById(R.id.btnSendMail);

        // Cache le clavier lorsqu'on clique en dehors des champs de saisie
        hideKeyboardOnClick();

    }

    @SuppressLint("SetTextI18n")
    public void sendCode(View view)
    {
        btnSendMail.setClickable(false);
        String email = getMail();
        prgb.setVisibility(View.VISIBLE);
        hideKeyboard(view);

        if(!hasError(email))
        {
            requete.forgotPassword(email, res -> {
                if(!res.equals("error"))
                {
                    emailSender mail = new emailSender(this);

                    // Aucune erreur => Envoi d'un mail
                    try
                    {
                        mail.sendMail("PasToutSeul : Mot de passe oublié",res, email, "code_6digit.html");
                        Toast.makeText(this, "Un code vient d'être envoyé sur cette adresse suivante : "+email, Toast.LENGTH_LONG).show();

                        // Lancement de l'activity pour vérifier le code reçu par mail
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

    @SuppressLint("SetTextI18n")
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
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
