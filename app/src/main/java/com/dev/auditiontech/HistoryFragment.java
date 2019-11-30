package com.dev.auditiontech;

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

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HistoryFragment extends Fragment implements ToolbarCustomizable {
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
            }
        };
        dateViewModel.getDate().observe(this,dateObserver);

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
}

