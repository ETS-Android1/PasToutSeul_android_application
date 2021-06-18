package com.example.test4;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Requete
{
    Context context;
    String URL_DOMAIN;
    RequestQueue queue;
    Toast toastError;

    public Requete(Context ctxt)
    {
        this.context = ctxt;
        this.URL_DOMAIN = "https://db-ezpfla.000webhostapp.com/";
        this.queue = Volley.newRequestQueue(context);
    }

    public interface VolleyCallBack{
        void onSuccess(String res) throws Exception;
    }

    /*
     * Procédure : Envoi d'une requête de type POST pour se connecter
     * */
    public void login(String mail,final VolleyCallBack callback)
    {
        // URL du serveur web
        String URL = URL_DOMAIN+"login.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("Réponse", response);
            // Appel d'une fonction à éxecuter si succès de la requête
            try
            {
                callback.onSuccess(response.trim()); // trim() pour enlever les espaces car la réponse contient des espaces.
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }, error ->
                Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show())
        {
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> logs = new HashMap<>();
                //Ajout des arguments
                logs.put("mail",mail);

                return logs;
            }
        };

        //Lance la requête
        queue.add(postRequest);
    }

    // Ajouter un pin dans notre base de données via une requête de type GET
    public void addPin(double latitude, double longitude, long id_user, String jour, String heure, int icone, String prenom, String comment, String envie, final VolleyCallBack callBack)
    {

        // Date yyyy-MM-dd HH-mm:ss
        String date = jour+" "+heure;

        // Si vide
        if(prenom.isEmpty())
        {
            prenom = "(Aucune informations)";
        }
        if(comment.isEmpty())
        {
            comment = "(Aucune informations)";
        }
        if(envie.isEmpty())
        {
            envie = "(Aucune informations)";
        }

        String URL = URL_DOMAIN+"addPin.php?id="+id_user+"&long="+longitude+"&lat="+latitude+"&date="+date+"&icone="+icone+"&prenom="+prenom+"&commentaire="+comment+"&envie="+envie;

        StringRequest getRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            try {
                callBack.onSuccess(null);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }, error -> Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show());

        // Ajoute la requête dans la file
        queue.add(getRequest);
    }

    // Déclaration et envoi d'une requête pour récupérer des données sur les marqueurs dans la BDD.
    public void getSDFMarkers(double latitude, double longitude, final VolleyCallBack callback)
    {
        String URL = URL_DOMAIN+"getPin.php?latitude="+latitude+"&longitude="+longitude;

        // Envoi de la requête et on redirige la réponse à sa réception
        StringRequest getRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            try {
                callback.onSuccess(response.trim());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }, error -> Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show());

        // Ajout de la requête dans la file
        queue.add(getRequest);
    }

    // Déclaration et envoi d'une requête pour récupérer des données sur les marqueurs dans la BDD.
    public void reportPin(String username,String id_pin, VolleyCallBack callBack)
    {
        String URL = URL_DOMAIN+"reportPin.php?username="+username+"&id_pin="+id_pin;

        // Envoi de la requête et on redirige la réponse à sa réception
        StringRequest postRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            try {
                callBack.onSuccess(response.trim());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }, error -> Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show());

        // Ajout de la requête dans la file
        queue.add(postRequest);
    }

    public void sdfInfos(int id_pin,final VolleyCallBack callBack)
    {
        String URL = URL_DOMAIN+"getSdfInfos.php?pin="+id_pin;

        // Envoi de la requête et on redirige la réponse à sa réception
        StringRequest getRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            try {
                callBack.onSuccess(response);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }, error -> Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show());

        // Ajout de la requête dans la file
        queue.add(getRequest);
    }

    public void disconnect(String username)
    {
        String URL = URL_DOMAIN+"disconnect.php?username="+username;

        // Envoi de la requête et on redirige la réponse à sa réception
        StringRequest getRequest = new StringRequest(Request.Method.GET, URL, response ->
                Log.i("Réponse", response),
                error -> Log.e("Réponse", error.toString()));

        // Ajout de la requête dans la file
        queue.add(getRequest);
    }

    // Changement du mot de passe
    public void changePassword(String mail,String code, VolleyCallBack callback)
    {
        // URL du serveur web
        String URL = URL_DOMAIN+"changePassword.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("Réponse", response);
            try {
                callback.onSuccess(response);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            // Appel d'une fonction à éxecuter si succès de la requête
        }, error -> Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> logs = new HashMap<>();
                //Ajout des arguments
                logs.put("email", mail);
                logs.put("code", code);
                return logs;
            }
        };

        queue.add(postRequest);
    }

    // Envoie d'un message
    public void sendMessage(String id_group, String id_user, String message,String date)
    {
        String URL = URL_DOMAIN+"sendMessage.php";

        // Types de requête
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response ->
                Log.i("Réponse", response),
                error -> Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show())
        {
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> logs = new HashMap<>();

                //Ajout des arguments
                logs.put("id_grp",id_group);
                logs.put("id_usr",id_user);
                logs.put("date",date);
                logs.put("msg",message);

                return logs;
            }
        };

        //Gérer les timeout
        postRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Ajout de la requête dans la file d'attente
        queue.add(postRequest);
    }

    // Récupération de tous les messages dans la base de données
    public void getMessages(String id_group, final VolleyCallBack callback)
    {
        String URL = URL_DOMAIN+"getMessages.php?id_grp="+id_group;

        StringRequest getRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            try {
                callback.onSuccess(response);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }, error -> Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show());

        queue.add(getRequest);
    }

    // Récupère les nouveaux messages côté serveur
    public void getNewMessages(String id_group, String id_user, String timeLastUpdate, final VolleyCallBack callback)
    {
        String URL = URL_DOMAIN+"getNewMessages.php?id_grp="+id_group+"&id_usr="+id_user+"&time="+timeLastUpdate;

        StringRequest getRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            try {
                callback.onSuccess(response);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }, error -> Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show());

        queue.add(getRequest);
    }

    // Récupère les participants d'une conversation
    public void getParticipants(String id_groupe, final VolleyCallBack callBack)
    {
        String URL = URL_DOMAIN+"getParticipants.php?id_groupe="+id_groupe;

        StringRequest getRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            try {
                callBack.onSuccess(response);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }, error -> Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show());

        queue.add(getRequest);
    }

    // Ajout d'une personne dans une conversation
    public void addPerson(String name, String id_grp, final VolleyCallBack callback)
    {
        String URL = URL_DOMAIN+"addPeople.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("Réponse", response);
            try {
                callback.onSuccess(response);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }, error -> Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show()){
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> logs = new HashMap<>();

                //Ajout des arguments
                logs.put("nom_user",name);
                logs.put("id_groupe",id_grp);

                return logs;
            }
        };

        //Gérer les timeout
        postRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Ajout de la requête dans la file d'attente
        queue.add(postRequest);
    }

    // Quitter une conversation
    public void leave(String id_user, String id_group, final VolleyCallBack callBack)
    {
        String URL = URL_DOMAIN+"leaveConv.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("Réponse", response);
            try {
                callBack.onSuccess(response);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }, error -> Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show()){
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> logs = new HashMap<>();

                //Ajout des arguments
                logs.put("id_user",id_user);
                logs.put("id_group",id_group);

                return logs;
            }
        };

        //Gérer les timeout
        postRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Ajout de la requête dans la file d'attente
        queue.add(postRequest);
    }

    // Vérification du code à 6 digits envoyé par mail
    public void getCode(String mail, VolleyCallBack callback)
    {
        // URL du serveur web
        String URL = URL_DOMAIN+"checkCode.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("Réponse", response);
            try {
                callback.onSuccess(response);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            // Appel d'une fonction à éxecuter si succès de la requête
        }, error -> Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> logs = new HashMap<>();
                //Ajout des arguments
                logs.put("email", mail);
                return logs;
            }
        };

        queue.add(postRequest);
    }

    // Requête pour récupérer toutes les conversations de l'utilisateur
    public void getConversation(String id_user, final VolleyCallBack callback)
    {
        String URL = URL_DOMAIN+"afficheConv.php?id_user="+id_user;

        StringRequest postRequest = new StringRequest(Request.Method.GET, URL, response ->
        {
            Log.i("Réponse", response);
            try {
                callback.onSuccess(response);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }, error -> Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show());

        // Ajoute la requête dans la file
        queue.add(postRequest);
    }

    // Requête permettant de créer un groupe de conversation
    public void newConversation(String id_user, String nom_groupe, String date, final VolleyCallBack callBack)
    {
        // Initisalisation de la reqûete
        RequestQueue queue = Volley.newRequestQueue(context);

        String URL = URL_DOMAIN+"createConv.php?id_user="+id_user+"&nom_groupe="+nom_groupe+"&date="+date;

        StringRequest postRequest = new StringRequest(Request.Method.GET, URL, response -> {
            Log.i("Réponse", response);
            try {
                callBack.onSuccess(response);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }, error -> Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show());

        // Ajoute la requête dans la file
        queue.add(postRequest);
    }

    public void forgotPassword(String mail, VolleyCallBack callback)
    {
        // Récupère une date
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        String string_date = dateFormat.format(date);

        // Génération d'un code au hasard à envoyer sur le mail de l'utilisateur
        int code = new Random().nextInt(999999-100000) + 100000;
        String string_code = String.valueOf(code);

        // URL du serveur web
        String URL = URL_DOMAIN+"forgotPassword.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("Réponse", response);
            try {
                callback.onSuccess(response.trim());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            // Appel d'une fonction à éxecuter si succès de la requête
        }, error -> Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show()) {
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

    public void createAccount(String username, String mail, String password, final VolleyCallBack callback)
    {
        RequestQueue queue = Volley.newRequestQueue(context);
        String URL = URL_DOMAIN+"createAccount.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response ->
        {
            Log.i("Réponse", response);
            try
            {
                callback.onSuccess(response.trim()); // trim() pour enlever les espaces car la réponse contient des espaces.
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }, error -> Toast.makeText(context,"Veuillez verifier votre connexion internet.",Toast.LENGTH_SHORT).show())
        {
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> logs = new HashMap<>();

                logs.put("username",username);
                logs.put("mail",mail);
                logs.put("password",password);

                return logs;
            }
        };

        queue.add(postRequest);
    }

}
