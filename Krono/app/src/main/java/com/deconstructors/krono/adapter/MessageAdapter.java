package com.deconstructors.krono.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Message;
import com.deconstructors.krono.module.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.MessageHolder>
{
    private FirebaseAuth Auth;
    private User friend;

    /************************************************************************
     * Purpose:         2 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    //public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options, List<Message> messages, User friend)
    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options, User friend)
    {
        super(options);
        this.friend = friend;
        this.Auth = FirebaseAuth.getInstance();
    }

    /************************************************************************
     * Purpose:         View Holder
     * Precondition:    .
     * Postcondition:   Inflate the layout to Recycler List View
     *                  Using the ui_drawer_listitem.XML File
     ************************************************************************/

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_listitem, parent, false);
        return new MessageHolder(view);
    }

    /************************************************************************
     * Purpose:         On Bind View Holder
     * Precondition:    .
     * Postcondition:   Assign Values from ViewHolder class to the Fields
     ************************************************************************/
    @Override
    protected void onBindViewHolder(@NonNull MessageHolder holder,
                                    int position,
                                    @NonNull Message model)
    {
        holder.setModel(model);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   Archive the element from the single list item
     ************************************************************************/
    public class MessageHolder extends RecyclerView.ViewHolder
    {
        Message userMessage;
        LinearLayout self_container;
        TextView self_message;
        TextView self_time;

        LinearLayout container;
        TextView message;
        TextView time;
        View view;

        public MessageHolder(@NonNull View itemView)
        {
            super(itemView);

            this.view = itemView;

            container = view.findViewById(R.id.chatlist_container);
            message = view.findViewById(R.id.chatlist_messageText);
            time = view.findViewById(R.id.chatlist_time);

            self_container = view.findViewById(R.id.chatlist_self_container);
            self_message = view.findViewById(R.id.chatlist_self_messageText);
            self_time = view.findViewById(R.id.chatlist_self_time);
        }

        public void setModel(Message model)
        {
            Calendar calendar = Calendar.getInstance();
            Calendar todayCal = Calendar.getInstance();
            SimpleDateFormat full = new SimpleDateFormat("MM/dd/yyyy h:mm a");
            SimpleDateFormat trim = new SimpleDateFormat("h:mm a");

            try { calendar.setTime(full.parse(model.getTime().toString())); }
            catch (ParseException e) { }
            String formattedDate = "";

            userMessage = model;

            if (todayCal.get(Calendar.DATE) == calendar.get(Calendar.DATE))
            {
                formattedDate = trim.format(calendar.getTime());
            }
            else
            {
                formattedDate = calendar.getTime().toString();
            }

            if (model.getSender().compareTo(Auth.getCurrentUser().getUid()) == 0)
            {
                self_container.setVisibility(View.VISIBLE);
                container.setVisibility(View.GONE);
                self_message.setText(model.getText());
                self_time.setText(formattedDate);
            }
            else
            {
                self_container.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
                message.setText(model.getText());
                time.setText(formattedDate);
            }
        }
    }
}