package com.deconstructors.krono.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Plan;
import com.deconstructors.krono.module.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends FirestoreRecyclerAdapter<User, NotificationAdapter.NotificationHolder>
        implements Filterable
{
    private NotificationClickListener ClickListener;
    private List<User> FilteredList;
    public boolean filtering = false;

    /************************************************************************
     * Purpose:         2 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public NotificationAdapter(@NonNull FirestoreRecyclerOptions<User> options,
                               NotificationClickListener clickListener)
    {
        super(options);
        this.ClickListener = clickListener;
        this.FilteredList = new ArrayList<>(getSnapshots());
    }

    @Override
    public void onDataChanged()
    {
        super.onDataChanged();
        if (!filtering)
            this.FilteredList = new ArrayList<>(getSnapshots());
    }

    @Override
    public int getItemCount()
    {
        onDataChanged();
        return FilteredList.size();
    }

    @NonNull
    @Override
    public User getItem(int position)
    {
        if (filtering)
        {
            return FilteredList.get(position);
        }
        else
        {
            return super.getItem(position);
        }
    }

    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                String pattern = constraint.toString().toLowerCase();
                if(pattern.isEmpty())
                {
                    FilteredList = new ArrayList<>(getSnapshots());
                    filtering = false;
                }
                else
                {
                    FilteredList = new ArrayList<>(getSnapshots());
                    List<User> filteredList = new ArrayList<>();
                    filtering = true;
                    for(User user: FilteredList)
                    {
                        if(user.getDisplayName().toLowerCase().contains(pattern))
                        {
                            filteredList.add(user);
                        }
                    }
                    FilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = FilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                FilteredList = (ArrayList<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    /************************************************************************
     * Purpose:         View Holder
     * Precondition:    .
     * Postcondition:   Inflate the layout to Recycler List View
     *                  Using the ui_drawer_listitem.XML File
     ************************************************************************/
    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        int res = R.layout.notif_listitem;
        View view = LayoutInflater.from(parent.getContext()).inflate(res, parent, false);

        return new NotificationHolder(view, this.ClickListener);
    }

    /************************************************************************
     * Purpose:         On Bind View Holder
     * Precondition:    .
     * Postcondition:   Assign Values from ViewHolder class to the Fields
     ************************************************************************/
    @Override
    protected void onBindViewHolder(@NonNull NotificationAdapter.NotificationHolder holder,
                                    int position,
                                    @NonNull User model)
    {
        holder.displayNameText.setText(FilteredList.get(position).getDisplayName());
        holder.emailText.setText(FilteredList.get(position).getEmail());
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   Archive the element from the single list item
     ************************************************************************/
    public class NotificationHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        LinearLayout profile;
        TextView displayNameText;
        TextView emailText;
        Button acceptButton;
        Button refuseButton;

        NotificationClickListener ClickListener;

        public NotificationHolder(@NonNull View itemView, NotificationClickListener clickListener)
        {
            super(itemView);
            this.profile = itemView.findViewById(R.id.notif_profile);
            this.displayNameText = itemView.findViewById(R.id.notif_Header);
            this.emailText = itemView.findViewById(R.id.notif_Description);
            this.acceptButton = itemView.findViewById(R.id.notif_listitem_accept);
            this.refuseButton = itemView.findViewById(R.id.notif_listitem_reject);
            this.ClickListener = clickListener;

            this.profile.setOnClickListener(this);
            this.acceptButton.setOnClickListener(this);
            this.refuseButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.notif_profile:
                {
                    this.ClickListener.onFriendSelected(getAdapterPosition());
                    break;
                }
                case R.id.notif_listitem_accept:
                {
                    this.ClickListener.onRequestAccepted(getAdapterPosition());
                    break;
                }
                case R.id.notif_listitem_reject:
                {
                    this.ClickListener.onRequestRefused(getAdapterPosition());
                    break;
                }
            }

        }
    }

    public interface NotificationClickListener
    {
        void onFriendSelected(final int position);
        void onRequestAccepted(final int position);
        void onRequestRefused(final int position);
    }
}