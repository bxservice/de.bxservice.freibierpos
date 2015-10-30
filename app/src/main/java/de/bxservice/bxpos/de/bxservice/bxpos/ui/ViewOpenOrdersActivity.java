package de.bxservice.bxpos.de.bxservice.bxpos.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import de.bxservice.bxpos.R;

public class ViewOpenOrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_open_orders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.open_order_toolbar);
        setSupportActionBar(toolbar);

        GridView gridview = (GridView) findViewById(R.id.open_orders_gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(ViewOpenOrdersActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_order_toolbar, menu);
        return true;
    }

}
