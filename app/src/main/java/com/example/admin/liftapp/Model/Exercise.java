package com.example.admin.liftapp.Model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.HashMap;



@Entity
public class Exercise {
    @PrimaryKey
    @NonNull
    public String exerciseId;

    public String name;
    public String reps;
    public String sets;
    public String weight;

    public String trainerEmail;
    public float lastUpdated;



    public String getexerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }


    public String getSets() {
        return sets;
    }

    public void setSets(String sets) {
        this.sets = sets;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getTrainerEmail() {
        return trainerEmail;
    }

    public void setTrainerEmail(String trainerEmail) {
        this.trainerEmail = trainerEmail;
    }

    public void lastUpdated(float lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public float getlastUpdated() {
        return lastUpdated;
    }

    HashMap<String,Object> toJson(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("exerciseId", exerciseId);
        result.put("name", name);
        result.put("trainerEmail", trainerEmail);
        result.put("reps", reps);
        result.put("sets", sets);
        result.put("weight", weight);
        result.put("lastUpdated", lastUpdated);
        return result;
    }
}
