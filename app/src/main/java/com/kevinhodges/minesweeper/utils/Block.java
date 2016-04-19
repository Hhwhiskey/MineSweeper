package com.kevinhodges.minesweeper.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Kevin on 4/18/2016.
 */
public class Block extends Button{

    private boolean isMine;
    private boolean isHidden;
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



//    public Block() {
//
//        this.isMine = new Random().nextBoolean();
//    }

//    public Block(boolean isHidden, boolean isFlagged, int value, int xAxis, int yAxis) {
//
//        this.isMine = new Random().nextBoolean();
//        this.isHidden = isHidden;
//        this.isFlagged = isFlagged;
//        this.value = value;
//        this.xAxis = xAxis;
//        this.yAxis = yAxis;
//    }

    // Method to get number of mines adjacent to this block and then display that number as the "value"
    public int getValue() {
       return 0;
    }



}
