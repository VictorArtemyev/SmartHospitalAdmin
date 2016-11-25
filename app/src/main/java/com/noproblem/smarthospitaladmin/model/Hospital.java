package com.noproblem.smarthospitaladmin.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victor Artemyev on 24/11/2016.
 */

@IgnoreExtraProperties
public class Hospital {

    public String id;
    public String name;
    public String description;
    public Image image;
    public Location location;
    public List<Doctor> doctors = new ArrayList<>();

    public Hospital() {
    }

    public Hospital(String id, String name, String description, Image image, Location location) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.location = location;

        doctors.add(new Doctor.Builder().setFirstName("One").build());
        doctors.add(new Doctor.Builder().setFirstName("Two").build());
        doctors.add(new Doctor.Builder().setFirstName("Three").build());
        doctors.add(new Doctor.Builder().setFirstName("Four").build());
    }

    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
    }
}
