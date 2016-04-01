package de.bxservice.bxpos.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataProvider;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.ui.RecyclerItemsListener;
import de.bxservice.bxpos.ui.adapter.TableDialogAdapter;

/**
 * Created by Diego Ruiz on 10/03/16.
 */
public class SwitchTableDialogFragment extends DialogFragment {


    public interface SwitchTableDialogListener {
        void onDialogPositiveClick(SwitchTableDialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    private SwitchTableDialogListener mListener;
    private ArrayList<Table> mGridData;
    private RecyclerView recyclerView;
    private Table table;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.switch_table_dialog, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.table_list);

        // use a grid layout manager with 2 columns
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity().getBaseContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);

        initGridData();

        TableDialogAdapter mGridAdapter = new TableDialogAdapter(mGridData);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemsListener(getActivity().getBaseContext(), recyclerView, new RecyclerItemsListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        table = mGridData.get(position);
                        mListener.onDialogPositiveClick(SwitchTableDialogFragment.this);
                        SwitchTableDialogFragment.this.getDialog().dismiss();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                    }

                })
        );

        recyclerView.setAdapter(mGridAdapter);

        builder.setTitle(R.string.change_table);
        builder.setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SwitchTableDialogFragment.this.getDialog().cancel();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    /**
     * init the grid data with all the tables
     */
    private void initGridData() {
        DataProvider dataProvider = new DataProvider(getActivity().getBaseContext());
        mGridData = new ArrayList<>(dataProvider.getAllTables());
    }

    public Table getTable() {
        return table;
    }

    // Override the Fragment.onAttach() method to instantiate the GuestNumberDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SwitchTableDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SwitchTableDialogListener");
        }
    }
}
