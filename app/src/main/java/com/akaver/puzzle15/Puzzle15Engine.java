package com.akaver.puzzle15;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by akaver on 27/02/16.
 */
public class Puzzle15Engine {

    private static final String TAG = "Puzzle15Engine";

    //y,x
    private int[][] gameBoard = new int[4][4];
    private int moveCount;

    public Puzzle15Engine() {
        randomizeBoard();
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public void makeMove(int x, int y) {
        //find 16
        int posx16 = -1;
        int posy16 = -1;

        Log.d(TAG, "Move started for x:" + x + " y:" + y);

        for (int tx = 0; tx <= 3; tx++) {
            for (int ty = 0; ty <= 3; ty++) {
                if (gameBoard[ty][tx] == 16) {
                    posx16 = tx;
                    posy16 = ty;
                    Log.d(TAG, "Found 16. x:" + posx16 + " y:" + posy16);
                }
            }
        }
        //are x and y valid
        if (x == posx16 || y == posy16) {
            if (x == posx16 && y == posy16) {
                Log.d(TAG, "Nothing to move!");
                return;
            }
            Log.d(TAG, "Move is legal!");
        } else {
            Log.d(TAG, "Move is NOT legal!");
            return;
        }

        //move tiles, count moves
        if (x == posx16) {
            if (posy16 < y) {
                for (int ty = posy16; ty <= y - 1; ty++) {
                    gameBoard[ty][x] = gameBoard[ty + 1][x];
                    moveCount++;
                }
            } else {
                for (int ty = posy16; ty >= y + 1; ty--) {
                    gameBoard[ty][x] = gameBoard[ty - 1][x];
                    moveCount++;
                }
            }
        } else {
            if (posx16 < x) {
                for (int tx = posx16; tx <= x - 1; tx++) {
                    gameBoard[y][tx] = gameBoard[y][tx + 1];
                    moveCount++;
                }
            } else {
                for (int tx = posx16; tx >= x + 1; tx--) {
                    gameBoard[y][tx] = gameBoard[y][tx - 1];
                    moveCount++;
                }
            }

        }
        gameBoard[y][x] = 16;


        return;
    }

    public int[][] getBoard() {
        return gameBoard;
    }

    public void setBoard(int[][] gameBoard){
        this.gameBoard = gameBoard;
    }

    public void randomizeBoard() {
        List<Integer> items = new ArrayList<Integer>();
        Random random = new Random();

        for (int itemNo = 1; itemNo <= 16; itemNo++) {
            items.add(itemNo);
        }

        for (int x = 0; x <= 3; x++) {
            for (int y = 0; y <= 3; y++) {
                int itemNo = random.nextInt(items.size());
                gameBoard[y][x] = items.get(itemNo);
                items.remove(itemNo);
            }
        }

        moveCount = 0;

        return;
    }

    public boolean isGameOver(){
        for (int x = 0; x <= 3; x++) {
            for (int y = 0; y <= 3; y++) {
                if (gameBoard[y][x] != (y*4)+x+1) return false;
            }
        }

        return true;
    }



}
