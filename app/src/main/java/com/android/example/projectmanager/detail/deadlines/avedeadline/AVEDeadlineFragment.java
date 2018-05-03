package com.android.example.projectmanager.detail.deadlines.avedeadline;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.projectmanager.detail.deadlines.Deadline;
import com.android.example.projectmanager.R;
import com.android.example.projectmanager.reminder.NotificationPublisher;
import com.android.example.projectmanager.utilities.Analytics;
import com.android.example.projectmanager.widget.TaskListWidget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AVEDeadlineFragment extends Fragment {

    private static final String ARG_PARAM1 = "projectKey";
    private static final String ARG_PARAM2 = "deadline";
    private static final String ARG_PARAM3 = "deadlineKey";

    private String projectKey;
    private Deadline deadline;
    private String deadlineKey;

    @BindView(R.id.panel_deadline_title) LinearLayout deadlineTitlePanel;
    @BindView(R.id.tv_deadline_title_requirement) TextView deadlineTitleRequirement;
    @BindView(R.id.et_deadline_title) EditText deadlineTitleEditText;
    @BindView(R.id.tv_deadline_title) TextView deadlineTitleTextView;

    @BindView(R.id.btn_save_deadline) Button saveDeadlineButton;

    @BindView(R.id.panel_deadline) LinearLayout deadlinePanel;
    @BindView(R.id.tv_deadline_optional_requirement) TextView deadlineRequirement;
    @BindView(R.id.tv_deadline_date) TextView deadlineDateTextView;
    @BindView(R.id.tv_deadline_time) TextView deadlineTimeTextView;

    @BindView(R.id.panel_notification) LinearLayout notificationPanel;
    @BindView(R.id.tv_deadline_notificaton_requirement) TextView notificationRequirement;
    @BindView(R.id.switch_notification) Switch notificationSwitch;

    public String dateStr;
    public String timeStr;

    public boolean dateIsSet;
    public boolean timeIsSet;

    private FirebaseDatabase database;
    private DatabaseReference deadlineListRef;

    private String user;

    private boolean isDeadlineTitleInViewOnlyMode;
    private boolean isDeadlinePanelInViewOnlyMode;
    private boolean isNotificationPanelInViewOnlyMode;

    private boolean hasNotification;

    private boolean isLastDeadline;


    public AVEDeadlineFragment() {
        // Required empty public constructor
    }


    public static AVEDeadlineFragment newInstance(String projectKey, Deadline deadline, String deadlineKey) {
        AVEDeadlineFragment fragment = new AVEDeadlineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, projectKey);
        args.putParcelable(ARG_PARAM2, deadline);
        args.putString(ARG_PARAM3, deadlineKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectKey = getArguments().getString(ARG_PARAM1);
            deadline = getArguments().getParcelable(ARG_PARAM2);
            deadlineKey = getArguments().getString(ARG_PARAM3);
        }

        if(savedInstanceState != null) {
            isDeadlineTitleInViewOnlyMode = savedInstanceState.getBoolean("isDeadlineTitleInViewOnlyMode");
            isDeadlinePanelInViewOnlyMode = savedInstanceState.getBoolean("isDeadlinePanelInViewOnlyMode");
            isNotificationPanelInViewOnlyMode = savedInstanceState.getBoolean("isNotificationPanelInViewOnlyMode");

            dateIsSet = savedInstanceState.getBoolean("dateIsSet");
            timeIsSet = savedInstanceState.getBoolean("timeIsSet");
            dateStr = savedInstanceState.getString("dateStr");
            timeStr = savedInstanceState.getString("timeStr");
        } else {
            if(deadline != null) {
                isDeadlineTitleInViewOnlyMode = true;
                isDeadlinePanelInViewOnlyMode = true;
                isNotificationPanelInViewOnlyMode = true;

                dateIsSet = true;
                timeIsSet = true;
                dateStr = deadline.getDate();
                timeStr = deadline.getTime();

                if(deadline.getNotificationId() != -1){
                    hasNotification = true;
                } else {
                    hasNotification = false;
                }

                isLastDeadline = getActivity().getIntent().getBooleanExtra("isLastDeadline", false);
            } else {
                isDeadlineTitleInViewOnlyMode = false;
                isDeadlinePanelInViewOnlyMode = false;
                isNotificationPanelInViewOnlyMode = false;

                dateIsSet = false;
                timeIsSet = false;
                dateStr = getString(R.string.label_na);
                timeStr = getString(R.string.label_na);
            }
        }

        user = FirebaseAuth.getInstance().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ave_deadline, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, getView());

        deadlineDateTextView.setText(dateStr);
        deadlineDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDeadlinePanelInViewOnlyMode) {
                    setEditModeForDeadlinePanel();
                }
                DialogFragment datePicker = DatePickerFragment.newInstance(dateStr);
                datePicker.setTargetFragment(AVEDeadlineFragment.this, 0);
                datePicker.show(getFragmentManager(), "datePicker");
            }
        });

        deadlineTimeTextView.setText(timeStr);
        deadlineTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dateIsSet){
                    if(isDeadlinePanelInViewOnlyMode) {
                        setEditModeForDeadlinePanel();
                    }
                    DialogFragment timePicker = TimePickerFragment.newInstance(timeStr);
                    timePicker.setTargetFragment(AVEDeadlineFragment.this, 0);
                    timePicker.show(getFragmentManager(), "timePicker");
                } else {
                    Toast.makeText(getContext(),getString(R.string.msg_set_date_first), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(hasNotification) {
            notificationSwitch.setChecked(true);
        }

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(isNotificationPanelInViewOnlyMode) {
                    setEditModeForNotificationPanel();
                }
            }
        });

        if(isDeadlineTitleInViewOnlyMode) {
            setReadOnlyModeForDeadlineTitle();
        }

        if(isDeadlinePanelInViewOnlyMode) {
            setReadOnlyModeForDeadlinePanel();
        }

        if(isNotificationPanelInViewOnlyMode) {
            setReadOnlyModeForNotificationPanel();
        }

        showButton();

        database = FirebaseDatabase.getInstance();
        deadlineListRef = database.getReference(user).child("deadlines");
    }

    private void showButton(){
        if(buttonShouldBeVisible()) {
            saveDeadlineButton.setVisibility(View.VISIBLE);
            saveDeadlineButton.setText(getButtonText());
            saveDeadlineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveDeadlineToFirebase();
                }
            });
        } else {
            saveDeadlineButton.setVisibility(View.GONE);
        }
    }

    private boolean buttonShouldBeVisible() {
        if(isDeadlineTitleInViewOnlyMode && isDeadlinePanelInViewOnlyMode && isNotificationPanelInViewOnlyMode){
            return false;
        } else if( isDeadlineTitleInViewOnlyMode || isDeadlinePanelInViewOnlyMode || isNotificationPanelInViewOnlyMode) {
            return true;
        } else {
            return true;
        }
    }

    private String getButtonText() {
        if(deadline == null) {
            return getString(R.string.label_add_deadline);
        } else {
            return getString(R.string.label_save_changes);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("dateIsSet", dateIsSet);
        outState.putBoolean("timeIsSet", timeIsSet);
        outState.putString("dateStr", dateStr);
        outState.putString("timeStr", timeStr);

        outState.putBoolean("isDeadlineTitleInViewOnlyMode", isDeadlineTitleInViewOnlyMode);
        outState.putBoolean("isDeadlinePanelInViewOnlyMode", isDeadlinePanelInViewOnlyMode);
        outState.putBoolean("isNotificationPanelInViewOnlyMode", isNotificationPanelInViewOnlyMode);

        super.onSaveInstanceState(outState);
    }

    private void setReadOnlyModeForDeadlineTitle() {
        deadlineTitleEditText.setVisibility(View.GONE);

        deadlineTitleTextView.setVisibility(View.VISIBLE);
        deadlineTitleTextView.setText(deadline.getDeadlineTitle());

        deadlineTitleRequirement.setVisibility(View.GONE);

        deadlineTitlePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditModeForDeadlineTitle();
            }
        });
    }

    private void setEditModeForDeadlineTitle() {
        deadlineTitleTextView.setVisibility(View.GONE);

        deadlineTitleEditText.setVisibility(View.VISIBLE);

        deadlineTitleRequirement.setVisibility(View.VISIBLE);

        deadlineTitleEditText.setText(deadline.getDeadlineTitle());
        deadlineTitleEditText.requestFocus();

        deadlineTitlePanel.setOnClickListener(null);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(deadlineTitleEditText,InputMethodManager.SHOW_IMPLICIT);

        isDeadlineTitleInViewOnlyMode = false;

        showButton();
    }

    private void setReadOnlyModeForDeadlinePanel() {
        deadlineRequirement.setVisibility(View.GONE);
    }

    private void setEditModeForDeadlinePanel() {
        deadlineRequirement.setVisibility(View.VISIBLE);

        isDeadlinePanelInViewOnlyMode = false;

        showButton();
    }

    private void setReadOnlyModeForNotificationPanel() {
        notificationRequirement.setVisibility(View.GONE);
    }

    private void setEditModeForNotificationPanel() {
        notificationRequirement.setVisibility(View.VISIBLE);

        isNotificationPanelInViewOnlyMode = false;

        showButton();
    }

    public void saveDeadlineToFirebase() {

        String title;
        if(deadlineTitleEditText.getVisibility()==View.VISIBLE) {
            title = deadlineTitleEditText.getText().toString();
        } else {
            title = deadlineTitleTextView.getText().toString();
        }

        if(title.isEmpty()){
            Toast.makeText(getContext(), R.string.msg_no_deadline_title, Toast.LENGTH_SHORT).show();
            return;
        }

        if(dateStr.equals(getString(R.string.label_na))){
            Toast.makeText(getContext(), R.string.msg_no_deadline_date, Toast.LENGTH_SHORT).show();
            return;
        }

        if(timeStr.equals(getString(R.string.label_na))){
            Toast.makeText(getContext(), R.string.msg_no_deadline_time, Toast.LENGTH_SHORT).show();
            return;
        }

        String deadlineStr = dateStr + " - " + timeStr;

        if(deadline != null) {
            deadline.setDeadlineTitle(title);
            deadline.setDeadlineStr(deadlineStr);

            if(notificationSwitch.isChecked()) {
                if(hasNotification) {
                    int notificationId = deadline.getNotificationId();
                    scheduleNotification(getNotification(title), notificationId);
                } else {
                    int notificationId = (int) System.currentTimeMillis();
                    deadline.setNotificationId(notificationId);
                    scheduleNotification(getNotification(title), notificationId);
                }
            } else {
                if(hasNotification) {
                    int notificationId = deadline.getNotificationId();
                    deadline.setNotificationId(-1);
                    cancelNotification(getNotification(title), notificationId);
                }
            }


            deadlineListRef.child(deadlineKey).setValue(deadline);

            Analytics.logEventDeadlineSaved(getContext(), deadline, false);

            if(isLastDeadline) {
                setLastDeadlineForProject();
            }
        } else {

            int notificationId = -1;
            if(notificationSwitch.isChecked()) {
                notificationId = (int) System.currentTimeMillis();
                scheduleNotification(getNotification(title), notificationId);
            }

            Deadline deadline = new Deadline(projectKey, title, deadlineStr, notificationId);
            deadlineListRef.push().setValue(deadline);
            Analytics.logEventDeadlineSaved(getContext(), deadline, true);

            setLastDeadlineForProject();
        }

        Intent intent = new Intent();
        intent.setAction(TaskListWidget.ACTION_DATA_UPDATED);
        getActivity().sendBroadcast(intent);

        getActivity().finish();

    }

    private void setLastDeadlineForProject() {

        Query projectDeadlinesQuery = deadlineListRef.orderByChild("projectKey").equalTo(projectKey);

        projectDeadlinesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Deadline> deadlines = new ArrayList<>();

                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    Deadline deadline = dataSnapshot1.getValue(Deadline.class);

                    deadlines.add(deadline);
                }

                Collections.sort(deadlines, new Comparator<Deadline>() {
                    @Override
                    public int compare(Deadline d1, Deadline d2) {

                        SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy - hh : mm");
                        try {
                            Date date1 = sdf.parse(d1.getDeadlineStr());
                            Date date2 = sdf.parse(d2.getDeadlineStr());

                            return date1.compareTo(date2);
                        } catch (ParseException e) {
                            return 0;
                        }

                    }
                });

                if(deadlines.size()!=0) {
                    database.getReference(user).child("projects").child(projectKey).child("deadline").setValue(deadlines.get(deadlines.size()-1).getDeadlineStr());
                } else {
                    database.getReference(user).child("projects").child(projectKey).child("deadline").setValue(getString(R.string.label_na));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void scheduleNotification(Notification notification, int notificationId) {

        Intent notificationIntent = new Intent(getContext(), NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = getTimeInMillis() - (1000 * 60 * 60 * 24);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC, futureInMillis, pendingIntent);

    }

    private void cancelNotification(Notification notification, int notificatioId) {

        Intent notificationIntent = new Intent(getContext(), NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificatioId);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), notificatioId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);

    }

    private Notification getNotification(String deadlineTitle) {

        Notification.Builder builder = new Notification.Builder(getContext());
        builder.setContentTitle(getActivity().getIntent().getStringExtra("projectName"));
        builder.setContentText(deadlineTitle + ":\n" + getString(R.string.msg_notification) );
        builder.setSmallIcon(R.drawable.ic_notifications_24dp);

        return builder.build();

    }

    private long getTimeInMillis() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy - hh : mm");
        try {
            Date date = sdf.parse(dateStr + " - " + timeStr);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

}
