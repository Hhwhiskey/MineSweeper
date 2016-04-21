package com.kevinhodges.minesweeper.model;

/**
 * Created by Kevin on 4/20/2016.
 */
public class User {

    private String name;
    private double bestTime = 0;
    private int gamesWon = 0;
    private int gamesPlayed = 0;


    public User(String name, double bestTime, int gamesWon, int gamesPlayed) {
        this.name = name;
        this.bestTime = bestTime;
        this.gamesWon = gamesWon;
        this.gamesPlayed = gamesPlayed;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBestTime() {
        return bestTime;
    }

    public void setBestTime(double bestTime) {
        this.bestTime = bestTime;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

}
