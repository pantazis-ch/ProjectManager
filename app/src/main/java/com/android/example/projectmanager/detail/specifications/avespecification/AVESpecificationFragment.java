package com.android.example.projectmanager.detail.specifications.avespecification;


import android.content.Context;
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

import com.android.example.projectmanager.detail.specifications.Specification;
import com.android.example.projectmanager.R;
import com.android.example.projectmanager.utilities.Analytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AVESpecificationFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private String projectKey;
    private Specification specification;
    private String specificationKey;

    @BindView(R.id.panel_specification_text) LinearLayout specNamePanel;
    @BindView(R.id.tv_spec_text_requirement) TextView specNameRequirement;
    @BindView(R.id.et_spec_text) EditText specNameEditText;
    @BindView(R.id.tv_spec_text) TextView specNameTextView;

    @BindView(R.id.btn_save_spec) Button saveSpecButton;

    @BindView(R.id.panel_spec_note) LinearLayout specNotePanel;
    @BindView(R.id.tv_spec_note_requirement) TextView specNoteRequirement;
    @BindView(R.id.et_spec_note) EditText specNoteEditText;
    @BindView(R.id.tv_spec_note) TextView specNoteTextView;

    private boolean isSpecNameInViewOnlyMode;
    private boolean isSpecNoteInViewOnlyMode;

    private FirebaseDatabase database;
    private DatabaseReference specListRef;

    String user;


    public AVESpecificationFragment() {
        // Required empty public constructor
    }


    public static AVESpecificationFragment newInstance(String projectKey, Specification spec, String specKey) {
        AVESpecificationFragment fragment = new AVESpecificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, projectKey);
        args.putParcelable(ARG_PARAM2, spec);
        args.putString(ARG_PARAM3, specKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectKey = getArguments().getString(ARG_PARAM1);
            specification = getArguments().getParcelable(ARG_PARAM2);
            specificationKey = getArguments().getString(ARG_PARAM3);
        }

        if(savedInstanceState != null) {
            isSpecNameInViewOnlyMode = savedInstanceState.getBoolean("isSpecNameInViewOnlyMode");
            isSpecNoteInViewOnlyMode = savedInstanceState.getBoolean("isSpecNoteInViewOnlyMode");
        } else {
            if(specification != null) {
                isSpecNameInViewOnlyMode = true;
                isSpecNoteInViewOnlyMode = true;
            } else {
                isSpecNameInViewOnlyMode = false;
                isSpecNoteInViewOnlyMode = false;
            }
        }

        user = FirebaseAuth.getInstance().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ave_specification, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, getView());

        if(isSpecNameInViewOnlyMode) {
            setReadOnlyModeForSpecName();
        }

        if(isSpecNoteInViewOnlyMode) {
            setReadOnlyModeForSpecNote();
        }

        showButton();


        database = FirebaseDatabase.getInstance();
        specListRef = database.getReference(user).child("specifications");

    }

    private void showButton(){
        if(buttonShouldBeVisible()) {
            saveSpecButton.setVisibility(View.VISIBLE);
            saveSpecButton.setText(getButtonText());
            saveSpecButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveSpecToFirebase();
                }
            });
        } else {
            saveSpecButton.setVisibility(View.GONE);
        }
    }

    private boolean buttonShouldBeVisible() {
        if(isSpecNameInViewOnlyMode && isSpecNoteInViewOnlyMode){
            return false;
        } else if( isSpecNameInViewOnlyMode || isSpecNoteInViewOnlyMode ) {
            return true;
        } else {
            return true;
        }
    }

    private String getButtonText() {
        if(specification == null) {
            return getString(R.string.label_add_specification);
        } else {
            return getString(R.string.label_save_changes);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isSpecNameInViewOnlyMode", isSpecNameInViewOnlyMode);
        outState.putBoolean("isSpecNoteInViewOnlyMode", isSpecNoteInViewOnlyMode);

        super.onSaveInstanceState(outState);
    }

    private void setReadOnlyModeForSpecName() {
        specNameEditText.setVisibility(View.GONE);


        specNameTextView.setVisibility(View.VISIBLE);
        specNameTextView.setText(specification.getSpecName());

        specNameRequirement.setVisibility(View.GONE);

        specNamePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditModeForSpecName();
            }
        });
    }

    private void setEditModeForSpecName() {
        specNameTextView.setVisibility(View.GONE);

        specNameEditText.setVisibility(View.VISIBLE);

        specNameRequirement.setVisibility(View.VISIBLE);

        specNameEditText.setText(specification.getSpecName());
        specNameEditText.requestFocus();

        specNamePanel.setOnClickListener(null);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(specNameEditText,InputMethodManager.SHOW_IMPLICIT);

        isSpecNameInViewOnlyMode = false;

        showButton();
    }

    private void setReadOnlyModeForSpecNote() {
        specNoteEditText.setVisibility(View.GONE);

        specNoteTextView.setVisibility(View.VISIBLE);
        specNoteTextView.setText(specification.getSpecNote());

        specNoteRequirement.setVisibility(View.GONE);

        specNotePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditModeForSpecNote();
            }
        });
    }

    private void setEditModeForSpecNote() {
        specNoteTextView.setVisibility(View.GONE);

        specNoteEditText.setVisibility(View.VISIBLE);

        specNoteRequirement.setVisibility(View.VISIBLE);

        specNoteEditText.setText(specification.getSpecNote());
        specNoteEditText.requestFocus();

        specNotePanel.setOnClickListener(null);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(specNoteEditText,InputMethodManager.SHOW_IMPLICIT);

        isSpecNoteInViewOnlyMode = false;

        showButton();
    }

    public void saveSpecToFirebase() {
        String name;
        if(specNameEditText.getVisibility()==View.VISIBLE) {
            name = specNameEditText.getText().toString();
        } else {
            name = specNameTextView.getText().toString();
        }

        String note;
        if(specNoteEditText.getVisibility()==View.VISIBLE) {
            note = specNoteEditText.getText().toString();
        } else {
            note = specNoteTextView.getText().toString();
        }

        if(name.isEmpty()){
            Toast.makeText(getContext(), R.string.msg_no_specification, Toast.LENGTH_SHORT).show();
            return;
        }

        if(specification != null) {
            specification.setSpecName(name);
            specification.setSpecNote(note);
            specListRef.child(specificationKey).setValue(specification);
            Analytics.logEventSpecificationSaved(getContext(), specification, false);
        } else {
            int specNumber = getActivity().getIntent().getIntExtra("specNumber", 0);

            Specification spec = new Specification(projectKey, name, note, specNumber);
            specListRef.push().setValue(spec);
            Analytics.logEventSpecificationSaved(getContext(), spec, true);
        }

        getActivity().finish();

    }

}
