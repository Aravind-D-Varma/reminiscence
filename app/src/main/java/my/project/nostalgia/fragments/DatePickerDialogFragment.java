package my.project.nostalgia.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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

import my.project.nostalgia.R;
import my.project.nostalgia.supplementary.changeTheme;

/**
 * Setup of getting and saving date from the date dialog in memory
 */
public class DatePickerDialogFragment extends DialogFragment {

    public static final String EXTRA_DATE = "my.project.criminalintent.date";
    private static final String ARG_DATE = "date_id";
    private DatePicker mDatePicker;

    /**
     * Creating a new DialogFragment from MemoryFragment. Gets memory's date if present, else current date as default and shows in dialog.
     */
    public static DatePickerDialogFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DatePickerDialogFragment dpfragment = new DatePickerDialogFragment();
        dpfragment.setArguments(args);

        return dpfragment;
    }

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

        mDatePicker = v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);
        AlertDialog.Builder dialogBuildernew = new AlertDialog.Builder(
                getActivity(), new changeTheme(getContext()).setDialogTheme())
                .setView(v)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    int year1 = mDatePicker.getYear();
                    int month1 = mDatePicker.getMonth();
                    int day1 = mDatePicker.getDayOfMonth();
                    Date calendarDate = new GregorianCalendar(year1, month1, day1
                            , calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE)).getTime();
                    sendResult(Activity.RESULT_OK, calendarDate);
                });
        return dialogBuildernew.create();
    }

    /**
     * When OK has been clicked, go back to memory with new date (if selected, else default is current date)
     */
    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null)
            return;
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
