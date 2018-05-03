package com.android.example.projectmanager.detail.deadlines.avedeadline;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.widget.DatePicker;

import com.android.example.projectmanager.R;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private int mDay = -1;
    private int mMonth = -1;
    private int mYear = -1;


    public static DatePickerFragment newInstance(String dateStr) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putString("dateStr", dateStr);
        fragment.setArguments(args);
        return fragment;
    }

    public DatePickerFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(getArguments()!=null) {
            String dateStr = getArguments().getString("dateStr");
            if(!dateStr.equals(getString(R.string.label_na))) {
                String d[] = dateStr.split(" / ");
                mDay = Integer.valueOf(d[0]);
                mMonth = Integer.valueOf(d[1])-1;
                mYear = Integer.valueOf(d[2]);
            }
        }

        if(mDay!=-1 && mMonth!=-1 && mYear!=-1){
            return new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
        } else {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        Fragment parent = getTargetFragment();
        if (parent instanceof AVEDeadlineFragment) {
            ((AVEDeadlineFragment) parent).dateStr = day + " / " +  ( month + 1 ) + " / " +  year;
            ((AVEDeadlineFragment) parent).deadlineDateTextView.setText(day + " / " +  ( month + 1 ) + " / " + year);
            ((AVEDeadlineFragment) parent).dateIsSet = true;
        }
        dismissAllowingStateLoss();

    }

}
