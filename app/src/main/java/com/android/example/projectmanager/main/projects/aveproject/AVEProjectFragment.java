package com.android.example.projectmanager.main.projects.aveproject;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.projectmanager.detail.deadlines.Deadline;
import com.android.example.projectmanager.main.projects.Project;
import com.android.example.projectmanager.R;
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
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AVEProjectFragment extends Fragment {

    private static final String ARG_PARAM1 = "project";
    private static final String ARG_PARAM2 = "projectKey";

    private Project project;
    private String projectKey;

    @BindView(R.id.panel_project_name) LinearLayout projectNamePanel;
    @BindView(R.id.tv_project_name_requirement) TextView projectNameRequirement;
    @BindView(R.id.et_project_name) EditText projectNameEditText;
    @BindView(R.id.tv_project_name) TextView projectNameTextView;

    @BindView(R.id.panel_project_tag) LinearLayout projectTagPanel;
    @BindView(R.id.tv_project_tag_requirement) TextView projectTagRequirement;
    @BindView(R.id.et_project_tag) EditText projectTagEditText;
    @BindView(R.id.tv_project_tag) TextView projectTagTextView;

    @BindView(R.id.panel_deadline) LinearLayout projectDeadlinePanel;
    @BindView(R.id.tv_deadline_optional_requirement) TextView projectDeadlineOptionalLabel;
    @BindView(R.id.tv_deadline_required_requirement) TextView projectDeadlineRequiredLabel;
    @BindView(R.id.btn_reset_deadline) ImageView btnResetDeadline;
    @BindView(R.id.tv_project_deadline_date) TextView dateTextView;
    @BindView(R.id.tv_project_deadline_time) TextView timeTextView;

    @BindView(R.id.btn_save_project) Button saveProjectButton;

    @BindView(R.id.panel_project_completed) LinearLayout projectCompletedPanel;
    @BindView(R.id.tv_project_completed_requirement) TextView projectCompletedRequirement;
    @BindView(R.id.checkbox_project_completed) CheckBox projectCompletedCheckbox;

    @BindView(R.id.panel_project_description) LinearLayout projectDescriptionPanel;
    @BindView(R.id.tv_project_description_requirement) TextView projectDescriptionRequirement;
    @BindView(R.id.et_project_description) EditText projectDescriptionEditText;
    @BindView(R.id.tv_project_description) TextView projectDescriptionTextView;

    private FirebaseDatabase database;
    private DatabaseReference projectListRef;
    private DatabaseReference deadlineListRef;

    private String user;

    public boolean dateIsSet;
    public boolean timeIsSet;

    public String dateStr;
    public String timeStr;

    private boolean isProjectNameInViewOnlyMode;
    private boolean isProjectTagInViewOnlyMode;
    private boolean isProjectDeadlineInViewOnlyMode;
    private boolean isProjectCompletedInViewOnlyMode;
    private boolean isProjectDescriptionInViewOnlyMode;

    public boolean deadlineRequired;
    public boolean isResetDeadlineAvailable;

    private ArrayList<Deadline> deadlines;
    private HashMap<Deadline,String> deadlineKeys;
    private Query projectDeadlinesQuery;


    public AVEProjectFragment() {
        // Required empty public constructor
    }

    public static AVEProjectFragment newInstance(@Nullable Project project, @Nullable String projectKey) {
        AVEProjectFragment fragment = new AVEProjectFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, project);
        args.putString(ARG_PARAM2, projectKey);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            project = getArguments().getParcelable(ARG_PARAM1);
            projectKey = getArguments().getString(ARG_PARAM2);
        }
        if(savedInstanceState != null) {
            isProjectNameInViewOnlyMode = savedInstanceState.getBoolean("isProjectNameInViewOnlyMode");
            isProjectTagInViewOnlyMode = savedInstanceState.getBoolean("isProjectTagInViewOnlyMode");
            isProjectDeadlineInViewOnlyMode = savedInstanceState.getBoolean("isProjectDeadlineInViewOnlyMode");
            isProjectCompletedInViewOnlyMode = savedInstanceState.getBoolean("isProjectCompletedInViewOnlyMode");
            isProjectDescriptionInViewOnlyMode = savedInstanceState.getBoolean("isProjectDescriptionInViewOnlyMode");

            deadlineRequired = savedInstanceState.getBoolean("deadlineRequired");
            isResetDeadlineAvailable = savedInstanceState.getBoolean("isResetDeadlineAvailable");

            dateIsSet = savedInstanceState.getBoolean("dateIsSet");
            timeIsSet = savedInstanceState.getBoolean("timeIsSet");
            dateStr = savedInstanceState.getString("dateStr");
            timeStr = savedInstanceState.getString("timeStr");
        } else {
            if(project != null) {
                isProjectNameInViewOnlyMode = true;
                isProjectTagInViewOnlyMode = true;
                isProjectDeadlineInViewOnlyMode = true;
                isProjectCompletedInViewOnlyMode = true;
                isProjectDescriptionInViewOnlyMode = true;

                if(project.getDeadline().equals(getString(R.string.label_na))) {
                    dateIsSet = false;
                    timeIsSet = false;
                    deadlineRequired = false;
                    isResetDeadlineAvailable = true;
                } else {
                    dateIsSet = true;
                    timeIsSet = true;
                    deadlineRequired = true;
                    isResetDeadlineAvailable = false;
                }

                dateStr = project.getDate();
                timeStr = project.getTime();
            } else {
                isProjectNameInViewOnlyMode = false;
                isProjectTagInViewOnlyMode = false;
                isProjectDeadlineInViewOnlyMode = false;
                isProjectCompletedInViewOnlyMode = false;
                isProjectDescriptionInViewOnlyMode = false;

                dateIsSet = false;
                timeIsSet = false;
                dateStr = getString(R.string.label_na);
                timeStr = getString(R.string.label_na);
                deadlineRequired = false;

                isResetDeadlineAvailable = true;
            }
        }

        user = FirebaseAuth.getInstance().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this aveProjectFragment
        return inflater.inflate(R.layout.fragment_ave_project, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, getView());

        dateTextView.setText(dateStr);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isProjectDeadlineInViewOnlyMode) {
                    setEditModeForDeadlinePanel();
                }
                DialogFragment datePicker = DatePickerFragment.newInstance(dateStr);
                datePicker.setTargetFragment(AVEProjectFragment.this, 0);
                datePicker.show(getFragmentManager(), "datePicker");
            }
        });

        timeTextView.setText(timeStr);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dateIsSet){
                    if(isProjectDeadlineInViewOnlyMode) {
                        setEditModeForDeadlinePanel();
                    }
                    DialogFragment timePicker = TimePickerFragment.newInstance(timeStr);
                    timePicker.setTargetFragment(AVEProjectFragment.this, 0);
                    timePicker.show(getFragmentManager(), "timePicker");
                } else {
                    Toast.makeText(getContext(), R.string.msg_set_date_first, Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(isProjectNameInViewOnlyMode) {
            setReadOnlyModeForProjectName();
        }

        if(isProjectTagInViewOnlyMode) {
            setReadOnlyModeForProjectTag();
        }

        if(isProjectDeadlineInViewOnlyMode) {
            setReadOnlyModeForDeadlinePanel();
        } else {
            if (deadlineRequired) {
                projectDeadlineOptionalLabel.setVisibility(View.GONE);
                projectDeadlineRequiredLabel.setVisibility(View.VISIBLE);
            }
            if (isResetDeadlineAvailable) {
                btnResetDeadline.setVisibility(View.VISIBLE);
                btnResetDeadline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resetDeadline();
                    }
                });
            }
        }

        if(isProjectCompletedInViewOnlyMode) {
            setReadOnlyModeForProjectCompletedPanel();
        }

        if(isProjectDescriptionInViewOnlyMode) {
            setReadOnlyModeForProjectDescription();
        }

        if(project != null) {
            projectCompletedCheckbox.setChecked(project.getStatus());
        } else {
            projectCompletedCheckbox.setChecked(false);
        }
        projectCompletedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(isProjectCompletedInViewOnlyMode) {
                    setEditModeForProjectCompletedPanel();
                }
            }
        });

        showButtonIfNeeded();

        database = FirebaseDatabase.getInstance();
        projectListRef = database.getReference(user).child("projects");

        deadlineListRef = database.getReference(user).child("deadlines");

        projectDeadlinesQuery = deadlineListRef.orderByChild("projectKey").equalTo(projectKey);

        projectDeadlinesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                deadlines = new ArrayList<>();
                deadlineKeys = new HashMap<>();

                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    Deadline deadline = dataSnapshot1.getValue(Deadline.class);

                    deadlines.add(deadline);
                    deadlineKeys.put(deadline, dataSnapshot1.getKey());
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void resetDeadline() {
        dateIsSet = false;
        timeIsSet = false;
        dateStr = getString(R.string.label_na);
        timeStr = getString(R.string.label_na);
        deadlineRequired = false;

        dateTextView.setText(dateStr);
        timeTextView.setText(timeStr);

        projectDeadlineOptionalLabel.setVisibility(View.VISIBLE);
        projectDeadlineRequiredLabel.setVisibility(View.GONE);
    }

    private void showButtonIfNeeded(){
        if(buttonShouldBeVisible()) {
            saveProjectButton.setVisibility(View.VISIBLE);
            saveProjectButton.setText(getButtonText());
            saveProjectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveProjectToFirebase();
                }
            });
        } else {
            saveProjectButton.setVisibility(View.GONE);
        }
    }

    private boolean buttonShouldBeVisible() {
        if(isProjectNameInViewOnlyMode && isProjectTagInViewOnlyMode && isProjectDeadlineInViewOnlyMode
                && isProjectCompletedInViewOnlyMode && isProjectDescriptionInViewOnlyMode){
            return false;
        } else if( isProjectNameInViewOnlyMode || isProjectTagInViewOnlyMode || isProjectDeadlineInViewOnlyMode
                || isProjectCompletedInViewOnlyMode || isProjectDescriptionInViewOnlyMode) {
            return true;
        } else {
            return true;
        }
    }

    private String getButtonText() {
        if(project == null) {
            return getString(R.string.label_add_project);
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

        outState.putBoolean("isResetDeadlineAvailable", isResetDeadlineAvailable);

        outState.putBoolean("isProjectNameInViewOnlyMode", isProjectNameInViewOnlyMode);
        outState.putBoolean("isProjectTagInViewOnlyMode", isProjectTagInViewOnlyMode);
        outState.putBoolean("isProjectDeadlineInViewOnlyMode", isProjectDeadlineInViewOnlyMode);
        outState.putBoolean("isProjectCompletedInViewOnlyMode", isProjectCompletedInViewOnlyMode);
        outState.putBoolean("isProjectDescriptionInViewOnlyMode", isProjectDescriptionInViewOnlyMode);
        outState.putBoolean("deadlineRequired", deadlineRequired);

        super.onSaveInstanceState(outState);
    }

    private void setReadOnlyModeForProjectName() {
        projectNameEditText.setVisibility(View.GONE);

        projectNameTextView.setVisibility(View.VISIBLE);
        projectNameTextView.setText(project.getName());

        projectNameRequirement.setVisibility(View.GONE);

        projectNamePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditModeForProjectName();
            }
        });
    }

    private void setEditModeForProjectName() {
        projectNameTextView.setVisibility(View.GONE);

        projectNameEditText.setVisibility(View.VISIBLE);

        projectNameRequirement.setVisibility(View.VISIBLE);

        projectNameEditText.setText(project.getName());
        projectNameEditText.requestFocus();

        projectNamePanel.setOnClickListener(null);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(projectNameEditText,InputMethodManager.SHOW_IMPLICIT);

        isProjectNameInViewOnlyMode = false;

        showButtonIfNeeded();
    }

    private void setReadOnlyModeForProjectTag() {
        projectTagEditText.setVisibility(View.GONE);

        projectTagTextView.setVisibility(View.VISIBLE);
        projectTagTextView.setText(project.getTag());

        projectTagRequirement.setVisibility(View.GONE);

        projectTagPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditModeForProjectTag();
            }
        });

        isProjectTagInViewOnlyMode = true;
    }

    private void setEditModeForProjectTag() {
        projectTagTextView.setVisibility(View.GONE);

        projectTagEditText.setVisibility(View.VISIBLE);

        projectTagRequirement.setVisibility(View.VISIBLE);

        projectTagEditText.setText(project.getTag());
        projectTagEditText.requestFocus();

        projectTagPanel.setOnClickListener(null);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(projectTagEditText,InputMethodManager.SHOW_IMPLICIT);

        isProjectTagInViewOnlyMode = false;

        showButtonIfNeeded();
    }

    private void setReadOnlyModeForDeadlinePanel() {
        projectDeadlineOptionalLabel.setVisibility(View.GONE);
    }

    private void setEditModeForDeadlinePanel() {
        if (deadlineRequired) {
            projectDeadlineRequiredLabel.setVisibility(View.VISIBLE);
        } else {
            projectDeadlineOptionalLabel.setVisibility(View.VISIBLE);
        }

        if (isResetDeadlineAvailable) {
            btnResetDeadline.setVisibility(View.VISIBLE);
            btnResetDeadline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetDeadline();
                }
            });
        }

        isProjectDeadlineInViewOnlyMode = false;

        showButtonIfNeeded();
    }

    private void setReadOnlyModeForProjectCompletedPanel() {
        projectCompletedRequirement.setVisibility(View.GONE);
    }

    private void setEditModeForProjectCompletedPanel() {
        projectCompletedRequirement.setVisibility(View.VISIBLE);

        isProjectCompletedInViewOnlyMode = false;

        showButtonIfNeeded();
    }

    private void setReadOnlyModeForProjectDescription() {
        projectDescriptionEditText.setVisibility(View.GONE);

        projectDescriptionTextView.setVisibility(View.VISIBLE);
        projectDescriptionTextView.setText(project.getDescription());

        projectDescriptionRequirement.setVisibility(View.GONE);

        projectDescriptionPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditModeForProjectDescription();
            }
        });

        isProjectDescriptionInViewOnlyMode = true;
    }

    private void setEditModeForProjectDescription() {
        projectDescriptionTextView.setVisibility(View.GONE);

        projectDescriptionEditText.setVisibility(View.VISIBLE);

        projectDescriptionRequirement.setVisibility(View.VISIBLE);

        projectDescriptionEditText.setText(project.getDescription());
        projectDescriptionEditText.requestFocus();

        projectDescriptionPanel.setOnClickListener(null);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(projectDescriptionEditText,InputMethodManager.SHOW_IMPLICIT);

        isProjectDescriptionInViewOnlyMode = false;

        showButtonIfNeeded();
    }

    public void saveProjectToFirebase() {
        String name;
        if(projectNameEditText.getVisibility()==View.VISIBLE) {
            name = projectNameEditText.getText().toString();
        } else {
            name = projectNameTextView.getText().toString();
        }

        if(name.isEmpty()){
            Toast.makeText(getContext(), R.string.msg_no_project_name, Toast.LENGTH_SHORT).show();
            return;
        }

        String tag;
        if(projectTagEditText.getVisibility()==View.VISIBLE) {
            tag = projectTagEditText.getText().toString();
        } else {
            tag = projectTagTextView.getText().toString();
        }

        if(tag.isEmpty()){
            Toast.makeText(getContext(), R.string.msg_no_project_tag, Toast.LENGTH_SHORT).show();
            return;
        }

        String deadlineStr;
        if(!dateStr.equals(getString(R.string.label_na))){
            if(timeStr.equals(getString(R.string.label_na))){
                Toast.makeText(getContext(), R.string.msg_no_deadline_time, Toast.LENGTH_SHORT).show();
                return;
            } else {
                deadlineStr = dateStr + " - " + timeStr;
            }
        } else {
            deadlineStr = getString(R.string.label_na);
        }

        if(project != null) {
            String projectDeadline = project.getDeadline();

            SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy - hh : mm");
            try {
                Date date1 = sdf.parse(deadlineStr);
                Date date2 = sdf.parse(projectDeadline);

                int res = date1.compareTo(date2);
                if(res == -1) {
                    Toast.makeText(getContext(), R.string.msg_deadline_extension, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String description;
        if(projectDescriptionEditText.getVisibility() == View.VISIBLE) {
            description = projectDescriptionEditText.getText().toString();
        } else {
            description = projectDescriptionTextView.getText().toString();
        }

        if(project != null) {
            project.setName(name);
            project.setTag(tag);
            project.setDeadline(deadlineStr);
            project.setStatus(projectCompletedCheckbox.isChecked());
            project.setDescription(description);

            projectListRef.child(projectKey).setValue(project);

            Analytics.logEventProjectSaved(getContext(), project, false);

            if(!deadlineStr.equals(getString(R.string.label_na))){
                Deadline deadline = new Deadline(projectKey, getString(R.string.final_deadline_title), deadlineStr, -1);
                if(deadlines.size() == 0) {
                    deadlineListRef.push().setValue(deadline);
                } else {
                    deadlineListRef.child(deadlineKeys.get(deadlines.get(deadlines.size()-1))).child("deadlineStr").setValue(deadlineStr);
                }
            }
        } else {

            Project project = new Project(name, tag, deadlineStr, description, projectCompletedCheckbox.isChecked());
            String projectKey = projectListRef.push().getKey();

            projectListRef.child(projectKey).setValue(project);

            Analytics.logEventProjectSaved(getContext(), project, true);

            if (!deadlineStr.equals(getString(R.string.label_na))) {
                Deadline deadline = new Deadline(projectKey, getString(R.string.final_deadline_title), deadlineStr, -1);
                deadlineListRef.push().setValue(deadline);
            }
        }

        Intent intent = new Intent();
        intent.setAction(TaskListWidget.ACTION_DATA_UPDATED);
        getActivity().sendBroadcast(intent);

        getActivity().finish();
    }

}