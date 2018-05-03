package com.android.example.projectmanager.detail.deadlines;

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

import com.android.example.projectmanager.detail.deadlines.avedeadline.AVEDeadlineActivity;
import com.android.example.projectmanager.main.projects.Project;
import com.android.example.projectmanager.R;
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


public class DeadlineListFragment extends Fragment implements DeadlineAdapter.DeadlineAdapterOnClickHandler {

    private String projectKey;

    private FirebaseDatabase database;
    private DatabaseReference deadlineListRef;
    private Query projectDeadlinesQuery;

    private String user;

    private RecyclerView mRecyclerView;
    private DeadlineAdapter mAdapter;
    private DeadlineAdapter.DeadlineAdapterOnClickHandler mClickHandler;

    private ArrayList<Deadline> deadlines;
    private HashMap<Deadline, String> deadlineKeys;

    private boolean delete;


    public static DeadlineListFragment newInstance() {
        DeadlineListFragment fragment = new DeadlineListFragment();
        return fragment;
    }

    public DeadlineListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        projectKey = getActivity().getIntent().getStringExtra("projectKey");
        user = FirebaseAuth.getInstance().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deadline_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = getView().findViewById(R.id.rv_deadline_list);

        mClickHandler = this;

        FloatingActionButton addNewDeadlineFab = getView().findViewById(R.id.fab_add_new_deadline);
        addNewDeadlineFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AVEDeadlineActivity.class);
                intent.putExtra("projectKey", projectKey);
                intent.putExtra("project", getActivity().getIntent().getParcelableExtra("project"));
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });

        database = FirebaseDatabase.getInstance();
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

                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                mAdapter = new DeadlineAdapter(mClickHandler, deadlines);
                mRecyclerView.setAdapter(mAdapter);

                mRecyclerView.setHasFixedSize(true);

                new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0 , ItemTouchHelper.RIGHT ) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                        delete = true;

                        final Snackbar mySnackbar = Snackbar.make(getView(), getString(R.string.msg_item_deleted), Snackbar.LENGTH_SHORT);
                        mySnackbar.setAction(getString(R.string.msg_undo), new View.OnClickListener() {
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

                                    String projectDeadline = ((Project)getActivity().getIntent().getParcelableExtra("project")).getDeadline();
                                    String removedDeadline = deadlines.get(position).getDeadlineStr();

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy - hh : mm");
                                    try {
                                        Date date1 = sdf.parse(projectDeadline);
                                        Date date2 = sdf.parse(removedDeadline);

                                        int res = date1.compareTo(date2);
                                        if(res == 0) {
                                            String previousDeadline = getString(R.string.label_na);
                                            if(deadlines.size() !=1 ){
                                                previousDeadline = deadlines.get(position-1).getDeadlineStr();
                                            }

                                            database.getReference(user).child("projects").child(projectKey).child("deadline").setValue(previousDeadline);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    deadlineListRef.child(deadlineKeys.get(deadlines.get(position))).removeValue();
                                    deadlineKeys.remove(deadlines.get(position));
                                    deadlines.remove(position);

                                    Intent intent = new Intent();
                                    intent.setAction(TaskListWidget.ACTION_DATA_UPDATED);
                                    getActivity().sendBroadcast(intent);

                                    mAdapter.notifyDataSetChanged();
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
    public void onClick(Deadline deadline, Boolean isLastDeadline) {
        Intent intent = new Intent(getActivity(), AVEDeadlineActivity.class);
        intent.putExtra("projectKey", getActivity().getIntent().getStringExtra("projectKey"));
        intent.putExtra("deadline", deadline);
        intent.putExtra("deadlineKey", deadlineKeys.get(deadline));
        intent.putExtra("projectName", ((Project)getActivity().getIntent().getParcelableExtra("project")).getName());
        intent.putExtra("isLastDeadline", isLastDeadline);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }
}
