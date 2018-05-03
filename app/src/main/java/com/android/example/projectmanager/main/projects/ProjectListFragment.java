package com.android.example.projectmanager.main.projects;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.example.projectmanager.detail.DetailActivity;
import com.android.example.projectmanager.main.projects.aveproject.AVEProjectActivity;
import com.android.example.projectmanager.R;
import com.android.example.projectmanager.utilities.FirebaseUtility;
import com.android.example.projectmanager.widget.TaskListWidget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;


public class ProjectListFragment extends Fragment implements ProjectRecyclerAdapter.ProjectAdapterOnClickHandler {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference projectListReference;

    private DatabaseReference specListReference;
    private DatabaseReference brainstormingListReference;
    private DatabaseReference taskListReference;
    private DatabaseReference deadlineListReference;
    private DatabaseReference noteListReference;

    private FirebaseAuth auth;
    private String user;

    private RecyclerView mRecyclerView;
    private ProjectRecyclerAdapter mAdapter;
    private ProjectRecyclerAdapter.ProjectAdapterOnClickHandler projectClickHandler;

    private ArrayList<Project> projects;
    private HashMap<Project, String> projectKeys;

    private boolean delete;


    public static ProjectListFragment newInstance() {
        ProjectListFragment fragment = new ProjectListFragment();
        return fragment;
    }

    public ProjectListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = auth.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase = FirebaseUtility.getDatabase();
        projectListReference = firebaseDatabase.getReference(user).child("projects");

        specListReference = firebaseDatabase.getReference(user).child("specifications");
        brainstormingListReference = firebaseDatabase.getReference(user).child("brainstorming");
        taskListReference = firebaseDatabase.getReference(user).child("tasks");
        deadlineListReference = firebaseDatabase.getReference(user).child("deadlines");
        noteListReference = firebaseDatabase.getReference(user).child("notes");

        projectClickHandler = this;

        FloatingActionButton addNewProjectFab = getView().findViewById(R.id.fab_add_new_project);
        addNewProjectFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AVEProjectActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });

        projectListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                projects = new ArrayList<>();
                projectKeys = new HashMap<>();

                for (final DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    Project project = dataSnapshot1.getValue(Project.class);

                    projects.add(project);
                    projectKeys.put(project, dataSnapshot1.getKey());
                }

                Collections.sort(projects, new Comparator<Project>() {
                    @Override
                    public int compare(Project p1, Project p2) {

                        int returnValue = 0;

                        String d1 = p1.getDeadline();
                        String d2 = p2.getDeadline();

                        boolean s1 = p1.getStatus();
                        boolean s2 = p2.getStatus();

                        if(s1 == false && s2 == false) {
                            if(!d1.equals(getString(R.string.label_na)) && !d2.equals(getString(R.string.label_na))) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy - hh : mm");
                                try {
                                    Date date1 = sdf.parse(p1.getDeadline());
                                    Date date2 = sdf.parse(p2.getDeadline());

                                    returnValue = date1.compareTo(date2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if(d1.equals(d2)) {
                                    returnValue = 0;
                                } else {
                                    if(d1.equals(getString(R.string.label_na))) {
                                        returnValue = 1;
                                    } else {
                                        returnValue = -1;
                                    }
                                }
                            }
                        } else {
                            if(s1 == false) {
                                returnValue = -1;
                            } else {
                                returnValue = 1;
                            }
                        }
                        return returnValue;
                    }
                });

                mRecyclerView = getView().findViewById(R.id.rv_project_list);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                mAdapter = new ProjectRecyclerAdapter(getContext(), projectClickHandler, projects);
                mRecyclerView.setAdapter(mAdapter);

                mRecyclerView.setHasFixedSize(true);

                new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                        delete = true;

                        final Snackbar mySnackbar = Snackbar.make(getView(), R.string.msg_item_deleted, Snackbar.LENGTH_SHORT);
                        mySnackbar.setAction(R.string.msg_undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                delete = false;
                            }
                        });
                        mySnackbar.setCallback( new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                if(delete) {
                                    int position = viewHolder.getAdapterPosition();

                                    projectListReference.child(projectKeys.get(projects.get(position))).removeValue();
                                    specListReference.orderByChild("projectKey").equalTo(projectKeys.get(projects.get(position)))
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                                                        dataSnapshot1.getRef().removeValue();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                    brainstormingListReference.orderByChild("projectKey").equalTo(projectKeys.get(projects.get(position)))
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                                                        dataSnapshot1.getRef().removeValue();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                    taskListReference.orderByChild("projectKey").equalTo(projectKeys.get(projects.get(position)))
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                                                        dataSnapshot1.getRef().removeValue();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                    deadlineListReference.orderByChild("projectKey").equalTo(projectKeys.get(projects.get(position)))
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                                                        dataSnapshot1.getRef().removeValue();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                    noteListReference.orderByChild("projectKey").equalTo(projectKeys.get(projects.get(position)))
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                                                        dataSnapshot1.getRef().removeValue();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                    projectKeys.remove(projectKeys.get(projects.get(position)));
                                    projects.remove(position);

                                    Intent intent = new Intent();
                                    intent.setAction(TaskListWidget.ACTION_DATA_UPDATED);
                                    getActivity().sendBroadcast(intent);
                                } else {
                                    mAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onShown(Snackbar sb) {
                                super.onShown(sb);
                            }
                        });
                        mySnackbar.show();
                    }
                }).attachToRecyclerView(mRecyclerView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(Project project, String action) {
        if (action.equals("detail")) {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("project", project);
            intent.putExtra("projectKey", projectKeys.get(project));
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        } else if (action.equals("overview")) {
            Intent intent = new Intent(getActivity(), AVEProjectActivity.class);
            intent.putExtra("project", project);
            intent.putExtra("projectKey", projectKeys.get(project));
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        }
    }

}
