package com.deconstructors.krono.activities.activities;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;

import java.util.List;

/************************************************************************
 * Class:           ActivityRVAdapter
 * Purpose:         Provide a binding from an app-specific data set to
 *  *                  views that are displayed within the RecyclerView.
 ************************************************************************/
public class ActivityAdapter_NoSelect extends RecyclerView.Adapter<ActivityAdapter_NoSelect.ViewHolder>
{
    private List<Activity> _ActivityList; // The original List

    /************************************************************************
     * Purpose:         1 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public ActivityAdapter_NoSelect(List<Activity> ActivityList)
    {
        this._ActivityList = ActivityList;
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
        //SimpleDateFormat spf = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        //String date = spf.format(_ActivityFilterList.get(position).getTimestamp().toDate());
        //final Activity temp_activity =  _ActivityFilterList.get(position);

        holder._nameText.setText(_ActivityList.get(position).getTitle());
        holder._descriptionText.setText(_ActivityList.get(position).getDescription());
        holder._durationText.setText(_ActivityList.get(position).getDuration()
                                        + " min");
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

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            ViewActivityDetails();
        }

        public void ViewActivityDetails() {

            Intent intent = new Intent(_view.getContext(), ActivityDetails.class);
            intent.putExtra("activity_name", _ActivityList.get(getAdapterPosition()).getActivityID());
            (_view.getContext()).startActivity(intent);
        }
    }
}
