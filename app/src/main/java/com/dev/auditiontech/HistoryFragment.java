package com.dev.auditiontech;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class HistoryFragment extends Fragment implements ToolbarCustomizable {
    // TODO: fix this.
    public static HistoryFragment instance;
    //    public static Toolbar toolbar;
    private static CharSequence previousToolbarTitle;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", /*Locale.getDefault()*/Locale.ENGLISH);
    private CompactCalendarView compactCalendarView;
    private boolean isExpanded;
    private AppCompatActivity activity;
    private AppBarLayout appBarLayout;
    private ImageView arrow;
    private RelativeLayout datePickerButton;
    private DateViewModel dateViewModel;
    private DatabaseReference userReference;
    private Query query;
    private static final long FIVE_MINUTES_IN_MILLI_SEC = 1000 * 5 * 60;
    private LineChart chart;
    private ValueEventListener listener = new ValueEventListener() {
        @Override
        @SuppressWarnings("ConstantConditions")
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            // start with  empty entry.
            List<Entry> dataList = new ArrayList<>();
            long startEpoch = getDateStartMilliSec(dateViewModel.getDate().getValue());
            long endEpoch = getNextDateStartMilliSec(dateViewModel.getDate().getValue());
            for (long epoch = startEpoch;
                 epoch <= endEpoch;
                 epoch += FIVE_MINUTES_IN_MILLI_SEC) {
                dataList.add(new Entry(epoch, 0));
            }

            // add data into entry.
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                if (!snapshot.hasChild("timestamp") || snapshot.hasChild("volume")) {
                    continue;
                }
                int index = (int) ((snapshot.child("timestamp").getValue(Long.class) - startEpoch) % (FIVE_MINUTES_IN_MILLI_SEC));
                dataList.get(index).setY(snapshot.child("volume").getValue(Integer.class));
            }

            LineDataSet set1 = new LineDataSet(dataList, "Max Volume");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setValueTextColor(ColorTemplate.getHoloBlue());
            set1.setLineWidth(1.5f);
            set1.setDrawCircles(false);
            set1.setDrawValues(false);
            set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);

            // create a data object with the data sets
            LineData data = new LineData(set1);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);

            // set data
            //chart.setData(data);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public static HistoryFragment getInstance() {
        if (instance == null) {
            instance = new HistoryFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userReference = FirebaseDatabase.getInstance().getReference(getID());
        dateViewModel = ViewModelProviders.of(this).get(DateViewModel.class);

        final Observer<Date> dateObserver = new Observer<Date>() {
            @Override
            public void onChanged(Date date) {

                activity.setTitle(dateFormat.format(date));
                if (compactCalendarView != null) {
                    compactCalendarView.setCurrentDate(date);
                }

                if (query != null) {
                    query.removeEventListener(listener);
                }

                query = getQuery(date);
                query.addValueEventListener(listener);
            }
        };
        dateViewModel.getDate().observe(this, dateObserver);

    }

    private Query getQuery(Date date) {
        return getQuery(date, "max_ambient_volume");
    }

    private Query getQuery(Date date, String relativePathUnderUserId) {
        return userReference
                .child(relativePathUnderUserId)
                .orderByChild("timestamp")
                .startAt(getDateStartMilliSec(date))
                .endAt(getNextDateStartMilliSec(date));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

//        if (toolbar == null) {
//            toolbar = getActivity().findViewById(R.id.main_toolbar);
//        }
        activity = ((AppCompatActivity) getActivity());
        appBarLayout = activity.findViewById(R.id.app_bar_layout);


        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        setToolbar();
        //chart = activity.findViewById(R.id.chart);
        //setChart();

    }

    @Override
    public void onStop() {
        resetToolbar();
        super.onStop();

    }

    @Override
    public void setToolbar() {
        previousToolbarTitle = activity.getSupportActionBar().getTitle();
        compactCalendarView = activity.findViewById(R.id.compactcalendar_view);
        compactCalendarView.setLocale(TimeZone.getDefault(), /*Locale.getDefault()*/Locale.ENGLISH);

        compactCalendarView.setShouldDrawDaysHeader(true);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                dateViewModel.getDate().setValue(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

                dateViewModel.getDate().setValue(firstDayOfNewMonth);
            }
        });

        arrow = activity.findViewById(R.id.date_picker_arrow);
        arrow.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_arrow_drop_down));
        datePickerButton = activity.findViewById(R.id.date_picker_button);
        datePickerButton.setClickable(true);
        datePickerButton.setFocusable(true);

        datePickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                float rotation = isExpanded ? 0 : 180;
                ViewCompat.animate(arrow).rotation(rotation).start();

                isExpanded = !isExpanded;
                appBarLayout.setExpanded(isExpanded, true);
            }
        });

        appBarLayout.bringToFront();
    }

    @Override
    public void resetToolbar() {
//        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        activity.setTitle(previousToolbarTitle);
        datePickerButton.setClickable(false);
        datePickerButton.setFocusable(false);
        arrow.setImageDrawable(null);
//        toolbar.setTitle(previousToolbarTitle);
    }

    private String getID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        return uid;
    }

    private ZonedDateTime getDateStart(Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, zoneId);
        return zdt.toLocalDate().atStartOfDay(zoneId);

    }

    private long getDateStartMilliSec(Date date) {

        return getDateStart(date).toInstant().toEpochMilli();
    }

    private long getNextDateStartMilliSec(Date date) {
        return getDateStart(date).plusDays(1).toInstant().toEpochMilli();
    }

    private void setChart() {
        // no description text
        //chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        chart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE);
        chart.setViewPortOffsets(0f, 0f, 0f, 0f);

        Legend l = chart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        //xAxis.setTypeface(tfLight);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(2f); // one hour
        xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {

                long millis = TimeUnit.MINUTES.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        //leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(170f);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }
}

