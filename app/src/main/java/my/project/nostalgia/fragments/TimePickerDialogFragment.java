package my.project.nostalgia.fragments;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import my.project.nostalgia.R;
import my.project.nostalgia.supplementary.changeTheme;

import java.util.Calendar;
import java.util.Date;

/**
 * Setup of the getting and saving time from the time dialog in memory
 */
public class TimePickerDialogFragment extends DialogFragment {

    private static final String ARG_TIME = "dialog_time_arg";
    public static final String EXTRA_TIME = "dialog_time_extra";
    private TimePicker mTimePicker;
    
    /**
    * Creating a new DialogFragment from MemoryFragment. Gets memory's time if present, else current time as default and shows in dialog.
    */
    public static TimePickerDialogFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);
        TimePickerDialogFragment tp = new TimePickerDialogFragment();
        tp.setArguments(args);

        return tp;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = v.findViewById(R.id.dialog_time_picker);
        Date date = (Date) getArguments().getSerializable(ARG_TIME);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        int hour = dateCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = dateCalendar.get(Calendar.MINUTE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                getActivity(),new changeTheme(getContext()).setDialogTheme()).setView(v)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        int dialogHour = mTimePicker.getHour();
                        int dialogMinute = mTimePicker. getMinute();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.set(Calendar.HOUR_OF_DAY, dialogHour);
                        calendar.set(Calendar.MINUTE, dialogMinute);
                        Date time = calendar.getTime();
                        sendResult(Activity.RESULT_OK, time);
                    }
                });
        return dialogBuilder.create();
    }
    /**
     * When OK has been clicked, go back to memory with new time (if selected, else default is current time)
     */
    private void sendResult(int resultCode, Date time){
        if(getTargetFragment() == null)
            return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, time);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }


}
