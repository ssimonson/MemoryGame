package com.example.ssimonson.memorygame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.InputType;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Manager extends Activity {
    private static int ROW_COUNT = -1;
    private static int COL_COUNT = -1;
    private Context context;
    private Drawable backImage;
    private int[][] cards;
    private List<Drawable> images;
    private Card firstCard;
    private Card secondCard;
    private ButtonListener buttonListener;
    private String theme;

    protected static Object lock = new Object();

    private int currentApiVersion = android.os.Build.VERSION.SDK_INT;

    int turns;
    int pairsMatched = 0;
    int totalPairs = 0;

    private Handler customHandler = new Handler();
    private TextView timerValue;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    long startTime = 0L;

    private TableLayout mainTable;
    private UpdateCardsHandler handler;

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            timerValue.setText(formatTime(updatedTime));
            customHandler.postDelayed(this, 0);
        }
    };

    public static String formatTime(long timeInMilliseconds) {
        int secs = (int) (timeInMilliseconds / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        int milliseconds = (int) (timeInMilliseconds % 1000);

        return "" + mins + ":"
                + String.format("%02d", secs) + ":"
                + String.format("%3d", milliseconds);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        theme = intent.getStringExtra("theme").toLowerCase();
        ROW_COUNT  = intent.getIntExtra("rows",4);
        COL_COUNT = intent.getIntExtra("columns",4);

        handler = new UpdateCardsHandler();
        //loadImages(theme);
        setContentView(R.layout.main);

        TextView url = ((TextView) findViewById(R.id.myWebSite));
        Linkify.addLinks(url, Linkify.WEB_URLS);
        timerValue = (TextView) findViewById(R.id.timerValue);

        backImage = getResources().getDrawable(R.drawable.icon);

        buttonListener = new ButtonListener();

        mainTable = (TableLayout) findViewById(R.id.TableLayout03);

        context = mainTable.getContext();

        newGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_newGame:
                SelectNewGame();
                return true;
            case R.id.action_highScores:
                ShowHighScores();
                return true;
            case R.id.action_settings:
                //openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ShowHighScores() {
        Intent myIntent = new Intent(getBaseContext(), HighScores.class);
        myIntent.putExtra("size", "4 x 4"); //Optional parameters
        myIntent.putExtra("type", "turns"); //Optional parameters
        startActivity(myIntent);
    }

    private void SelectNewGame() {
        Intent intent = new Intent(getBaseContext(),newGame.class);
        startActivity(intent);
    }

    private void newGame() {

        cards = new int[COL_COUNT][ROW_COUNT];

        mainTable.removeView(findViewById(R.id.TableRow01));
        mainTable.removeView(findViewById(R.id.TableRow02));

        TableRow tr = ((TableRow) findViewById(R.id.TableRow03));
        tr.removeAllViews();

        mainTable = new TableLayout(context);
        tr.addView(mainTable);

        for (int y = 0; y < ROW_COUNT; y++) {
            mainTable.addView(createRow(y));
        }

        turns=0;
        pairsMatched = 0;
        totalPairs = 0;
        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
        firstCard = null;

        loadCards();

        ((TextView) findViewById(R.id.tv1)).setText("Tries: " + turns);
        ((TextView) findViewById(R.id.timerValue)).setText("00:00:00");
    }

    private void gameOver() {
        ((TextView) findViewById(R.id.tv1)).setText("You completed the " + COL_COUNT + " x " + ROW_COUNT + " '" + theme + "' game in " + formatTime(updatedTime) + " and " + Integer.toString(turns) + " turns");
        ((TextView) findViewById(R.id.timerValue)).setText("");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        new AlertDialog.Builder(Manager.this)
                .setMessage("Enter your name")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String name = input.getText().toString();
                        saveScore(new Score(name, "" + COL_COUNT + " X " + ROW_COUNT, theme, (int) updatedTime, turns));
                    }
                }).show();
    }

    private void loadImages(ArrayList<Integer> randomCards) {
        images = new ArrayList<Drawable>();

        for(int i = 0; i < randomCards.size(); i++)
        {
            images.add(getResources().getDrawable(getResources().getIdentifier(theme + "_card_" + randomCards.get(i),"drawable",getPackageName())));
        }
    }

    private void loadCards() {

        try {
            int size = ROW_COUNT * COL_COUNT;
            int cardSize = getResources().getInteger(R.integer.defaultCardsCount);
            totalPairs = size / 2;

            int randomNum = 0;

            Log.i("loadCards()", "size=" + size);

            ArrayList<Integer> listofAvaiableCards = new ArrayList<Integer>();

            ArrayList<Integer> allCards = new ArrayList<Integer>();
            ArrayList<Integer> randomCards = new ArrayList<Integer>();

            for (int i = 1; i <= cardSize; i++) {
                allCards.add(i);
            }

            Random r = new Random();

            for (int i = 0; i < size / 2; i++) {
                randomNum = r.nextInt(allCards.size());

                randomCards.add(allCards.remove(randomNum));
            }

            for (int i = 0; i < size; i++) {
                listofAvaiableCards.add(i);
            }

            for (int i = size - 1; i >= 0; i--) {
                int t = 0;

                if (i > 0) {
                    t = r.nextInt(i);
                }

                t = listofAvaiableCards.remove(t);

                int randomCardsIndex = randomCards.indexOf(randomCards.get(t%(size/2)));

                cards[i % COL_COUNT][i / COL_COUNT] = randomCardsIndex;

                Log.i("loadCards()", "card[" + (i % COL_COUNT) +
                        "][" + (i / COL_COUNT) + "]=" + cards[i % COL_COUNT][i / COL_COUNT]);
            }

            loadImages(randomCards);
        } catch (Exception e) {
            Log.e("loadCards()", e + "");
        }
    }

    private TableRow createRow(int y) {
        TableRow row = new TableRow(context);
        row.setHorizontalGravity(Gravity.CENTER);

        for (int x = 0; x < COL_COUNT; x++) {
            row.addView(createImageButton(x, y));
        }
        return row;
    }

    private View createImageButton(int x, int y) {
        Button button = new Button(context);
        if (currentApiVersion < 16) {
            button.setBackgroundDrawable(backImage);
        } else {
            button.setBackground(backImage);
        }
        button.setId(100 * x + y);
        button.setOnClickListener(buttonListener);
        return button;
    }

    private void saveScore(Score score) {
        DatabaseHandler db = new DatabaseHandler(this);
        db.addScore(score);
    }

    class ButtonListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            synchronized (lock) {
                if (firstCard != null && secondCard != null) {
                    return;
                }
                int id = v.getId();
                int x = id / 100;
                int y = id % 100;
                turnCard((Button) v, x, y);
                if(firstCard!=null && startTime == 0)
                {
                    findViewById(R.id.timerValue).setVisibility(View.VISIBLE);
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread,0);
                }
            }
        }

        private void turnCard(Button button, int x, int y) {
            if (currentApiVersion < 16) {
                button.setBackgroundDrawable(images.get(cards[x][y]));
            } else {
                button.setBackground(images.get(cards[x][y]));
            }

            if (firstCard == null) {
                firstCard = new Card(button, x, y);
            } else {

                if (firstCard.x == x && firstCard.y == y) {
                    return; //the user pressed the same card
                }

                secondCard = new Card(button, x, y);

                turns++;
                ((TextView) findViewById(R.id.tv1)).setText("Tries: " + turns);

                TimerTask tt = new TimerTask() {

                    @Override
                    public void run() {
                        try {
                            synchronized (lock) {
                                handler.sendEmptyMessage(0);
                            }
                        } catch (Exception e) {
                            Log.e("E1", e.getMessage());
                        }
                    }
                };

                Timer t = new Timer(false);
                t.schedule(tt, 1300);
            }
        }
    }

    class UpdateCardsHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            synchronized (lock) {
                checkCards();
            }
        }

        public void checkCards() {
            if (cards[secondCard.x][secondCard.y] == cards[firstCard.x][firstCard.y]) {
                firstCard.button.setVisibility(View.INVISIBLE);
                secondCard.button.setVisibility(View.INVISIBLE);
                pairsMatched++;
                if (pairsMatched == totalPairs) {
                    customHandler.removeCallbacks(updateTimerThread);
                    gameOver();
                }
            } else {
                if (currentApiVersion < 16) {
                    secondCard.button.setBackgroundDrawable(backImage);
                    firstCard.button.setBackgroundDrawable(backImage);
                } else {
                    secondCard.button.setBackground(backImage);
                    firstCard.button.setBackground(backImage);
                }
            }

            firstCard = null;
            secondCard = null;
        }
    }
}
