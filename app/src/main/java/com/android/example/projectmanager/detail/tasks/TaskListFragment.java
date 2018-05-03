package com.android.example.projectmanager.detail.tasks;


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

import com.android.example.projectmanager.detail.tasks.avetask.AVETaskActivity;
import com.android.example.projectmanager.main.projects.Project;
import com.android.example.projectmanager.R;
import com.android.example.projectmanager.widget.TaskListWidget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class TaskListFragment extends Fragment implements TaskAdapter.TaskAdapterOnClickHandler {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Project project;
    private String projectKey;

    private FirebaseDatabase database;
    private DatabaseReference taskListRef;
    private Query projectTasksQuery;

    private String user;

    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;
    private TaskAdapter.TaskAdapterOnClickHandler taskItemClickHandler;

    private ArrayList<Task> tasks;
    private HashMap<Task, String> taskKeys;

    private boolean delete;


    public static TaskListFragment newInstance(Project project, String projectKey) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, project);
        args.putString(ARG_PARAM2, projectKey);
        fragment.setArguments(args);
        return fragment;
    }

    public TaskListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            project = getArguments().getParcelable(ARG_PARAM1);
            projectKey = getArguments().getString(ARG_PARAM2);
        }

        user = FirebaseAuth.getInstance().getUid();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tasks = new ArrayList<>();
        taskKeys = new HashMap<>();

        mRecyclerView = getView().findViewById(R.id.rv_task_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        taskItemClickHandler = this;

        mAdapter = new TaskAdapter(getContext(), taskItemClickHandler, tasks);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setHasFixedSize(true);

        FloatingActionButton addNewTaskFab = getView().findViewById(R.id.fab_add_new_task);
        addNewTaskFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AVETaskActivity.class);
                intent.putExtra("projectKey", getActivity().getIntent().getStringExtra("projectKey"));
                if(mAdapter != null && mAdapter.getItemCount() != 0) {
                    intent.putExtra("taskNumber", mAdapter.getItemCount());
                }
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });


        database = FirebaseDatabase.getInstance();
        taskListRef = database.getReference(user).child("tasks");

        projectTasksQuery = taskListRef.orderByChild("projectKey").equalTo(projectKey);

        projectTasksQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Task task = dataSnapshot.getValue(Task.class);

                tasks.add(task);

                Collections.sort(tasks, new Comparator<Task>() {
                    @Override
                    public int compare(Task t1, Task t2) {
                        return t1.getTaskNumber() - t2.getTaskNumber();
                    }
                });

                mAdapter.notifyItemInserted(mAdapter.getItemCount());

                taskKeys.put(task, dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Task task = dataSnapshot.getValue(Task.class);

                int index = task.getTaskNumber();

                boolean notifyAdapter = false;
                if(!tasks.get(index).getTaskName().equals(task.getTaskName())) {
                    tasks.get(index).setTaskName(task.getTaskName());
                    notifyAdapter = true;
                }

                if(!tasks.get(index).getTaskNote().equals(task.getTaskNote())) {
                    tasks.get(index).setTaskNote(task.getTaskNote());
                    notifyAdapter = true;
                }

                if(notifyAdapter) {
                    mAdapter.notifyItemChanged(index);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int fromPosition = viewHolder.getAdapterPosition();
                final int toPosition = target.getAdapterPosition();

                String fromPositionKey = taskKeys.get((Task) mAdapter.taskList.get(fromPosition));
                String toPositionKey = taskKeys.get((Task) mAdapter.taskList.get(toPosition));

                taskListRef.child(fromPositionKey).child("taskNumber").setValue(toPosition);
                taskListRef.child(toPositionKey).child("taskNumber").setValue(fromPosition);

                Collections.swap(mAdapter.taskList, fromPosition, toPosition);

                ((TaskAdapter.TaskHolder)viewHolder).taskNumber.setText(getString(R.string.label_task_number) + " " + (toPosition + 1));
                ((TaskAdapter.TaskHolder)target).taskNumber.setText(getString(R.string.label_task_number) + " " + (fromPosition + 1));

                mAdapter.notifyItemMoved(fromPosition, toPosition);

                Intent intent = new Intent();
                intent.setAction(TaskListWidget.ACTION_DATA_UPDATED);
                getActivity().sendBroadcast(intent);

                return true;
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
                            for (int i = position + 1; i < tasks.size(); i++) {
                                Task task = tasks.get(i);
                                int oldPosition = task.getTaskNumber();
                                int newPosition = oldPosition - 1;

                                tasks.get(i).setTaskNumber(newPosition);
                                taskListRef.child(taskKeys.get(tasks.get(i))).child("taskNumber").setValue(newPosition);
                            }

                            taskListRef.child(taskKeys.get(tasks.get(position))).removeValue();
                            taskKeys.remove(tasks.get(position));
                            tasks.remove(position);

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
    public void onChecked(Task task, int position, boolean checked) {
        taskListRef.child(taskKeys.get(task)).child("completed").setValue(checked);
        tasks.get(position).setCompleted(checked);
    }


    @Override
    public void onClick(Task task) {
        Intent intent = new Intent(getActivity(), AVETaskActivity.class);
        intent.putExtra("projectKey", projectKey);
        intent.putExtra("task", task);
        intent.putExtra("taskKey", taskKeys.get(task));
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }

}
