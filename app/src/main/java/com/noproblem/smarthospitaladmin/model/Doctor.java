package com.noproblem.smarthospitaladmin.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Victor Artemyev on 25/11/2016.
 */

@IgnoreExtraProperties
public class Doctor {

    public  String id;
    public  String firstName;
    public  String lastName;
    public  String specialty;
    public  Time workTime;
    public  Image image;

    public Doctor() {
    }

    private Doctor(String id, String firstName, String lastName, String specialty, Time workTime, Image image) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialty = specialty;
        this.workTime = workTime;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public Time getWorkTime() {
        return workTime;
    }

    public Image getImage() {
        return image;
    }

    public static class Builder {

        private String mId;
        private String mFirstName;
        private String mLastName;
        private String mSpecialty;
        private Time mWorkTime;
        private Image mImage;

        public Builder setId(String id) {
            mId = id;
            return this;
        }

        public Builder setFirstName(String firstName) {
            mFirstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            mLastName = lastName;
            return this;
        }

        public Builder setSpecialty(String specialty) {
            mSpecialty = specialty;
            return this;
        }

        public Builder setWorkTime(Time workTime) {
            mWorkTime = workTime;
            return this;
        }

        public Builder setImage(Image image) {
            mImage = image;
            return this;
        }

        public Doctor build() {
            return new Doctor(mId, mFirstName, mLastName, mSpecialty, mWorkTime, mImage);
        }
    }
}
