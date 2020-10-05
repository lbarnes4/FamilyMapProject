package com.example.familymapclient;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import model.User;
import request.LoginRequest;
import request.RegisterRequest;
import response.EventResponse;
import response.EventsResponse;
import response.LoginResponse;
import response.PersonsResponse;
import response.RegisterResponse;
import response.Response;

public class HttpClient {
    private static final String LOG_TAG = "HttpClient";
    private URL url;

    public HttpClient(String urlString) {
        try {
            this.url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public LoginResponse login(LoginRequest request) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.connect();

            Gson gson;
            try(OutputStream requestBody = connection.getOutputStream()) {
                // Write request body to OutputStream ...
                gson = new GsonBuilder().create();
                String requestData = gson.toJson(request);
                writeString(requestData, requestBody);
            }

            // Get response body input stream
            InputStream responseBody;
            if (connection.getResponseCode() == 400) {
                responseBody = connection.getErrorStream();
            }
            else {
                responseBody = connection.getInputStream();
            }
            InputStreamReader reader = new InputStreamReader(responseBody);
            gson = new Gson();

            return gson.fromJson(reader, LoginResponse.class);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return null;
    }

    public RegisterResponse register(RegisterRequest request) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.connect();

            Gson gson;
            try(OutputStream requestBody = connection.getOutputStream()) {
                // Write request body to OutputStream ...
                gson = new GsonBuilder().create();
                String requestData = gson.toJson(request);
                writeString(requestData, requestBody);
            }

            // Get response body input stream
            InputStream responseBody;
            if (connection.getResponseCode() == 400) {
                responseBody = connection.getErrorStream();
            }
            else {
                responseBody = connection.getInputStream();
            }
            InputStreamReader reader = new InputStreamReader(responseBody);
            gson = new Gson();

            return gson.fromJson(reader, RegisterResponse.class);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return null;
    }

    public PersonsResponse getPersons() {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Authorization",DataCache.getInstance().authString);
            connection.setRequestMethod("GET");
            connection.connect();

            // Get response body input stream
            InputStream responseBody = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(responseBody);
            Gson gson = new Gson();
            PersonsResponse response = gson.fromJson(reader, PersonsResponse.class);
            return response;
        } catch (Exception e) {
            return null;
        }
    }

    public EventsResponse getEvents() {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Authorization",DataCache.getInstance().authString);
            connection.setRequestMethod("GET");
            connection.connect();

            // Get response body input stream
            InputStream responseBody = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(responseBody);
            Gson gson = new Gson();
            EventsResponse response = gson.fromJson(reader, EventsResponse.class);
            return response;
        } catch (Exception e) {
            return null;
        }
    }

    public void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(sw);
        bw.write(str);
        bw.flush();
    }
}
