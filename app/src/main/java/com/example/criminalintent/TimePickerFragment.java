package com.example.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by jarod on 5/27/2017.
 */

public class TimePickerFragment extends DialogFragment {
    private static final String ARG_DATE = "date";
    public static final String EXTRA_TIME_HOUR = "com.example.criminalintent.hour";
    public static final String EXTRA_TIME_MINUTE = "com.example.criminalintent.minute";
    private TimePicker mTimePicker;
    public static TimePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE,date);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time,null);
        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        }
        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.time_picker_title).setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            int hour = mTimePicker.getHour();
                            int minute = mTimePicker.getMinute();
                            sendResult(Activity.RESULT_OK, hour,minute);

                        }
                    }
                }).create();
    }

    private void sendResult(int resultCode, int hour, int minute){
        if(getTargetFragment() == null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME_HOUR, hour);
        intent.putExtra(EXTRA_TIME_MINUTE, minute);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
