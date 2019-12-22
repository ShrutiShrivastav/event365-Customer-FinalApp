package com.ebabu.event365live.home.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityCalenderBinding;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
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
import org.threeten.bp.ZoneId;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalenderActivity extends AppCompatActivity {

    private ActivityCalenderBinding calenderBinding;
    private MaterialCalendarView mcv;
    private Calendar myCalendar = Calendar.getInstance();
    private CalendarDay omg;
    CalendarDay startDate, endDate;
    private MyLoader myLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calenderBinding = DataBindingUtil.setContentView(this,R.layout.activity_calender);
        init();

        calenderBinding.calendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {
                //view.setDaysDisabled(true);

            }
        });


        calenderBinding.calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

            }
        });

        calenderBinding.calendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
                        int date= dates.size()-1;
                        startDate = dates.get(0);
                        endDate = dates.get(date);
                        Log.d("bfafbjakbfjkafa", startDate.getYear()+"/"+ startDate.getMonth()+"/"+ startDate.getDay()+" ====== "+ endDate.getYear()+"/"+ endDate.getMonth()+"/"+ endDate.getDay());
            }
        });



        LocalDate date = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
        calenderBinding.calendarView.state().edit()
                .setMinimumDate(date)
                /*.setMinimumDate(LocalDate.of(myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),1))*/
                .setMaximumDate(LocalDate.of(myCalendar.get(Calendar.YEAR)+1,myCalendar.get(Calendar.MONTH),28))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

    }

    private void init(){
        myLoader = new MyLoader(this);
    }

    public void backBtnOnClick(View view) {
        finish();
    }

    public void submitOnClick(View view) {
        if(startDate == null || endDate == null){
            ShowToast.errorToast(CalenderActivity.this,getString(R.string.please_select_event_date));
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(Constants.startDate,startDate.toString());
        intent.putExtra(Constants.endDate,endDate.toString());
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
