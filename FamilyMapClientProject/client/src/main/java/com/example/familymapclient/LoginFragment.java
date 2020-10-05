package com.example.familymapclient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.dynamic.SupportFragmentWrapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import model.Person;
import request.LoginRequest;
import request.RegisterRequest;
import response.EventsResponse;
import response.LoginResponse;
import response.PersonsResponse;
import response.RegisterResponse;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private EditText serverHost;
    private EditText serverPort;
    private EditText username;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private RadioGroup gender;
    private Button loginButton;
    private Button registerButton;
    private String urlBase;
    private String userPersonID;


    private static final String LOG_TAG = "LoginFragment";

    public LoginFragment() {
        // Required empty public constructor
    }
    /*
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = view.findViewById(R.id.login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveURLBase();
                    LoginTask task = new LoginTask();
                    LoginRequest request = new LoginRequest(
                            username.getText().toString(),
                            password.getText().toString());
                    task.execute(request);

                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
            }
        });

        registerButton = view.findViewById(R.id.register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveURLBase();
                    RegisterTask task = new RegisterTask();
                    RegisterRequest request = new RegisterRequest(
                            username.getText().toString(),
                            password.getText().toString(),
                            email.getText().toString(),
                            firstName.getText().toString(),
                            lastName.getText().toString(),
                            gender.getCheckedRadioButtonId() == view.findViewById(R.id.male).getId() ? "m" : "f");
                    task.execute(request);

                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
            }
        });

        serverHost = (EditText)view.findViewById(R.id.serverHost);
        serverPort = (EditText)view.findViewById(R.id.serverPort);
        username = (EditText)view.findViewById(R.id.username);
        password = (EditText)view.findViewById(R.id.password);
        firstName = (EditText)view.findViewById(R.id.firstName);
        lastName = (EditText)view.findViewById(R.id.lastName);
        email = (EditText)view.findViewById(R.id.email);
        gender = (RadioGroup)view.findViewById(R.id.gender);

        serverHost.setText("10.0.2.2");
        serverPort.setText("8080");
        username.setText("sheila");
        password.setText("parker");
        firstName.setText("Sheila");
        lastName.setText("Parker");
        email.setText("sheila@parker.com");
        ((RadioButton)view.findViewById(R.id.female)).setChecked(true);

        serverHost.addTextChangedListener(new InputWatcher());
        serverPort.addTextChangedListener(new InputWatcher());
        username.addTextChangedListener(new InputWatcher());
        password.addTextChangedListener(new InputWatcher());
        firstName.addTextChangedListener(new InputWatcher());
        lastName.addTextChangedListener(new InputWatcher());
        email.addTextChangedListener(new InputWatcher());
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkInputAndActivateButtons();
            }
        });
        checkInputAndActivateButtons();
        return view;
    }

    private class LoginTask extends AsyncTask<LoginRequest,Void, LoginResponse> {
        @Override
        protected LoginResponse doInBackground(LoginRequest... requests) {
            HttpClient httpClient = new HttpClient(urlBase +  "/user/login");
            return httpClient.login(requests[0]);
        }

        protected void onPostExecute(LoginResponse response) {
            if (response.wasSuccessful()) {
                DataCache dataCache = DataCache.getInstance();
                dataCache.authString = response.getAuthToken();
                userPersonID = response.getPersonID();
                GetPersonsTask getPersonsTask = new GetPersonsTask();
                getPersonsTask.execute();
                GetEventsTask getEventsTask = new GetEventsTask();
                getEventsTask.execute();
            }
            else {
                Toast.makeText(getActivity(),response.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private class RegisterTask extends AsyncTask<RegisterRequest,Void, RegisterResponse> {
        @Override
        protected RegisterResponse doInBackground(RegisterRequest... requests) {
            HttpClient httpClient = new HttpClient(urlBase +  "/user/register");
            return httpClient.register(requests[0]);
        }

        protected void onPostExecute(RegisterResponse response) {
            if (response.wasSuccessful()) {
                DataCache dataCache = DataCache.getInstance();
                dataCache.authString = response.getAuthToken();
                userPersonID = response.getPersonID();
                GetPersonsTask getPersonsTask = new GetPersonsTask();
                getPersonsTask.execute();
                GetEventsTask getEventsTask = new GetEventsTask();
                getEventsTask.execute();


            }
            else {
                Toast.makeText(getActivity(),response.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private class GetPersonsTask extends AsyncTask<Void,Void, PersonsResponse> {
        @Override
        protected PersonsResponse doInBackground(Void... voids) {
            HttpClient httpClient = new HttpClient(urlBase + "/person");
            return httpClient.getPersons();
        }

        protected void onPostExecute(PersonsResponse response) {
            if (response.wasSuccessful()) {
                DataCache dataCache = DataCache.getInstance();
                dataCache.persons = response.data;
                for (Person person : dataCache.persons) {
                    if (person.getPersonID().equals(userPersonID)) {
                        dataCache.userPerson = person;
                        Toast.makeText(getActivity(),"Welcome " + person.getFirstName() + " " +
                                person.getLastName() + "!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private class GetEventsTask extends AsyncTask<Void,Void, EventsResponse> {
        @Override
        protected EventsResponse doInBackground(Void... voids) {
            HttpClient httpClient = new HttpClient(urlBase + "/event");
            return httpClient.getEvents();
        }

        protected void onPostExecute(EventsResponse response) {
            if (response.wasSuccessful()) {
                DataCache dataCache = DataCache.getInstance();
                dataCache.allEvents = response.data;
                //go to map fragment
                ((MainActivity)getActivity()).goToMap();

            }
        }
    }

    private class InputWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkInputAndActivateButtons();
        }
    }

    private void saveURLBase() throws MalformedURLException {
        urlBase = "http://" + serverHost.getText().toString() + ":" + serverPort.getText().toString();
    }

    private void checkInputAndActivateButtons() {
        if (username.getText().length() != 0 &&
            password.getText().length() != 0 &&
            serverHost.getText().length() != 0 &&
            serverPort.getText().length() != 0
        ) {
            loginButton.setEnabled(true);
            if (firstName.getText().length() != 0 &&
                lastName.getText().length() != 0 &&
                email.getText().length() != 0 &&
                gender.getCheckedRadioButtonId()!= -1
            ) {
                registerButton.setEnabled(true);
            }
            else {
                registerButton.setEnabled(false);
            }
        }
        else {
            loginButton.setEnabled(false);
        }
    }
}