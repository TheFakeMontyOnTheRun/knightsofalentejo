package br.odb.menu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import br.odb.knights.R;

public class ShowCreditsActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credits_layout);
    }
}