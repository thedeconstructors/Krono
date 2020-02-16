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
    private List<Activity> _ActivityFilterList; // For Search Filter

    /************************************************************************
     * Purpose:         1 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public ActivityRVAdapter(List<Activity> ActivityList)
    {
        this._ActivityList = ActivityList;
        this._ActivityFilterList = ActivityList;
    }

    /************************************************************************
     * Purpose:         resetFilterList
     * Precondition:    onQueryTextChange
     * Postcondition:   Reset _ActivityFilterList after search
     ************************************************************************/
    public void resetFilterList()
    {
        this._ActivityFilterList = new ArrayList<>(_ActivityList);
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
        int temp_res = R.layout.menu0_activitylist_item;
        View view = LayoutInflater.from(parent.getContext()).inflate(temp_res, parent, false);
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
        final Activity temp_activity =  _ActivityFilterList.get(position);

        holder._nameText.setText(_ActivityFilterList.get(position).getTitle());
        holder._descriptionText.setText(_ActivityFilterList.get(position).getDescription());
        holder._durationText.setText(_ActivityFilterList.get(position).getDuration());
    }

    public int getSelectedBGColor(Activity activity)
    {
        return activity.isSelected() ? Color.rgb(208,208,208) : Color.WHITE;
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   Return the number of items in the list
     ************************************************************************/
    @Override
    public int getItemCount()
    {
        return _ActivityFilterList == null ? 0 : _ActivityFilterList.size();
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   Archive the element from the single list item
     ************************************************************************/
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private View _view;
        private TextView _nameText;
        private TextView _descriptionText;
        private TextView _durationText;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            _view = itemView;
            _nameText = (TextView) _view.findViewById(R.id.activitylist_name_text);
            _descriptionText = (TextView) _view.findViewById(R.id.activitylist_description_text);
            _durationText = (TextView) _view.findViewById(R.id.activitylist_duration_text);

            _view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            final Activity temp_activity = _ActivityFilterList.get(getAdapterPosition());
            temp_activity.setSelected(!temp_activity.isSelected());
            view.setBackgroundColor(getSelectedBGColor(temp_activity));
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
            String filterPattern = constraint.toString().toLowerCase().trim();

            if (constraint == null || constraint.length() == 0)
            {
                filteredList = new ArrayList<>(_ActivityFilterList);
            }
            else
            {
                for (Activity activity : _ActivityFilterList)
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
            _ActivityFilterList.clear();
            _ActivityFilterList.addAll((List)results.values);
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
