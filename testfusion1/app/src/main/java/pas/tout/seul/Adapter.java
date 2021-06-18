package pas.tout.seul;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
{
    Boolean isChat, isConversation, isParticipantPopup;

    Context context;

    ArrayList<String> nom, message, temps;

    public Adapter(Context ctxt, ArrayList<String> nom, ArrayList<String> message, ArrayList<String> time, String source)
    {
        this.nom = nom;
        this.message = message;
        this.context = ctxt;
        this.temps = time;

        // Source de l'adapter : ChatActivity, ConversationActivity ou la fenêtre popup (popup affichant la liste des participants dans une conversation)
        switch (source)
        {
            case "chat" :
                this.isChat = true;
                this.isConversation = false;
                this.isParticipantPopup = false;
                break;
            case "conversation" :
                this.isChat = false;
                this.isConversation = true;
                this.isParticipantPopup = false;
                break;
            default:
                this.isChat = false;
                this.isConversation = false;
                this.isParticipantPopup = true;
                break;
        }
    }

    // Récupération des éléments contenu dans chaque items dans la recycler view
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView titre, lastMessage, messageChat, nomParticipant;
        @SuppressLint("CutPasteId")
        public ViewHolder(View view)
        {
            super(view);
            if(isConversation)
            {
                titre = itemView.findViewById(R.id.textViewTitleConversation);
                lastMessage = itemView.findViewById(R.id.textViewLastMessage);
            }
            if(isChat)
            {
                messageChat = itemView.findViewById(R.id.textViewMessage);
            }
            if(isParticipantPopup)
            {
                nomParticipant = itemView.findViewById(R.id.textViewMessage);
            }
        }
    }

    // Récupération des layouts des items dans la recyclerView
    @Override
    public @NotNull ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = null;

        LayoutInflater inflater = LayoutInflater.from(this.context);
        if(isConversation)
        {
            view = inflater.inflate(R.layout.row_conversation, parent,false);
        }
        if(isChat)
        {
            view = inflater.inflate(R.layout.message, parent,false);
        }
        if(isParticipantPopup)
        {
            view = inflater.inflate(R.layout.participants, parent,false);
        }

        return new ViewHolder(view);
    }


    // Assignation des valeurs dans chaque éléments des items dans la recyclerView
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final @NotNull ViewHolder holder, int position)
    {
        if(isConversation)
        {
            holder.titre.setText(nom.get(position));

            // Infos sur le dernier message
            String[] date_heure = temps.get(position).split(" ");
            String date = yyyy_mm_ddTodd_mm_yyyy(date_heure[0]);
            String[] h_m_s = date_heure[1].split(":");
            String printedMessage = message.get(position);

            // Si le message est trop long, on affiche partiellement le message
            if(printedMessage.length() >= 80)
            {
                printedMessage = printedMessage.substring(0,79)+" ...";
            }

            holder.lastMessage.setText("["+date+" "+h_m_s[0]+":"+h_m_s[1]+"]  "+"Dernier message : "+printedMessage);

        }
        if(isChat)
        {
            // Infos sur un message
            String[] date_heure = temps.get(position).split(" ");
            String date_formatFR = yyyy_mm_ddTodd_mm_yyyy(date_heure[0]);
            String[] time = date_heure[1].split(":");

            holder.messageChat.setText("["+date_formatFR+" "+time[0]+":"+time[1]+"] "+nom.get(position)+" : "+ message.get(position));

        }
        if(isParticipantPopup)
        {
            holder.nomParticipant.setText(nom.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return nom.size();
    }

    // Ajout d'un item dans la recyclerView + mise à jour direct de la recycler view
    public void addItem(String name, String messageContent, String time)
    {
        this.nom.add(name);
        this.message.add(messageContent);
        this.temps.add(time);

        notifyDataSetChanged();
    }

    public void update()
    {
        notifyDataSetChanged();
    }

    // Converti une date yyyy-dd-mm en dd-mm-yyyy
    public String yyyy_mm_ddTodd_mm_yyyy(String d)
    {
        String []string = d.split("-");

        return string[2]+"/"+string[1]+"/"+string[0];
    }

}
