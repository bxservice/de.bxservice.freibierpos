package de.bxservice.bxpos.logic.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import de.bxservice.bxpos.logic.DataReader;
import de.bxservice.bxpos.ui.LoginActivity;
import de.bxservice.bxpos.ui.MainActivity;

/**
 * Created by Diego Ruiz on 5/23/16.
 */
public class ReadServerDataTask extends AsyncTask<Void, Void, Boolean> {

    private Activity mActivity;

    public ReadServerDataTask(Activity callerActivity) {
        mActivity = callerActivity;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        DataReader data = new DataReader(mActivity.getBaseContext());

        return data.isDataComplete() && !data.isError();
    }

    @Override
    protected void onPostExecute(Boolean result) {

        if (mActivity instanceof MainActivity)
            ((MainActivity) mActivity).postExecuteReadDataTask(result);
        else if (mActivity instanceof LoginActivity)
            ((LoginActivity) mActivity).postExecuteReadDataTask(result);

    }
}