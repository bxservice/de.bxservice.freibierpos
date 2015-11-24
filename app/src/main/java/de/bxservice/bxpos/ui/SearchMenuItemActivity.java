package de.bxservice.bxpos.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataMediator;
import de.bxservice.bxpos.logic.model.Order;
import de.bxservice.bxpos.logic.model.Product;
import de.bxservice.bxpos.logic.model.ProductPrice;
import de.bxservice.bxpos.ui.adapter.OrderArrayAdapter;

public class SearchMenuItemActivity extends AppCompatActivity {

    private ListView listView;
    private OrderArrayAdapter<String> mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menu_item);

        listView = (ListView) findViewById(R.id.list_search_View);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            List items = new ArrayList<Order>();

            List<Product> p =  DataMediator.getInstance().getProductList();

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DataMediator.LOCALE);
            ProductPrice productPrice;

            for (Product pa : p){
                boolean b = pa.getProductName().matches("(?i).*" + query + ".*");
                if(b){
                    productPrice = DataMediator.getInstance().getProductPriceHashMap().get(pa);
                    items.add(new Order(pa.getProductName(), currencyFormat.format(productPrice.getStdPrice())));
                }
            }

            mAdapter = new OrderArrayAdapter<>(this, items);




            listView.setAdapter(mAdapter);

            //use the query to search your data somehow
        }
    }
}
