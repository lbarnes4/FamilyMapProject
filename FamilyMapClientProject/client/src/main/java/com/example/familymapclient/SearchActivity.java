package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Event;
import model.Person;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private DataCache dataCache = DataCache.getInstance();
    private final List<Pair<Event,Person>> eventResults = new ArrayList<Pair<Event,Person>>();
    private final List<Person> personResults = new ArrayList<Person>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        setAdapter();

        SearchView searchView = (SearchView)findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Start typing to search people and events");
        searchView.setOnQueryTextListener(this);
    }

    private void setAdapter() {
        recyclerView.setAdapter(new SearchAdapter(personResults,eventResults));
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<Pair<Event,Person>> eventResults;
        private final List<Person> personResults;

        public SearchAdapter(List<Person> personResults, List<Pair<Event, Person>> eventResults) {
            this.eventResults = eventResults;
            this.personResults = personResults;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.life_event,parent,false);
            return new SearchViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if (position < personResults.size()) {
                holder.bind(personResults.get(position));
            }
            else {
                holder.bind(eventResults.get(position - personResults.size()));
            }
        }

        @Override
        public int getItemCount() {
            return eventResults.size() + personResults.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final IconTextView iconTextView;
        private final TextView topInfo;
        private final TextView bottomInfo;
        private Event event = null;
        private Person person = null;


        public SearchViewHolder(@NonNull View view) {
            super(view);
            itemView.setOnClickListener(this);
            iconTextView = itemView.findViewById(R.id.lifeEventIcon);
            topInfo = itemView.findViewById(R.id.eventInfo);
            bottomInfo = itemView.findViewById(R.id.personName);
        }

        private void bind(Pair<Event,Person> pair) {
            this.event = pair.first;
            this.person = pair.second;
            topInfo.setText(String.format("%s: %s, %s (%s)",event.getEventType().toUpperCase(),
                    event.getCity(),event.getCountry(),event.getYear()));
            bottomInfo.setText(String.format("%s %s",person.getFirstName(),person.getLastName()));
            iconTextView.setText(R.string.marker_icon);
            iconTextView.setTextColor(getResources().getColor(
                    dataCache.eventTypeColors.get(event.getEventType().toUpperCase())));
        }

        private void bind(Person person) {
            this.person = person;
            topInfo.setText(String.format("%s %s",person.getFirstName(),person.getLastName()));

            iconTextView.setText(person.getGender().equals("m") ? R.string.maleIcon : R.string.femaleIcon);
            iconTextView.setTextColor(getResources().getColor(
                    person.getGender().equals("m") ? R.color.cornflowerblue : R.color.hotpink));
        }

        @Override
        public void onClick(View v) {
            if (event == null) {
                //make person activity
                Intent intent = new Intent(SearchActivity.this,PersonActivity.class);
                intent.putExtra("person",person.getPersonID());
                startActivity(intent);
            }
            else {
                //make event activity
                Intent intent = new Intent(SearchActivity.this,EventActivity.class);
                intent.putExtra("event",event.getEventID());
                startActivity(intent);
            }
        }
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        setAdapter();
        return true;
    }

    private void search(String newText) {
        eventResults.clear();
        personResults.clear();
        if (newText != null && !newText.equals("")) {
            for (Person person : dataCache.persons) {
                String personName = person.getFirstName() + " " + person.getLastName();
                if (personName.toLowerCase().contains(newText.toLowerCase())) {
                    personResults.add(person);
                    for (Event event : dataCache.getEventsOfPerson(person.getPersonID())) {
                        eventResults.add(Pair.create(event,person));
                    }
                }
                else {
                    for (Event event : dataCache.getEventsOfPerson(person.getPersonID())) {
                        String info = event.getEventType().toUpperCase() +
                                ": " +
                                event.getCity() +
                                ", " +
                                event.getCountry() +
                                " (" +
                                event.getYear() +
                                ")";
                        if (info.toLowerCase().contains(newText.toLowerCase())) {
                            eventResults.add(Pair.create(event,person));
                        }
                    }
                }
            }
        }
    }
}