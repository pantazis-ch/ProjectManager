package com.android.example.projectmanager.detail.brainstorming;

import android.os.Parcel;
import android.os.Parcelable;


public class Idea implements Parcelable {

    private String projectKey;
    private String idea;
    private int ideaNumber;

    private String backgroundColor;

    public Idea(){

    }

    public Idea(String projectKey, String idea, int ideaNumber, String backgroundColor){
        this.projectKey = projectKey;
        this.idea = idea;
        this.ideaNumber = ideaNumber;
        this.backgroundColor = backgroundColor;
    }

    protected Idea(Parcel in) {
        projectKey = in.readString();
        idea = in.readString();
        ideaNumber = in.readInt();
        backgroundColor = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(projectKey);
        parcel.writeString(idea);
        parcel.writeInt(ideaNumber);
        parcel.writeString(backgroundColor);
    }

    public static final Creator<Idea> CREATOR = new Creator<Idea>() {
        @Override
        public Idea createFromParcel(Parcel in) {
            return new Idea(in);
        }

        @Override
        public Idea[] newArray(int size) {
            return new Idea[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void setProjectName() {
        this.projectKey = projectKey;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setIdea(String idea) {
        this.idea = idea;
    }

    public String getIdea() {
        return idea;
    }

    public int getIdeaNumber() {
        return ideaNumber;
    }

    public void setIdeaNumber(int ideaNumber) {
        this.ideaNumber = ideaNumber;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
