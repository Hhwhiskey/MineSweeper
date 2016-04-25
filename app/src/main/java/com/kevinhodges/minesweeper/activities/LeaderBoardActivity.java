package com.kevinhodges.minesweeper.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.kevinhodges.minesweeper.utils.Constants;
import com.kevinhodges.minesweeper.utils.CustomComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {

    private static final String TAG = "LeaderBoardActivity";
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
    private boolean isNameTaken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        setTitle("Leaderboard");

        mAllUserList = new ArrayList<>();

        // Global dialog and shared prefs////////////////////////////////////////////////
        mBuilder = new AlertDialog.Builder(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mEditor = mSharedPreferences.edit();
        ////////////////////////////////////////////////////////////////////////////////


        //UI Declarations///////////////////////////////////////////////////////////
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Leaderboards" );

        userRecyclerView = (RecyclerView) findViewById(R.id.rv_user);
        layoutManager = new LinearLayoutManager(this);
        userAdapter = new UserAdapter(this, mAllUserList);
        userRecyclerView.setLayoutManager(layoutManager);
        userRecyclerView.setAdapter(userAdapter);

        newUserAC = (AutoCompleteTextView) findViewById(R.id.ac_new_user);
        newUserButton = (Button) findViewById(R.id.bttn_new_user);
        ///////////////////////////////////////////////////////////////////////////

        if (newUserButton != null) {
            newUserButton.setEnabled(false);
        }

        newUserAC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newUserButton.setEnabled(true);
                // Reset isNameTaken boolean back to false for another attempt at creating user
                isNameTaken = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

                enableSubmitIfReady();
            }
        });


        // If there is a currentUserList, get the users from that list and display them
        if (mSharedPreferences.contains(Constants.ALL_USER_LIST)) {

            Gson gson = new Gson();
            mAllUserList = new ArrayList<>();

            String json = mSharedPreferences.getString(Constants.ALL_USER_LIST, "" );
            User[] sharedPrefsUserList = gson.fromJson(json, User[].class);
            mAllUserList = Arrays.asList(sharedPrefsUserList);
            mAllUserList = new ArrayList<>(mAllUserList);

            // Sort the user list by high score priority
            Collections.sort(mAllUserList, new CustomComparator());

            userAdapter = new UserAdapter(this, mAllUserList);
            userRecyclerView.setAdapter(userAdapter);
        }


        // Create the new user and store json object to shared prefs based on their userName
        // Add this user to the player list
        newUserButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                     Gson gson = new Gson();

                     // Get name from editText if not at least 3 characters, alert the user
                     if (newUserAC.getText().toString().length() < 2) {
                         Toast.makeText(LeaderBoardActivity.this, "Please enter at least 3 characters", Toast.LENGTH_SHORT).show();

                         // Otherwise move on with user creation
                     } else {

                         mNewUserString = newUserAC.getText().toString();

                         // This block checks to see if the userName is already taken
                         // If so, it sets a global boolean isNameTaken to true
                         if (mSharedPreferences.contains(Constants.ALL_USER_LIST)) {

                             String json = mSharedPreferences.getString(Constants.ALL_USER_LIST, "" );
                             User[] sharedPrefsUserList = gson.fromJson(json, User[].class);
                             List<User> allUserList = Arrays.asList(sharedPrefsUserList);
                             allUserList = new ArrayList<>(allUserList);

                             // Iterate through all users and check if the name is taken
                             for (User userObject : allUserList) {

                                 // If the name is taken, alert the user
                                 if (userObject.getName().equals(mNewUserString)) {
                                     Toast.makeText(LeaderBoardActivity.this, "That user already exists. Try a different name", Toast.LENGTH_SHORT).show();
                                     isNameTaken = true;
                                 }
                             }
                         }

                         // If the name is not taken, then create the user and add to userList
                         if (!isNameTaken) {
                             // Create new User object
                             User user = new User(mNewUserString);

                             // Add the user to a temp list
                             mAllUserList.add(user);

                             // Create json data for the user and the mAllUserList
                             String currentUserJson = gson.toJson(user);
                             String userListJson = gson.toJson(mAllUserList);

                             // Save user data to shared prefs and set the currentUser
                             mEditor.putString(Constants.CURRENT_USER, mNewUserString);
                             mEditor.putString(Constants.USER + mNewUserString, currentUserJson);
                             mEditor.putString(Constants.ALL_USER_LIST, userListJson);
                             mEditor.commit();

                             // Show the new user at the top of the user list
                             userRecyclerView.setAdapter(userAdapter);

                             // Set edit text to ""
                             newUserAC.setText("" );

                             Toast.makeText(LeaderBoardActivity.this, "Profile switched to "
                                     + mNewUserString, Toast.LENGTH_LONG).show();

                             Intent returnToTitleIntent = new Intent(LeaderBoardActivity.this, TitleActivity.class);
                             startActivity(returnToTitleIntent);
                         }
                     }
                 }
             }
        );
    }


    // Set button to enabled if newUserAC > 2 characters
    public void enableSubmitIfReady() {
        boolean isReady = newUserAC.getText().toString().length() > 2;
        newUserButton.setEnabled(isReady);
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

            // Show info about the leaderboard activity
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setTitle("Info" );
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

    @Override
    protected void onPause() {
        super.onPause();

        // If all users are deleted from user list, then delete the current user so
        // user is forced to create one for stat tracking
        if (mAllUserList.isEmpty()) {
            mEditor.putString(Constants.CURRENT_USER, "" );
            mEditor.commit();
        }
    }
}
