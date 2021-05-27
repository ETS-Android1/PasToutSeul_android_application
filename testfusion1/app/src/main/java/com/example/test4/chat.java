package com.example.test4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.listners.AlLoginHandler;
import com.applozic.mobicomkit.uiwidgets.async.AlGroupInformationAsyncTask;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.task.AlTask;

import java.util.Calendar;


public class chat extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        loginChat(getUserIDToString(),getUsername(),getMail(), getUserPassword());


        if(Applozic.isConnected(this)){
            Intent intent = new Intent(this, ConversationActivity.class);
            intent.putExtra(ConversationUIService.USER_ID, getUserIDToString());
            intent.putExtra(ConversationUIService.DISPLAY_NAME, getUsername()); //put it for displaying the title.
            intent.putExtra(ConversationUIService.TAKE_ORDER,true); //Skip chat list for showing on back press

        }

    }

    public void loginChat(String id_user, String username, String email, String password)
    {
        User user = new User();
        user.setUserId(id_user);
        user.setDisplayName(username);
        user.setEmail(email);
        user.setAuthenticationTypeId(User.AuthenticationType.APPLOZIC.getValue());
        user.setPassword(password);

        Applozic.connectUser(this, user, new AlLoginHandler() {
            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                System.out.println("Ca marche");
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                System.out.println("Ca marche pas");
                exception.printStackTrace();
            }
        });
    }

    // Récupère le nom d'utilisateur
    public String getUsername()
    {
        return getIntent().getStringExtra("USER_NAME");
    }

    // Récupère l'id de l'utilisateur
    public String getUserIDToString()
    {
        return getIntent().getStringExtra("USER_ID");
    }

    public String getUserPassword()
    {
        return getIntent().getStringExtra("USER_PASSWORD");
    }

    public String getMail(){ return getIntent().getStringExtra("USER_MAIL"); }

}