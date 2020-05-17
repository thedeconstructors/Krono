package com.deconstructors.krono.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter_Selectable extends FirestoreRecyclerAdapter<User, FriendAdapter_Selectable.FriendHolder>
                                        implements Filterable
{
    private FriendAdapter_Selectable.FriendClickListener ClickListener;
    private SelectModifier modifier;
    private List<User> FilteredList;
    public boolean filtering = false;

    /************************************************************************
     * Purpose:         2 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public FriendAdapter_Selectable(@NonNull FirestoreRecyclerOptions<User> options,
                                    FriendAdapter_Selectable.FriendClickListener clickListener,
                                    SelectModifier modifier)
    {
        super(options);
        this.ClickListener = clickListener;
        this.modifier = modifier;
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
    public User getItem(int position) {
        if (filtering)
            return FilteredList.get(position);
        return super.getItem(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String pattern = constraint.toString().toLowerCase();
                if(pattern.isEmpty()){
                    FilteredList = new ArrayList<>(getSnapshots());
                    filtering = false;
                } else {
                    FilteredList = new ArrayList<>(getSnapshots());
                    List<User> filteredList = new ArrayList<>();
                    filtering = true;
                    for(User friend: FilteredList){
                        if(friend.getDisplayName().toLowerCase().contains(pattern)) {
                            filteredList.add(friend);
                        }
                    }
                    FilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = FilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
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
        holder.displayNameText.setText(FilteredList.get(position).getDisplayName());
        holder.emailText.setText(FilteredList.get(position).getEmail());
        modifier.selectionCheck(FilteredList.get(position), holder);
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
        public CheckBox cb;
        FriendAdapter_Selectable.FriendClickListener ClickListener;

        public FriendHolder(@NonNull View itemView, FriendAdapter_Selectable.FriendClickListener clickListener)
        {
            super(itemView);

            this.displayNameText = (TextView) itemView.findViewById(R.id.friendlist_nameText);
            this.emailText = (TextView) itemView.findViewById(R.id.friendlist_emailText);
            this.cb = itemView.findViewById(R.id.friendlistitem_checkbox);
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

    public interface SelectModifier
    {
        void selectionCheck(User model, FriendAdapter_Selectable.FriendHolder holder);
    }
}
