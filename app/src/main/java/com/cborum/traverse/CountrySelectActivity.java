package com.cborum.traverse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Created by ChristopherBorum on 02/12/2016.
 */

public class CountrySelectActivity extends Activity {
    private final String TAG = getClass().getSimpleName();

    private Spinner countrySpinner;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_select);

        countrySpinner = (Spinner) findViewById(R.id.countrySpinner);
        continueButton = (Button) findViewById(R.id.continueButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.country_array, android.R.layout.simple_spinner_item);
        countrySpinner.setAdapter(adapter);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("selectedCountry", countrySpinner.getSelectedItem().toString());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

}
