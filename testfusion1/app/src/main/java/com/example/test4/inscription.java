package com.example.test4;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class inscription extends AppCompatActivity {

    EditText mail;
    EditText pwd;
    EditText pwd2;
    TextView errMail;
    TextView errPwd1;
    TextView errPwd2;

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
        this.errMail = findViewById(R.id.txtErrMailRegister);
        this.errPwd1 = findViewById(R.id.txtErrPwd1);
        this.errPwd2 = findViewById(R.id.txtErrPwd2);
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
    public String getPwd2()
    {
        return pwd2.getText().toString();
    }

    public int checkErrorMail()
    {
        String email = getMail();

        //Détection du bon format d'un mail : https://www.javatpoint.com/java-email-validation
        //Pattern d'un mail "@ ... "
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if(!matcher.matches())
        {
            return 1;
        }
        else
        {
            return  0;
        }

    }

    public int checkErrorPassword()
    {
        String password1 = getPwd();
        String password2 = getPwd2();

        if(password1.equals(""))
        {
            return 1;
        }
        if(!password2.equals(password1))
        {
            return 2;
        }
        return 0;
    }

    public void createAccount(View view)
    {
        int errEmail = checkErrorMail();
        int errPwd = checkErrorPassword();

        switch (errEmail){
            case 1 :
                errMail.setText("Veuillez saisir un email valide");
                break;
            default :
                errMail.setText("");
                break;
        }

        switch (errPwd)
        {
            case 1 :
                errPwd1.setText("Veuillez saisir un mot de passe valide");
                errPwd2.setText("");
                break;
            case 2 :
                errPwd1.setText("");
                errPwd2.setText("Mots de passe non identiques");
                break;
            default:
                errPwd2.setText("");
                errPwd1.setText("");
                break;
        }

        if (errEmail == 0 && errPwd == 0)
        {
            Toast.makeText(getApplicationContext(), "Compte crée", Toast.LENGTH_SHORT).show();
        }

    }
}
