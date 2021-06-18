package pas.tout.seul;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pas.tout.seul.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CheckCodeActivity extends AppCompatActivity
{
    Context context;
    Requete requete;

    EditText editTextCheckCode;
    Button btnCheckCode;
    TextView txtVErrCode;
    ProgressBar prgb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_check_code);

        this.context = this;
        this.requete = new Requete(context);

        this.editTextCheckCode = findViewById(R.id.editTextCheckCode);
        this.btnCheckCode = findViewById(R.id.btnCheckCode);
        this.txtVErrCode = findViewById(R.id.txtVErrCode);
        this.prgb = findViewById(R.id.progressBarCheckCode);

        // Affiche le code
        editTextCheckCode.setTransformationMethod(null);

        // Cache le clavier lorsqu'on clique en dehors des champs de saisie
        hideKeyboardOnClick();
    }

    public void checkCode(View view)
    {
        // Chargement
        this.prgb.setVisibility(View.VISIBLE);


        // Infos nécessaire pour vérifier les informations dans la base de données
        String mail = getMail();
        String code = getCode();

        resetAffichage();

        // Verifie si l'utilisateur a bien renseigné un code pour éviter d'envoyer des requêtes inutiles
        if(code.length() != 0)
        {
            // Requête pour vérifier si le code est correct
            requete.getCode(mail, res ->
            {
                // Bloque les click
                btnCheckCode.setClickable(false);

                if(!res.contains("error"))
                {
                    // Tri les informations reçues : "(DATE : yyyy-MM-dd) (DATE : HH-mm-ss) (CODE)"
                    String[] string = res.split(" ");

                    String string_code = string[2];

                    // Seule l'heure, les minutes et les secondes sont nécessaires pour vérifier si le code est expiré (5min et après le code est expiré)
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.FRANCE);

                    // L'heure à laquelle le mail a été envoyé
                    String time1 = string[1];

                    // L'heure actuelle
                    String time2 = dateFormat.format(new Date());

                    // Conversion String -> Date
                    Date date1 = dateFormat.parse(time1);
                    Date date2 = dateFormat.parse(time2);

                    // Différence en ms entre l'emission du code et maintenant
                    long diff = date2.getTime() - date1.getTime();

                    // Expiration si la différence == 5 min (300000 ms)
                    if(diff < 300000) // Valide
                    {
                        // Réactive les clicks
                        btnCheckCode.setClickable(true);

                        if(code.equals(string_code))
                        {
                            // Si le code est bon, alors on peut changer le mot de passe
                            launchChangePassword();
                        }
                        else
                        {
                            affichageCodeIncorrect();
                        }
                    }
                    else // Expiré
                    {
                        affichageCodeExpire();
                    }
                }
                else
                {
                    System.out.println("ERROR");
                }

                // Fin chargement
                this.prgb.setVisibility(View.INVISIBLE);

                // Réactive les clicks
                btnCheckCode.setClickable(true);
            });
        }
        else
        {
            affichageCodeInvalide();

            // Fin chargement
            this.prgb.setVisibility(View.INVISIBLE);

            // Réactive les clicks
            btnCheckCode.setClickable(true);
        }
    }

    public void resetAffichage()
    {
        this.editTextCheckCode.setBackgroundResource(R.drawable.backwithborder_noerror);
        this.editTextCheckCode.setTextColor(this.getResources().getColor(R.color.black));
        this.editTextCheckCode.setHintTextColor(this.getResources().getColor(R.color.gris));
        this.txtVErrCode.setText("");
    }

    @SuppressLint("SetTextI18n")
    public void affichageCodeIncorrect()
    {
        this.editTextCheckCode.setBackgroundResource(R.drawable.backwithborder_error);
        this.editTextCheckCode.setTextColor(this.getResources().getColor(R.color.rouge_clair));
        this.editTextCheckCode.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
        this.txtVErrCode.setText("Le code est incorrect");
    }

    @SuppressLint("SetTextI18n")
    public void affichageCodeExpire()
    {
        this.editTextCheckCode.setBackgroundResource(R.drawable.backwithborder_error);
        this.editTextCheckCode.setTextColor(this.getResources().getColor(R.color.rouge_clair));
        this.editTextCheckCode.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
        this.txtVErrCode.setText("Le code a expiré.");
    }

    @SuppressLint("SetTextI18n")
    public void affichageCodeInvalide()
    {
        this.editTextCheckCode.setBackgroundResource(R.drawable.backwithborder_error);
        this.editTextCheckCode.setTextColor(this.getResources().getColor(R.color.rouge_clair));
        this.editTextCheckCode.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
        this.txtVErrCode.setText("Veuillez entrer un code valide.");
    }

    public String getCode()
    {
        return this.editTextCheckCode.getText().toString();
    }

    public String getMail()
    {
        return getIntent().getStringExtra("USER_MAIL");
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
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void launchChangePassword()
    {
        Intent intent = new Intent(CheckCodeActivity.this, ChangePasswordActivity.class);

        intent.putExtra("USER_MAIL",getMail());

        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
