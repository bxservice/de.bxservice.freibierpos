package de.bxservice.bxpos.ui;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.SearchView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataMediator;
import de.bxservice.bxpos.logic.model.Order;
import de.bxservice.bxpos.logic.model.Product;
import de.bxservice.bxpos.logic.model.ProductPrice;
import de.bxservice.bxpos.ui.adapter.OrderArrayAdapter;

public class SearchMenuItemActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private SearchView mSearchView;
    private ListView listView;
    private OrderArrayAdapter<String> mAdapter;
    private List items = new ArrayList<Order>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menu_item);

        mSearchView = (SearchView) findViewById(R.id.search_items_view);
        listView = (ListView) findViewById(R.id.list_search_View);

        initItems();

        mAdapter = new OrderArrayAdapter<>(this, items);

        listView.setAdapter(mAdapter);

        setupSearchView();
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
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryHint(getString(R.string.search));
    }
}
