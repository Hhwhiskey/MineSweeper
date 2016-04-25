package com.kevinhodges.minesweeper.utils;

import com.kevinhodges.minesweeper.model.User;

import java.util.Comparator;

/**
 * Created by Kevin on 4/25/2016.
 */
public class CustomComparator implements Comparator<User> {
    @Override
    public int compare(User user1, User user2) {
        return  user2.getHighScore() - user1.getHighScore();
    }
}
