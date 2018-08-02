package com.example.admin.liftapp.Model;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by menachi on 27/12/2017.
 */

public class UserFirebase {

    public static ValueEventListener listener;
    private static ChildEventListener deleteListener;


    public interface Callback<T> {
        void onComplete(T data);
        void onDelete(T data);
    }

    public static void cancellGetAllSUsers() {
        DatabaseReference stRef = FirebaseDatabase.getInstance().getReference().child("users");
    //    stRef.removeEventListener(listener);
    }




    public static void getAllUsersAndObserve(final Callback<List<User>> callback) {
        Log.d("TAG", "getAllUsersAndObserve");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        deleteListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                List<User> list = new LinkedList<User>();
                list.add(user);
                callback.onDelete(list);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };




         listener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> list = new LinkedList<User>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    list.add(user);
                }
                callback.onComplete(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onComplete(null);
            }


        });
        myRef.removeEventListener(deleteListener);
        myRef.addChildEventListener(deleteListener);
    }

    public static void returnListener() {
        Log.d("TAG", "returnListener");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        listener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    /*
    public static void addUser(User user){
        Log.d("TAG", "add user to firebase");
        HashMap<String, Object> json = user.toJson();
        json.put("lastUpdated", ServerValue.TIMESTAMP);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.child(user.userName).setValue(json);
    }
    */



    public interface OnDeletionUser {
        void onDeletion (boolean success);
    }

    public interface OnCreationUser {
        void onCompletion(boolean success);
    }

    public static void removeUser(final User userName, final OnDeletionUser listener) {
        Log.d("TAG", "remove user from firebase");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        DatabaseReference ref = myRef.child(userName.userName);
        ref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e("TAG", "Error: User could not be deleted "
                            + databaseError.getMessage());
                    listener.onDeletion(false);
                } else {
                    Log.e("TAG", "Success : User removed successfully.");
                    listener.onDeletion(true);
                }
            }
        });
    }

    public static void addUser(User user, final OnCreationUser listener) {
        Log.d("TAG", "add user to firebase");
        HashMap<String, Object> json = user.toJson();
        json.put("lastUpdated", ServerValue.TIMESTAMP);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        DatabaseReference ref = myRef.child(user.userName);
      //  returnListener();
        getAllUsersAndObserve(new Callback<List<User>>() {
            @Override
            public void onComplete(List<User> data) {
                Log.d("TAG", "returned listener");

            }

            @Override
            public void onDelete(List<User> data) {

            }
        });
        ref.setValue(json, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e("TAG", "Error: User could not be saved "
                            + databaseError.getMessage());
                    listener.onCompletion(false);
                } else {
                    Log.e("TAG", "Success : User saved successfully.");
                    listener.onCompletion(true);
                }
            }
        });

    }
}
