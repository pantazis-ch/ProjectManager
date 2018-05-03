package com.android.example.projectmanager.main.projects.aveproject;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.android.example.projectmanager.R;

import java.util.Calendar;


public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private int mHour = -1;
    private int mMinute = -1;


    public static TimePickerFragment newInstance(String timeStr) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putString("timeStr", timeStr);
        fragment.setArguments(args);
        return fragment;
    }

    public TimePickerFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(getArguments()!=null) {
            String timeStr = getArguments().getString("timeStr");
            if(!timeStr.equals(getString(R.string.label_na))) {
                String t[] = timeStr.split(" : ");
                mHour = Integer.valueOf(t[0]);
                mMinute = Integer.valueOf(t[1]);
            }
        }

        if(mHour!=-1 && mMinute!=-1){
            return new TimePickerDialog(getActivity(), this, mHour, mMinute,
                    DateFormat.is24HourFormat(getActivity()));
        } else {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Fragment parent = getTargetFragment();
        if (parent instanceof AVEProjectFragment) {

            String minuteStr = String.valueOf(minute);
            if(minuteStr.length() == 1) {
                minuteStr = "0" + minuteStr;
            }
            ((AVEProjectFragment) parent).timeTextView.setText(hourOfDay + " : "  +  minuteStr);

            ((AVEProjectFragment) parent).timeStr = hourOfDay + " : " +  minuteStr;

            ((AVEProjectFragment) parent).timeIsSet = true;
        }
        dismissAllowingStateLoss();
    }

}
