package pas.tout.seul;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pas.tout.seul.R;

import org.mindrot.jbcrypt.BCrypt;

public class ChangePasswordActivity extends AppCompatActivity
{
    EditText editPassword1,editPassword2;
    TextView errPassword1, errPassword2;
    ProgressBar prgb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_password);

        // Champ de saisie de l'utilisateur
        this.editPassword1 = findViewById(R.id.editTextChangePassword1);
        this.editPassword2 = findViewById(R.id.editTextChangePassword2);

        // Affichage des messages d'erreurs
        this.errPassword1 = findViewById(R.id.txtVErrPass1);
        this.errPassword2 = findViewById(R.id.txtVErrPass2);

        // ProgressBar
        this.prgb = findViewById(R.id.prgbChangePassword);

        // Cache le clavier lorsqu'on clique en dehors des champs de saisie
        hideKeyboardOnClick();
    }

    // Changement du mot de passe
    public void changePassword(View view)
    {
        // Chargement
        this.prgb.setVisibility(View.VISIBLE);

        // Remise à zéro de l'affichage
        resetAffichage();

        // Récupération de la saisie de l'utilisateur
        String password1 = getPassword1();
        String password2 = getPassword2();

        // Vérification de la validité du mot de passe
        if(isValid(getPassword1()))
        {
            // Vérification si les mots de passe sont identiques
            if(isSame(password1,password2))
            {
                Requete requete = new Requete(this);

                // Hachage du mot de passe
                String salt = BCrypt.gensalt();
                String hashPass = BCrypt.hashpw(password1,salt);

                // Changement du mot de passe
                requete.changePassword(getMail(),hashPass, res ->
                {
                    // Sujet du message
                    String subject = "PasToutSeul : Votre mot de passe vient d'être modifié.";

                    // Envoi d'un mail afin d'informer l'utilisateur du changement de son mot de passe
                    emailSender email = new emailSender(this);
                    email.sendMail(subject,"",getMail(), "change_password_success.html");

                    // Message affiché à l'utilisateur indiquant que son mot de passe a été modifié
                    Toast.makeText(this, "Votre mot de passe a été modifié", Toast.LENGTH_SHORT).show();

                    // On lance la page de connexion
                    launchMainActivity();

                    // Fin chargement
                    this.prgb.setVisibility(View.VISIBLE);
                });
            }
            else
            {
                // Les mots de passes ne sont pas identiques
                affichageErrorPassword2();
                this.prgb.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            // Le mot de passe n'est pas valide
            affichageErrorPassword1();
            this.prgb.setVisibility(View.VISIBLE);
        }
    }

    // Détection d'un mot de passe valide :
    // true si valide
    // false sinon
    public boolean isValid(String password)
    {
        int len = password.length();

        // Mot de passe compris entre 8 et 16 caractères
        if(len < 8 || len > 16)
        {
            return false;
        }

        // Liste de caractères bannis
        char [] banArray = {'!','"','#','$','%','&','\'', '(',')','*','+',',','-','.','/',':',';','<','=','>','?','@','[','\\',']','^','_','`','{','|','}','~'};

        for (char c : banArray) {
            if (password.contains(Character.toString(c))) {
                return false;
            }
        }
        return true;
    }

    // Détection des mots de passes identiques
    public boolean isSame(String password1, String password2)
    {
        return password1.equals(password2);
    }

    // Cache le clavier lorsqu'on clique en dehors des champs de saisie
    public void hideKeyboardOnClick()
    {
        findViewById(R.id.layoutAcitivtyChangePassword).setOnTouchListener((view, event) -> {
            if (MotionEvent.ACTION_UP == event.getAction()) {
                ChangePasswordActivity.this.hideKeyboard(view);
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

    // Remise à 0 de l'affichage
    public void resetAffichage()
    {
        this.editPassword1.setBackgroundResource(R.drawable.backwithborder_noerror);
        this.editPassword1.setTextColor(this.getResources().getColor(R.color.black));
        this.editPassword1.setHintTextColor(this.getResources().getColor(R.color.gris));
        this.errPassword1.setText("");

        this.editPassword2.setBackgroundResource(R.drawable.backwithborder_noerror);
        this.editPassword2.setTextColor(this.getResources().getColor(R.color.black));
        this.editPassword2.setHintTextColor(this.getResources().getColor(R.color.gris));
        this.errPassword2.setText("");
    }

    @SuppressLint("SetTextI18n")
    public void affichageErrorPassword1()
    {
        this.editPassword1.setBackgroundResource(R.drawable.backwithborder_error);
        this.editPassword1.setTextColor(this.getResources().getColor(R.color.rouge_fonce));
        this.editPassword1.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
        this.errPassword1.setText("Le mot de passe doit être compris entre 8 et 16 caractères et ne doit pas contenir ces caractères :  !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~");
    }

    @SuppressLint("SetTextI18n")
    public void affichageErrorPassword2()
    {
        this.editPassword2.setBackgroundResource(R.drawable.backwithborder_error);
        this.editPassword2.setTextColor(this.getResources().getColor(R.color.rouge_fonce));
        this.editPassword2.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
        this.errPassword2.setText("Les mots de passe ne sont pas identiques.");
    }

    public String getPassword1()
    {
        return editPassword1.getText().toString();
    }

    public String getPassword2()
    {
        return editPassword2.getText().toString();
    }

    public String getMail()
    {
        return getIntent().getStringExtra("USER_MAIL");
    }

    public void launchMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
