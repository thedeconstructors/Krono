package com.deconstructors.krono.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Message;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.MessageHolder>
{
    private MessageClickListener ClickListener;

    /************************************************************************
     * Purpose:         2 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options)
    {
        super(options);
        //this.ClickListener = clickListener;
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
        int res = R.layout.chat_listitem;
        View view = LayoutInflater.from(parent.getContext()).inflate(res, parent, false);

        return new MessageHolder(view, this.ClickListener);
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
        holder.displayNameText.setText(model.GetSender());
        holder.emailText.setText(model.GetSender());
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   Archive the element from the single list item
     ************************************************************************/
    public class MessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView displayNameText;
        TextView emailText;
        MessageClickListener ClickListener;

        public MessageHolder(@NonNull View itemView, MessageClickListener clickListener)
        {
            super(itemView);

            this.displayNameText = (TextView) itemView.findViewById(R.id.friendlist_nameText);
            this.emailText = (TextView) itemView.findViewById(R.id.friendlist_emailText);
            this.ClickListener = clickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            this.ClickListener.onMessageSelected(getAdapterPosition());
        }
    }

    public interface MessageClickListener
    {
        void onMessageSelected(int position);
    }
}
