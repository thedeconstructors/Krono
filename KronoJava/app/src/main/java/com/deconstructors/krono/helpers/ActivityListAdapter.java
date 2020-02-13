package com.deconstructors.krono.helpers;

import com.deconstructors.structures.Activity;
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
 * Class:           ActivityListAdapter
 * Purpose:         To customize list view layout
 ************************************************************************/
public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.ViewHolder>
{
    public List<Activity> _ActivityList;

    /************************************************************************
     * Purpose:         1 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public ActivityListAdapter(List<Activity> ActivityList)
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
     * Postcondition:   .
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


}
