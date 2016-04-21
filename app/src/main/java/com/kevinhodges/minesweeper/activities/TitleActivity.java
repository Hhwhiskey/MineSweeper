package com.kevinhodges.minesweeper.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kevinhodges.minesweeper.R;


public class TitleActivity extends AppCompatActivity {


    private Button newGameButton;
    private Button leaderBoardsButton;
    private TextView sweeperTV;
    private TextView currentUserTV;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = sharedPreferences.edit();

        //UI Declarations///////////////////////////////////////////////////////////
        newGameButton = (Button) findViewById(R.id.bttn_new_game);
        leaderBoardsButton = (Button) findViewById(R.id.bttn_leaderboards);
        sweeperTV = (TextView) findViewById(R.id.tv_current_sweeper);
        currentUserTV = (TextView) findViewById(R.id.tv_current_user);
        ///////////////////////////////////////////////////////////////////////////

        if (currentUserTV != null) {
            currentUserTV.setText(mCurrentUser);
        }


        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGameDialog();
            }
        });

        leaderBoardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent leaderboardsButton = new Intent(TitleActivity.this, LeaderBoardsActivity.class);
                startActivity(leaderboardsButton);
            }
        });

        sweeperTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userActivity = new Intent(TitleActivity.this, UserActivity.class);
                startActivity(userActivity);
            }
        });

        currentUserTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userActivity = new Intent(TitleActivity.this, UserActivity.class);
                startActivity(userActivity);
            }
        });

    }

    public void startNewGameDialog() {

        AlertDialog.Builder difficultyDialog = new AlertDialog.Builder(TitleActivity.this);
        difficultyDialog.setTitle("Choose difficulty");
        difficultyDialog.setItems(new CharSequence[]{
                        "Easy:   9 x 9 with 10 mines",
                        "Medium:   15 x 15 with 20 mines",
                        "Hard:   20 x 20 with 40 mines"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {

                            case 0:

                                Toast.makeText(TitleActivity.this, "Easy: 9 x 9 with 10 mines", Toast.LENGTH_SHORT).show();

                                Intent easyGameIntent = new Intent(TitleActivity.this, MainActivity.class);
                                easyGameIntent.putExtra("newGameDifficulty", 1);
                                startActivity(easyGameIntent);
                                break;

                            case 1:

                                Toast.makeText(TitleActivity.this, "Medium: 15 x 15 with 20 mines", Toast.LENGTH_SHORT).show();

                                Intent mediumGameIntent = new Intent(TitleActivity.this, MainActivity.class);
                                mediumGameIntent.putExtra("newGameDifficulty", 2);
                                startActivity(mediumGameIntent);
                                break;

                            case 2:

                                Toast.makeText(TitleActivity.this, "Hard: 20 x 20 with 40 mines", Toast.LENGTH_SHORT).show();

                                Intent hardGameIntent = new Intent(TitleActivity.this, MainActivity.class);
                                hardGameIntent.putExtra("newGameDifficulty", 3);
                                startActivity(hardGameIntent);
                                break;
                        }
                    }
                });
        difficultyDialog.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = sharedPreferences.getString("currentUser", "");

        currentUserTV.setText(mCurrentUser);
    }
}
