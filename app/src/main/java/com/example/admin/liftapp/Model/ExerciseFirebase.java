package com.example.admin.liftapp.Model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Idan Lapid on 13/6/2018.
 */

public class ExerciseFirebase {


    public static ValueEventListener listener;
    public static String userMail = "";
    private static boolean signedIn = false;




    public static void cancelGetAllExercises() {
        if (!signedIn) {
            return;
        }
        String email = Authentication.getUserEmail();
        String[] separated = email.split("\\.");
        String emailToId = separated[0] + separated[1];
        DatabaseReference stRef = FirebaseDatabase.getInstance().getReference().child("exercises").child(emailToId);
  //      stRef.removeEventListener(listener);
    }

    public interface Callback<T> {
        void onComplete(T data);
    }

    public static void getAllExercisesAndObserve(String user,final Callback<List<Exercise>> callback) {
        userMail = user;
        Log.d("TAG", "getAllExercisesAndObserve");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("exercises");
        DatabaseReference ref = myRef.child(user);
        listener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Exercise> list = new LinkedList<Exercise>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Exercise exercise = snap.getValue(Exercise.class);

                    list.add(exercise);
                }
                callback.onComplete(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onComplete(null);
            }
        });
        signedIn = true;
    }

    public static void returnListener(final Callback<List<Exercise>> callback) {
        String email = Authentication.getUserEmail();
        String[] separated = email.split("\\.");
        String emailToId = separated[0] + separated[1];
        Log.d("TAG", "return Observer");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("exercises");
        DatabaseReference ref = myRef.child(emailToId);
        listener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                List<Exercise> list = new LinkedList<Exercise>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Exercise exercise = snap.getValue(Exercise.class);

                    list.add(exercise);
                }
                callback.onComplete(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
        signedIn = true;
    }



    public interface OnCreationExercise {
        void onCompletion(boolean success);
    }

    public static void addExercise(Exercise exercise, final OnCreationExercise listener) {
        Log.d("TAG", "add exercise to firebase");
        String[] separated = exercise.getexerciseId().split("\\ ");
        String user = separated[0];
        HashMap<String, Object> json = exercise.toJson();
        json.put("lastUpdated", ServerValue.TIMESTAMP);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("exercises");
        DatabaseReference ref = myRef.child(user).child(exercise.exerciseId);
       getAllExercisesAndObserve(user, new Callback<List<Exercise>>() {
           @Override
           public void onComplete(List<Exercise> data) {
               Log.d("TAG", "Listener back on");

           }
       });
        ref.setValue(json, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e("TAG", "Error: Exercise could not be saved "
                            + databaseError.getMessage());
                    listener.onCompletion(false);
                } else {
                    Log.e("TAG", "Success : Exercise saved successfully.");
                    listener.onCompletion(true);
                }
            }
        });
    }
}
