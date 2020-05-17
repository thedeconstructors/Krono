package com.deconstructors.krono.ui;

import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.krono.R;
import com.deconstructors.krono.utility.SettingsFragment;

import java.util.Objects;

import javax.annotation.Nullable;

public class SettingsPage_Main extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();

        setToolbar();
    }

    private void setToolbar() {
        // XML Widgets
        androidx.appcompat.widget.Toolbar _toolbar = findViewById(R.id.settingsToolbar);
        this.setSupportActionBar(_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        _toolbar.setTitle(getString(R.string.menu_settings));

        Objects.requireNonNull(_toolbar.getNavigationIcon()).setColorFilter(
                getColor(R.color.White), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
