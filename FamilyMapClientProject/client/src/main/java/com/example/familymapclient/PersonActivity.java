package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import model.Event;
import model.Person;

public class PersonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        DataCache dataCache = DataCache.getInstance();
        Person selectedPerson = dataCache.getPerson(getIntent().getStringExtra("person"));
        TextView firstName = findViewById(R.id.firstName);
        firstName.setText(selectedPerson.getFirstName());
        TextView lastName = findViewById(R.id.lastName);
        lastName.setText(selectedPerson.getLastName());
        TextView gender = findViewById(R.id.gender);
        gender.setText(selectedPerson.getGender().equals("m") ? "Male" : "Female");

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        Person father = dataCache.getPerson(selectedPerson.getFatherID());
        Person mother = dataCache.getPerson(selectedPerson.getMotherID());
        Person spouse = dataCache.getPerson(selectedPerson.getSpouseID());
        Person child = dataCache.getChildOf(selectedPerson.getPersonID());
        List<Event> events = dataCache.getEventsOfPerson(selectedPerson.getPersonID());

        expandableListView.setAdapter(new ExpandableListAdapter(selectedPerson,father,mother,spouse,child,events));
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private static final int LIFE_EVENTS_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;
        private final List<Event> events;
        private Map<String,Person> familyMembers;
        private Person selectedPerson;

        private ExpandableListAdapter(Person selectedPerson, Person father, Person mother, Person spouse, Person child, List<Event> events) {
            this.selectedPerson = selectedPerson;
            familyMembers = new LinkedHashMap<String, Person>();

            if (father != null) {
                familyMembers.put("father",father);
            }
            if (mother != null) {
                familyMembers.put("mother",mother);
            }
            if (spouse != null) {
                familyMembers.put("spouse",spouse);
            }
            if (child != null) {
                familyMembers.put("child",child);
            }
            this.events = events;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    return events.size();
                case FAMILY_GROUP_POSITION:
                    return familyMembers.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    return R.string.lifeEventsTitle;
                case FAMILY_GROUP_POSITION:
                    return R.string.familyTitle;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    return events.get(childPosition);
                case FAMILY_GROUP_POSITION:
                    int i = 0;
                    for (Map.Entry<String,Person> entry : familyMembers.entrySet()) {
                        if (i == childPosition) {
                            return entry;
                        }
                        i++;
                    }

                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    titleView.setText(R.string.lifeEventsTitle);
                    break;
                case FAMILY_GROUP_POSITION:
                    titleView.setText(R.string.familyTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView = null;

            switch(groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.life_event, parent, false);
                    initializeLifeEventView(itemView, childPosition);
                    break;
                case FAMILY_GROUP_POSITION:
                    int i = 0;
                    Map.Entry<String,Person> myEntry = null;
                    for (Map.Entry<String,Person> entry : familyMembers.entrySet()) {
                        if (i == childPosition) {
                            myEntry = entry;
                            break;
                        }
                        i++;
                    }
                    itemView = getLayoutInflater().inflate(R.layout.family_member, parent, false);
                    assert myEntry != null;
                    initializeFamilyMemberView(itemView, myEntry);

                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeFamilyMemberView(View familyMemberView, Map.Entry<String,Person> entry) {
            final Person person = entry.getValue();
            IconTextView icon = familyMemberView.findViewById(R.id.personIcon);
            if (person.getGender() != null && person.getGender().equals("m")) {
                icon.setText(R.string.maleIcon);
                icon.setTextColor(getResources().getColor(R.color.cornflowerblue));
            }
            else {
                icon.setText(R.string.femaleIcon);
                icon.setTextColor(getResources().getColor(R.color.hotpink));
            }
            TextView name = familyMemberView.findViewById(R.id.personName);
            name.setText(String.format("%s %s", person.getFirstName(), person.getLastName()));
            TextView relationship = familyMemberView.findViewById(R.id.relationship);
            relationship.setText(entry.getKey());

            familyMemberView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonActivity.this,PersonActivity.class);
                    intent.putExtra("person",person.getPersonID());
                    startActivity(intent);
                }
            });
        }

        private void initializeLifeEventView(View lifeEventView, int childPosition) {
            final Event event = (Event)getChild(LIFE_EVENTS_GROUP_POSITION,childPosition);
            TextView eventInfo = lifeEventView.findViewById(R.id.eventInfo);
            eventInfo.setText(String.format("%s: %s, %s (%s)",event.getEventType().toUpperCase(),
                    event.getCity(),event.getCountry(),event.getYear()));
            TextView personName = lifeEventView.findViewById(R.id.personName);
            personName.setText(String.format("%s %s",selectedPerson.getFirstName(),selectedPerson.getLastName()));
            IconTextView markerIcon = lifeEventView.findViewById(R.id.lifeEventIcon);
            DataCache dataCache = DataCache.getInstance();
            markerIcon.setTextColor(getResources().getColor(dataCache.eventTypeColors.get(event.getEventType().toUpperCase())));

            lifeEventView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonActivity.this,EventActivity.class);
                    intent.putExtra("event",event.getEventID());
                    startActivity(intent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
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
}