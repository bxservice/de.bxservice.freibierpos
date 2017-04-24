package de.bxservice.bxpos.ui.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import de.bxservice.bxpos.logic.tasks.ReadServerDataTask;

/**
 * Fragment with no UI to avoid killing the AsyncTask
 * on configuration changes such as screen rotation
 * Created by Diego Ruiz
 */
public class AsyncFragment extends Fragment {

    private ParentActivity mParent;
    private ReadServerDataTask readDataTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void runAsyncTask() {
        readDataTask = new ReadServerDataTask(this);
        readDataTask.execute();
    }

    public ParentActivity getParentActivity() {
        return mParent;
    }

    public boolean isTaskRunning() {
        return (readDataTask != null) && (readDataTask.getStatus() == AsyncTask.Status.RUNNING);
    }

    public void handleTaskFinish(boolean result) {
        mParent.handleReadDataTaskFinish(result);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mParent = (ParentActivity) context;
    }

    public interface ParentActivity {
        void handleReadDataTaskFinish(boolean success);
    }
}
