package com.example.test4;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.listners.AlLoginHandler;
import com.applozic.mobicomkit.listners.AlLogoutHandler;

public class Chat extends AppCompatActivity {

    String email;
    String id_user;
    String username;
    String password;

    public Chat(Utilisateur utilisateur)
    {
        this.id_user = utilisateur.id_user;
        this.email = utilisateur.email;
        this.username = utilisateur.username;
        this.email = utilisateur.email;
        this.password = utilisateur.password;
    }



    public void loginChat(Context context)//,String id_user, String username, String email, String password)
    {
        User user = new User();
        user.setUserId(this.id_user);
        user.setDisplayName(this.username);
        user.setEmail(this.email);
        user.setAuthenticationTypeId(User.AuthenticationType.APPLOZIC.getValue());
        user.setPassword(this.password);

        Applozic.connectUser(context, user, new AlLoginHandler() {
            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                Toast.makeText(context,"Erreur lors de la connexion au chat. Veuillez ressayer.",Toast.LENGTH_LONG).show();
                exception.printStackTrace();
            }
        });
    }

    public void logout(Context context)
    {
        Applozic.logoutUser(context, new AlLogoutHandler() {
            @Override
            public void onSuccess(Context context) {
                System.out.println("Déconnexion réussi");
            }

            @Override
            public void onFailure(Exception exception) {
                System.out.println("Erreur déconnexion");
            }
        });
    }

}