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

        System.out.println(getUserIDToString());
        if(Applozic.isConnected(this)){
            System.out.println("Youpi");
            Intent intent = new Intent(this, ConversationActivity.class);
            startActivity(intent);
            /*
            AlGroupInformationAsyncTask.GroupMemberListener taskListener = new AlGroupInformationAsyncTask.GroupMemberListener() {
                @Override
                public void onSuccess(Channel channel, Context context) {
                    Intent chatIntent = new Intent(context, ConversationActivity.class);
                    chatIntent.putExtra(ConversationUIService.GROUP_ID, channel.getKey());
                    chatIntent.putExtra(ConversationUIService.GROUP_NAME, channel.getName());
                    chatIntent.putExtra(ConversationUIService.TAKE_ORDER, true);
                    context.startActivity(chatIntent);
                }

                @Override
                public void onFailure(Channel channel, Exception e, Context context) {
                    e.printStackTrace();
                }
            };
            AlGroupInformationAsyncTask groupInfoTask = new AlGroupInformationAsyncTask( this, 2 , taskListener);
            AlTask.execute(groupInfoTask);

             */
        }
        else{
            loginChat(getUserIDToString(),getUsername(),"jeremy.ros99@gmail.com", getUserPasswordToString());
            Intent intent = new Intent(this, ConversationActivity.class);
            intent.putExtra(ConversationUIService.USER_ID, getUserIDToString());
            intent.putExtra(ConversationUIService.DISPLAY_NAME, getUsername()); //put it for displaying the title.
            intent.putExtra(ConversationUIService.TAKE_ORDER,true); //Skip chat list for showing on back press
            startActivity(intent);
        }
    }

    public void loginChat(String id_user, String username, String email, String password)
    {
        User user = new User();
        user.setUserId(id_user); //userId it can be any unique user identifier NOTE : +,*,? are not allowed chars in userId.
        user.setDisplayName(username); //displayName is the name of the user which will be shown in chat messages
        user.setEmail(email); //optional
        user.setAuthenticationTypeId(User.AuthenticationType.APPLOZIC.getValue());  //User.AuthenticationType.APPLOZIC.getValue() for password verification from Applozic server and User.AuthenticationType.CLIENT.getValue() for access Token verification from your server set access token as password
        user.setPassword(password); //optional, leave it blank for testing purpose, read this if you want to add additional security by verifying password from your server https://www.applozic.com/docs/configuration.html#access-token-url

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

    public String getUserPasswordToString()
    {
        return getIntent().getStringExtra("USER_PASSWORD");
    }
}