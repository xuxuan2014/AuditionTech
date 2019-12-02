package com.dev.auditiontech;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.dev.auditiontech.persistence.dao.ExposureDAO;
import com.dev.auditiontech.persistence.dao.MaxVolumeDAO;
import com.dev.auditiontech.persistence.entity.MaxVolume;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class HistoryFragment extends Fragment implements ToolbarCustomizable, OnChartValueSelectedListener {
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
    //   private DatabaseReference userReference;
    //   private Query query;
    private static final long FIVE_MINUTES_IN_MILLI_SEC = 1000 * 5 * 60;
    private LineChart chart;
    private TextView timestampText;
    private TextView decibelText;
    private YAxis leftAxis;
    private TextView exposureText;
    private List<MaxVolume> maxVolumes;
//    private ValueEventListener listener = new ValueEventListener() {
//        @Override
//        @SuppressWarnings("ConstantConditions")
//        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//            // start with  empty entry.
//            List<Entry> dataList = new ArrayList<>();
//            long startEpoch = getDateStartMilliSec(dateViewModel.getDate().getValue());
//            Log.d("start epoch", String.valueOf(startEpoch));
//            long endEpoch = getNextDateStartMilliSec(dateViewModel.getDate().getValue());
//            Log.d("end epoch", String.valueOf(endEpoch));
//            for (long epoch = startEpoch;
//                 epoch <= endEpoch;
//                 epoch += FIVE_MINUTES_IN_MILLI_SEC) {
//                dataList.add(new Entry(epoch, 0));
//            }
//
//            // add data into entry.
//            int maxVolume = 0;
//            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                if (!snapshot.hasChild("timestamp") || !snapshot.hasChild("volume")) {
//                    continue;
//                }
//                Log.d("snapshot timestamp", String.valueOf(snapshot.child("timestamp").getValue(Long.class)));
//                Log.d("snapshot volume", String.valueOf(snapshot.child("volume").getValue(Integer.class)));
//                int index = (int) ((snapshot.child("timestamp").getValue(Long.class) - startEpoch) / (FIVE_MINUTES_IN_MILLI_SEC));
//                Log.d("snapshot index", String.valueOf(index));
//                dataList.get(index).setY(snapshot.child("volume").getValue(Integer.class));
//                maxVolume = Math.max(maxVolume, snapshot.child("volume").getValue(Integer.class));
//            }
//
    //            leftAxis.setAxisMaximum((float) (maxVolume + 20));
//setDataList(dataList);

//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//        }
//    };

    public static HistoryFragment getInstance() {
        if (instance == null) {
            instance = new HistoryFragment();
        }
        return instance;
    }

    private static class GetMaxVolumeAsync extends AsyncTask<Long, Integer, List<MaxVolume>> {
        private MaxVolumeDAO maxVolumeDAO;


        public GetMaxVolumeAsync(AuditionTechApplication application) {
            maxVolumeDAO = application.getAppDatabase().maxVolumeDAO();

        }

        @Override
        protected List<MaxVolume> doInBackground(Long... longs) {
            Long startTimestamp = longs[0];
            Long endTimestamp = longs[1];
            return maxVolumeDAO.getMaxVolumeWithin(startTimestamp, endTimestamp);

        }
    }

    private static class GetExposureAsync extends AsyncTask<Long, Integer, Double> {
        private ExposureDAO exposureDAO;

        public GetExposureAsync(AuditionTechApplication application) {
            exposureDAO = application.getAppDatabase().exposureDAO();
        }

        @Override
        protected Double doInBackground(Long... longs) {
            Long startTimestamp = longs[0];
            Long endTimestamp = longs[1];
            return exposureDAO.getExposureSum(startTimestamp, endTimestamp);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //userReference = FirebaseDatabase.getInstance().getReference(getID());
        dateViewModel = ViewModelProviders.of(this).get(DateViewModel.class);

        final Observer<Date> dateObserver = new Observer<Date>() {
            @Override
            public void onChanged(Date date) {

                activity.setTitle(dateFormat.format(date));
                if (compactCalendarView != null) {
                    compactCalendarView.setCurrentDate(date);
                }

            }
        };
        dateViewModel.getDate().observe(this, dateObserver);

    }

//    private Query getQuery(Date date) {
//        return getQuery(date, "max_ambient_volume");
//    }
//
//    private Query getQuery(Date date, String relativePathUnderUserId) {
//        return userReference
//                .child(relativePathUnderUserId)
//                .orderByChild("timestamp")
//                .startAt(getDateStartMilliSec(date))
//                .endAt(getNextDateStartMilliSec(date));
//    }

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        chart = getView().findViewById(R.id.chart);
        setChart();
        exposureText = getView().findViewById(R.id.exposure_text);
        final Observer<Date> maxVolumeObserver = new Observer<Date>() {
            @Override
            public void onChanged(Date date) {
//                if (query != null) {
//                    query.removeEventListener(listener);
//                }
//                Log.d("query time start", String.valueOf(System.currentTimeMillis()));
//                query = getQuery(date);
//                Log.d("query time end", String.valueOf(System.currentTimeMillis()));
//                query.addValueEventListener(listener);

                Long[] params = getTimestampParams(date);
                try {
                    maxVolumes = new GetMaxVolumeAsync((AuditionTechApplication) (getActivity().getApplication()))
                            .execute(params).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                List<Entry> dataList = new ArrayList<>();
                long startEpoch = getDateStartMilliSec(dateViewModel.getDate().getValue());
                Log.d("start epoch", String.valueOf(startEpoch));
                long endEpoch = getNextDateStartMilliSec(dateViewModel.getDate().getValue());
                Log.d("end epoch", String.valueOf(endEpoch));
                for (long epoch = startEpoch;
                     epoch <= endEpoch;
                     epoch += FIVE_MINUTES_IN_MILLI_SEC) {
                    dataList.add(new Entry(epoch, 0));
                }

                // add data into entry.
                int maxVolumeLevel = 0;
                for (MaxVolume maxVolumeEntry : maxVolumes) {

                    Log.d("snapshot timestamp", String.valueOf(maxVolumeEntry.getTimestamp()));
                    Log.d("snapshot volume", String.valueOf(maxVolumeEntry.getVolume()));
                    int index = (int) ((maxVolumeEntry.getTimestamp() - startEpoch) / (FIVE_MINUTES_IN_MILLI_SEC));
                    Log.d("snapshot index", String.valueOf(index));
                    dataList.get(index).setY(maxVolumeEntry.getVolume());
                    maxVolumeLevel = Math.max(maxVolumeLevel, maxVolumeEntry.getVolume());
                }

                leftAxis.setAxisMaximum((float) (maxVolumeLevel + 20));

                setDataList(dataList);
            }
        };
        dateViewModel.getDate().observe(this, maxVolumeObserver);

        final Observer<Date> exposureObserver = new Observer<Date>() {
            @Override
            public void onChanged(Date date) {
                Long[] params = getTimestampParams(date);
                try {
                    exposureText.setText(
                            String.valueOf(
                                    new GetExposureAsync(
                                            (AuditionTechApplication) getActivity().getApplication())
                                            .execute(params).get()));
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        dateViewModel.getDate().observe(this,exposureObserver);
        timestampText = getView().findViewById(R.id.timestamp_text);
        decibelText = getView().findViewById(R.id.decibel_text);
    }

    private Long[] getTimestampParams(Date date) {
        Long startTimestamp = getDateStartMilliSec(date);
        Long endTimestamp = getNextDateStartMilliSec(date);
        return new Long[]{startTimestamp, endTimestamp};
    }

    @Override
    public void onResume() {
        super.onResume();
        setToolbar();
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
        chart.setOnChartValueSelectedListener(this);
        chart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE);
        //chart.setViewPortOffsets(-10f, -10f, -10f, -10f);
        int secondaryColor = ContextCompat.getColor(getContext(), R.color.secondaryColor);

        Legend l = chart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTypeface(tfLight);
        xAxis.setTextSize(14f);
        //xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(secondaryColor);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(4f);
        xAxis.setYOffset(-2f);
        xAxis.setValueFormatter(new ValueFormatter() {

            private final DateTimeFormatter mFormat = DateTimeFormatter.ofPattern("HH:mm");

            @Override
            public String getFormattedValue(float value) {
                ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli((long) value), ZoneId.systemDefault());
                return mFormat.format(zdt);
            }
        });

        leftAxis = chart.getAxisLeft();
        //leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        //leftAxis.setTypeface(tfLight);
        //leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setTextSize(14f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(170f);
        leftAxis.setYOffset(-9f);

        leftAxis.setTextColor(secondaryColor);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.d("entry", String.valueOf(e.getX()));
        ZonedDateTime zonedDateTime = ZonedDateTime.
                ofInstant(Instant.ofEpochMilli((long) e.getX()), ZoneId.systemDefault());
        String localTime = DateTimeFormatter.ofPattern("hh:mm").format(zonedDateTime);
        timestampText.setText(localTime);
        if (e.getY() == 0f) {
            decibelText.setText("N/A");
        } else {
            decibelText.setText((int) e.getY() + "dB");
        }
    }

    @Override
    public void onNothingSelected() {

    }

    private void setDataList(List<Entry> entryList) {
        LineDataSet set1 = new LineDataSet(entryList, "Max Volume");

        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        int primaryLightColor = ContextCompat.getColor(getContext(), R.color.primaryLightColor);
        set1.setColor(primaryLightColor);
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(2f);
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
        chart.setData(data);
        chart.invalidate();
    }
}

