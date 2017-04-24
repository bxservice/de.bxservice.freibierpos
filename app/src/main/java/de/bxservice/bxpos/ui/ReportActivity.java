/**********************************************************************
 * This file is part of FreiBier POS                                   *
 *                                                                     *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Diego Ruiz - Bx Service GmbH                                      *
 **********************************************************************/
package de.bxservice.bxpos.ui;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;
import java.util.ArrayList;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.report.Report;
import de.bxservice.bxpos.ui.fragment.ReportResultFragment;
import de.bxservice.bxpos.ui.fragment.ReportsFragment;
import de.bxservice.bxpos.ui.utilities.ReportActivityHelper;

public class ReportActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //If the app is running on a large-screen device
    private boolean mTablet;
    private boolean mShowSaveItem = false;

    //Components to call the result activity
    private ArrayList<Integer> ranReportCodes;
    private ArrayList<String> reportResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null)
            setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.query_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Report> selectedReports = getReportsFragment().getSelectedReports();

                if (selectedReports.size() > 0) {
                    for (Report report : selectedReports) {
                        runReport(report);
                    }

                } else {
                    Toast.makeText(ReportActivity.this,
                            getString(R.string.no_report_selected), Toast.LENGTH_LONG)
                            .show();
                }

                if (reportResults != null && !reportResults.isEmpty())
                    viewDetailFragment();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        ViewGroup fragmentContainer = (ViewGroup) findViewById(R.id.detail_report_fragment_container);
        mTablet = (fragmentContainer != null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report_result, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {

        MenuItem saveItem = menu.findItem(R.id.save_pdf);

        if (mTablet) {
            if (saveItem != null) {
                saveItem.setEnabled(mShowSaveItem);
                saveItem.setVisible(mShowSaveItem);
            }
        } else {
            saveItem.setEnabled(false);
            saveItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_pdf:
                int permissionCheck = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

                //Version API 23+, you have to ask for permission in the activity to be allow to save files
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            this,
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                } else {
                    //We have permission so save the pdf
                    if (getReportResultFragment() != null)
                        ReportActivityHelper.saveToPdf(this, getReportResultFragment().getRecyclerView());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ReportsFragment getReportsFragment() {
        return (ReportsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.report_fragment);
    }

    private ReportResultFragment getReportResultFragment() {
        return (ReportResultFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_report_fragment_container);
    }

    private void viewDetailFragment() {

        if (mTablet) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            ReportResultFragment fragment =
                    ReportResultFragment.newInstance(reportResults);

            fragmentManager.beginTransaction()
                    .replace(R.id.detail_report_fragment_container, fragment)
                    .commit();

            mShowSaveItem  = true;
            invalidateOptionsMenu();

        } else {
            Intent intent = new Intent(this, ReportResultActivity.class);
            intent.putExtra(ReportResultFragment.REPORT_RESULTS, reportResults);
            startActivity(intent);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        getReportsFragment().onDateSet(year, month, day);
    }

    private void runReport(Report report) {
        boolean runReport = true;

        if (ranReportCodes != null && ranReportCodes.size() > 0) {
            for (Integer reportCode : ranReportCodes) {
                if (reportCode == report.getCode()) {
                    runReport = false; //The report was ran before
                    break;
                }
            }
        } else {
            ranReportCodes = new ArrayList<>();
            reportResults = new ArrayList<>();
        }

        if (runReport) {
            report.setFromDate(getReportsFragment().getFromDate());
            report.setToDate(getReportsFragment().getToDate());
            report.runReport();
            if (!report.getHtmlResult().toString().isEmpty()) {
                reportResults.add(report.getHtmlResult().toString());
                ranReportCodes.add(report.getCode());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    runReport(getReportsFragment().getSelectedReports().get(0));
                } else {
                    // permission denied
                }
            }
        }
    }
}
