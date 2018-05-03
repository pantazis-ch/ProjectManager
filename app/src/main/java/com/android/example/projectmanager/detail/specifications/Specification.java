package com.android.example.projectmanager.detail.specifications;

import android.os.Parcel;
import android.os.Parcelable;

public class Specification implements Parcelable {

    private String projectKey;
    private String specName;
    private String specNote;

    private int specNumber;

    public Specification(){

    }

    public Specification(String projectKey, String specName, String specNote, int specNumber){
        this.projectKey = projectKey;
        this.specName = specName;
        this.specNote = specNote;
        this.specNumber = specNumber;
    }

    protected Specification(Parcel in) {
        projectKey = in.readString();
        specName = in.readString();
        specNote = in.readString();
        specNumber = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(projectKey);
        parcel.writeString(specName);
        parcel.writeString(specNote);
        parcel.writeInt(specNumber);
    }

    public static final Creator<Specification> CREATOR = new Creator<Specification>() {
        @Override
        public Specification createFromParcel(Parcel in) {
            return new Specification(in);
        }

        @Override
        public Specification[] newArray(int size) {
            return new Specification[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getSpecNote() {
        return specNote;
    }

    public void setSpecNote(String specNote) {
        this.specNote = specNote;
    }

    public int getSpecNumber() {
        return specNumber;
    }

    public void setSpecNumber(int specNumber) {
        this.specNumber = specNumber;
    }
}
