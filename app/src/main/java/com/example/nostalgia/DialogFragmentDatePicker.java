package com.example.nostalgia;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Setup of getting and saving date from the date dialog in memory
 */
public class DialogFragmentDatePicker extends DialogFragment {

    private static final String ARG_DATE = "date_id";
    public static final String EXTRA_DATE = "com.example.criminalintent.date";
    private DatePicker mDatePicker;
    
    /**
    * Creating a new DialogFragment from MemoryFragment. Gets memory's date if present, else current date as default and shows in dialog.
    */
    public static DialogFragmentDatePicker newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DialogFragmentDatePicker dpfragment = new DialogFragmentDatePicker();
        dpfragment.setArguments(args);

        return dpfragment;
    }
    //endregion

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        SharedPreferences getData = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
        String themeValues = getData.getString("GlobalTheme", "Dark");
        AlertDialog.Builder dialogBuilder;
        if(themeValues.equals("Light"))
            dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.LightDialog);

        else
            dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DarkDialog);

                dialogBuilder.setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date calendarDate = new GregorianCalendar(year, month, day, calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE)).getTime();
                        sendResult(Activity.RESULT_OK, calendarDate);
                    }
                });
        return dialogBuilder.create();
    }

    /**
     * When OK has been clicked, go back to memory with new date (if selected, else default is current date)
     */
    private void sendResult(int resultCode, Date date){
        if(getTargetFragment() == null)
            return;
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
