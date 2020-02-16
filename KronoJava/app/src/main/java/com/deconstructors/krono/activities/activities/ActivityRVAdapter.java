package com.deconstructors.krono.activities.activities;

import com.deconstructors.structures.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;

import java.util.ArrayList;
import java.util.List;

/************************************************************************
 * Class:           ActivityRVAdapter
 * Purpose:         Provide a binding from an app-specific data set to
 *  *                  views that are displayed within the RecyclerView.
 ************************************************************************/
public class ActivityRVAdapter extends RecyclerView.Adapter<ActivityRVAdapter.ViewHolder> implements Filterable
{
    private List<Activity> _ActivityList; // The original List
    private List<Activity> _ActivityListFull; // For Search Filter

    /************************************************************************
     * Purpose:         1 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public ActivityRVAdapter(List<Activity> ActivityList)
    {
        this._ActivityList = ActivityList;
        // List = an interface
        // ArrayList = a class and subtype of List interface
        // In Java, supertype can store an object of subtype
        this._ActivityListFull = new ArrayList<>(ActivityList);
    }

    /************************************************************************
     * Purpose:         View Holder
     * Precondition:    .
     * Postcondition:   Inflate the layout to Recycler List View
     *                  Using the menu0_activitylist_item.XML File
     ************************************************************************/
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu0_activitylist_item, parent, false);
        return new ViewHolder(view);
    }

    /************************************************************************
     * Purpose:         Bind View Holder
     * Precondition:    .
     * Postcondition:   Assign Values from ViewHolder class to the Fields
     *                  Assign Click Listener to ViewHolder List Items
     ************************************************************************/
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position)
    {
        final Activity temp_activity =  _ActivityList.get(position);

        holder._nameText.setText(_ActivityList.get(position).getTitle());
        holder._descriptionText.setText(_ActivityList.get(position).getDescription());
        holder._durationText.setText(_ActivityList.get(position).getDuration());

        holder._view.setBackgroundColor(temp_activity.isSelected() ? Color.rgb(208,208,208) : Color.WHITE);
        holder._view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                temp_activity.setSelected(!temp_activity.isSelected());
                holder._view.setBackgroundColor(temp_activity.isSelected() ? Color.rgb(208,208,208) : Color.WHITE);
            }
        });
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   Return the number of items in the list
     ************************************************************************/
    @Override
    public int getItemCount()
    {
        return _ActivityList == null ? 0 : _ActivityList.size();
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   Archive the element from the single list item
     ************************************************************************/
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        View _view;
        public TextView _nameText;
        public TextView _descriptionText;
        public TextView _durationText;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            _view = itemView;

            _nameText = (TextView) _view.findViewById(R.id.activitylist_name_text);
            _descriptionText = (TextView) _view.findViewById(R.id.activitylist_description_text);
            _durationText = (TextView) _view.findViewById(R.id.activitylist_duration_text);
        }
    }

    /************************************************************************
     * Purpose:         Private Filter Class Override
     * Precondition:    .
     * Postcondition:   This filter class allows "performFiltering"
     *                  automatically executed in the background thread.
     *                  In this way, our app won't freeze even if the logic
     *                  is complicated, and if results are huge.
     *                  The result will automatically published to
     *                  "publishResults" in the end.
     ************************************************************************/
    private Filter _ActivityListFilter = new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            List<Activity> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(_ActivityListFull);
            }
            else
            {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Activity activity : _ActivityListFull)
                {
                    if (activity.getTitle().toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(activity);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            _ActivityList.clear();
            _ActivityList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    /************************************************************************
     * Purpose:         Get Filter Override
     * Precondition:    .
     * Postcondition:   Return Activity Toolbar Search Filter
     ************************************************************************/
    @Override
    public Filter getFilter()
    {
        return _ActivityListFilter;
    }
}
