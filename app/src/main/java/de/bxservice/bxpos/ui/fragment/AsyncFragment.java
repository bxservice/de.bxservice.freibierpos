/**********************************************************************
 * This file is part of Freibier POS                                   *
 *                                                                     *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Diego Ruiz - Bx Service GmbH                                      *
 **********************************************************************/
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
