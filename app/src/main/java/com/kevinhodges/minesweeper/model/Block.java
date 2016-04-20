package com.kevinhodges.minesweeper.model;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.kevinhodges.minesweeper.R;

/**
 * Created by Kevin on 4/18/2016.
 */
public class Block extends Button {

    public static int MINE_COUNT = 0;
    private boolean isMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private int value;
    private int xAxis;
    private int yAxis;

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
        value = 0;

        this.setBackgroundResource(R.drawable.covered_block);
    }

    public void plantMine() {
        isMine = true;

//        this.setBackgroundResource(R.drawable.ic_bomb_black);

        MINE_COUNT++;
    }

    public void plantFlag() {
        isFlagged = true;

        this.setBackgroundResource(R.drawable.ic_flag);
    }

    public void removeFlag() {
        isFlagged = false;

        this.setBackgroundResource(R.drawable.covered_block);
    }

    public void flipBlock() {

        isRevealed = true;

        if (isMine) {
            this.setBackgroundResource(R.drawable.ic_explosion);
        } else {
            this.setBackgroundResource(R.drawable.uncovered_block);
        }
    }

    public void showAllMines() {

        if (isMine()) {
            this.setBackgroundResource(R.drawable.ic_bomb_black);
        }
    }

    // Returns mine status
    public boolean isMine() {
        return isMine;
    }

    // Returns hidden status
    public boolean isRevealed() {
        return isRevealed;
    }

    // Returns flagged status
    public boolean isFlagged() {
        return isFlagged;
    }

    // Returns x axis
    public int getXAxis() {
        return xAxis;
    }

    // Returns y axis
    public int getYAxis() {
        return yAxis;
    }

    // Returns value of this block, according to the amount of bombs adjacent to it
    public int getValue() {
        return 0;
    }


}
