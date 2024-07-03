package com.example.calendarapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String APPLICATION_NAME = "Calendarapi";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private RecyclerView recyclerView;
    private EventsAdapter eventsAdapter;
    private List<Event> events;
    private GoogleSignInAccount mGoogleSignInAccount;
    private GoogleAccountCredential credential;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                }
            });

    private final ActivityResultLauncher<Intent> authLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    fetchEvents();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button addEventButton = findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(v -> {
            // 添加新事件的逻辑
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("1051550726904-rgcbkmoe6nllvko6c5g2fe784f5jce0q.apps.googleusercontent.com")
                .requestScopes(new com.google.android.gms.common.api.Scope(CalendarScopes.CALENDAR))
                .build();

        Intent signInIntent = GoogleSignIn.getClient(this, gso).getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                Log.d("MainActivity", "Google Sign-In successful, account: " + account.getEmail());
                credential = GoogleAccountCredential.usingOAuth2(
                        this, Collections.singleton(CalendarScopes.CALENDAR));
                credential.setSelectedAccountName(account.getEmail());
                fetchEvents();
            }
        } catch (ApiException e) {
            Log.e("MainActivity", "Sign-in failed", e);
            Toast.makeText(this, "Sign-in failed", Toast.LENGTH_LONG).show();
        }
    }

    private void fetchEvents() {
        new Thread(() -> {
            try {
                HttpTransport httpTransport = new NetHttpTransport();
                Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                        .setApplicationName(APPLICATION_NAME)
                        .build();

                Events events = service.events().list("primary")
                        .setMaxResults(10)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();
                List<Event> items = events.getItems();

                Log.d("MainActivity", "Fetched events: " + items.size());

                runOnUiThread(() -> {
                    MainActivity.this.events = items;
                    eventsAdapter = new EventsAdapter(MainActivity.this.events);
                    recyclerView.setAdapter(eventsAdapter);
                });
            } catch (UserRecoverableAuthIOException e) {
                authLauncher.launch(e.getIntent());
            } catch (Exception e) {
                Log.e("MainActivity", "Failed to fetch events", e);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to fetch events", Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}