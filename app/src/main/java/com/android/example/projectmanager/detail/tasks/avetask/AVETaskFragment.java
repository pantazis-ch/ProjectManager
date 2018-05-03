package com.android.example.projectmanager.detail.tasks.avetask;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.projectmanager.detail.tasks.Task;
import com.android.example.projectmanager.R;
import com.android.example.projectmanager.utilities.Analytics;
import com.android.example.projectmanager.widget.TaskListWidget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AVETaskFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private String projectKey;
    private Task task;
    private String taskKey;

    @BindView(R.id.panel_task_description) LinearLayout taskDescriptionPanel;
    @BindView(R.id.tv_task_description_requirement) TextView taskDescriptionRequirement;
    @BindView(R.id.et_task_description) EditText taskDescriptionEditText;
    @BindView(R.id.tv_task_description) TextView taskDescriptionTextView;

    @BindView(R.id.btn_save_task) Button saveTaskButton;

    @BindView(R.id.panel_task_note) LinearLayout taskNotePanel;
    @BindView(R.id.tv_task_note_requirement) TextView taskNoteRequirement;
    @BindView(R.id.et_task_note) EditText taskNoteEditText;
    @BindView(R.id.tv_task_note) TextView taskNoteTextView;

    private boolean isTaskDescriptionInViewOnlyMode;
    private boolean isTaskNoteInViewOnlyMode;

    private FirebaseDatabase database;
    private DatabaseReference taskListRef;

    private String user;


    public AVETaskFragment() {
        // Required empty public constructor
    }


    public static AVETaskFragment newInstance(String projectKey, Task task, String taskKey) {
        AVETaskFragment fragment = new AVETaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, projectKey);
        args.putParcelable(ARG_PARAM2, task);
        args.putString(ARG_PARAM3, taskKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectKey = getArguments().getString(ARG_PARAM1);
            task = getArguments().getParcelable(ARG_PARAM2);
            taskKey = getArguments().getString(ARG_PARAM3);
        }

        if(savedInstanceState != null) {
            isTaskDescriptionInViewOnlyMode = savedInstanceState.getBoolean("isTaskDescriptionInViewOnlyMode");
            isTaskNoteInViewOnlyMode = savedInstanceState.getBoolean("isTaskNoteInViewOnlyMode");
        } else {
            if(task != null) {
                isTaskDescriptionInViewOnlyMode = true;
                isTaskNoteInViewOnlyMode = true;
            } else {
                isTaskDescriptionInViewOnlyMode = false;
                isTaskNoteInViewOnlyMode = false;
            }
        }

        user = FirebaseAuth.getInstance().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ave_task, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, getView());

        if(isTaskDescriptionInViewOnlyMode) {
            setReadOnlyModeForTaskDescription();
        }

        if(isTaskNoteInViewOnlyMode) {
            setReadOnlyModeForTaskNote();
        }

        showButtonIfNeeded();

        database = FirebaseDatabase.getInstance();
        taskListRef = database.getReference(user).child("tasks");

    }

    private void showButtonIfNeeded(){
        if(buttonShouldBeVisible()) {
            saveTaskButton.setVisibility(View.VISIBLE);
            saveTaskButton.setText(getButtonText());
            saveTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveTaskToFirebase();
                }
            });
        } else {
            saveTaskButton.setVisibility(View.GONE);
        }
    }

    private boolean buttonShouldBeVisible() {
        if(isTaskDescriptionInViewOnlyMode && isTaskNoteInViewOnlyMode){
            return false;
        } else if( isTaskDescriptionInViewOnlyMode || isTaskNoteInViewOnlyMode ) {
            return true;
        } else {
            return true;
        }
    }

    private String getButtonText() {
        if(task == null) {
            return getString(R.string.label_add_task);
        } else {
            return getString(R.string.label_save_changes);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isTaskDescriptionInViewOnlyMode", isTaskDescriptionInViewOnlyMode);
        outState.putBoolean("isTaskNoteInViewOnlyMode", isTaskNoteInViewOnlyMode);

        super.onSaveInstanceState(outState);
    }

    private void setReadOnlyModeForTaskDescription() {
        taskDescriptionEditText.setVisibility(View.GONE);

        taskDescriptionTextView.setVisibility(View.VISIBLE);
        taskDescriptionTextView.setText(task.getTaskName());

        taskDescriptionRequirement.setVisibility(View.GONE);

        taskDescriptionPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditModeForTaskDescription();
            }
        });
    }

    private void setEditModeForTaskDescription() {
        taskDescriptionTextView.setVisibility(View.GONE);

        taskDescriptionEditText.setVisibility(View.VISIBLE);

        taskDescriptionRequirement.setVisibility(View.VISIBLE);

        taskDescriptionEditText.setText(task.getTaskName());
        taskDescriptionEditText.requestFocus();

        taskDescriptionPanel.setOnClickListener(null);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(taskDescriptionEditText,InputMethodManager.SHOW_IMPLICIT);

        isTaskDescriptionInViewOnlyMode = false;

        showButtonIfNeeded();
    }

    private void setReadOnlyModeForTaskNote() {
        taskNoteEditText.setVisibility(View.GONE);

        taskNoteTextView.setVisibility(View.VISIBLE);
        taskNoteTextView.setText(task.getTaskNote());

        taskNoteRequirement.setVisibility(View.GONE);

        taskNotePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditModeForTaskNote();
            }
        });
    }

    private void setEditModeForTaskNote() {
        taskNoteTextView.setVisibility(View.GONE);

        taskNoteEditText.setVisibility(View.VISIBLE);

        taskNoteRequirement.setVisibility(View.VISIBLE);

        taskNoteEditText.setText(task.getTaskNote());
        taskNoteEditText.requestFocus();

        taskNotePanel.setOnClickListener(null);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(taskNoteEditText,InputMethodManager.SHOW_IMPLICIT);

        isTaskNoteInViewOnlyMode = false;

        showButtonIfNeeded();
    }

    public void saveTaskToFirebase() {
        String name;
        if(taskDescriptionEditText.getVisibility()==View.VISIBLE) {
            name = taskDescriptionEditText.getText().toString();
        } else {
            name = taskDescriptionTextView.getText().toString();
        }

        String note;
        if(taskNoteEditText.getVisibility()==View.VISIBLE) {
            note = taskNoteEditText.getText().toString();
        } else {
            note = taskNoteTextView.getText().toString();
        }

        if(name.isEmpty()){
            Toast.makeText(getContext(), R.string.msg_no_task, Toast.LENGTH_SHORT).show();
            return;
        }

        if(task != null) {
            task.setTaskName(name);
            task.setTaskNote(note);
            taskListRef.child(taskKey).setValue(task);
            Analytics.logEventTaskSaved(getContext(), task, false);
        } else {
            int taskNumber = getActivity().getIntent().getIntExtra("taskNumber", 0);

            Task task = new Task(projectKey, name, note, false, taskNumber);
            taskListRef.push().setValue(task);
            Analytics.logEventTaskSaved(getContext(), task, true);
        }

        Intent intent = new Intent();
        intent.setAction(TaskListWidget.ACTION_DATA_UPDATED);
        getActivity().sendBroadcast(intent);

        getActivity().finish();

    }

}
