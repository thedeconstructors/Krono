package com.deconstructors.krono.activities;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.ChatMessage;

public class FriendsRAdapter extends RecyclerView.Adapter<FriendsRAdapter>
{
    private ChatMessage[] m_data;

    @NonNull
    @Override
    public FriendsRAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsRAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}