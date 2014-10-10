package com.example.ssimonson.memorygame;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;


public class HighScores extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);
        TableLayout highScores = ((TableLayout)findViewById(R.id.highScoreTable));

        loadHighScores();
    }

    public void loadHighScores() {
        DatabaseHandler db = new DatabaseHandler(this);
        List<Score> scores = db.getAllScores();

        TableLayout highScoreTable = (TableLayout) findViewById(R.id.highScoreTable);

        highScoreTable.removeAllViews();

        TableRow header = new TableRow(this);
        TableRow.LayoutParams headerLP = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        header.setLayoutParams(headerLP);

        TextView headerName = new TextView(this);
        headerName.setText("Name");
        headerName.setPadding(5, 5, 5, 5);
        header.addView(headerName);

        TextView headerSize = new TextView(this);
        headerSize.setText("Size");
        headerSize.setPadding(5, 5, 5, 5);
        header.addView(headerSize);

        TextView headerTurns = new TextView(this);
        headerTurns.setText("Turns");
        headerTurns.setPadding(5, 5, 5, 5);
        header.addView(headerTurns);

        TextView headerTime = new TextView(this);
        headerTime.setText("Time");
        headerTime.setPadding(5, 5, 5, 5);
        header.addView(headerTime);

        highScoreTable.addView(header);


        for (int i = 0; i < scores.size(); i++) {
            Score score = scores.get(i);

            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView name = new TextView(this);
            name.setText(score.getName());
            name.setPadding(5, 5, 5, 5);
            row.addView(name);

            TextView size = new TextView(this);
            size.setText(score.getSize());
            size.setPadding(5, 5, 5, 5);
            row.addView(size);

            TextView turns = new TextView(this);
            turns.setText(Integer.toString(score.getTries()));
            turns.setPadding(5, 5, 5, 5);
            row.addView(turns);

            TextView time = new TextView(this);
            time.setText(Manager.formatTime(score.getTime()));
            time.setPadding(5, 5, 5, 5);
            row.addView(time);

            highScoreTable.addView(row);
        }
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
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //builder.setMessage(R.string.reset_high_scores_confirmation)
        //        .setTitle(R.string.reset_high_scores_confirmation_title);

        //AlertDialog dialog = builder.create();

        DatabaseHandler db = new DatabaseHandler(this);
        db.deleteAllScores();
        loadHighScores();
    }
}
