//package com.kevinhodges.minesweeper.utils;
//
//import java.util.Random;
//
///**
// * Created by Kevin on 4/18/2016.
// */
//public class Generator {
//
//    public static int [][] generate(int bombNumber, final int width, final int height) {
//
//        Random rand = new Random();
//
//        int[][] grid = new int[width][height];
//        for (int x = 0; x < width; x++) {
//            grid[x] = new int[height];
//        }
//
//        while (bombNumber > 0) {
//            int x = rand.nextInt(width);
//            int y = rand.nextInt(height);
//
//
//            if (grid[x][y] != -1) {
//                grid[x][y] = -1;
//                bombNumber--;
//            }
//        }
//    }
//
//    private static int[][] calculateAdjacentBombs(int[][] grid, final int width, final int height) {
//
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                grid[x][y] = getAdjacentNumber(grid, x, y, width, height);
//            }
//        }
//    }
//
//    private static int getAdjacentNumber(int[][] grid, int x, int y, int width, int height) {
//        return 0;
//    }
//
//
//}
