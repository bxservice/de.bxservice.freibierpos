package de.bxservice.bxpos.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataProvider;
import de.bxservice.bxpos.logic.model.pos.OpenOrderGridItem;
import de.bxservice.bxpos.logic.model.pos.POSOrder;

public class ViewOpenOrdersActivity extends AppCompatActivity {

    private GridView gridview;
    private ArrayList<OpenOrderGridItem> mGridData;
    private GridOpenOrderViewAdapter mGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_open_orders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.open_order_toolbar);
        setSupportActionBar(toolbar);

        gridview = (GridView) findViewById(R.id.open_orders_gridview);

        mGridData = new ArrayList<>();

        DataProvider dataProvider = new DataProvider(getBaseContext());


        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DataProvider.LOCALE);

        OpenOrderGridItem item;
        BigDecimal totalLines;
        int qtyOrdered;
        for(POSOrder order : dataProvider.getAllOpenOrders()) {
            item = new OpenOrderGridItem();
            item.setOrderNo(getString(R.string.order) + ": " + String.valueOf(order.getOrderId()));

            totalLines = order.getTotallines();
            item.setPrice(getString(R.string.total) + ": " + currencyFormat.format(totalLines));

            String table;

            if(order.getTable() == null)
                table = getString(R.string.unset_table);
            else
                table = order.getTable().getTableName();

            item.setTable(getString(R.string.table) + ": " + table);

            mGridData.add(item);
        }

        mGridAdapter = new GridOpenOrderViewAdapter(this, R.layout.open_order_grid_item_layout, mGridData);

        gridview.setAdapter(mGridAdapter);
        gridview.setGravity(Gravity.CENTER_HORIZONTAL);


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
