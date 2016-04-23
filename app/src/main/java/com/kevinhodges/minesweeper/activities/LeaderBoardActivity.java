package com.kevinhodges.minesweeper.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kevinhodges.minesweeper.R;
import com.kevinhodges.minesweeper.model.User;
import com.kevinhodges.minesweeper.model.UserAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {

    private AlertDialog.Builder builder;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RecyclerView userRecyclerView;
    private LinearLayoutManager layoutManager;
//    private ArrayList<User> userList;
    private UserAdapter userAdapter;
    private AutoCompleteTextView newUserAC;
    private Button newUserButton;
    private String newUserString;
    private List<User> allUserList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        setTitle("Leaderboards");

        allUserList = new ArrayList<>();

        // Global dialog and shared prefs////////////////////////////////////////////////
        builder = new AlertDialog.Builder(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = sharedPreferences.edit();
        ////////////////////////////////////////////////////////////////////////////////


        //UI Declarations///////////////////////////////////////////////////////////
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Leaderboards");

        userRecyclerView = (RecyclerView) findViewById(R.id.rv_user);
        layoutManager = new LinearLayoutManager(this);
        userAdapter = new UserAdapter(this, allUserList);
        userRecyclerView.setLayoutManager(layoutManager);
        userRecyclerView.setAdapter(userAdapter);

        newUserAC = (AutoCompleteTextView) findViewById(R.id.ac_new_user);
        newUserButton = (Button) findViewById(R.id.bttn_new_user);
        ///////////////////////////////////////////////////////////////////////////


        // If there is a currentUserList, get the users from that list and display them
        if (sharedPreferences.contains("allUserList")) {

            Gson gson = new Gson();
            allUserList = new ArrayList<>();

            String json = sharedPreferences.getString("allUserList", "");
            User[] sharedPrefsUserList = gson.fromJson(json, User[].class);
            allUserList = Arrays.asList(sharedPrefsUserList);
            allUserList = new ArrayList<>(allUserList);

            userAdapter = new UserAdapter(this, allUserList);
            userRecyclerView.setAdapter(userAdapter);

            Toast.makeText(LeaderBoardActivity.this, "listsize = " + allUserList.size(), Toast.LENGTH_SHORT).show();
        }



        // Create the new user and store json object to shared prefs based on their userName
        // Add this user to the player list
        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ArrayList<User> userList = new ArrayList<>();

                // Get name from editText
                newUserString = newUserAC.getText().toString();

                // Create new User object
                User user = new User(newUserString, String.valueOf(0), 0, 0, 0);

                // Add the user to a temp list
                allUserList.add(user);

                // Create json data for the user and the allUserList
                Gson gson = new Gson();
                String currentUserJson = gson.toJson(user);
                String userListJson = gson.toJson(allUserList);

                // Save user data to shared prefs and set the currentUser
                editor.putString("currentUser", newUserString);
                editor.putString("user" + newUserString, currentUserJson);
                editor.putString("allUserList", userListJson);
                editor.commit();

                // Show the new user at the top of the user list
                userRecyclerView.setAdapter(userAdapter);

                // Set edit text to ""
                newUserAC.setText("");

                Toast.makeText(LeaderBoardActivity.this, "You are now logged in under " +
                        newUserString +". Click users to change profiles and long press " +
                        "to delete.", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_leaderboards, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.leaderboardStats) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setTitle("Info");
            builder.setMessage("This is the leader board. You can create new users here. Press a " +
                    "user to switch to that profile. Long press to delete that user.");

            builder.setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
