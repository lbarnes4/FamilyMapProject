package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.MapFragment;

import java.util.HashMap;
import java.util.Map;

import model.Event;

public class EventActivity extends AppCompatActivity {
    private Event selectedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        DataCache dataCache = DataCache.getInstance();
        selectedEvent = dataCache.getEvent(getIntent().getStringExtra("event"));

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        MapsFragment mapsFragment = (MapsFragment) fragmentManager.findFragmentById(R.id.mapsFrameLayout);
        if (mapsFragment == null) {
            mapsFragment = new MapsFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.mapsFrameLayout, mapsFragment)
                    .commit();
        }
    }

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}