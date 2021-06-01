package com.example.test4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    Boolean isChat;

    String[] name,mess,heure;
    Context context;

    public Adapter(Context ctxt, String[] nom, String[] message)
    {
        this.name = nom;
        this.mess = message;
        this.context = ctxt;
        this.isChat = false;
    }


    public Adapter(Context ctxt, String[] nom, String[] message, String []time)
    {
        this.name = nom;
        this.mess = message;
        this.context = ctxt;
        this.heure = time;
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
        else{
            holder.nom.setText(name[position]);
            holder.messageChat.setText(mess[position]);
            holder.heure.setText(heure[position]);
        }

    }

    @Override
    public int getItemCount() {
        return name.length;
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
                nom = itemView.findViewById(R.id.textViewExpediteur);
                messageChat = itemView.findViewById(R.id.textViewMessage);
            }

        }
    }


}
