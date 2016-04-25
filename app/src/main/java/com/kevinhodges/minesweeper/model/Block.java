package com.kevinhodges.minesweeper.model;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.kevinhodges.minesweeper.R;
import com.kevinhodges.minesweeper.activities.MainActivity;

/**
 * Created by Kevin on 4/18/2016.
 */
public class Block extends Button {

    private static final String TAG = "Block";
    public static int MINE_COUNT = 0;
    private boolean isMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private int numberOfAdjacentMines;

    public Block(Context context) {
        super(context);
    }

    public Block(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Block(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDefaults() {
        isMine = false;
        isFlagged = false;
        isRevealed = false;
        numberOfAdjacentMines = 0;

        this.setBackgroundResource(R.drawable.covered_block);
    }

    public void plantMine() {
        isMine = true;

//        this.setBackgroundResource(R.drawable.ic_bomb_black);

        MINE_COUNT++;
    }

    public void plantFlag() {
        isFlagged = true;

        if (isMine()) {
            MainActivity.mCorrectFlagsPlaced++;
        }

        this.setBackgroundResource(R.drawable.ic_flag);
    }

    public void removeFlag() {
        isFlagged = false;

        if (isMine()) {
            MainActivity.mCorrectFlagsPlaced--;
        }

        this.setBackgroundResource(R.drawable.covered_block);
    }

    public void flipBlock() {

        if (isMine) {
            this.setBackgroundResource(R.drawable.ic_explosion);

        } else if (numberOfAdjacentMines > 0) {
            showAdjacentMineCount(numberOfAdjacentMines);
            isRevealed = true;

        } else {
            this.setBackgroundResource(R.drawable.uncovered_block);
            isRevealed = true;
        }
    }

    public void showAllMines() {

        if (isMine()) {
            this.setBackgroundResource(R.drawable.ic_bomb_black);
        }
    }

    public void showAdjacentMineCount(int mines) {

        isRevealed = true;

       switch(mines) {
           case 1:
               this.setBackgroundResource(R.drawable.number_1);
               break;

           case 2:
               this.setBackgroundResource(R.drawable.number_2);

               break;

           case 3:
               this.setBackgroundResource(R.drawable.number_3);

               break;

           case 4:
               this.setBackgroundResource(R.drawable.number_4);
               break;

           case 5:
               this.setBackgroundResource(R.drawable.number_5);
               break;

           case 6:
               this.setBackgroundResource(R.drawable.number_6);
               break;

           case 7:
               this.setBackgroundResource(R.drawable.number_7);
               break;

           case 8:
               this.setBackgroundResource(R.drawable.number_8);
               break;

           default:

       }
    }

    public int getNumberOfAdjacentMines() {
        return numberOfAdjacentMines;
    }

    public void setNumberOfAdjacentMines(int numOfMines) {
        this.numberOfAdjacentMines = numOfMines;
    }

    // Returns mine status
    public boolean isMine() {
        return isMine;
    }

    // Returns hidden status
    public boolean isRevealed() {
        return isRevealed;
    }

    public void setRevealed(boolean revealed) {
        isRevealed = revealed;
    }

    // Returns flagged status
    public boolean isFlagged() {
        return isFlagged;
    }

    // Returns value of this block, according to the amount of bombs adjacent to it
    public int getValue() {
        return 0;
    }



}
