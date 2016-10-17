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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.ui.adapter.ReportResultAdapter;
import de.bxservice.bxpos.ui.decorator.DividerItemDecoration;

public class ReportResultActivity extends AppCompatActivity {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private RecyclerView recyclerView;
    private ArrayList<String> reportResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.report_result);

        getExtras();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext(), DividerItemDecoration.VERTICAL_LIST));


        ReportResultAdapter mGridAdapter = new ReportResultAdapter(reportResults);

        recyclerView.setAdapter(mGridAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report_result, menu);
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
                    saveToPdf();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveToPdf() {

        // create a new document
        PdfDocument document = new PdfDocument();

        // draw something on the page
        int pages = 1; //reportResults.size(); If in the future we decided to create a page per report

        View content = recyclerView;
        // create a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(content.getWidth(), content.getHeight(), pages).create();

        // First page
        PdfDocument.Page page = document.startPage(pageInfo);
        content.draw(page.getCanvas());
        // finish the page
        document.finishPage(page);

        //When the document has more than on one
        /*for(int i = 1; i < reportResults.size(); i++) {
            // New page
            page = document.startPage(pageInfo);
            content = recyclerView.getChildAt(i);
            content.draw(page.getCanvas());
            // finish the page
            document.finishPage(page);

        }*/

        FileOutputStream fos;
        // saving pdf document to sdcard
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");
        String pdfName = "Report"
                + sdf.format(Calendar.getInstance().getTime()) + ".pdf";
        try {

            File f = new File(Environment.getExternalStorageDirectory().getPath(), pdfName);
            //f.createNewFile();

            fos = new FileOutputStream(f);
            // write the document content
            document.writeTo(fos);
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // close the document
        document.close();

        openPDF(pdfName);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    saveToPdf();

                } else {
                    // permission denied
                }
            }
        }
    }

    private void openPDF(String pdfName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath(),  pdfName));
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    /**
     * Get extras from the previous activity
     */
    private void getExtras() {

        Intent intent = getIntent();

        if(intent != null) {
            reportResults = (ArrayList<String>) intent.getSerializableExtra("results");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }

}
