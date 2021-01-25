package com.ebabu.event365live.home.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CalenderActivity extends AppCompatActivity {

    CalendarDay startDate, endDate, calenderDayStartDate, calenderDayEndDate;
    String selectedDate = "", selectedEndDate = "";
    String startDateFormat = "", endDateFormat = "";
    private ActivityCalenderBinding calenderBinding;
    private MaterialCalendarView mcv;
    private Calendar myCalendar = Calendar.getInstance();
    private CalendarDay omg;
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


        if (selectedDate != null && !selectedDate.equals("") && selectedEndDate != null && selectedEndDate != null) {
            calenderBinding.calendarView.setSelectedDate(LocalDate.parse(selectedDate));

            calenderDayStartDate = CalendarDay.from(LocalDate.parse(selectedDate));
            calenderDayEndDate = CalendarDay.from(LocalDate.parse(selectedEndDate));

            calenderBinding.calendarView.selectRange(calenderDayStartDate, calenderDayEndDate);
//            setEvent(dates);
            setEvent(calenderBinding.calendarView.getSelectedDates());
        }

        calenderBinding.calendarView.setOnDateChangedListener((widget, date, selected) -> {

        });


        //Changed by Lokesh panchal on 21-01-2021
        calenderBinding.calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {


                Log.v("SMTEB","selected> " + selected);

                calenderBinding.calendarView.removeDecorators();
                if (selected) {
                    calenderDate = date;
                    try {
                        LocalDate currentDate = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
                        //Sun Apr 05 22:34:26 GMT+05:30 2020

                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                        String currentDateTimeString = sdf.format(Calendar.getInstance().getTime());

                        if (date.getDate().toString().equals(currentDate.toString())) {
                            selectedDate = date.getDate().toString();
                            selectedEndDate = date.getDate().plusDays(3).toString();

                            startDateFormat = selectedDate + " " + currentDateTimeString;
                            endDateFormat = selectedEndDate + " " + currentDateTimeString;

                        } else {
                            selectedDate = date.getDate().minusDays(3).toString();
                            selectedEndDate = date.getDate().plusDays(3).toString();

                            startDateFormat = selectedDate + " " + currentDateTimeString;
                            endDateFormat = selectedEndDate + " " + currentDateTimeString;
                        }
                        SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                        sdf_.setTimeZone(TimeZone.getDefault());

                        Date startDate = sdf_.parse(startDateFormat);
                        Date endDate = sdf_.parse(endDateFormat);

                        Utility.startDate = Utility.localToUTC(startDate);
                        Utility.endDate = Utility.localToUTC(endDate);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {

                    calenderDate = null;
                    selectedDate = "";
                    selectedEndDate = "";
                    startDateFormat = "";
                    endDateFormat = "";
                    startDate = null;
                    endDate = null;
                }
            }
        });

        calenderBinding.calendarView.setOnRangeSelectedListener((widget, dates) -> {

            int date = dates.size() - 1;
            startDate = dates.get(0);
            endDate = dates.get(date);
            selectedDate = startDate.getYear() + "-" + startDate.getMonth() + "-" + startDate.getDay();
            selectedEndDate = endDate.getYear() + "-" + endDate.getMonth() + "-" + endDate.getDay();

            try {
//            selectedDate = startDate.getDate().minusDays(3).toString();
//            selectedEndDate = endDate.getDate().plusDays(3).toString();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                String currentDateTimeString = sdf.format(Calendar.getInstance().getTime());
                startDateFormat = selectedDate + " " + currentDateTimeString;
                endDateFormat = selectedEndDate + " " + currentDateTimeString;

                SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                sdf_.setTimeZone(TimeZone.getDefault());

                Date startDate = sdf_.parse(startDateFormat);
                Date endDate = sdf_.parse(endDateFormat);

                Utility.startDate = Utility.localToUTC(startDate);
                Utility.endDate = Utility.localToUTC(endDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            calenderDate = null;

            setEvent(dates);
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
        selectedDate = SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.startDate);
        selectedEndDate = SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.endDate);
        myLoader = new MyLoader(this);
    }

    public void backBtnOnClick(View view) {
        finish();
    }

    public void submitOnClick(View view) {
        if (TextUtils.isEmpty(startDateFormat) || TextUtils.isEmpty(endDateFormat)) {
            ShowToast.infoToast(CalenderActivity.this, getString(R.string.please_select_event_date));
            return;
        }

        if (calenderDate != null) {
            SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.showSelectedCurrentCalenderDate, calenderDate.getDate().toString());
            SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.startDate, "");
            SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.endDate, "");
        } else {
            if (startDate != null && endDate != null) {
                SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.startDate, startDate.getDate().toString());
                SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.endDate, endDate.getDate().toString());
            }
        }

        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    void setEvent(List<CalendarDay> dateList) {

        calenderBinding.calendarView.removeDecorators();

        List<CalendarDay> datesLeft = new ArrayList<>();
        List<CalendarDay> datesCenter = new ArrayList<>();
        List<CalendarDay> datesRight = new ArrayList<>();
        List<CalendarDay> datesIndependent = new ArrayList<>();

        for (int i = 0; i < dateList.size(); i++) {

            boolean right = false;
            boolean left = false;

            if (i == 0) {
                left = true;
            } else if (i == dateList.size() - 1) {
                right = true;
            } else {
                left = true;
                right = true;
            }

            if (left && right) {
                datesCenter.add(dateList.get(i));
            } else if (left) {
                datesLeft.add(dateList.get(i));
            } else if (right) {
                datesRight.add(dateList.get(i));
            } else {
                datesIndependent.add(dateList.get(i));
            }
        }

        setDecor(datesCenter, R.drawable.calender_bg_center, Color.BLACK);
        setDecor(datesLeft, R.drawable.calender_bg_left, Color.WHITE);
        setDecor(datesRight, R.drawable.calender_bg_right, Color.WHITE);
//            setDecor(datesIndependent, R.drawable.g_independent);

    }

    void setDecor(List<CalendarDay> calendarDayList, int drawable, int textColor) {

        calenderBinding.calendarView.addDecorators(new EventDecorator(CalenderActivity.this
                , drawable
                , calendarDayList, textColor));
    }

    public class EventDecorator implements DayViewDecorator {
        Context context;
        private int drawable;
        private HashSet<CalendarDay> dates;
        private int textColor;

        public EventDecorator(Context context, int drawable, List<CalendarDay> calendarDays1, int textColor) {
            this.context = context;
            this.drawable = drawable;
            this.textColor = textColor;
            this.dates = new HashSet<>(calendarDays1);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            // apply drawable to dayView
            view.setSelectionDrawable(context.getResources().getDrawable(drawable));
            // white text color
            view.addSpan(new ForegroundColorSpan(textColor));
        }
    }
}
