package de.bxservice.bxpos.de.bxservice.bxpos.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.bxservice.bxpos.R;


public class CreateReservationDialogFragment extends DialogFragment {

    private static final String TAG = "CreateReservationDialogFragment";
    private static final int TOKEN_CALENDARS = 1 << 3;
    private static final String KEY_DATE_STRING = "date_string";
    private static final String KEY_DATE_IN_MILLIS = "date_in_millis";
    private static final String EVENT_DATE_FORMAT = "%a, %b %d, %Y";
    private AlertDialog alertDialog;
    //private CalendarQueryService mService;
    private EditText mEventTitle;
    private View mColor;
    private TextView mCalendarName;
    private TextView mAccountName;
    private TextView mDate;
    private Button mButtonAddEvent;
    //private CalendarController mController;
    //private EditEventHelper mEditEventHelper;
    private String mDateString;
    private long mDateInMillis;
    //private CalendarEventModel mModel;
    private long mCalendarId = -1;
    private String mCalendarOwner;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Activity activity = getActivity();

        final LayoutInflater layoutInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.create_reservation_dialog, null);

        mColor = view.findViewById(R.id.color);
        mCalendarName = (TextView) view.findViewById(R.id.calendar_name);
        mAccountName = (TextView) view.findViewById(R.id.account_name);
        mEventTitle = (EditText) view.findViewById(R.id.event_title);
        //mEventTitle.addTextChangedListener(this);
        mDate = (TextView) view.findViewById(R.id.event_day);

        if (mDateString != null) {
            mDate.setText(mDateString);
        }

        alertDialog = new AlertDialog.Builder(activity)
                .setTitle("AAA")
                .setView(view)
                .setPositiveButton(R.string.save,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //createAllDayEvent();
                                dismiss();
                            }
                        })
                /*.setNeutralButton(R.string.edit_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mController.sendEventRelatedEventWithExtraWithTitleWithCalendarId(this,
                                        EventType.CREATE_EVENT, -1, mDateInMillis,
                                        mDateInMillis + DateUtils.DAY_IN_MILLIS, 0, 0,
                                        CalendarController.EXTRA_CREATE_ALL_DAY, -1,
                                        mEventTitle.getText().toString(),
                                        mCalendarId);
                                dismiss();
                            }
                        })*/
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        return alertDialog;
    }
}
