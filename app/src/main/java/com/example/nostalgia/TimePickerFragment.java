package com.example.nostalgia;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment {

    //region Declarations
    private static final String ARG_TIME = "dialog_time_arg";
    public static final String EXTRA_TIME = "dialog_time_extra";
    private TimePicker mTimePicker;
    //

    public static TimePickerFragment newInstance( Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);
        TimePickerFragment tp = new TimePickerFragment();
        tp.setArguments(args);

        return tp;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker);
        Date date = (Date) getArguments().getSerializable(ARG_TIME);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        int hour = dateCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = dateCalendar.get(Calendar.MINUTE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Enter Time")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            int dialogHour = mTimePicker.getHour();
                            int dialogMinute = mTimePicker. getMinute();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.set(Calendar.HOUR_OF_DAY, dialogHour);
                            calendar.set(Calendar.MINUTE, dialogMinute);
                            Date time = calendar.getTime();
                            System.out.println("Before sending time back to main list "+time);
                            sendResult(Activity.RESULT_OK, time);
                        }
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, Date time){
        if(getTargetFragment() == null)
            return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, time);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }


}
