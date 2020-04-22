package com.deconstructors.krono.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class FriendAdapter_Selectable extends FirestoreRecyclerAdapter<User, FriendAdapter_Selectable.FriendHolder>
{
    private FriendAdapter_Selectable.FriendClickListener ClickListener;

    /************************************************************************
     * Purpose:         2 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public FriendAdapter_Selectable(@NonNull FirestoreRecyclerOptions<User> options,
                                    FriendAdapter_Selectable.FriendClickListener clickListener)
    {
        super(options);
        this.ClickListener = clickListener;
    }

    /************************************************************************
     * Purpose:         View Holder
     * Precondition:    .
     * Postcondition:   Inflate the layout to Recycler List View
     *                  Using the ui_drawer_listitem.XML File
     ************************************************************************/
    @NonNull
    @Override
    public FriendAdapter_Selectable.FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        int res = R.layout.friend_listitem_selectable;
        View view = LayoutInflater.from(parent.getContext()).inflate(res, parent, false);

        return new FriendAdapter_Selectable.FriendHolder(view, this.ClickListener);
    }

    /************************************************************************
     * Purpose:         On Bind View Holder
     * Precondition:    .
     * Postcondition:   Assign Values from ViewHolder class to the Fields
     ************************************************************************/
    @Override
    protected void onBindViewHolder(@NonNull FriendAdapter_Selectable.FriendHolder holder,
                                    int position,
                                    @NonNull User model)
    {
        holder.displayNameText.setText(model.getDisplayName());
        holder.emailText.setText(model.getEmail());
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   Archive the element from the single list item
     ************************************************************************/
    public class FriendHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView displayNameText;
        TextView emailText;
        FriendAdapter_Selectable.FriendClickListener ClickListener;

        public FriendHolder(@NonNull View itemView, FriendAdapter_Selectable.FriendClickListener clickListener)
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
            this.ClickListener.onFriendSelected(getAdapterPosition());
        }
    }

    public interface FriendClickListener
    {
        void onFriendSelected(int position);
    }
}
