package com.example.ssimonson.memorygame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;


public class HighScores extends Activity {

    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        db = new DatabaseHandler(this);
        loadHighScores();
    }

    public void loadHighScores() {

        LinearLayout highScoreList = ((LinearLayout) findViewById(R.id.highScoreList));
        highScoreList.removeAllViews();

        String[] sizes = getResources().getStringArray(R.array.type);

        for (int s = 0; s < sizes.length; s++) {
            if(sizes[s].equals("New Game")) {
                continue;
            }
            List<Score> scores = db.getAllScores(sizes[s], "tries");

            TextView tv = new TextView(this);
            tv.setText(sizes[s]);
            tv.setTextSize(15);
            tv.setGravity(Gravity.CENTER);
            highScoreList.addView(tv);

            TableLayout tl = new TableLayout(this);
            TableLayout.LayoutParams tllp = new TableLayout.LayoutParams();
            tllp.gravity=Gravity.CENTER_HORIZONTAL;
            tllp.width=TableLayout.LayoutParams.WRAP_CONTENT;
            tllp.bottomMargin=10;

            tl.setLayoutParams(tllp);
            tl.addView(createHighScoreRow("Name", "Size", "Turns", "Time"));

            for (int i = 0; i < scores.size(); i++) {
                Score score = scores.get(i);
                tl.addView(createHighScoreRow(score.getName(), score.getSize(), score.getTries(), score.getTime()));
            }
            highScoreList.addView(tl);
        }
    }

    private TableRow createHighScoreRow(String name, String size, int tries, int time) {
        return createHighScoreRow(name, size, Integer.toString(tries), Manager.formatTime(time));
    }

    private TableRow createHighScoreRow(String name, String size, String tries, String time) {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView rname = new TextView(this);
        rname.setText(name);
        rname.setPadding(5, 5, 5, 5);
        row.addView(rname);

        TextView rsize = new TextView(this);
        rsize.setText(size);
        rsize.setPadding(5, 5, 5, 5);
        row.addView(rsize);

        TextView rtries = new TextView(this);
        rtries.setText(tries);
        rtries.setPadding(5, 5, 5, 5);
        row.addView(rtries);

        TextView rtime = new TextView(this);
        rtime.setText(time);
        rtime.setPadding(5, 5, 5, 5);
        row.addView(rtime);

        return row;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.high_scores, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_reset) {
            resetHighScores();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetHighScores() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.reset_high_scores_confirmation_title)
                .setMessage(R.string.reset_high_scores_confirmation)
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();

    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    db.deleteAllScores();
                    loadHighScores();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

}
