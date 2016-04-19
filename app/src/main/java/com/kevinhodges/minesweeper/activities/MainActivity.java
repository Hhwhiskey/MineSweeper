package com.kevinhodges.minesweeper.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.kevinhodges.minesweeper.R;
import com.kevinhodges.minesweeper.utils.Block;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private int newGameDifficulty;
    private TableLayout tableLayout;
    private int totalRows;
    private int totalColumns;
    private TableRow tr;
    private Block[][] tiles;
    private int row;
    private int col;
    private Block[][] blocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent getIntent = getIntent();
        newGameDifficulty = getIntent.getIntExtra("newGameDifficulty", 0);

        //UI Declarations///////////////////////////////////////////////////////////

        // Google suggests the use of the Toolbar in place of the action bar to support older devices.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tableLayout = (TableLayout) findViewById(R.id.table_game_board);
        ///////////////////////////////////////////////////////////////////////////


        if (newGameDifficulty != 0) {

            switch (newGameDifficulty) {
                case 1:
//                    totalRows = 9;
//                    totalColumns = 9;

                    blocks = new Block[9][9];
                    break;

                case 2:
//                    totalRows = 12;
//                    totalColumns = 12;

                    blocks = new Block[12][12];
                    break;

                case 3:
//                    totalRows = 15;
//                    totalColumns = 15;

                    blocks = new Block[15][15];
                    break;
            }

            createNewGameBoard(blocks);
        }


    }

    // TODO: 4/18/2016 Finish creation of the game board
    // Creates a new board based on the difficulty the user has chosen
    // Easy = 9*9 with 10 bombs
    // Medium = 12*12 with 20 bombs
    // Hard = 18*18 with 40 bombs
    public void createNewGameBoard(Block[][] blocks) {

        for (int row = 1; row < totalRows; row++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(8, 8));

            for (int column = 1; column < totalColumns; column++) {
                blocks[row][column].setLayoutParams(new TableLayout.LayoutParams(8, 8));
                blocks[row][column].setPadding(2, 2, 2, 2);
                tableRow.addView(blocks[row][column]);
            }

            tableLayout.addView(tableRow, new TableLayout.LayoutParams((8) * totalColumns, 8));
        }
    }


    public TableLayout getTable() {
        TableLayout tableLayout = new TableLayout(this);
        return tableLayout;
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
                    difficultyDialog.setItems(new CharSequence[]{"Easy", "Medium", "Hard"},
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    switch (which) {

                                        case 0:

//                                            blocks = new Block[9][9];
//                                            createNewGameBoard(blocks);
                                            Toast.makeText(MainActivity.this, "Easy", Toast.LENGTH_SHORT).show();
//                                            Intent easyGameIntent = new Intent(MainActivity.this, MainActivity.class);
//                                            easyGameIntent.putExtra("newGameDifficulty", 0);

                                            Intent intent = getIntent();
                                            intent.putExtra("newGameDifficulty", 1);
                                            finish();
                                            startActivity(intent);
                                            break;

                                        case 1:
//                                            blocks = new Block[12][12];
//                                            createNewGameBoard(blocks);
                                            Toast.makeText(MainActivity.this, "Medium", Toast.LENGTH_SHORT).show();
                                            Intent mediumGameIntent = new Intent(MainActivity.this, MainActivity.class);
                                            mediumGameIntent.putExtra("newGameDifficulty", 1);
                                            break;

                                        case 2:
//                                            blocks = new Block[15][15];
//                                            createNewGameBoard(blocks);
                                            Toast.makeText(MainActivity.this, "Hard", Toast.LENGTH_SHORT).show();
                                            Intent hardGameIntent = new Intent(MainActivity.this, MainActivity.class);
                                            hardGameIntent.putExtra("newGameDifficulty", 2);
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
            return true;
        }

        if (id == R.id.stats) {
            return true;
        }

        if (id == R.id.about) {
            return true;
        }

        if (id == R.id.faq) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
