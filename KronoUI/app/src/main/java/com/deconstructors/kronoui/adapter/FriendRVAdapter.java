package com.deconstructors.kronoui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.kronoui.R;
import com.deconstructors.kronoui.module.User;

import java.util.List;

public class FriendRVAdapter extends RecyclerView.Adapter<FriendRVAdapter.ViewHolder>
{
    public List<User> FriendList;
    private FriendRVClickListener ClickListener;

    /************************************************************************
     * Purpose:         2 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public FriendRVAdapter(List<User> friendList, FriendRVClickListener clickListener)
    {
        this.FriendList = friendList;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        int res = R.layout.friend_listitem;
        View view = LayoutInflater.from(parent.getContext()).inflate(res, parent, false);
        ViewHolder viewholder = new ViewHolder(view, this.ClickListener);
        return viewholder;
    }

    /************************************************************************
     * Purpose:         On Bind View Holder
     * Precondition:    .
     * Postcondition:   Assign Values from ViewHolder class to the Fields
     ************************************************************************/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.displayNameText.setText(this.FriendList.get(position).getDisplayName());
        holder.emailText.setText(this.FriendList.get(position).getEmail());
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public int getItemCount()
    {
        return this.FriendList == null ? 0 : this.FriendList.size();
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   Archive the element from the single list item
     ************************************************************************/
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView displayNameText;
        TextView emailText;
        FriendRVClickListener ClickListener;

        public ViewHolder(@NonNull View itemView, FriendRVClickListener clickListener)
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

    public interface FriendRVClickListener
    {
        void onFriendSelected(int position);
    }
}