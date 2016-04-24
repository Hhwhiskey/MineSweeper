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

    private static final String TAG = "TitleActivity";
    private static final String NEW_GAME_DIFFICULTY = "newGameDifficulty";
    private static final String CURRENT_USER = "currentUser";
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

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentUserTV.getText().toString().equals("")) {
                    Intent createUserIntent = new Intent(TitleActivity.this, LeaderBoardActivity.class);
                    startActivity(createUserIntent);
                    Toast.makeText(TitleActivity.this, "Create a user so your stats can be tracked", Toast.LENGTH_LONG).show();

                } else {

                    startNewGameDialog();

                }
            }
        });

        leaderBoardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent leaderboardsButton = new Intent(TitleActivity.this, LeaderBoardActivity.class);
                startActivity(leaderboardsButton);
            }
        });

        sweeperTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userActivity = new Intent(TitleActivity.this, LeaderBoardActivity.class);
                startActivity(userActivity);
            }
        });

        currentUserTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userActivity = new Intent(TitleActivity.this, LeaderBoardActivity.class);
                startActivity(userActivity);
            }
        });

    }

    public void startNewGameDialog() {

        final Intent newGameIntent = new Intent(TitleActivity.this, MainActivity.class);

        AlertDialog.Builder difficultyDialog = new AlertDialog.Builder(TitleActivity.this, R.style.MyAlertDialogStyle);
        difficultyDialog.setTitle("Choose difficulty");
        difficultyDialog.setItems(new CharSequence[]{
                        "Easy:   10 x 10 with 10 mines",
                        "Medium:   10 x 20 with 20 mines",
                        "Hard:   10 x 40 with 40 mines",
                        "Insane:   10 x 80 with 160 mines"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {

                            case 0:
                                Toast.makeText(TitleActivity.this, R.string.easy_toast, Toast.LENGTH_SHORT).show();
                                newGameIntent.putExtra(NEW_GAME_DIFFICULTY, 1);

                                break;

                            case 1:
                                Toast.makeText(TitleActivity.this, R.string.medium_toast, Toast.LENGTH_SHORT).show();
                                newGameIntent.putExtra(NEW_GAME_DIFFICULTY, 2);

                                break;

                            case 2:
                                Toast.makeText(TitleActivity.this, R.string.hard_toast, Toast.LENGTH_SHORT).show();
                                newGameIntent.putExtra(NEW_GAME_DIFFICULTY, 3);

                                break;

                            case 3:
                                Toast.makeText(TitleActivity.this, R.string.insane_toast, Toast.LENGTH_SHORT).show();
                                newGameIntent.putExtra(NEW_GAME_DIFFICULTY, 4);

                                break;
                        }

                        startActivity(newGameIntent);
                    }
                });

        difficultyDialog.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = sharedPreferences.getString(CURRENT_USER, "");

        currentUserTV.setText(mCurrentUser);
    }
}
