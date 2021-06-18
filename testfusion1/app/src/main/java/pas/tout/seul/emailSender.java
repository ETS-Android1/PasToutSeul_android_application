package pas.tout.seul;


import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class emailSender extends javax.mail.Authenticator{

    Context context;
    private final String mailSender;
    private final String password;
    private final Session session;

    // Initialisation des propriétes pour l'envoi d'un mail
    public emailSender(Context ctxt) {
        Properties properties=new Properties();

        // Mail que nous avons créer uniquement pour envoyer les mots de passes oubliées.
        this.mailSender = "pastoutseul19@gmail.com";
        this.password = "NbthD3y&H{Uhwga7";
        this.context = ctxt;

        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.port","587");

        this.session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailSender,password);
            }
        });
    }

    // Envoi d'un mail avec le titre, son contenu et le destinataire.
    public synchronized void sendMail(String subject, String content, String sender, String filename) {
        try
        {
            // Récupère le fichier html sous forme de string
            String html = fileToString(filename,content);

            // Nouveau message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailSender));
            message.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(sender)});
            message.setSubject(subject);

            // Conversion de string en html
            message.setContent(html,"text/html; charset=utf-8");

            // Envoi du message dans un thread pour éviter de bloquer l'application
            Thread thread = new Thread(() -> {
                try {
                    // Envoi d'un message
                    Transport.send(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });

            // Lance le thread
            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String fileToString(String filename, String content) throws IOException
    {
        // Récupère les assets
        AssetManager am = context.getAssets();

        // Fichier concerné
        InputStream is = am.open(filename);

        int size = is.available();
        byte[] buff = new byte[size];

        is.read(buff);

        is.close();

        // Ajoute le contenu d'une variable dans le html
        return new String(buff).replace("$content", content);
    }


}
