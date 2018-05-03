package com.android.example.projectmanager.detail.tasks;

import android.os.Parcel;
import android.os.Parcelable;


public class Task implements Parcelable {

    private String projectKey;
    private String taskName;
    private String taskNote;

    private boolean completed;

    private int taskNumber;

    public Task(){

    }

    public Task(String projectKey, String taskName, String taskNote, boolean completed, int taskNumber){
        this.projectKey = projectKey;
        this.taskName = taskName;
        this.taskNote = taskNote;
        this.completed = completed;
        this.taskNumber = taskNumber;
    }

    protected Task(Parcel in) {
        projectKey = in.readString();
        taskName = in.readString();
        taskNote = in.readString();
        completed = in.readByte() != 0;
        taskNumber = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(projectKey);
        parcel.writeString(taskName);
        parcel.writeString(taskNote);
        parcel.writeByte((byte) (completed ? 1 : 0));
        parcel.writeInt(taskNumber);
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public int getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    public String getTaskNote() {
        return taskNote;
    }

    public void setTaskNote(String taskNote) {
        this.taskNote = taskNote;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
