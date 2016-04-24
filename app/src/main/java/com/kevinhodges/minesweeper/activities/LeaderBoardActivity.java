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

    private static final String ALL_USER_LIST = "allUserList";
    private static final String CURRENT_USER = "currentUser";
    private static final String USER = "user";
    private AlertDialog.Builder mBuilder;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private RecyclerView userRecyclerView;
    private LinearLayoutManager layoutManager;
    private UserAdapter userAdapter;
    private AutoCompleteTextView newUserAC;
    private Button newUserButton;
    private String mNewUserString;
    private List<User> mAllUserList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        setTitle("Leaderboards");

        mAllUserList = new ArrayList<>();

        // Global dialog and shared prefs////////////////////////////////////////////////
        mBuilder = new AlertDialog.Builder(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mEditor = mSharedPreferences.edit();
        ////////////////////////////////////////////////////////////////////////////////


        //UI Declarations///////////////////////////////////////////////////////////
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Leaderboards");

        userRecyclerView = (RecyclerView) findViewById(R.id.rv_user);
        layoutManager = new LinearLayoutManager(this);
        userAdapter = new UserAdapter(this, mAllUserList);
        userRecyclerView.setLayoutManager(layoutManager);
        userRecyclerView.setAdapter(userAdapter);

        newUserAC = (AutoCompleteTextView) findViewById(R.id.ac_new_user);
        newUserButton = (Button) findViewById(R.id.bttn_new_user);
        ///////////////////////////////////////////////////////////////////////////


        // If there is a currentUserList, get the users from that list and display them
        if (mSharedPreferences.contains(ALL_USER_LIST)) {

            Gson gson = new Gson();
            mAllUserList = new ArrayList<>();

            String json = mSharedPreferences.getString(ALL_USER_LIST, "");
            User[] sharedPrefsUserList = gson.fromJson(json, User[].class);
            mAllUserList = Arrays.asList(sharedPrefsUserList);
            mAllUserList = new ArrayList<>(mAllUserList);

            userAdapter = new UserAdapter(this, mAllUserList);
            userRecyclerView.setAdapter(userAdapter);
        }


        // Create the new user and store json object to shared prefs based on their userName
        // Add this user to the player list
        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get name from editText
                mNewUserString = newUserAC.getText().toString();

                // Create new User object
                User user = new User(mNewUserString);

                // Add the user to a temp list
                mAllUserList.add(user);

                // Create json data for the user and the mAllUserList
                Gson gson = new Gson();
                String currentUserJson = gson.toJson(user);
                String userListJson = gson.toJson(mAllUserList);

                // Save user data to shared prefs and set the currentUser
                mEditor.putString(CURRENT_USER, mNewUserString);
                mEditor.putString(USER + mNewUserString, currentUserJson);
                mEditor.putString(ALL_USER_LIST, userListJson);
                mEditor.commit();

                // Show the new user at the top of the user list
                userRecyclerView.setAdapter(userAdapter);

                // Set edit text to ""
                newUserAC.setText("");

                Toast.makeText(LeaderBoardActivity.this, "You are now logged in under " +
                        mNewUserString +". Click users to change profiles and long press " +
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

        if (id == R.id.leaderboardInfo) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setTitle("Info");
            builder.setMessage(getString(R.string.leaderboard_info));

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
