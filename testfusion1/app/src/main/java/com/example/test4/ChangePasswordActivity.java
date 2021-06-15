package com.example.test4;


import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity
{
    EditText editPassword1,editPassword2;
    TextView errPassword1, errPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_password);

        this.editPassword1 = findViewById(R.id.editTextChangePassword1);
        this.editPassword2 = findViewById(R.id.editTextChangePassword2);

        this.errPassword1 = findViewById(R.id.txtVErrPass1);
        this.errPassword2 = findViewById(R.id.txtVErrPass2);

        // Cache le clavier lorsqu'on clique en dehors des champs de saisie
        hideKeyboardOnClick();
    }

    public void changePassword(View view)
    {
        String password1 = getPassword1();
        String password2 = getPassword2();

        if(isValid(getPassword1()))
        {
            System.out.println("VALIDE");
            if(isSame(password1,password2))
            {
                System.out.println("MOTS DE PASSES IDENTIQUES");
            }
            else
            {
                System.out.println("MOTS DE PASSES NON IDENTIQUES");
                affichageErrorPassword2();
            }
        }
        else
        {
            affichageErrorPassword1();
            System.out.println("NON VALIDE");
        }
    }

    // Détection d'un mot de passe valide
    public boolean isValid(String password)
    {
        int len = password.length();

        if(len < 8 || len > 16)
        {
            return false;
        }

        char [] banArray = {'!','"','#','$','%','&','\'', '(',')','*','+',',','-','.','/',':',';','<','=','>','?','@','[','\\',']','^','_','`','{','|','}','~'};

        for(int i = 0; i < banArray.length; i++)
        {
            if(password.contains(Character.toString(banArray[i])))
            {
                return false;
            }
        }

        return true;
    }

    // Détection des mots de passes identiques
    public boolean isSame(String password1, String password2)
    {
        if(!password1.equals(password2))
        {
            return false;
        }
        return true;
    }

    // Cache le clavier lorsqu'on clique en dehors des champs de saisie
    public void hideKeyboardOnClick()
    {
        findViewById(R.id.layoutAcitivtyChangePassword).setOnTouchListener((view, event) -> {
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

    public void affichageErrorPassword1()
    {
        this.editPassword1.setBackgroundResource(R.drawable.backwithborder_error);
        this.editPassword1.setTextColor(this.getResources().getColor(R.color.rouge_fonce));
        this.editPassword1.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
        this.errPassword1.setText("Le mot de passe ne doit pas contenir ces caractères :  !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~");
    }

    public void affichageErrorPassword2()
    {
        this.editPassword1.setBackgroundResource(R.drawable.backwithborder_error);
        this.editPassword1.setTextColor(this.getResources().getColor(R.color.rouge_fonce));
        this.editPassword1.setHintTextColor(this.getResources().getColor(R.color.rouge_clair));
        this.errPassword1.setText("Les mots de passe ne sont pas identiques.");
    }

    public String getPassword1()
    {
        return editPassword1.getText().toString();
    }

    public String getPassword2()
    {
        return editPassword2.getText().toString();
    }
}
