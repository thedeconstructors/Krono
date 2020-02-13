package com.deconstructors.krono.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import com.deconstructors.krono.R;
import com.deconstructors.krono.activities.activities.Menu0_Activities;
import com.deconstructors.krono.activities.friends.Menu3_Friends;
import com.deconstructors.krono.activities.plans.Menu1_Plans;
import com.deconstructors.krono.activities.profile.Menu4_Users;

public class MainActivity extends AppCompatActivity
{
    GridLayout m_MainGrid;

    public static final String TAG = "Krono";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_MainGrid = findViewById(R.id.mainGrid);
        setSingleEvent(m_MainGrid);
    }

    /************************************************************************
     * Purpose:         Set Event Handler
     * Precondition:    Menu items are w/o button events
     * Postcondition:   Menu items are set with onClick indexing events
     *                  This is mainly to separate the xml files
     ************************************************************************/
    private void setSingleEvent(GridLayout mainGrid)
    {
        for(int i = 0; i < mainGrid.getChildCount(); i++)
        {
            CardView cardView = (CardView)mainGrid.getChildAt(i);
            final int finalindex = i;
            cardView.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    // TEST CODE for indexing
                    // Toast.makeText(MainActivity.this, "Clicked at index " + finalindex, Toast.LENGTH_SHORT).show();

                    if (finalindex == 0)
                    {
                        Intent intent = new Intent(MainActivity.this, Menu0_Activities.class);
                        startActivity(intent);
                    }
                    else if (finalindex == 1)
                    {
                        Intent intent = new Intent(MainActivity.this, Menu1_Plans.class);
                        startActivity(intent);
                    }
                    else if (finalindex == 3)
                    {
                        Intent intent = new Intent(MainActivity.this, Menu3_Friends.class);
                        startActivity(intent);
                    }
                    else if (finalindex == 4)
                    {
                        Intent intent = new Intent(MainActivity.this, Menu4_Users.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Error: No Activity Found in Menu", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
