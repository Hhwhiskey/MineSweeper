package com.kevinhodges.minesweeper.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.kevinhodges.minesweeper.R;
import com.kevinhodges.minesweeper.model.Block;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int newGameDifficulty;
    private TableLayout tableLayout;
    private int totalRows;
    private int totalColumns;
    private int totalMines;
    private Block[][] blocks;
    private Handler timer;
    private int secondsPassed;
    private TextView gameTimerTV;
    private TextView mineCountTV;
    private boolean firstClick = true;
    private ImageView smileyFaceIV;
    private Boolean isGameOver = false;
    private Menu menu;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isUserSignedOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent getIntent = getIntent();
        newGameDifficulty = getIntent.getIntExtra("newGameDifficulty", 0);

        timer = new Handler();
        secondsPassed = 0;

        Block.MINE_COUNT = 0;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = sharedPreferences.edit();

        //UI Declarations///////////////////////////////////////////////////////////
        // Google suggests the use of the Toolbar in place of the action bar to support older devices.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tableLayout = (TableLayout) findViewById(R.id.table_game_board);
        gameTimerTV = (TextView) findViewById(R.id.tv_game_timer);
        mineCountTV = (TextView) findViewById(R.id.tv_mine_count);
        smileyFaceIV = (ImageView) findViewById(R.id.iv_smiley);
        ImageView clockIV = (ImageView) findViewById(R.id.iv_clock);
        ImageView mineIV = (ImageView) findViewById(R.id.iv_mine);
        ///////////////////////////////////////////////////////////////////////////


        //OnClicks//////////////////////////////////////////////////////////////////////////////
        assert mineIV != null;
        mineIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        "The amount of mines left. But who knows if you placed your flags correctly...", Toast.LENGTH_LONG).show();
            }
        });

        assert clockIV != null;
        clockIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "The clock is ticking! Hurry up, this is how you will be ranked!", Toast.LENGTH_LONG).show();
            }
        });

        smileyFaceIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smileyFaceIV.setImageResource(R.drawable.ic_smiley_worried);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        smileyFaceIV.setImageResource(R.drawable.ic_smiley_happy);
                    }
                }, 250);
            }
        });

        smileyFaceIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                smileyFaceIV.setImageResource(R.drawable.ic_smiley_worried);

                return false;
            }
        });

        // Show the proper number of mines based on difficulty
        switch (newGameDifficulty) {
            case 1:
                mineCountTV.setText(String.valueOf(10));
                break;

            case 2:
                mineCountTV.setText(String.valueOf(20));
                break;

            case 3:
                mineCountTV.setText(String.valueOf(40));
                break;
        }

        if (newGameDifficulty != 0) {
            createNewGameBoard(newGameDifficulty);
        }
        /////////////////////////////////////////////////////////////////////////////////////


    }

    // Method to update the mine count textView
    public void updateMineCount() {
        mineCountTV.setText(String.valueOf(Block.MINE_COUNT));
    }

    // TODO: 4/19/2016 Custom game board and custom mine density
    // Creates a new board based on the difficulty the user has chosen
    // Easy = 9*9 with 10 bombs
    // Medium = 12*12 with 20 bombs
    // Hard = 18*18 with 40 bombs
    public void createNewGameBoard(int difficulty) {

        switch (difficulty) {
            case 1:
                totalRows = 9;
                totalColumns = 9;
                totalMines = 10;
                break;

            case 2:
                totalRows = 15;
                totalColumns = 15;
                totalMines = 20;
                break;

            case 3:
                totalRows = 20;
                totalColumns = 20;
                totalMines = 40;

                break;
        }

        //setup the blocks array
        blocks = new Block[totalRows][totalColumns];

        //for every row
        for (int row = 0; row < totalRows; row++) {
            //create a new table row
            TableRow tableRow = new TableRow(this);
            //set the height and width of the row
            int blockSize = 148;
            int blockPadding = 1;
            tableRow.setLayoutParams(new TableRow.LayoutParams((blockSize * blockPadding) * totalColumns, blockSize * blockPadding));

            //for every column
            for (int col = 0; col < totalColumns; col++) {
                //create a block
                blocks[row][col] = new Block(this);
                //set the block defaults
                blocks[row][col].setDefaults();
                //set the width and height of the block
                blocks[row][col].setLayoutParams(new TableRow.LayoutParams(blockSize * blockPadding, blockSize * blockPadding));
                //add some padding to the block
                blocks[row][col].setPadding(blockPadding, blockPadding, blockPadding, blockPadding);
                //add the block to the table row
                tableRow.addView(blocks[row][col]);

                final int curRow = row;
                final int curCol = col;

                //add a click listener
                blocks[row][col].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // If game is over, all clicks will restart game
                        if (isGameOver) {
                            Intent easyGameIntent = getIntent();
                            easyGameIntent.putExtra("newGameDifficulty", newGameDifficulty);
                            finish();
                            startActivity(easyGameIntent);

                        } else {

                            // If the block is a mine, call lose game
                            if (blocks[curRow][curCol].isMine() && !blocks[curRow][curCol].isFlagged()) {

                                smileyFaceIV.setImageResource(R.drawable.ic_smiley_sad);
                                loseGame();

                            } else {

                                // Show the worried face whenever a block is clicked
                                smileyFaceIV.setImageResource(R.drawable.ic_smiley_worried);

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        smileyFaceIV.setImageResource(R.drawable.ic_smiley_happy);
                                    }
                                }, 500);
                            }

                            // If click is the first click, place all mines
                            if (firstClick) {
                                placeMinesOnBoard(curRow, curCol);
                            }

                            // Flip the block over when it's pressed if it's not flagged by user
                            if (!blocks[curRow][curCol].isFlagged()) {
                                blocks[curRow][curCol].flipBlock();
                            }

                            // Start the game timer on first click
                            startTimer();
                        }
                    }
                });

                //add a long click listener
                blocks[row][col].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        // If game is over, all clicks will start new game
                        if (isGameOver) {
                            Intent easyGameIntent = getIntent();
                            easyGameIntent.putExtra("newGameDifficulty", newGameDifficulty);
                            finish();
                            startActivity(easyGameIntent);

                        } else {

                            // If it's not the first click, allow long presses
                            if (!firstClick) {

                                if (!blocks[curRow][curCol].isRevealed()) {
                                    // If the block already has a flag, remove it and update MINE_COUNT and textView
                                    if (blocks[curRow][curCol].isFlagged()) {
                                        blocks[curRow][curCol].removeFlag();
                                        mineCountTV.setText(String.valueOf(++Block.MINE_COUNT));

                                        // Otherwise, plant a flag and update static MINE_COUNT and textView
                                    } else {

                                        if (Block.MINE_COUNT > 0) {

                                            // Give vibration feedback of the flag placement
                                            Vibrator v = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                                            v.vibrate(500);

                                            blocks[curRow][curCol].plantFlag();
                                            mineCountTV.setText(String.valueOf(--Block.MINE_COUNT));
                                        } else {
                                            Toast.makeText(MainActivity.this, "You have already flagged all supposed mines. Questioning yourself now, are you?", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }
                        }
                        return true;
                    }
                });

            }
            //add the row to the tableLayout
            tableLayout.addView(tableRow, new TableLayout.LayoutParams((blockSize * blockPadding) * totalColumns, blockSize * blockPadding));
        }
    }

    public void placeMinesOnBoard(int row, int col) {

        int mineRow;
        int mineCol;
        firstClick = false;

        Random random = new Random();

        for (int i = 0; i < totalMines; i++) {

            // Generate random positions for rows and columns
            mineRow = random.nextInt(totalRows);
            mineCol = random.nextInt(totalColumns);

            // Check to make sure mine is not placed where the user first clicks subtract mine from total
            if (mineRow == row && mineCol == col) {
                i--;
                Toast.makeText(MainActivity.this, "Mine was going to be placed here", Toast.LENGTH_SHORT).show();

                // Check to make sure the block is not already a mine, if so, subtract mine from total
            } else if (blocks[mineRow][mineCol].isMine()) {
                i--;

            } else {
                // If both tests pass, plant the mine
                blocks[mineRow][mineCol].plantMine();
            }
        }

        // Call to method that will count mines and display the correct value on the blocks
        countAdjacentMines();

        // Update the mine count textView
        updateMineCount();
    }

    private void countAdjacentMines() {

        // Set i to 0. While it's less than blocks.length do next and ++
        for (int i = 0; i < blocks[totalRows][totalColumns].length(); i++) {

            // Set j to 0. While it's less than blocks.length do next and ++
            for (int j = 0; j < blocks[totalRows][totalColumns].length(); j++) {

                // If the current block is not a mine, do next
                if (!blocks[i][j].isMine()) {

                    // Create a counter for mine count, starting at 0
                    int currentMineCount = 0;

                    // Set p to i-1 and do next until p is == i+1 (Max of 3 times)
                    for (int p = i - 1; p <= i + 1; p++) {

                        // Set q to j-1 and do next until q is == j +1 (Max of 3 times)
                        for (int q = j - 1; q <= j + 1; q++) {

                            // If 0 is less/equal to value of p
                            if (0 <= p

                                    // && p is less than blocks.length
                                    && p < blocks[totalRows][totalColumns].length()

                                    //&& 0 is less/equal
                                    && 0 <= q

                                    //  && q is less than blocks.length
                                    && q < blocks[totalRows][totalColumns].length()) {

                                // If block at p/q is a mine, add 1 to currentMineCount
                                if (blocks[p][q].isMine())
                                    ++currentMineCount;
                            }
                        }
                    }

                    blocks[i][j].showAdjacentMineCount(currentMineCount);
                }
            }
        }
    }

    // Starts the game time when the first block is clicked
    public void startTimer() {

        if (secondsPassed == 0) {
            timer.removeCallbacks(updateTimeElapsed);
            // tell timer to run call back after 1 second
            timer.postDelayed(updateTimeElapsed, 1);
        }
    }

    // Resumes the ongoing game timer, called in onResume
    public void resumeTimer() {
        if (secondsPassed != 0) {
            timer.removeCallbacks(updateTimeElapsed);
            // tell timer to run call back after 1 second
            timer.postDelayed(updateTimeElapsed, 1000);
        }
    }

    // Pauses the ongoing game timer, called in onPause
    public void stopTimer() {
        // disable call backs
        timer.removeCallbacks(updateTimeElapsed);
    }

    // Runnable to track time and update gameTimerTV textView
    private Runnable updateTimeElapsed = new Runnable() {
        public void run() {
            long currentMilliseconds = System.currentTimeMillis();
            ++secondsPassed;
            gameTimerTV.setText(Integer.toString(secondsPassed));

            // add notification
            timer.postAtTime(this, currentMilliseconds);
            // notify to call back after 1 seconds
            // basically to remain in the timer loop
            timer.postDelayed(updateTimeElapsed, 1000);
        }
    };

    // Game over: Vibrate, stop timer, show all bombs, restart game on another click
    public void loseGame() {

        Toast.makeText(MainActivity.this, "BOOM!", Toast.LENGTH_SHORT).show();

        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1000);

        isGameOver = true;

        stopTimer();

        for (int i = 0; i < totalRows; i++) {

            for (int j = 0; j < totalColumns; j++) {

                if (blocks[i][j].isMine()) {
                    blocks[i][j].showAllMines();
                }
            }
        }
    }

    public void winGame(double finishTime) {

        //// TODO: 4/20/2016
//        Games.Leaderboards.submitScore(mGoogleApiClient, LEADERBOARD_ID, 1337);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.new_game) {

            AlertDialog.Builder newGameDialog = new AlertDialog.Builder(this);
            newGameDialog.setTitle("New Game");
            newGameDialog.setMessage("Would you like to start a new game? Current game will be lost");
            newGameDialog.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    AlertDialog.Builder difficultyDialog = new AlertDialog.Builder(MainActivity.this);
                    difficultyDialog.setTitle("Choose difficulty");
                    difficultyDialog.setItems(new CharSequence[]{
                                    "Easy:   9 x 9 with 10 mines",
                                    "Medium:   15 x 15 with 20 mines",
                                    "Hard:   20 x 20 with 40 mines"},
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    switch (which) {

                                        case 0:
                                            Toast.makeText(MainActivity.this, "Easy: 9 x 9 with 10 mines", Toast.LENGTH_SHORT).show();
//
                                            Intent easyGameIntent = getIntent();
                                            easyGameIntent.putExtra("newGameDifficulty", 1);
                                            finish();
                                            startActivity(easyGameIntent);
                                            break;

                                        case 1:
                                            Toast.makeText(MainActivity.this, "Medium: 15 x 15 with 20 mines", Toast.LENGTH_SHORT).show();

                                            Intent mediumGameIntent = getIntent();
                                            mediumGameIntent.putExtra("newGameDifficulty", 2);
                                            finish();
                                            startActivity(mediumGameIntent);
                                            break;

                                        case 2:
                                            Toast.makeText(MainActivity.this, "Hard: 20 x 20 with 40 mines", Toast.LENGTH_SHORT).show();

                                            Intent hardGameIntent = getIntent();
                                            hardGameIntent.putExtra("newGameDifficulty", 3);
                                            finish();
                                            startActivity(hardGameIntent);
                                            break;
                                    }
                                }
                            });
                    difficultyDialog.create().show();
                }
            });

            newGameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            newGameDialog.show();

            return true;
        }

        if (id == R.id.give_up) {

            smileyFaceIV.setImageResource(R.drawable.ic_smiley_sad);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Giving up already?");
            builder.setPositiveButton("Yes :(", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent titleActivityIntent = new Intent(MainActivity.this, TitleActivity.class);
                    startActivity(titleActivityIntent);
                }
            });

            builder.setNegativeButton("No way, I can do it!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    smileyFaceIV.setImageResource(R.drawable.ic_smiley_happy);
                }
            });

            builder.show();

            return true;
        }

        if (id == R.id.stats) {

            Intent leaderboardsButton = new Intent(MainActivity.this, LeaderBoardsActivity.class);
            startActivity(leaderboardsButton);

            return true;
        }

        if (id == R.id.about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Mine Sweeper");
            builder.setMessage("This application was created by Kevin Hodges " +
                    "for a JP Morgan & Chase code assessment. It incorporates all of the tried" +
                    " and true Mine Sweeper game play that you have grown to love, with a new, " +
                    "polished, material design look.");

            builder.setPositiveButton("Leave Feedback", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.show();

            return true;
        }

        if (id == R.id.faq) {
            Intent faqIntent = new Intent(MainActivity.this, FAQActivity.class);
            startActivity(faqIntent);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

//    private void signOut() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Log out");
//        builder.setMessage("Are you sure you would like to sign out of Google Play Services? " +
//                "You will not be able to track your high scores, stats or view leaderboards.");
//
//        builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                        new ResultCallback<Status>() {
//                            @Override
//                            public void onResult(Status status) {
//                                Toast.makeText(MainActivity.this,
//                                        "You have been signed out of Google Play Services",
//                                        Toast.LENGTH_SHORT).show();
//
//                                editor.putBoolean("isUserSignedOut", true);
//                                editor.commit();
//                            }
//                        });
//
//
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//
//        builder.show();
//
//    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }
}
