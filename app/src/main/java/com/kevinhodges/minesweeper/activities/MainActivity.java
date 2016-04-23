package com.kevinhodges.minesweeper.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kevinhodges.minesweeper.R;
import com.kevinhodges.minesweeper.model.Block;
import com.kevinhodges.minesweeper.model.User;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private int newGameDifficulty;
    private TableLayout tableLayout;
    private int totalRows;
    private int totalColumns;
    private static int totalMines;
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
    private AlertDialog.Builder builder;
    private boolean[][] isBlockVisited;
    private int blockSize;
    public static int revealedBlockCount;
    private int totalBlocks;
    public static int correctFlagsPlaced;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Global dialog and shared prefs////////////////////////////////////////////////
        builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = sharedPreferences.edit();
        ////////////////////////////////////////////////////////////////////////////////

        Intent getIntent = getIntent();
        newGameDifficulty = getIntent.getIntExtra("newGameDifficulty", 0);

        timer = new Handler();

        secondsPassed = 0;
        correctFlagsPlaced = 0;
        Block.MINE_COUNT = 0;


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

                incrementGameCount("played");

                Intent easyGameIntent = getIntent();
                easyGameIntent.putExtra("newGameDifficulty", newGameDifficulty);
                finish();
                startActivity(easyGameIntent);


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

            case 4:
                mineCountTV.setText(String.valueOf(200));
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

        // Get the display size to form a ratio that can be used to alter the block size accordingly
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int ratio = width / 10;


        switch (difficulty) {
            case 1:
                totalRows = 9;
                totalColumns = 9;
                totalMines = 10;
                totalBlocks = totalRows * totalColumns;
                blockSize = ratio;
                break;

            case 2:
                totalRows = 20;
                totalColumns = 9;
                totalMines = 20;
                totalBlocks = totalRows * totalColumns;
                blockSize = ratio;
                break;

            case 3:
                totalRows = 40;
                totalColumns = 9;
                totalMines = 40;
                totalBlocks = totalRows * totalColumns;
                blockSize = ratio;
                break;

            case 4:
                totalRows = 100;
                totalColumns = 9;
                totalMines = 200;
                totalBlocks = totalRows * totalColumns;
                blockSize = ratio;
                break;
        }

        //setup the blocks array
        blocks = new Block[totalRows][totalColumns];
        // Create a 2d boolean array that matches the size of the game board for our recursive function
        isBlockVisited = new boolean[totalRows][totalColumns];

        //for every row
        for (int row = 0; row < totalRows; row++) {
            //create a new table row
            TableRow tableRow = new TableRow(this);
            //set the height and width of the row
//            blockSize = 125;
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
                                revealFirstBlock(blocks, curRow, curCol);
                                // Start the game timer on first click
                                startTimer();

                            } else {

                                if (!blocks[curRow][curCol].isFlagged()) {
                                    blocks[curRow][curCol].flipBlock();
                                }
                            }
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
                                            vibrate(500);

                                            blocks[curRow][curCol].plantFlag();
                                            mineCountTV.setText(String.valueOf(--Block.MINE_COUNT));
                                        } else {
                                            Toast.makeText(MainActivity.this, "You have already flagged all supposed mines. Questioning yourself now, are you?", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }
                        }

                        Log.d(TAG, "correctFlagsPlaced = " + correctFlagsPlaced);

                        // If blocks left = mineCount player wins
                        if (correctFlagsPlaced == totalMines) {
                            winGame();
                        }

                        return true;
                    }
                });

            }
            //add the row to the tableLayout
            tableLayout.addView(tableRow, new TableLayout.LayoutParams((blockSize * blockPadding) * totalColumns, blockSize * blockPadding));
        }
    }

    // Will cascade blocks adjacent to the one that is touched, until they come near mines
    public void revealFirstBlock(Block[][] blocks, int x, int y) {

        //Set global first click to false to alter click functions
        firstClick = false;

        // Exit for oob
        if (x > totalRows - 1 || x < 0 || y > totalColumns - 1 || y < 0) {
            return;
        }

        // Show the block in question and mark it as revealed
        blocks[x][y].flipBlock();

        // Exit if this block is checked
        if (isBlockVisited[x][y]) {
            return;
        }

        // If this point is reached, set the block corresponding to this index to true
        isBlockVisited[x][y] = true;


        // If block is not a mine and does not border any mines
        if (!blocks[x][y].isMine() && blocks[x][y].getNumberOfAdjacentMines() == 0) {

            // Check in all 8 directions from the current block
            revealFirstBlock(blocks, x - 1, y - 1);
            revealFirstBlock(blocks, x - 1, y);
            revealFirstBlock(blocks, x - 1, y + 1);
            revealFirstBlock(blocks, x, y - 1);
            revealFirstBlock(blocks, x, y);
            revealFirstBlock(blocks, x, y + 1);
            revealFirstBlock(blocks, x + 1, y - 1);
            revealFirstBlock(blocks, x + 1, y);
            revealFirstBlock(blocks, x + 1, y + 1);

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
        int i;
        int j;
        for (i = 0; i < totalRows; i++) {

            // Set j to 0. While it's less than blocks.length do next and ++
            for (j = 0; j < totalColumns; j++) {

                // If the current block is not a mine, do next
                if (!blocks[i][j].isMine()) {

                    // Create a counter for mine count, starting at 0
                    int currentMineCount = 0;

                    // Set p to i-1 and do next until p is == i+1 (Max of 3 times)
                    int p;
                    int q;
                    for (p = i - 1; p <= i + 1; p++) {

                        // Set q to j-1 and do next until q is == j +1 (Max of 3 times)
                        for (q = j - 1; q <= j + 1; q++) {

                            // 4 statements to prevent out of bounds exception
                            if (0 <= p && p < totalRows && 0 <= q && q < totalColumns) {

                                // If block at p/q is a mine, add 1 to currentMineCount
                                if (blocks[p][q].isMine())
                                    ++currentMineCount;
                            }
                        }
                    }

                    // Set block objects values for number of adjacent mines and
                    blocks[i][j].setNumberOfAdjacentMines(currentMineCount);
//                    blocks[i][j].flipBlock();
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

    public void winGame() {
        Toast.makeText(MainActivity.this, "You win", Toast.LENGTH_LONG).show();
    }

    // Game over: Vibrate, stop timer, show all bombs, restart game on another click
    public void loseGame() {

        vibrate(1000);

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

    // This method will add to users total game count or won game count, based on the argument
    // Get the user json string from shared prefs
    // Convert it to a user object to interact with it
    // Convert it back to a jsonString to save it back to shared prefs
    public void incrementGameCount(String countToIncrement) {

        Gson gson = new Gson();
        String currentUserString = sharedPreferences.getString("currentUser", "");
        String fromJsonUser = sharedPreferences.getString("user" + currentUserString, "");

        User currentUserObject = gson.fromJson(fromJsonUser, User.class);

        if (countToIncrement.equals("played")) {
            int gamesPlayed = currentUserObject.getGamesPlayed();
            currentUserObject.setGamesPlayed(gamesPlayed + 1);

        } else if (countToIncrement.equals("won")) {
            int gamesWon = currentUserObject.getGamesWon();
            currentUserObject.setGamesWon(gamesWon + 1);
        }

        String toJsonUser = gson.toJson(currentUserObject);

        editor.putString("user" + currentUserString, toJsonUser);
        editor.commit();
    }


    public void enterNewScore(final double finishTime) {

        final Dialog newScoreDialog = new Dialog(MainActivity.this);
        newScoreDialog.setContentView(R.layout.dialog_new_score);

        AutoCompleteTextView nickNameAC = (AutoCompleteTextView) newScoreDialog.findViewById(R.id.ac_nickname);
        TextView newScoreTV = (TextView) newScoreDialog.findViewById(R.id.tv_enter_score);

        final Editable nickName = nickNameAC.getText();

        if (newScoreTV != null) {
            newScoreTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, nickName + " with a time of " + finishTime, Toast.LENGTH_SHORT).show();
                    newScoreDialog.dismiss();


                }
            });
        }
        newScoreDialog.show();
    }


    public void vibrate(int length) {
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(length);
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


            AlertDialog.Builder difficultyDialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
            difficultyDialog.setTitle("Choose difficulty");
            difficultyDialog.setItems(new CharSequence[]{
                            "Easy:   9 x 9 with 10 mines",
                            "Medium:   9 x 15 with 20 mines",
                            "Hard:   9 x 20 with 40 mines",
                            "Insane:   9 x 100 with 200 mines"},
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
                                    Toast.makeText(MainActivity.this, "Medium: 9 x 15 with 20 mines", Toast.LENGTH_SHORT).show();

                                    Intent mediumGameIntent = getIntent();
                                    mediumGameIntent.putExtra("newGameDifficulty", 2);
                                    finish();
                                    startActivity(mediumGameIntent);
                                    break;

                                case 2:
                                    Toast.makeText(MainActivity.this, "Hard: 9 x 20 with 40 mines", Toast.LENGTH_SHORT).show();

                                    Intent hardGameIntent = getIntent();
                                    hardGameIntent.putExtra("newGameDifficulty", 3);
                                    finish();
                                    startActivity(hardGameIntent);
                                    break;

                                case 3:
                                    Toast.makeText(MainActivity.this, "Insane: 9 x 100 with 200 mine. Your device may have trouble, this is insane after all...", Toast.LENGTH_SHORT).show();

                                    Intent insaneGameIntent = getIntent();
                                    insaneGameIntent.putExtra("newGameDifficulty", 4);
                                    finish();
                                    startActivity(insaneGameIntent);
                                    break;
                            }
                        }
                    });
            difficultyDialog.create().show();
        }

        if (id == R.id.give_up) {

            smileyFaceIV.setImageResource(R.drawable.ic_smiley_sad);

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
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

            Gson gson = new Gson();
            String currentUserString = sharedPreferences.getString("currentUser", "");
            String jsonUser = sharedPreferences.getString("user" + currentUserString, "");

            User currentUserObject = gson.fromJson(jsonUser, User.class);

            String currentUserName = currentUserObject.getName();
            double currentUserBestTime = currentUserObject.getBestTime();
            String currentUserDifficulty = currentUserObject.getDifficulty();

            double currentUserGamesWon = currentUserObject.getGamesWon();
            String gamesWonString = String.format("%.0f", currentUserGamesWon);

            double currentUserGamesPlayed = currentUserObject.getGamesPlayed();
            String gamesPlayedString = String.format("%.0f", currentUserGamesPlayed);

            double currentUserWinRatio = currentUserGamesWon / currentUserGamesPlayed;
            currentUserWinRatio *= 100;

            String winRatioString = String.format("%.0f", currentUserWinRatio);

            if (currentUserGamesPlayed == 0) {
                builder.setMessage("You have not played any games yet.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

            } else {

                builder.setTitle(currentUserName + "'s " + "stats");

                builder.setMessage("Your best time is " + currentUserBestTime + " seconds on " +
                        currentUserDifficulty + " difficulty. You have won " + gamesWonString +
                        " out of " + gamesPlayedString + " which is a win percentage of " + winRatioString + "%" + "!");
                builder.setPositiveButton("Sweet!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }

            builder.show();

            return true;
        }

        if (id == R.id.about) {

            builder.setTitle("Mine Sweeper \nKevin Hodges");
            builder.setMessage("This application was created " +
                    "for a JP Morgan & Chase code assessment. It incorporates all of the tried" +
                    " and true Mine Sweeper game play that you have grown to love, with a new, " +
                    "polished, material design look. I made the decision to go with a " +
                    "consistent 9 column layout for the game board regardless of the game board " +
                    "difficulty chosen. This is not how the typical game is presented, but" +
                    " I felt this allowed much easier mobile oriented/one handed game play which" +
                    " adds to the experience. I hope you enjoy my version of this classic. " +
                    "Please leave some feedback on Google Play. Thanks!");

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

        if (id == R.id.faq)

        {
            Intent faqIntent = new Intent(MainActivity.this, FAQActivity.class);
            startActivity(faqIntent);
            return true;
        }


        return super.

                onOptionsItemSelected(item);

    }

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

    //Getters and Setters
    public int getTotalMines() {
        return totalMines;
    }

    public void setTotalMines(int totalMines) {
        this.totalMines = totalMines;
    }

    public static int getRevealedBlockCount() {
        return revealedBlockCount;
    }

    public static void setRevealedBlockCount(int revealedBlockCount) {
        MainActivity.revealedBlockCount = revealedBlockCount;
    }


    // TODO: 4/23/2016
    //override on Back pressed with "Press again to exit"
    //run method to look at each block and current state of each block
    //create new 2d array
    // for i= 0 to minefield.width
    // for j = 0 to minefield.height
    //iterate thru and store value of each block in the new array
    // if value of block is mine, then store 9
    // if value of block is number, then store number
    // if value is flag and mine, then store 11

    // example 2d array
    // 4    0   11      9
    // 3    1   4       6
    // 11   0   0       0
    // 3    0   1       5

    //Time
    //Mines left
    // Empty is 0. Numbers are 1-8. Mine is -1. Mine with flag is -11. Number with flag = number+10

    //store this array in the exit in the Bundle outstate/savedInstanceState or sharedPrefs
    //when game is loaded, look to see if "savedGame" exists, if so, then load this saved array
    //and assign the minefield conditions according whatever integers are stored by iterating thru
    //saved array
}
