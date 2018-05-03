package com.android.example.projectmanager.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import com.android.example.projectmanager.main.projects.Project;
import com.android.example.projectmanager.R;
import com.android.example.projectmanager.utilities.FirebaseUtility;
import com.android.example.projectmanager.utilities.PrefUtils;
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


public class TaskListWidget extends AppWidgetProvider {

    public static final String ACTION_DATA_UPDATED = "com.android.example.projectmanager.ACTION_DATA_UPDATED";
    private static final String ACTION_NEXT_PROJECT = "com.android.example.projectmanager.ACTION_NEXT_PROJECT";
    private static final String ACTION_PREVIOUS_PROJECT = "com.android.example.projectmanager.ACTION_PREVIOUS_PROJECT";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference projectListReference;

    private String user;

    private ArrayList<Project> projects;
    private HashMap<Project, String> projectKeys;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String projectKey, String projectName) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_tasks);

        if(PrefUtils.getProjectCount(context) != 0) {
            views.setTextViewText(R.id.widget_project_name, projectName);

            views.setViewVisibility(R.id.widget_btn_previous, View.VISIBLE);
            views.setViewVisibility(R.id.widget_project_counter, View.VISIBLE);
            views.setViewVisibility(R.id.widget_btn_next, View.VISIBLE);

            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtra("projectKey", projectKey);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            views.setRemoteAdapter(appWidgetId, R.id.widget_task_list, intent);
            views.setEmptyView(R.id.widget_task_list, R.id.widget_empty_view);

            Intent nextProjectIntent = new Intent(context, TaskListWidget.class);
            nextProjectIntent.setAction(TaskListWidget.ACTION_NEXT_PROJECT);
            nextProjectIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent nextProjectPendingIntent = PendingIntent.getBroadcast(context, 1, nextProjectIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_btn_next, nextProjectPendingIntent);

            int currentProject = PrefUtils.getCurrentProjectPosition(context) + 1;
            int projectCount = PrefUtils.getProjectCount(context);

            views.setTextViewText(R.id.widget_project_counter, currentProject + "/" + projectCount);

            Intent previousProjectIntent = new Intent(context, TaskListWidget.class);
            previousProjectIntent.setAction(TaskListWidget.ACTION_PREVIOUS_PROJECT);
            previousProjectIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent previousRrojectPendingIntent = PendingIntent.getBroadcast(context, 1, previousProjectIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_btn_previous, previousRrojectPendingIntent);
        } else {
            views.setTextViewText(R.id.widget_project_name, context.getString(R.string.app_name));

            views.setTextViewText(R.id.widget_empty_view, context.getString(R.string.msg_no_projects_short));

            views.setViewVisibility(R.id.widget_btn_previous, View.GONE);
            views.setViewVisibility(R.id.widget_project_counter, View.GONE);
            views.setViewVisibility(R.id.widget_btn_next, View.GONE);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

        firebaseDatabase = FirebaseUtility.getDatabase();
        user = FirebaseAuth.getInstance().getUid();
        projectListReference = firebaseDatabase.getReference(user).child("projects");

        projectListReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                projects = new ArrayList<>();
                projectKeys = new HashMap<>();

                for (final DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    Project project = dataSnapshot1.getValue(Project.class);

                    if (project.getStatus() == false) {
                        projects.add(project);
                    }
                    projectKeys.put(project, dataSnapshot1.getKey());
                }

                sortList(context);

                String projectKey = "";
                String projectName = "";
                if(projects != null && projects.size() != 0) {
                    PrefUtils.setCurrentProjectPosition(context, 0);
                    PrefUtils.setProjectCount(context, projects.size());

                    Project project = projects.get(0);

                    projectKey = projectKeys.get(project);
                    projectName = project.getName();
                } else {
                    PrefUtils.setProjectCount(context, 0);
                }

                for (int appWidgetId : appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId, projectKey, projectName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();

        if (action.equals(ACTION_DATA_UPDATED)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));

            onUpdate(context, appWidgetManager, appWidgetIds);

            return;
        }

        if (action.equals(ACTION_NEXT_PROJECT)) {

            firebaseDatabase = FirebaseUtility.getDatabase();
            user = FirebaseAuth.getInstance().getUid();
            projectListReference = firebaseDatabase.getReference(user).child("projects");

            projectListReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    projects = new ArrayList<>();
                    projectKeys = new HashMap<>();

                    for (final DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                        Project project = dataSnapshot1.getValue(Project.class);

                        if (project.getStatus() == false) {
                            projects.add(project);
                        }
                        projectKeys.put(project, dataSnapshot1.getKey());
                    }

                    sortList(context);

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

                    int currentPosition = PrefUtils.getCurrentProjectPosition(context);
                    int projectCount = PrefUtils.getProjectCount(context);

                    if (currentPosition < projectCount - 1) {

                        currentPosition++;
                        PrefUtils.setCurrentProjectPosition(context, currentPosition);

                        String projectKey = "";
                        String projectName = "";
                        if(projects != null && projects.size() != 0) {
                            PrefUtils.setCurrentProjectPosition(context, currentPosition);
                            PrefUtils.setProjectCount(context, projects.size());

                            Project project = projects.get(currentPosition);

                            projectKey = projectKeys.get(project);
                            projectName = project.getName();
                        }

                        updateAppWidget(context,appWidgetManager, appWidgetId, projectKey, projectName);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        if (action.equals(ACTION_PREVIOUS_PROJECT)) {

            firebaseDatabase = FirebaseUtility.getDatabase();
            user = FirebaseAuth.getInstance().getUid();
            projectListReference = firebaseDatabase.getReference(user).child("projects");

            projectListReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    projects = new ArrayList<>();
                    projectKeys = new HashMap<>();

                    for (final DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                        Project project = dataSnapshot1.getValue(Project.class);

                        if (project.getStatus() == false) {
                            projects.add(project);
                        }
                        projectKeys.put(project, dataSnapshot1.getKey());
                    }

                    sortList(context);

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

                    int currentPosition = PrefUtils.getCurrentProjectPosition(context);

                    if(currentPosition > 0) {
                        currentPosition--;
                        PrefUtils.setCurrentProjectPosition(context, currentPosition);

                        String projectKey = "";
                        String projectName = "";
                        if(projects != null && projects.size() != 0) {
                            PrefUtils.setCurrentProjectPosition(context, currentPosition);
                            PrefUtils.setProjectCount(context, projects.size());

                            Project project = projects.get(currentPosition);

                            projectKey = projectKeys.get(project);
                            projectName = project.getName();
                        }
                        updateAppWidget(context, appWidgetManager, appWidgetId, projectKey, projectName);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void sortList(final Context context) {
        Collections.sort(projects, new Comparator<Project>() {
            @Override
            public int compare(Project p1, Project p2) {
                int returnValue = 0;

                String d1 = p1.getDeadline();
                String d2 = p2.getDeadline();

                if(!d1.equals(context.getString(R.string.label_na)) && !d2.equals(context.getString(R.string.label_na))) {
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
                        if(d1.equals(context.getString(R.string.label_na))) {
                            returnValue = 1;
                        } else {
                            returnValue = -1;
                        }
                    }
                }
                return returnValue;
            }
        });
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

