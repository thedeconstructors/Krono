package com.deconstructors.kronoui.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.kronoui.R;
import com.deconstructors.kronoui.module.User;

public class FriendDetailPage extends AppCompatActivity implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "FriendDetailPage";

    //result constant for extra
    private Toolbar Toolbar;
    private EditText Title;
    private EditText Description;
    private EditText DateTime;

    // Vars
    private User Friend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_detail);

        this.setContents();
        this.getFriendIntent();
    }

    private void setContents()
    {
        // Toolbar
        this.Toolbar = findViewById(R.id.frienddetail_Toolbar);
        this.Toolbar.setTitle("");
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*AppBarLayout appbar = (AppBarLayout) findViewById(R.id.friendetail_appbar);
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()
        {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
            {
                relativeLayoutToFadeOut.setAlpha(1.0f - Math.abs(verticalOffset / (float)
                        appBarLayout.getTotalScrollRange()));
            }
        });*/

        // Other Widgets
        //this.Title = findViewById(R.id.activitydetail_titleEditText);
        //this.Description = findViewById(R.id.activitydetail_descriptionText);
        //this.DateTime = findViewById(R.id.activitydetail_dueDateEditText);
    }

    private void getFriendIntent()
    {
        if(getIntent().hasExtra(getString(R.string.intent_friend)))
        {
            this.Friend = getIntent().getParcelableExtra(getString(R.string.intent_friend));
            this.getSupportActionBar().setTitle(this.Friend.getDisplayName());

            /*this.Title.setText(this.Activity.getTitle());
            this.Description.setText(this.Activity.getDescription());
            if (this.Activity.getTimestamp() != null)
            {
                this.DateTime.setText(this.Activity.getTimestamp().toDate().toString());
            }*/
        }
    }

    @Override
    public void onClick(View v)
    {

    }
}
