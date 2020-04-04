package com.ebabu.event365live.home.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityCalenderBinding;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.ebabu.event365live.utils.Utility;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CalenderActivity extends AppCompatActivity {

    private ActivityCalenderBinding calenderBinding;
    private MaterialCalendarView mcv;
    private Calendar myCalendar = Calendar.getInstance();
    private CalendarDay omg;
    CalendarDay startDate, endDate;
    String selectedDate = "", selectedEndDate = "";
    private MyLoader myLoader;
    private String selectedCalenderDate;
    private CalendarDay calenderDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calenderBinding = DataBindingUtil.setContentView(this, R.layout.activity_calender);

        init();

        calenderBinding.calendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setDaysDisabled(true);

            }
        });

        if (selectedCalenderDate != null)
            calenderBinding.calendarView.setSelectedDate(LocalDate.parse(selectedCalenderDate));

        calenderBinding.calendarView.setOnDateChangedListener((widget, date, selected) -> {

        });


        calenderBinding.calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                calenderDate = date;



                Date dateOne = null,dateTwo = null;
                LocalDate currentDate = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDate();






                //Sun Apr 05 00:18:44 GMT+05:30 2020

                if (date.getDate().toString().equals(currentDate.toString())) {
                    selectedDate = date.getDate().toString();
                    selectedEndDate = date.getDate().plusDays(3).toString();
                }else {
                    selectedDate = date.getDate().minusDays(3).toString();
                    selectedEndDate = date.getDate().plusDays(3).toString();
                }



                Utility.startDate = selectedDate;
                Utility.endDate = selectedEndDate;

              //  Log.d("bfafbjakbfjkafa", selectedDate + " ====== " + currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Log.d("bfafbjakbfjkafa", selectedDate + " ====== " + currentDate.atTime(LocalTime.from(LocalDate.now(ZoneId.systemDefault()))));
               // Date date1 = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());




            }
        });

        calenderBinding.calendarView.setOnRangeSelectedListener((widget, dates) -> {
            int date = dates.size() - 1;
            startDate = dates.get(0);
            endDate = dates.get(date);
            selectedDate = startDate.getYear() + "-" + startDate.getMonth() + "-" + startDate.getDay();
            selectedEndDate = endDate.getYear() + "-" + endDate.getMonth() + "-" + endDate.getDay();


            Log.d("bfafbjakbfjkafa", selectedDate + " ====== " + selectedEndDate);


        });

        LocalDate date = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDate();



        calenderBinding.calendarView.state().edit()
                .setMinimumDate(date)
                .setMaximumDate(LocalDate.parse(date.toString()).plusYears(1))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
    }

    private void init() {
        selectedCalenderDate = SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.showSelectedCurrentCalenderDate);
        myLoader = new MyLoader(this);
    }

    public void backBtnOnClick(View view) {
        finish();
    }

    public void submitOnClick(View view) {

        if (!TextUtils.isEmpty(selectedDate)  || !TextUtils.isEmpty(selectedEndDate)) {
            ShowToast.infoToast(CalenderActivity.this, getString(R.string.please_select_event_date));
            return;
        }
        SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.showSelectedCurrentCalenderDate, calenderDate.getDate().toString());
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
