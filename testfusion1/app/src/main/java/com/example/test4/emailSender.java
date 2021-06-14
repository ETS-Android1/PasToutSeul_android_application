package com.example.test4;


import android.content.pm.PackageInstaller;
import android.service.textservice.SpellCheckerService;

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

    private String mailSender;
    private String password;
    private Session session;

    public emailSender()
    {
        Properties properties=new Properties();

        // Mail que nous avons créer uniquement pour envoyer les mots de passes oubliées.
        this.mailSender = "pastoutseul19@gmail.com";
        this.password = "NbthD3y&H{Uhwga7";

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

    public synchronized void sendMail(String subject, String body, String sender) throws Exception {

        try
        {

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailSender));
            message.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(sender)});
            message.setSubject(subject);
            message.setText(body);

            Thread thread = new Thread(() -> {
                try {
                    Transport.send(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });

            thread.start();
        }
        catch (Exception e)
        {

            e.printStackTrace();
        }
    }
}
