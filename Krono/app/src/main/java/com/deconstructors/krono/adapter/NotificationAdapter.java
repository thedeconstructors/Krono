package com.deconstructors.krono.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Notification;
import com.deconstructors.krono.module.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder>
{
    //Can add more notification types here later if/when we expand notifications
    private final int FRIEND_REQUEST = 0;

    //Database
    private FirebaseFirestore DBInstance;

    List<String> names;

    //Data Members
    private String HeaderText;
    private String DescriptionText;

    /*public NotificationAdapter(@NonNull FirestoreRecyclerOptions<User> options)
    {
        this.friendRequestNames = Names;
        super(options);
        this.DBInstance = FirebaseFirestore.getInstance();
        this.HeaderText = "Friend Request";
        this.DescriptionText = "You have a new friend request from ";
    }*/

    public NotificationAdapter(List<String> _names)
    {
        names = _names;
        this.HeaderText = "Friend Request";
        this.DescriptionText = "You have a new friend request from ";
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        int res = R.layout.notif_listitem;
        View view = LayoutInflater.from(parent.getContext()).inflate(res, parent, false);

        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        String CurrentDescriptionText = DescriptionText + names.get(position);

        holder.HeaderTextView.setText(HeaderText);
        holder.DescriptionTextView.setText(CurrentDescriptionText);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class NotificationHolder extends RecyclerView.ViewHolder
    {
        TextView HeaderTextView, DescriptionTextView;

        public NotificationHolder(View itemView)
        {
            super(itemView);

            HeaderTextView = (TextView) itemView.findViewById(R.id.notif_Header);
            DescriptionTextView = (TextView) itemView.findViewById(R.id.notif_Description);
        }
    }
}
