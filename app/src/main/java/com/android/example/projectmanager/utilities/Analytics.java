package com.android.example.projectmanager.utilities;

import android.content.Context;
import android.os.Bundle;

import com.android.example.projectmanager.detail.deadlines.Deadline;
import com.android.example.projectmanager.detail.specifications.Specification;
import com.android.example.projectmanager.detail.tasks.Task;
import com.android.example.projectmanager.main.projects.Project;
import com.android.example.projectmanager.R;
import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {

    public static void logEventProjectSaved(Context context, Project project, boolean newProject) {
        Bundle params = new Bundle();
        params.putBoolean("newProject", newProject);
        params.putBoolean("editedProject", newProject);
        params.putBoolean("hasDeadline", project.getDeadline().equals(context.getString(R.string.label_na)));
        params.putBoolean("hasOverview", !project.getDescription().isEmpty());

        FirebaseAnalytics.getInstance(context).logEvent("saved_project", params);
    }

    public static void logEventSpecificationSaved(Context context, Specification specification, boolean newSpec) {
        Bundle params = new Bundle();
        params.putBoolean("newSpec", newSpec);
        params.putBoolean("editedSpec", newSpec);
        params.putBoolean("hasNote", !specification.getSpecNote().isEmpty());

        FirebaseAnalytics.getInstance(context).logEvent("saved_spec", params);
    }

    public static void logEventIdeaSaved(Context context, boolean newIdea) {
        Bundle params = new Bundle();
        params.putBoolean("newIdea", newIdea);
        params.putBoolean("editedIdea", newIdea);

        FirebaseAnalytics.getInstance(context).logEvent("idea_saved", params);
    }

    public static void logEventTaskSaved(Context context, Task task, boolean newTask) {
        Bundle params = new Bundle();
        params.putBoolean("newTask", newTask);
        params.putBoolean("editedTask", newTask);
        params.putBoolean("hasNote", !task.getTaskNote().isEmpty());

        FirebaseAnalytics.getInstance(context).logEvent("saved_task", params);
    }

    public static void logEventDeadlineSaved(Context context, Deadline deadline, boolean newDeadline) {
        Bundle params = new Bundle();
        params.putBoolean("newDeadline", newDeadline);
        params.putBoolean("editedDeadline", newDeadline);
        params.putBoolean("hasNotification", deadline.getNotificationId() != -1);

        FirebaseAnalytics.getInstance(context).logEvent("saved_deadline", params);
    }

    public static void logEventNoteSaved(Context context, boolean newNote) {
        Bundle params = new Bundle();
        params.putBoolean("newNote", newNote);
        params.putBoolean("editedNote", newNote);

        FirebaseAnalytics.getInstance(context).logEvent("saved_note", params);
    }

}
