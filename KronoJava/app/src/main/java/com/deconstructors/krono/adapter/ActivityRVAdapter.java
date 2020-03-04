package com.deconstructors.krono.adapter;

import android.content.Intent;
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
import com.deconstructors.krono.activities.activities.Activity;
import com.deconstructors.krono.activities.activities.ActivityDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/************************************************************************
 * Class:           ActivityRVAdapter
 * Purpose:         Provide a binding from an app-specific data set to
 *  *                  views that are displayed within the RecyclerView.
 ************************************************************************/
public class ActivityRVAdapter extends RecyclerView.Adapter<ActivityRVAdapter.ViewHolder>
        implements Filterable
{
    private List<Activity> ActivityList; // The original List
    private List<Activity> ActivityFilterList; // For Search Filter
    private List<Activity> ActivitySelectList; // For Multi-Select

    /************************************************************************
     * Purpose:         1 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public ActivityRVAdapter(List<Activity> ActivityList)
    {
        this.ActivityList = ActivityList;
        this.ActivityFilterList = ActivityList;
    }

    /************************************************************************
     * Purpose:         resetFilterList
     * Precondition:    onQueryTextChange
     * Postcondition:   Reset _ActivityFilterList after search
     ************************************************************************/
    public void resetFilterList()
    {
        this.ActivityFilterList = new ArrayList<>(ActivityList);
    }

    /************************************************************************
     * Purpose:         View Holder
     * Precondition:    .
     * Postcondition:   Inflate the layout to Recycler List View
     *                  Using the ui_activity_listitem.XML File
     ************************************************************************/
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        int temp_res = R.layout.ui_activity_listitem;
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
        SimpleDateFormat spf = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        String date = spf.format(ActivityFilterList.get(position).getTimestamp().toDate());
        //final Activity temp_activity =  _ActivityFilterList.get(position);

        holder.nameText.setText(ActivityFilterList.get(position).getTitle());
        holder.descriptionText.setText(ActivityFilterList.get(position).getDescription());
        holder.timestampText.setText(date);
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
        return ActivityFilterList == null ? 0 : ActivityFilterList.size();
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   Archive the element from the single list item
     ************************************************************************/
    public class ViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener
    {
        View view;
        TextView nameText;
        TextView descriptionText;
        TextView timestampText;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            this.view = itemView;
            this.nameText = (TextView) view.findViewById(R.id.activitylist_name_text);
            this.descriptionText = (TextView) view.findViewById(R.id.activitylist_description_text);
            this.timestampText = (TextView) view.findViewById(R.id.activitylist_timestamp_text);

            view.setOnLongClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            //this.ClickListener.onActiviySelected(getAdapterPosition());
            ViewActivityDetails();
        }

        @Override
        public boolean onLongClick(View v)
        {
            // Initiate an Active view multi selection
            return false;
        }

        public void ViewActivityDetails() {

            Intent intent = new Intent(this.view.getContext(), ActivityDetails.class);
            intent.putExtra("activity_name", ActivityList.get(getAdapterPosition()).getActivityID());
            this.view.getContext().startActivity(intent);
        }

        public void ToggleSelect()
        {
            final Activity temp_activity = ActivityFilterList.get(getAdapterPosition());
            temp_activity.setSelected(!temp_activity.isSelected());
            if (temp_activity.isSelected())
                this.view.setBackgroundColor(Color.rgb(208,208,208));
            else
                this.view.setBackgroundColor(Color.WHITE);
        }

        public void Select()
        {
            final Activity temp_activity = ActivityFilterList.get(getAdapterPosition());
            temp_activity.setSelected(true);
            this.view.setBackgroundColor(Color.rgb(208,208,208));
        }

        public void Deselect()
        {
            final Activity temp_activity = ActivityFilterList.get(getAdapterPosition());
            temp_activity.setSelected(false);
            this.view.setBackgroundColor(Color.WHITE);
        }
    }

    public interface ActivityRVClickListener
    {
        void onActiviySelected(int position);
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
                filteredList = new ArrayList<>(ActivityFilterList);
            }
            else
            {
                for (Activity activity : ActivityFilterList)
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
            ActivityFilterList.clear();
            ActivityFilterList.addAll((List)results.values);
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
