package de.bxservice.bxpos.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.ui.dialog.DatePickerFragment;

public class ReportsActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {

    private Button  fromButton, toButton;
    private boolean isFromDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fromButton = (Button) findViewById(R.id.from_button);
        toButton = (Button) findViewById(R.id.to_button);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        setFromButtonText(year, month, day);
        setToButtonText(year, month, day);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.query_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * On Button listener defined in the xml file
     * @param view
     */
    public void onButtonClick(View view) {
        switch (view.getId()) {

            case R.id.from_button:
                isFromDate = true;
                showDatePickerDialog();
                break;

            case R.id.to_button:
                isFromDate = false;
                showDatePickerDialog();
                break;
        }
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user

        if (isFromDate)
            setFromButtonText(year, month, day);
        else
            setToButtonText(year, month, day);

    }

    private void setFromButtonText(int year, int month, int day) {
        StringBuilder date = new StringBuilder().append(day).append(".").append(month).append(".").append(year);
        fromButton.setText(getString(R.string.from_date, date));
    }

    private void setToButtonText(int year, int month, int day) {
        StringBuilder date = new StringBuilder().append(day).append(".").append(month).append(".").append(year);
        toButton.setText(getString(R.string.to_date, date));
    }

}
