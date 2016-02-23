package de.bxservice.bxpos.ui;

import android.content.Intent;
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
import java.util.HashMap;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataProvider;
import de.bxservice.bxpos.logic.model.pos.OpenOrderGridItem;
import de.bxservice.bxpos.logic.model.pos.POSOrder;

public class ViewOpenOrdersActivity extends AppCompatActivity {

    static final int EDIT_ORDER_REQUEST = 2;  // The request code

    private GridView gridview;
    private ArrayList<OpenOrderGridItem> mGridData;
    private GridOpenOrderViewAdapter mGridAdapter;
    private HashMap<OpenOrderGridItem, POSOrder> orderItemHashMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_open_orders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.open_order_toolbar);
        setSupportActionBar(toolbar);

        gridview = (GridView) findViewById(R.id.open_orders_gridview);

        mGridData = new ArrayList<>();
        orderItemHashMap = new HashMap<>();

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
            orderItemHashMap.put(item,order);
        }

        mGridAdapter = new GridOpenOrderViewAdapter(this, R.layout.open_order_grid_item_layout, mGridData);

        gridview.setAdapter(mGridAdapter);
        gridview.setGravity(Gravity.CENTER_HORIZONTAL);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //Get item at position
                OpenOrderGridItem item = (OpenOrderGridItem) parent.getItemAtPosition(position);
                POSOrder order = orderItemHashMap.get(item);
                openEditOrder(order);
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

    public void openEditOrder(POSOrder order) {
        Intent intent = new Intent(this, EditOrderActivity.class);
        intent.putExtra("caller","ViewOpenOrdersActivity");
        intent.putExtra("draftOrder", order);
        startActivityForResult(intent, EDIT_ORDER_REQUEST);
    }

    /**
     * When it comes back from the edit order Activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
         if(requestCode == EDIT_ORDER_REQUEST ) {
            if (resultCode == 2 || resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

}
