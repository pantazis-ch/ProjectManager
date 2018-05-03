package com.android.example.projectmanager.detail.notes;

import android.os.Parcel;
import android.os.Parcelable;


public class Note implements Parcelable {

    private String projectKey;
    private String noteName;

    private int noteNumber;

    public Note(){

    }

    public Note(String projectKey, String taskName, int noteNumber){
        this.projectKey = projectKey;
        this.noteName = taskName;
        this.noteNumber = noteNumber;
    }

    protected Note(Parcel in) {
        projectKey = in.readString();
        noteName = in.readString();
        noteNumber = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(projectKey);
        parcel.writeString(noteName);
        parcel.writeInt(noteNumber);
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public int getNoteNumber() {
        return noteNumber;
    }

    public void setNoteNumber(int noteNumber) {
        this.noteNumber = noteNumber;
    }
}
