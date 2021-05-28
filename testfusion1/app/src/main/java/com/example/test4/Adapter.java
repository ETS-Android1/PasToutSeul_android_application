package com.example.test4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    String[] name;
    String[] mess;
    Context context;

    public Adapter(Context ctxt, String[] string1, String[] string2)
    {
        this.name = string1;
        this.mess = string2;
        this.context = ctxt;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.row_conversation, parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull Adapter.ViewHolder holder, int position) {
        holder.titre.setText(name[position]);
        holder.message.setText("Dernier message : "+mess[position]);
    }

    @Override
    public int getItemCount() {
        return name.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titre, message;
        public ViewHolder(@NonNull View view)
        {
            super(view);
            titre = itemView.findViewById(R.id.textViewTitleConversation);
            message = itemView.findViewById(R.id.textViewLastMessage);
        }
    }
}
