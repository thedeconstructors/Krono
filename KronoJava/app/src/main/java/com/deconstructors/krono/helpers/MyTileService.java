package com.deconstructors.krono.helpers;

import android.content.Intent;
import com.deconstructors.krono.activities.activities.NewActivity;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.deconstructors.krono.R;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyTileService extends TileService
{
    private static final String _Tag = TileService.class.getSimpleName();
    private Tile _tile = null;

    @Override
    public void onTileAdded()
    {
        super.onTileAdded();
        Log.d(_Tag, "onTileAdded");
    }

    @Override
    public void onStartListening()
    {
        super.onStartListening();
        _tile = getQsTile();
        Log.d(_Tag, "onStartListening: " + _tile.getLabel());
    }

    @Override
    public void onClick()
    {
        super.onClick();
        Log.d(_Tag, "onClick: " + Integer.toString(getQsTile().getState()));
        _tile.setState(Tile.STATE_ACTIVE);
        _tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.tileservice_addactivity));
        _tile.updateTile();

        // Call Add Activity
        Intent intent = new Intent(this, NewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}