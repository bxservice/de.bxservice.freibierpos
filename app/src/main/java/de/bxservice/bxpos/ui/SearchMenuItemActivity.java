package de.bxservice.bxpos.ui;


import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
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

import static android.support.v7.widget.SearchView.*;

public class SearchMenuItemActivity extends AppCompatActivity implements OnQueryTextListener {

    private SearchView mSearchView;
    private ListView listView;
    private OrderArrayAdapter<String> mAdapter;
    private List items = new ArrayList<Order>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menu_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_item_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //mSearchView = (SearchView) findViewById(R.id.search_items_view);
        listView = (ListView) findViewById(R.id.list_search_View);

        initItems();

        mAdapter = new OrderArrayAdapter<>(this, items);

        listView.setAdapter(mAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_item, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        mSearchView = (SearchView) menu.findItem(R.id.search_items_view).getActionView();

        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        setupSearchView();


        return true;
    }

    public void initItems(){

        ProductPrice productPrice;
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DataMediator.LOCALE);

        for( Product product : DataMediator.getInstance().getProductList() ){
            productPrice = DataMediator.getInstance().getProductPriceHashMap().get(product);
            items.add(new Order(product.getProductName(), currencyFormat.format(productPrice.getStdPrice())));
        }
    }

    public boolean onQueryTextChange(String newText) {
        OrderArrayAdapter adapter = (OrderArrayAdapter)listView.getAdapter();

        if ( TextUtils.isEmpty(newText) ) {
            adapter.getFilter().filter(null);
        } else {
            adapter.getFilter().filter(newText);
        }
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setFocusable(true);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryHint(getString(R.string.search));
        mSearchView.requestFocus();
    }
}
