package de.bxservice.bxpos.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.ui.adapter.OrderingLineAdapter;
import de.bxservice.bxpos.ui.adapter.ReportResultAdapter;

public class ReportResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<String> reportResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_result);

        recyclerView = (RecyclerView) findViewById(R.id.report_result);

        getExtras();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        ReportResultAdapter mGridAdapter = new ReportResultAdapter(reportResults);

        recyclerView.setAdapter(mGridAdapter);
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

}
