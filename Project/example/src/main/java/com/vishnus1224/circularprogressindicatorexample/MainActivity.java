package com.vishnus1224.circularprogressindicatorexample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.vishnus1224.circularprogressindicator.CircularProgressIndicator;

public class MainActivity extends ActionBarActivity {

    private CircularProgressIndicator circularProgressIndicator;

    private Button increaseProgressButton;
    private Button decreaseProgressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circularProgressIndicator = (CircularProgressIndicator) findViewById(R.id.circularProgress);

        increaseProgressButton = (Button) findViewById(R.id.increaseProgressButton);
        decreaseProgressButton = (Button) findViewById(R.id.decreaseProgressButton);

        increaseProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circularProgressIndicator.animateProgress(1000, 95);
            }
        });

        decreaseProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circularProgressIndicator.animateProgress(1000, 20);
            }
        });
    }

}
