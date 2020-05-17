package com.deconstructors.krono.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Activity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.List;

public class ActivityAdapter extends FirestoreRecyclerAdapter<Activity, ActivityAdapter.ActivityHolder>
                                implements Filterable
{
    private ActivityClickListener ClickListener;
    private List<Activity> FilteredList;
    public boolean filtering = false;

    /************************************************************************
     * Purpose:         2 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public ActivityAdapter(@NonNull FirestoreRecyclerOptions<Activity> options,
                           ActivityClickListener clickListener)
    {
        super(options);
        this.ClickListener = clickListener;
    }

    public void clearFilteredList()
    {
        FilteredList = new ArrayList<>();
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
    public Activity getItem(int position) {
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
                    List<Activity> filteredList = new ArrayList<>();
                    filtering = true;
                    for(Activity act: FilteredList){
                        if(act.getTitle().toLowerCase().contains(pattern)) {
                            filteredList.add(act);
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
                FilteredList = (ArrayList<Activity>) results.values;
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
    public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        int res = R.layout.activity_listitem;
        View view = LayoutInflater.from(parent.getContext()).inflate(res, parent, false);

        return new ActivityHolder(view, this.ClickListener);
    }

    /************************************************************************
     * Purpose:         On Bind View Holder
     * Precondition:    .
     * Postcondition:   Assign Values from ViewHolder class to the Fields
     ************************************************************************/
    @Override
    protected void onBindViewHolder(@NonNull ActivityHolder holder,
                                    int position,
                                    @NonNull Activity model)
    {
        holder.Title.setText(model.getTitle());
        holder.Description.setText(model.getDescription());

        holder.Title.setText(FilteredList.get(position).getTitle());
        holder.Description.setText(FilteredList.get(position).getDescription());
    }

    public class ActivityHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener
    {
        TextView Title;
        TextView Description;
        ActivityClickListener ClickListener;

        public ActivityHolder(View itemView, ActivityClickListener clickListener)
        {
            super(itemView);

            this.Title = itemView.findViewById(R.id.activitylist_title_text);
            this.Description = itemView.findViewById(R.id.activitylist_description_text);
            this.ClickListener = clickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            this.ClickListener.onActivitySelected(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v)
        {
            // Initiate an Active view multi selection
            return false;
        }
    }

    public interface ActivityClickListener
    {
        void onActivitySelected(int position);
    }
}
