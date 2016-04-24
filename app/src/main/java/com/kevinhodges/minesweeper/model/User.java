package com.kevinhodges.minesweeper.model;

/**
 * Created by Kevin on 4/20/2016.
 */
public class User {

    private String name;
    private int highScore = 0;
    private int gamesWon = 0;
    private int gamesLost = 0;
    private int gamesPlayed = 0;


    public User(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public int getGamesLost() {
        return gamesLost;
    }

    public void setGamesLost(int gamesLost) {
        this.gamesLost = gamesLost;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getGamesPlayed() {
        return gamesWon + gamesLost;
    }
}
