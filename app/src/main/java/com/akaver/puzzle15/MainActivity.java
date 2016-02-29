package com.akaver.puzzle15;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
//icon: https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/15-puzzle.svg/800px-15-puzzle.svg.png
//http://iconhandbook.co.uk/reference/chart/android/

    private static final String TAG = "MainActivity";

    static final String STATE_MOVES = "playerMoves";
    static final String STATE_TIME = "playerTime";

    private Timer gameTimer = new Timer();

    private Puzzle15Engine board = new Puzzle15Engine();

    private TextView textViewMoves;
    private TextView textViewTime;
    private int timeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textViewMoves = (TextView) findViewById(R.id.textViewMoves);
        textViewTime = (TextView) findViewById(R.id.textViewTime);

        //restore state or initial start
        if (savedInstanceState != null) {

            int gameBoard[][] = board.getBoard();

            for (int y = 0; y <= 3; y++) {
                gameBoard[y] = savedInstanceState.getIntArray("BoardRow" + y);
            }

            board.setBoard(gameBoard);


            board.setMoveCount(savedInstanceState.getInt(STATE_MOVES));

            timeCount = savedInstanceState.getInt(STATE_TIME);
            textViewTime.setText("" + timeCount);

            if (timeCount < 999) {
                startGameTimer();
            }
            drawCurrentGameBoard();
        } else {
            restartGame();
        }

    }

    public void drawCurrentGameBoard() {
        int gameBoard[][] = board.getBoard();
        for (int x = 0; x <= 3; x++) {
            for (int y = 0; y <= 3; y++) {
                int item = gameBoard[x][y];
                if (BuildConfig.DEBUG) {
                    //Log.d(TAG, "X:" + x + " Y:" + y + " Val:" + item);
                }

                int buttonId = this.getResources().getIdentifier("button" + x + y, "id", this.getPackageName());
                Button b = (Button) findViewById(buttonId);
                if (b != null) {
                    if (item < 16) {
                        b.setText(Integer.toString(item));
                    } else {
                        b.setText("");
                    }
                } else {
                    Log.e(TAG, "Got null ref for button");
                }
            }
        }

        //update move count
        textViewMoves.setText(Integer.toString(board.getMoveCount()));
    }

    public void buttonClicked(View view) {
        Button btn = (Button) view;

        String idAsString = btn.getResources().getResourceName(btn.getId());
        Log.d(TAG, "Button clicked: " + idAsString);

        int x = Integer.parseInt(idAsString.substring(idAsString.length() - 1, idAsString.length() - 0));
        int y = Integer.parseInt(idAsString.substring(idAsString.length() - 2, idAsString.length() - 1));
        Log.d(TAG, "x: " + x + " y:" + y);

        //move
        board.makeMove(x, y);
        //draw it
        drawCurrentGameBoard();

        //puzzle is done
        if (board.isGameOver()) {
            gameTimer.cancel();
            GameOverDialog();
//            Toast.makeText(MainActivity.this, "Puzzle solved!",
//                    Toast.LENGTH_LONG).show();
        }
    }


    public void GameOverDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Puzzle solved!\nTime: " + timeCount + " Moves:" + board.getMoveCount() + " Score:" + (timeCount * board.getMoveCount()));

        alertDialogBuilder.setPositiveButton("Start new puzzle!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(MainActivity.this, "Starting new puzzle...", Toast.LENGTH_LONG).show();
                restartGame();
            }
        });

        alertDialogBuilder.setNegativeButton("Exit game.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exitApplication();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void restartGame() {
        //restart game
        board.randomizeBoard();
        drawCurrentGameBoard();
        timeCount = 0;
        textViewTime.setText("" + timeCount);

        startGameTimer();
    }

    public void startGameTimer() {
        gameTimer.cancel();

        gameTimer = new Timer();

        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        textViewTime.setText("" + timeCount);
                        if (timeCount >= 999) {
                            gameTimer.cancel();
                        }
                        timeCount++;
                    }
                });
            }
        }, 1000, 1000);
    }

    public void exitApplication(){
        //exit
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_game_restart) {
            restartGame();
            return true;
        }

        if (id==R.id.action_game_exit){
            GameOverDialog();
            //exitApplication();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putInt(STATE_MOVES, board.getMoveCount());
        savedInstanceState.putInt(STATE_TIME, timeCount);

        int gameBoard[][] = board.getBoard();

        for (int y = 0; y <= 3; y++) {
            savedInstanceState.putIntArray("BoardRow" + y, gameBoard[y]);
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        Log.d(TAG, "onPause");
        //gameTimer.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        Log.d(TAG, "onResume");
        startGameTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
        Log.d(TAG, "onStop");
        gameTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        Log.d(TAG, "onDestroy");
    }
}
