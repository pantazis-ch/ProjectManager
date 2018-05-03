package com.android.example.projectmanager.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.example.projectmanager.detail.tasks.Task;
import com.android.example.projectmanager.R;
import com.android.example.projectmanager.utilities.FirebaseUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference taskListReference;
    private Query projectTasksQuery;

    private String user;

    private Context mContext;
    private String projectKey;

    private ArrayList<Task> tasks;
    private HashMap<Task, String> taskKeys;

    private int widgetId;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;

        projectKey = intent.getStringExtra("projectKey");
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
    }

    @Override
    public void onCreate() {
        firebaseDatabase = FirebaseUtility.getDatabase();
        user = FirebaseAuth.getInstance().getUid();

        taskListReference = firebaseDatabase.getReference(user).child("tasks");

        projectTasksQuery = taskListReference.orderByChild("projectKey").equalTo(projectKey);

        populateListItem();

    }

    private void populateListItem() {

        projectTasksQuery.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tasks = new ArrayList<>();
                taskKeys = new HashMap<>();

                for (DataSnapshot taskSnapshot: dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    tasks.add(task);
                    taskKeys.put(task, taskSnapshot.getKey());

                    Collections.sort(tasks, new Comparator<Task>() {
                        @Override
                        public int compare(Task t1, Task t2) {
                            return t1.getTaskNumber() - t2.getTaskNumber();
                        }
                    });
                }
                AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(widgetId, R.id.widget_task_list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(tasks != null) {
            return tasks.size();
        } else {
            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int i) {

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_task_list_item);

        String taskName = tasks.get(i).getTaskName();

        int taskNumber = tasks.get(i).getTaskNumber();

        views.setTextViewText(R.id.widget_tv_task_name, taskName);

        views.setTextViewText(R.id.widget_tv_task_number, String.valueOf(taskNumber + 1));

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
