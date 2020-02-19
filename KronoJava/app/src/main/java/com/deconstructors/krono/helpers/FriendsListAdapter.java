package com.deconstructors.krono.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.activities.friends.Friend;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder>
{
    List<Friend> _friendsList;

    public FriendsListAdapter(List<Friend> friends)
    {
        _friendsList = friends;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu3_friendlist_item, parent, false);
        return new FriendsListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.firstName.setText(_friendsList.get(position).GetFirstName());
        holder.lastName.setText(_friendsList.get(position).GetLastName());
    }

    @Override
    public int getItemCount() {
        return _friendsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        View layout;

        TextView firstName;
        TextView lastName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            firstName = (TextView) layout.findViewById(R.id.friendlist_item_firstname);
            lastName = (TextView) layout.findViewById(R.id.friendlist_item_lastname);
        }
    }
}
