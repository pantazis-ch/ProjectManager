package com.android.example.projectmanager.detail.deadlines;

import android.os.Parcel;
import android.os.Parcelable;


public class Deadline implements Parcelable {

    private String projectKey;
    private String deadlineTitle;
    private String deadlineStr;

    private int notificationID;

    private String dateInfo[];

    public Deadline(){

    }

    public Deadline(String projectKey, String deadlineTitle, String deadlineStr, int notificationID){
        this.projectKey = projectKey;
        this.deadlineTitle = deadlineTitle;
        this.deadlineStr = deadlineStr;
        this.notificationID = notificationID;
    }

    protected Deadline(Parcel in) {
        projectKey = in.readString();
        deadlineTitle = in.readString();
        deadlineStr = in.readString();
        notificationID = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(projectKey);
        parcel.writeString(deadlineTitle);
        parcel.writeString(deadlineStr);
        parcel.writeInt(notificationID);
    }

    public static final Creator<Deadline> CREATOR = new Creator<Deadline>() {
        @Override
        public Deadline createFromParcel(Parcel in) {
            return new Deadline(in);
        }

        @Override
        public Deadline[] newArray(int size) {
            return new Deadline[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDeadlineStr() {
        return deadlineStr;
    }

    public void setDeadlineStr(String deadlineStr) {
        this.deadlineStr = deadlineStr;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getDeadlineTitle() {
        return deadlineTitle;
    }

    public void setDeadlineTitle(String deadlineTitle) {
        this.deadlineTitle = deadlineTitle;
    }

    public int getNotificationId() {
        return notificationID;
    }

    public void setNotificationId(int notificationId) {
        this.notificationID = notificationId;
    }

    public String getDate() {
        if(deadlineStr.equals("n / a")) {
            return "n / a";
        } else {
            if(dateInfo == null) {
                dateInfo = deadlineStr.split(" - ");
            }
            return dateInfo[0];
        }
    }

    public String getTime() {
        if(deadlineStr.equals("n / a")) {
            return "n / a";
        } else {
            if(dateInfo == null) {
                dateInfo = deadlineStr.split(" - ");
            }
            return dateInfo[1];
        }
    }

}
