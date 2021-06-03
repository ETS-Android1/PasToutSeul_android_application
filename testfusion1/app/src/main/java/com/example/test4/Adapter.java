package com.example.test4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    Boolean isChat;

    String[] name,mess,heure;

    Context context;

    ArrayList<String> nom;
    ArrayList<String> message;
    ArrayList<String> temps;

    public Adapter(Context ctxt, String[] nom, String[] message)
    {
        this.name = nom;
        this.mess = message;
        this.context = ctxt;
        this.isChat = false;
    }

    public Adapter(Context ctxt, ArrayList<String> nom, ArrayList<String> message, ArrayList<String> time)
    {
        this.nom = nom;
        this.message = message;
        this.context = ctxt;
        this.temps = time;
        this.isChat = true;
    }

    @SuppressLint("ResourceType")
    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(this.context);
        if(!isChat)
        {
            view = inflater.inflate(R.layout.row_conversation, parent,false);
        }
        else
        {
            view = inflater.inflate(R.layout.message, parent,false);
        }

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        if(!isChat)
        {
            holder.titre.setText(name[position]);
            holder.lastMessage.setText("Dernier message : "+mess[position]);
        }
        else
        {
            String[] date_heure = temps.get(position).split(" ");
            String date = yyyy_mm_ddTodd_mm_yyyy(date_heure[0]);
            String[] time = date_heure[1].split(":");
            holder.messageChat.setText("["+date+" "+time[0]+":"+time[1]+"] "+nom.get(position)+" : "+ message.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if(!isChat){return name.length;}
        else{return nom.size();}

    }

    public void addItem(String name, String messageContent, String time)
    {
        this.nom.add(name);
        this.message.add(messageContent);
        this.temps.add(time);

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titre, lastMessage;
        TextView nom, messageChat, heure;
        public ViewHolder(View view)
        {
            super(view);

            if(!isChat)
            {
                titre = itemView.findViewById(R.id.textViewTitleConversation);
                lastMessage = itemView.findViewById(R.id.textViewLastMessage);
            }
            else
                {
                messageChat = itemView.findViewById(R.id.textViewMessage);
            }

        }
    }

    // Converti une date yyyy-dd-mm en dd-mm-yyyy
    public String yyyy_mm_ddTodd_mm_yyyy(String date)
    {
        String []string = date.split("-");

        return string[2]+"/"+string[1]+"/"+string[0];
    }
}
