package com.deconstructors.krono;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/************************************************************************
 * Class:           ActivityListAdapter
 * Purpose:         To customize list view layout
 ************************************************************************/
public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.ViewHolder>
{
    public List<Plans> _ActivityList;

    /************************************************************************
     * Purpose:         1 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public ActivityListAdapter(List<Plans> ActivityList)
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
     ************************************************************************/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder._nameText.setText(_ActivityList.get(position).getTitle());
        holder._descriptionText.setText(_ActivityList.get(position).getDescription());
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public int getItemCount()
    {
        return _ActivityList.size();
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   Archive the element from the single list item
     ************************************************************************/
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        View m_view;
        public TextView _nameText;
        public TextView _descriptionText;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            m_view = itemView;

            _nameText = (TextView) m_view.findViewById(R.id.activitylist_name_text);
            _descriptionText = (TextView) m_view.findViewById(R.id.activitylist_description_text);
        }
    }
}
