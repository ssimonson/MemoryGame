package com.example.ssimonson.memorygame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Spinner;

public class newGame extends Activity {

    public int x = 0;
    public int y = 0;
    public String theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_game);

        Spinner type = (Spinner) findViewById(R.id.Spinner01);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(
                    android.widget.AdapterView<?> arg0,
                    View arg1, int pos, long arg3) {

                switch (pos) {
                    case 1:
                        x = 4;
                        y = 4;
                        break;
                    case 2:
                        x = 4;
                        y = 5;
                        break;
                    case 3:
                        x = 4;
                        y = 6;
                        break;
                    case 4:
                        x = 5;
                        y = 6;
                        break;
                    case 5:
                        x = 6;
                        y = 6;
                        break;
                    default:
                        return;
                }
        }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        Spinner theme = (Spinner) findViewById(R.id.Spinner02);
        ArrayAdapter themeAdapter = ArrayAdapter.createFromResource(this, R.array.theme, android.R.layout.simple_spinner_item);
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        theme.setAdapter(themeAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
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

    public void startGame(View view)
    {
        Spinner themeSpinner = (Spinner)findViewById(R.id.Spinner02);
        theme = themeSpinner.getSelectedItem().toString();

        Intent myIntent = new Intent(getBaseContext(), Manager.class);
        myIntent.putExtra("rows", x); //new game row
        myIntent.putExtra("columns", y); //new game column
        myIntent.putExtra("theme",theme); //new games card theme
        startActivity(myIntent);
    }
}
