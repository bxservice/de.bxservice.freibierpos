package de.bxservice.bxpos.logic.tasks;

import android.os.AsyncTask;

import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.webservices.UpdateTableStatusWebServiceAdapter;

/**
 * Created by Diego Ruiz on 5/24/16.
 */
public class UpdateTableStatusTask extends AsyncTask<Table, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Table... tables) {

        boolean success = true;

        for (Table table : tables) {

            UpdateTableStatusWebServiceAdapter updateTableStatusWebServiceAdapter = new UpdateTableStatusWebServiceAdapter(table);
            if (!updateTableStatusWebServiceAdapter.isSuccess() && updateTableStatusWebServiceAdapter.isConnectionError()) {
                success = false;
                break;
            }
        }

        return success;
    }

}
