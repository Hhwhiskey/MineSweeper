package com.kevinhodges.minesweeper.activities;

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
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kevinhodges.minesweeper.R;
import com.kevinhodges.minesweeper.model.Block;
import com.kevinhodges.minesweeper.model.User;
import com.kevinhodges.minesweeper.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private int mNewGameDifficulty;
    private TableLayout mTableLayout;
    private int mTotalRows;
    private int mTotalColumns;
    private static int mTotalMines;
    private Block[][] mBlocks;
    private Handler mTimer;
    private int mSecondsPassed;
    private TextView gameTimerTV;
    private TextView mineCountTV;
    private boolean mFirstClick = true;
    private ImageView smileyFaceIV;
    private Boolean mIsGameOver = false;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private AlertDialog.Builder mBuilder;
    private boolean[][] mIsBlockVisited;
    //    private int ratio;
//    public static int revealedBlockCount;
//    private int mTotalBlocks = mTotalRows * mTotalColumns;
    public static int mCorrectFlagsPlaced;
    private int mInitialScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI Declarations///////////////////////////////////////////////////////////
        // Google suggests the use of the Toolbar in place of the action bar to support older devices.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTableLayout = (TableLayout) findViewById(R.id.table_game_board);
        gameTimerTV = (TextView) findViewById(R.id.tv_game_timer);
        mineCountTV = (TextView) findViewById(R.id.tv_mine_count);
        smileyFaceIV = (ImageView) findViewById(R.id.iv_smiley);
        ImageView clockIV = (ImageView) findViewById(R.id.iv_clock);
        ImageView mineIV = (ImageView) findViewById(R.id.iv_mine);
        ///////////////////////////////////////////////////////////////////////////

        // Global dialog and shared prefs////////////////////////////////////////////////
        mBuilder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mEditor = mSharedPreferences.edit();
        ////////////////////////////////////////////////////////////////////////////////

        Intent getIntent = getIntent();
        mNewGameDifficulty = getIntent.getIntExtra(Constants.NEW_GAME_DIFFICULTY, 0);

        mTimer = new Handler();
        mSecondsPassed = 0;
        mCorrectFlagsPlaced = 0;
        Block.MINE_COUNT = 0;

        //OnClicks//////////////////////////////////////////////////////////////////////////////
        assert mineIV != null;
        mineIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        R.string.mines_toast, Toast.LENGTH_LONG).show();
            }
        });

        assert clockIV != null;
        clockIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.timer_toast, Toast.LENGTH_LONG).show();
            }
        });

        smileyFaceIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smileyFaceIV.setImageResource(R.drawable.ic_smiley_worried);

                restartGameActivity();

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
        switch (mNewGameDifficulty) {
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
                mineCountTV.setText(String.valueOf(80));
                break;
        }

        if (mNewGameDifficulty != 0) {
            createNewGameBoard(mNewGameDifficulty);
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
        int ratio = width / 11;
//        int blockSize = ratio;

        switch (difficulty) {
            case 1:
                mTotalRows = 10;
                mTotalColumns = 10;
                mTotalMines = 10;
                mInitialScore = 10000;
//                mTotalBlocks = mTotalRows * mTotalColumns;
//                mBlockSize = ratio;
                break;

            case 2:
                mTotalRows = 20;
                mTotalColumns = 10;
                mTotalMines = 20;
                mInitialScore = 12500;
//                mTotalBlocks = mTotalRows * mTotalColumns;
//                mBlockSize = ratio;
                break;

            case 3:
                mTotalRows = 40;
                mTotalColumns = 10;
                mTotalMines = 40;
                mInitialScore = 15000;
//                mTotalBlocks = mTotalRows * mTotalColumns;
//                mBlockSize = ratio;
                break;

            case 4:
                mTotalRows = 80;
                mTotalColumns = 10;
                mTotalMines = 160;
                mInitialScore = 17500;
//                mTotalBlocks = mTotalRows * mTotalColumns;
//                mBlockSize = ratio;
                break;
        }

        //setup the blocks array
        mBlocks = new Block[mTotalRows][mTotalColumns];
        // Create a 2d boolean array that matches the size of the game board for our recursive function
        mIsBlockVisited = new boolean[mTotalRows][mTotalColumns];

        //for every row
        for (int row = 0; row < mTotalRows; row++) {

            //create a new table row
            TableRow tableRow = new TableRow(this);
            int blockPadding = 1;
            tableRow.setLayoutParams(new TableRow.LayoutParams((ratio * blockPadding) * mTotalColumns, ratio * blockPadding));

            //for every column
            for (int col = 0; col < mTotalColumns; col++) {
                //create a block
                mBlocks[row][col] = new Block(this);
                //set the block defaults
                mBlocks[row][col].setDefaults();
                //set the width and height of the block
                mBlocks[row][col].setLayoutParams(new TableRow.LayoutParams(ratio * blockPadding, ratio * blockPadding));
                //add some padding to the block
                mBlocks[row][col].setPadding(blockPadding, blockPadding, blockPadding, blockPadding);
                //add the block to the table row
                tableRow.addView(mBlocks[row][col]);

                final int curRow = row;
                final int curCol = col;

                //add a click listener
                mBlocks[row][col].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // If game is over, all clicks will restart game
                        if (mIsGameOver) {

                            restartGameActivity();

                        } else {

                            // If the block is a mine, call lose game
                            if (mBlocks[curRow][curCol].isMine() && !mBlocks[curRow][curCol].isFlagged()) {

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
                            if (mFirstClick) {
                                placeMinesOnBoard(curRow, curCol);
                                revealFirstBlock(mBlocks, curRow, curCol);
                                // Start the game timer on first click
                                startTimer();

                            } else {

                                if (!mBlocks[curRow][curCol].isFlagged()) {
                                    mBlocks[curRow][curCol].flipBlock();
                                }
                            }
                        }
                    }
                });

                //add a long click listener
                mBlocks[row][col].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        // If game is over, all clicks will start new game
                        if (mIsGameOver) {
                            restartGameActivity();

                        } else {

                            // If it's not the first click, allow long presses
                            if (!mFirstClick) {

                                if (!mBlocks[curRow][curCol].isRevealed()) {
                                    // If the block already has a flag, remove it and update MINE_COUNT and textView
                                    if (mBlocks[curRow][curCol].isFlagged()) {
                                        mBlocks[curRow][curCol].removeFlag();
                                        mineCountTV.setText(String.valueOf(++Block.MINE_COUNT));

                                        // Otherwise, plant a flag and update static MINE_COUNT and textView
                                    } else {

                                        if (Block.MINE_COUNT > 0) {

                                            // Give vibration feedback of the flag placement
                                            vibrate(500);

                                            mBlocks[curRow][curCol].plantFlag();
                                            mineCountTV.setText(String.valueOf(--Block.MINE_COUNT));
                                        } else {
                                            Toast.makeText(MainActivity.this, R.string.too_many_flags_toast, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }
                        }

                        // If blocks left = mineCount player wins
                        if (mCorrectFlagsPlaced == mTotalMines) {
                            winGame();
                        }

                        return true;
                    }
                });

            }
            //add the row to the tableLayout
            mTableLayout.addView(tableRow, new TableLayout.LayoutParams((ratio * blockPadding) * mTotalColumns, ratio * blockPadding));
        }
    }

    // Will cascade blocks adjacent to the one that is touched, until they come near mines
    public void revealFirstBlock(Block[][] blocks, int x, int y) {

        //Set global first click to false to alter click functions
        mFirstClick = false;

        // Exit for oob
        if (x > mTotalRows - 1 || x < 0 || y > mTotalColumns - 1 || y < 0) {
            return;
        }

        // Show the block in question and mark it as revealed
        blocks[x][y].flipBlock();

        // Exit if this block is checked
        if (mIsBlockVisited[x][y]) {
            return;
        }

        // If this point is reached, set the block corresponding to this index to true
        mIsBlockVisited[x][y] = true;


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

    // Randomly place mines on the board. Special cases below.
    public void placeMinesOnBoard(int row, int col) {

        int mineRow;
        int mineCol;
        mFirstClick = false;

        Random random = new Random();

        for (int i = 0; i < mTotalMines; i++) {

            // Generate random positions for rows and columns
            mineRow = random.nextInt(mTotalRows);
            mineCol = random.nextInt(mTotalColumns);

            // Check to make sure mine is not placed where the user first clicks subtract mine from total
            if (mineRow == row && mineCol == col) {
                i--;

                // Check to make sure the block is not already a mine, if so, subtract mine from total
            } else if (mBlocks[mineRow][mineCol].isMine()) {
                i--;

            } else {
                // If both tests pass, plant the mine
                mBlocks[mineRow][mineCol].plantMine();
            }
        }

        // Call to method that will count mines and display the correct value on the blocks
        countAdjacentMines();

        // Update the mine count textView
        updateMineCount();
    }

    // Counts each blocks adjacent mines to figure which int to display on that block
    private void countAdjacentMines() {

        // Set i to 0. While it's less than blocks.length do next and ++
        int i;
        int j;
        for (i = 0; i < mTotalRows; i++) {

            // Set j to 0. While it's less than blocks.length do next and ++
            for (j = 0; j < mTotalColumns; j++) {

                // If the current block is not a mine, do next
                if (!mBlocks[i][j].isMine()) {

                    // Create a counter for mine count, starting at 0
                    int currentMineCount = 0;

                    // Look -1, 0 and +1 from the current block(Range of 3 blocks)
                    int q;
                    int r;
                    for (q = i - 1; q <= i + 1; q++) {

                        // Look -1, 0 and +1 from the current block(Range of 3 blocks)
                        for (r = j - 1; r <= j + 1; r++) {

                            // 4 statements to prevent out of bounds exception
                            if (0 <= q && q < mTotalRows && 0 <= r && r < mTotalColumns) {

                                // If block at p/q is a mine, add 1 to currentMineCount
                                if (mBlocks[q][r].isMine())
                                    ++currentMineCount;
                            }
                        }
                    }

                    // Set block objects values for number of adjacent mines and
                    mBlocks[i][j].setNumberOfAdjacentMines(currentMineCount);
                }
            }
        }
    }


    // Starts the game time when the first block is clicked
    public void startTimer() {

        if (mSecondsPassed == 0) {
            mTimer.removeCallbacks(updateTimeElapsed);
            // tell timer to run call back after 1 second
            mTimer.postDelayed(updateTimeElapsed, 1);
        }
    }

    // Resumes the ongoing game timer, called in onResume
    public void resumeTimer() {
        if (mSecondsPassed != 0) {
            mTimer.removeCallbacks(updateTimeElapsed);
            // tell timer to run call back after 1 second
            mTimer.postDelayed(updateTimeElapsed, 1000);
        }
    }

    // Pauses the ongoing game timer, called in onPause
    public void stopTimer() {
        // disable call backs
        mTimer.removeCallbacks(updateTimeElapsed);
    }

    // Runnable to track time and update gameTimerTV textView
    private Runnable updateTimeElapsed = new Runnable() {
        public void run() {
            long currentMilliseconds = System.currentTimeMillis();
            ++mSecondsPassed;
            gameTimerTV.setText(Integer.toString(mSecondsPassed));

            // add notification
            mTimer.postAtTime(this, currentMilliseconds);
            // notify to call back after 1 seconds
            // basically to remain in the timer loop
            mTimer.postDelayed(updateTimeElapsed, 1000);
        }
    };


    public void winGame() {

        stopTimer();
        incrementUserGameCount("won");
        mIsGameOver = true;
        smileyFaceIV.setImageResource(R.drawable.ic_smiley_big);

        final Intent leaderBoardIntent = new Intent(MainActivity.this, LeaderBoardActivity.class);

        // Get currentUser from shared prefs json and create as a User
        Gson gson = new Gson();
        String currentUserString = mSharedPreferences.getString("currentUser", "");
        String fromJsonUser = mSharedPreferences.getString("user" + currentUserString, "");

        User currentUserObject = gson.fromJson(fromJsonUser, User.class);
        int userBestScore = currentUserObject.getHighScore();

        int timeMultiplier = mSecondsPassed * 10;
        int newScore = mInitialScore - timeMultiplier;

        if (newScore > userBestScore) {

            currentUserObject.setHighScore(newScore);
            String toJsonUser = gson.toJson(currentUserObject);
            mEditor.putString("user" + currentUserString, toJsonUser);
            mEditor.commit();

            mBuilder.setTitle("Win!");
            mBuilder.setMessage("You have flagged all the mines and set a new high score with " + newScore + " points! Nice work!");
            mBuilder.setPositiveButton("Play again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    restartGameActivity();
                }
            });

            mBuilder.setNegativeButton("Leader board", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(leaderBoardIntent);
                }
            });

            mBuilder.show();

        } else {

            mBuilder.setTitle("Win!");
            mBuilder.setMessage(getString(R.string.win_message_no_record));
            mBuilder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    restartGameActivity();
                }
            });

            mBuilder.setNegativeButton("Leader Board", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(leaderBoardIntent);
                }
            });

            mBuilder.show();
        }

        updateUserList();
    }

    // Game over: Vibrate, stop timer, show all bombs, restart game on another click
    public void loseGame() {

        vibrate(1000);

        mIsGameOver = true;

        stopTimer();

        incrementUserGameCount("lost");

        for (int i = 0; i < mTotalRows; i++) {

            for (int j = 0; j < mTotalColumns; j++) {

                if (mBlocks[i][j].isMine()) {
                    mBlocks[i][j].showAllMines();
                }
            }
        }

        updateUserList();
    }

    // This method will add to users total game count or won game count, based on the argument
    // Get the user json string from shared prefs
    // Convert it to a user object to interact with it
    // Convert it back to a jsonString to save it back to shared prefs
    public void incrementUserGameCount(String countToIncrement) {

        Gson gson = new Gson();
        String currentUserString = mSharedPreferences.getString("currentUser", "");
        String fromJsonUser = mSharedPreferences.getString("user" + currentUserString, "");

        User currentUserObject = gson.fromJson(fromJsonUser, User.class);

        if (countToIncrement.equals("lost")) {
            int gamesLost = currentUserObject.getGamesLost();
            currentUserObject.setGamesLost(gamesLost + 1);

        } else if (countToIncrement.equals("won")) {
            int gamesWon = currentUserObject.getGamesWon();
            currentUserObject.setGamesWon(gamesWon + 1);
        }

        String toJsonUser = gson.toJson(currentUserObject);
        mEditor.putString("user" + currentUserString, toJsonUser);
        mEditor.commit();
    }

    // This method will update the user list stored in shared prefs
    public void updateUserList() {

        // If there is a currentUserList, get the users from that list and display them
        if (mSharedPreferences.contains(Constants.ALL_USER_LIST)) {

            Gson gson = new Gson();
            List<User> allUserList;

            String userNameString = mSharedPreferences.getString(Constants.CURRENT_USER, "");
            String userJson = mSharedPreferences.getString(Constants.USER + userNameString, "");
            User currentUserObject = gson.fromJson(userJson, User.class);

            String json = mSharedPreferences.getString(Constants.ALL_USER_LIST, "");
            User[] sharedPrefsUserList = gson.fromJson(json, User[].class);
            allUserList = Arrays.asList(sharedPrefsUserList);
            allUserList = new ArrayList<>(allUserList);

            // Delete the old user with the matching name
            User userToRemove = null;
            for (User user : allUserList) {

                if (user.getName().equals(userNameString)) {
                    // Don't delete the user yet - just mark the user for deletion.
                    userToRemove = user;
                }
            }

            // It's now safe to remove the user.
            if (userToRemove != null) {
                allUserList.remove(userToRemove);
            }

            // Get the currentUser properties
            int currentUserHighScore = currentUserObject.getHighScore();
            int currentUserGamesWon = currentUserObject.getGamesWon();
            int currentUserGamesLost = currentUserObject.getGamesLost();

            // Recreate the user with the new properties
            currentUserObject.setHighScore(currentUserHighScore);
            currentUserObject.setGamesWon(currentUserGamesWon);
            currentUserObject.setGamesLost(currentUserGamesLost);

            // Add the user back to the list
            allUserList.add(currentUserObject);

            // Turn userList into jsonString so it can be saved in shared prefs
            String userListJson = gson.toJson(allUserList);
            mEditor.putString(Constants.ALL_USER_LIST, userListJson);
            mEditor.commit();
        }
    }


    // Show new game dialog, and create new game board based on difficulty user chooses
    public void startNewGame() {

        final Intent newGameIntent = getIntent();

        AlertDialog.Builder difficultyDialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
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
                                Toast.makeText(MainActivity.this, R.string.easy_toast, Toast.LENGTH_SHORT).show();
                                newGameIntent.putExtra(Constants.NEW_GAME_DIFFICULTY, 1);

                                break;

                            case 1:
                                Toast.makeText(MainActivity.this, R.string.medium_toast, Toast.LENGTH_SHORT).show();
                                newGameIntent.putExtra(Constants.NEW_GAME_DIFFICULTY, 2);

                                break;

                            case 2:
                                Toast.makeText(MainActivity.this, R.string.hard_toast, Toast.LENGTH_SHORT).show();
                                newGameIntent.putExtra(Constants.NEW_GAME_DIFFICULTY, 3);

                                break;

                            case 3:
                                Toast.makeText(MainActivity.this, R.string.insane_toast, Toast.LENGTH_SHORT).show();
                                newGameIntent.putExtra(Constants.NEW_GAME_DIFFICULTY, 4);

                                break;
                        }

                        finish();
                        startActivity(newGameIntent);
                    }
                });

        difficultyDialog.create().show();
    }

    // Ask the user if they would like to give up, if so, return to title activity
    public void showGiveUpDialog() {

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

            }
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                smileyFaceIV.setImageResource(R.drawable.ic_smiley_happy);
            }
        });

        builder.show();
    }

    // Show the users current stats
    public void showStatsDialog() {

        Gson gson = new Gson();
        String currentUserString = mSharedPreferences.getString(Constants.CURRENT_USER, "");
        String jsonUser = mSharedPreferences.getString(Constants.USER + currentUserString, "");

        User currentUserObject = gson.fromJson(jsonUser, User.class);

        String currentUserName = currentUserObject.getName();

        int currentUserBestScore = currentUserObject.getHighScore();

        double currentUserGamesWon = currentUserObject.getGamesWon();
        String gamesWonString = String.format("%.0f", currentUserGamesWon);

        double currentUserGamesPlayed = currentUserObject.getGamesPlayed();
        String gamesPlayedString = String.format("%.0f", currentUserGamesPlayed);

        double currentUserWinRatio = currentUserGamesWon / currentUserGamesPlayed;
        currentUserWinRatio *= 100;

        String winRatioString = String.format("%.0f", currentUserWinRatio);

        if (currentUserGamesPlayed == 0) {
            mBuilder.setMessage("You have not played any games yet.");
            mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

        } else {

            mBuilder.setTitle(currentUserName + "'s " + "stats");

            mBuilder.setMessage("Your high score is " + currentUserBestScore +
                    "! You have won " + gamesWonString + " of your " + gamesPlayedString +
                    " games; giving you a win percentage of " + winRatioString + "%" + "!");
            mBuilder.setPositiveButton("Sweet!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            mBuilder.setNegativeButton("Leader board", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent leaderBoardIntent = new Intent(MainActivity.this, LeaderBoardActivity.class);
                    startActivity(leaderBoardIntent);
                }
            });
        }

        mBuilder.show();
    }

    // Show about dialog
    public void showAboutDialog() {

        mBuilder.setTitle("Mine Sweeper");
        mBuilder.setMessage(getString(R.string.about_dialog));

        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        mBuilder.setNegativeButton("Leave Feedback", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Not released to Google Play yet", Toast.LENGTH_SHORT).show();
            }
        });

        mBuilder.show();

    }

    // Restart game with same difficulty
    public void restartGameActivity() {

        Intent easyGameIntent = getIntent();
        easyGameIntent.putExtra(Constants.NEW_GAME_DIFFICULTY, mNewGameDifficulty);
        finish();
        startActivity(easyGameIntent);
    }

    // Simple vibrate method
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
            startNewGame();
            return true;
        }

        if (id == R.id.give_up) {
            showGiveUpDialog();
            return true;
        }

        if (id == R.id.stats) {
            showStatsDialog();
            return true;
        }

        if (id == R.id.about) {
            showAboutDialog();
            return true;
        }

        if (id == R.id.faq) {
            Intent faqIntent = new Intent(MainActivity.this, FAQActivity.class);
            startActivity(faqIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mIsGameOver) {
            resumeTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    // Check if there is an on going game, if so, ask the user to verify exit
    @Override
    public void onBackPressed() {

        if (!mFirstClick) {
            mBuilder.setTitle("Leave game");
            mBuilder.setMessage("Game will be lost. Are you sure you would like to leave this game?");
            mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            mBuilder.show();

        } else {
            super.onBackPressed();
        }
    }
}
