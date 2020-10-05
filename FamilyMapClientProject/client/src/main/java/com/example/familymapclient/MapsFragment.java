package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import model.Event;
import model.Person;

public class MapsFragment extends Fragment implements GoogleMap.OnMarkerClickListener {
    private GoogleMap googleMap;
    private DataCache dataCache;
    private Person selectedPerson = null;
    private Event selectedEvent = null;
    Marker selectedMarker = null;
    private boolean mapIsReady = false;
    int currentIndex = 0;
    int[] colorsArray;



    @Override
    public void onResume() {
        super.onResume();
        if (mapIsReady) {
            updateMap(selectedPerson,selectedEvent);
        }
    }
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mapIsReady = true;
            TypedArray ta = getResources().obtainTypedArray(R.array.markerColors);
            colorsArray = new int[ta.length()];
            for (int i = 0; i < ta.length(); i++) {
                colorsArray[i] = ta.getResourceId(i,0);
            }
            dataCache = DataCache.getInstance();
            MapsFragment.this.googleMap = googleMap;
            if (Objects.requireNonNull(getActivity()).getClass() == MainActivity.class) {
                updateMap();
            }
            else {
                selectedEvent = ((EventActivity)getActivity()).getSelectedEvent();
                updateMap(dataCache.getPerson(selectedEvent.getPersonID()),selectedEvent);
                if (selectedMarker != null) {
                    onMarkerClick(selectedMarker);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(selectedMarker.getPosition()));
                }
            }
        }
    };

    public void updateMap(Person person, Event selectedEvent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));
        boolean showLifeStoryLines = preferences.getBoolean(
                getResources().getString(R.string.lifeStoryLinesKey), false);
        boolean showFamilyTreeLines = preferences.getBoolean(
                getResources().getString(R.string.familyTreeLinesKey), false);
        boolean showSpouseLines = preferences.getBoolean(
                getResources().getString(R.string.spouseLinesKey), false);
        boolean showFathersSide = preferences.getBoolean(
                getResources().getString(R.string.fathersSideKey), false);
        boolean showMothersSide = preferences.getBoolean(
                getResources().getString(R.string.mothersSideKey), false);
        boolean showMaleEvents = preferences.getBoolean(
                getResources().getString(R.string.maleEventsKey), false);
        boolean showFemaleEvents = preferences.getBoolean(
                getResources().getString(R.string.femaleEventsKey), false);
        dataCache.filterEvents(showFathersSide, showMothersSide, showMaleEvents, showFemaleEvents);

        googleMap.clear();

        for (Event event : dataCache.events) {
            addMarker(event);
        }
        googleMap.setOnMarkerClickListener(MapsFragment.this);
        if (person != null) {
            if (showSpouseLines) {
                drawSpouseLine(person, selectedEvent);
            }
            if (showFamilyTreeLines) {
                drawFamilyLines(person,15, selectedEvent);
            }

            if (showLifeStoryLines) {
                drawLifeStoryLines(person);
            }
        }
    }

    public void updateMap() {
        updateMap(null,null);
    }

    public void addMarker(Event event) {
        BitmapDescriptor markerDescriptor;
        if (dataCache.eventTypeColors.containsKey(event.getEventType().toUpperCase())) {
            markerDescriptor = markerFactory(dataCache.eventTypeColors.get(event.getEventType().toUpperCase()));
        }
        else {
            int newColor = colorsArray[nextIndex()];
            markerDescriptor = markerFactory(newColor);
            dataCache.eventTypeColors.put(event.getEventType().toUpperCase(),newColor);
        }
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(event.getLatitude(),event.getLongitude()))
                .icon(markerDescriptor));
        if (event.equals(selectedEvent)) {
            selectedMarker = marker;
        }
        marker.setTag(event.getEventID());
    }

    public BitmapDescriptor markerFactory(int color) {
        Drawable drawable = new IconDrawable(getActivity(),FontAwesomeIcons.fa_map_marker).colorRes(color).sizeDp(40);
        Bitmap bitmap = drawableToBitmap(drawable);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public int nextIndex() {
        if (currentIndex >= colorsArray.length) {
            currentIndex = 0;
        }
        while (currentIndex < colorsArray.length &&
                (colorsArray[currentIndex] == dataCache.BIRTH_COLOR ||
                colorsArray[currentIndex] == dataCache.MARRIAGE_COLOR ||
                colorsArray[currentIndex] == dataCache.DEATH_COLOR)) {
            currentIndex++;
            if (currentIndex >= colorsArray.length) {
                currentIndex = 0;
            }
        }
        return currentIndex++;
    }



    public void drawSpouseLine(Person person, Event event) {
        if (event != null && person.getSpouseID() != null) {
            Event spousesFirstEvent = dataCache.getFirstEventOfPerson(person.getSpouseID());
            if (spousesFirstEvent != null) {
                //draw line from the current event to spousesFirstEvent
                googleMap.addPolyline(new PolylineOptions().add(
                        new LatLng(event.getLatitude(),event.getLongitude()),
                        new LatLng(spousesFirstEvent.getLatitude(),spousesFirstEvent.getLongitude()))
                        .color(Color.YELLOW).width(15));

            }
        }
    }

    public void drawFamilyLines(Person person, int width, Event event) {
        if (event != null) {
            if (person.getFatherID() != null) {
                //draw line to father's birth
                Event fathersFirstEvent = dataCache.getFirstEventOfPerson(person.getFatherID());
                if (fathersFirstEvent != null) {
                    //draw line from the current event to fathersFirstEvent
                    googleMap.addPolyline(new PolylineOptions().add(
                            new LatLng(event.getLatitude(),event.getLongitude()),
                            new LatLng(fathersFirstEvent.getLatitude(),fathersFirstEvent.getLongitude()))
                            .color(getResources().getColor(R.color.seagreen)).width(width));
                }
                drawFamilyLines(dataCache.getPerson(person.getFatherID()),width * 2 / 3, fathersFirstEvent);
            }
            if (person.getMotherID() != null) {
                //draw line to mother's birth
                Event mothersFirstEvent = dataCache.getFirstEventOfPerson(person.getMotherID());
                if (mothersFirstEvent != null) {
                    //draw line from the current event to fathersFirstEvent
                    googleMap.addPolyline(new PolylineOptions().add(
                            new LatLng(event.getLatitude(),event.getLongitude()),
                            new LatLng(mothersFirstEvent.getLatitude(),mothersFirstEvent.getLongitude()))
                            .color(getResources().getColor(R.color.seagreen)).width(width));
                }
                drawFamilyLines(dataCache.getPerson(person.getMotherID()),width * 2 / 3, mothersFirstEvent);
            }
        }
        else {
            if (person.getFatherID() != null) {
                drawFamilyLines(dataCache.getPerson(person.getFatherID()),width * 2 / 3);
            }
            if (person.getMotherID() != null) {
                drawFamilyLines(dataCache.getPerson(person.getMotherID()),width * 2 / 3);
            }
        }
    }

    public void drawFamilyLines(Person person, int width) {
        drawFamilyLines(person, width, dataCache.getFirstEventOfPerson(person.getPersonID()));
    }

    public void drawLifeStoryLines(Person person) {
        List<Event> events = dataCache.getEventsOfPerson(person.getPersonID());
        for (int i = 1; i < events.size(); i++) {
            googleMap.addPolyline(new PolylineOptions().add(
                    new LatLng(events.get(i - 1).getLatitude(),events.get(i - 1).getLongitude()),
                    new LatLng(events.get(i).getLatitude(),events.get(i).getLongitude()))
                    .color(Color.RED).width(15));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Objects.requireNonNull(getActivity()).getClass() == MainActivity.class) {
            setHasOptionsMenu(true);
        }
        Iconify.with(new FontAwesomeModule());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_maps, container, false);
        LinearLayout mapInfo = view.findViewById(R.id.mapInfo);
        mapInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPerson != null) {
                    Intent intent = new Intent(getActivity(),PersonActivity.class);
                    intent.putExtra("person",selectedPerson.getPersonID());
                    startActivity(intent);
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_menu, menu);

        if (Objects.requireNonNull(getActivity()).getClass().equals(MainActivity.class)) {
            MenuItem searchMenuItem = menu.findItem(R.id.search);

            searchMenuItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_search)
                    .colorRes(R.color.white)
                    .actionBarSize());

            MenuItem settingsMenuItem = menu.findItem(R.id.settings);
            settingsMenuItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_cog)
                    .colorRes(R.color.white)
                    .actionBarSize());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Intent i = new Intent(getActivity(),SearchActivity.class);
                startActivity(i);
                return true;
            case R.id.settings:
                Intent intent = new Intent(getActivity(),SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Event event = dataCache.getEvent((String)marker.getTag());
        Person person = dataCache.getPerson(event.getPersonID());
        TextView textView = Objects.requireNonNull(getView()).findViewById(R.id.mapInfoText);
        String sb = person.getFirstName() +
                ' ' +
                person.getLastName() +
                "\n" +
                event.getEventType().toUpperCase() +
                ": " +
                event.getCity() +
                ", " +
                event.getCountry() +
                " (" +
                event.getYear() +
                ")";
        textView.setText(sb);

        IconTextView iconTextView = getView().findViewById(R.id.mapInfoIcon);
        iconTextView.setText(R.string.userIcon);
        iconTextView.setTextColor(person.getGender().equals("m") ?
                getResources().getColor(R.color.cornflowerblue) : getResources().getColor(R.color.pink));
        updateMap(person,event);
        selectedPerson = person;
        selectedEvent = event;
        return false;
    }
}