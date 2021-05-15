package com.example.test4;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import java.util.regex.*;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.PasswordTransformationMethod;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    boolean hide;
    EditText pwd;
    EditText mail;
    TextView errMail;
    TextView errPwd;
    ImageButton btnShowHide;

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
                errMail.setText("");
                errPwd.setText("");
                Toast.makeText(getApplicationContext(), "Pas d'erreur(s)\nMail = "+getMail()+"\n Mot de passe = "+getPwd(), Toast.LENGTH_SHORT).show();
                break;
        }
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
        Intent intent = new Intent(this,map.class);
        startActivity(intent);
    }


}