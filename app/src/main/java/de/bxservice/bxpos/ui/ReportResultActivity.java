package de.bxservice.bxpos.ui;


import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NavUtils;
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
                saveToPdf();
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
