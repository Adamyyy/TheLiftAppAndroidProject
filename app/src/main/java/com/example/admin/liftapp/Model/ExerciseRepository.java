package com.example.admin.liftapp.Model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.admin.liftapp.Controller.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 09/05/2018.
 */

public class ExerciseRepository {


    //Adam Note repositories are singleTon hence we use instance.
    public static final ExerciseRepository instance = new ExerciseRepository();


    ExerciseRepository() {
    }

    MutableLiveData<List<Exercise>> ExerciseListliveData;

    public LiveData<List<Exercise>> getExerciseList(String user) {
        synchronized (this) {                                // Adam Note Synchronized means only one thread can enter function (critical section)
            if (ExerciseListliveData == null) {             //Adam Note if the employeeListliveData isnt null it means it was already created and can be sent to ViewModel's
                ExerciseListliveData = new MutableLiveData<List<Exercise>>();
                ExerciseFirebase.getAllExercisesAndObserve(user,new ExerciseFirebase.Callback<List<Exercise>>() {

                    @Override
                    public void onComplete(List<Exercise> data) {
                        if (data != null) ExerciseListliveData.setValue(data); //Adam note = sends data to listening fragments (mostly via ViewModel)
                        Log.d("TAG", "got exercise data");
                    }
                });
            }
        }
        return ExerciseListliveData;
    }



    public LiveData<List<Exercise>> getAllExercises(String user) {
        synchronized (this) {
            if (ExerciseListliveData == null) {
                ExerciseListliveData = new MutableLiveData<List<Exercise>>();
                long lastUpdateDate = 0;
                //lastUpdateDate
                try {
                    lastUpdateDate = MyApplication.getMyContext()
                            .getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("lastUpdateDate", 0);

                    Log.d("Tag","got the last update date");
                }catch (Exception e){
                    Log.d("Tag","in the exception");


                }
                final long recentUpdate = lastUpdateDate;

                //2. get all exercises records that where updated since last update date
                ExerciseFirebase.getAllExercisesAndObserve(user,new ExerciseFirebase.Callback<List<Exercise>>() {
                    @Override
                    public void onComplete(List<Exercise> data) {
                        if (data != null && data.size() > 0) {
                            //3. update the local DB
                            float reacentUpdate = recentUpdate;
                            addCommentDataInLocalStorage(data);
                            List<Exercise> toDelete = new ArrayList<Exercise>();
                            for (Exercise exercise : data) {
                               // addCommentDataInLocalStorage(data);
                                if (exercise.lastUpdated > reacentUpdate) {
                                    reacentUpdate = exercise.lastUpdated;
                                }

                                Log.d("TAG", "updating: " + exercise.toString());
                            }

                            ExerciseListliveData.setValue(data);
                            Log.d("TAG", "ListValueChanged");

                        }


                    }
                });
            }
        }

        /*
        List<Exercise> completeList = new ArrayList<Exercise>();
        List<Exercise> toDelete = new ArrayList<Exercise>();
        completeList = ExerciseListliveData.getValue();
        String email = Authentication.getUserEmail();
        for (Exercise ex: completeList){
            if(ex.trainerEmail != email) {
                toDelete.add(ex);
            }

        }
        deleteCommentDataInLocalStorage(toDelete);
        */
        return ExerciseListliveData;
    }


    //ADAM NOTE IMPORATANT STEP 6
    private void addCommentDataInLocalStorage(List<Exercise> data) {
        AddingTask task = new AddingTask();
       // task.setAlbumId(albumId);
        task.execute(data);
    }




    class AddingTask extends AsyncTask<List<Exercise>,String,List<Exercise>> {

        @Override
        protected List<Exercise> doInBackground(List<Exercise>[] lists) {
            Log.d("TAG","starting updateAlbumDataInLocalStorage in thread");
            if (lists.length > 0) {
                List<Exercise> data = lists[0];
                long lastUpdateDate = 0;
                try {
                    lastUpdateDate = MyApplication.getMyContext()
                            .getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("lastUpdateDate", 0);

                    Log.d("Tag","got the last update date");
                }catch (Exception e){
                    Log.d("Tag","in the exception");


                }
                if (data != null && data.size() > 0) {
                    //3. update the local DB
                    long reacentUpdate = lastUpdateDate;

                    for (Exercise comment : data) {
                        if (comment.getName() != null) {
                            String value = comment.getName();
                            Log.d("Tag", value);
                            AppLocalStore.db.exerciseDao().insertAll(comment);
                            Log.d("Tag", "after insert all");

                            if (comment.getlastUpdated() > reacentUpdate) {
                                reacentUpdate = (long) comment.getlastUpdated();
                            }
                            Log.d("TAG", "updating: " + comment.toString());
                        }
                    }
                    SharedPreferences.Editor editor = MyApplication.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).edit();
                    editor.putLong("lastUpdateDate", reacentUpdate);
                    editor.commit();
                }
                //return the complete student list to the caller
               // List<Exercise> commentList = AppLocalStore.db.exerciseDao().getAll();
                String email = Authentication.getUserEmail();
                List<Exercise> commentList = AppLocalStore.db.exerciseDao().loadAllByEmail(email);
                Log.d("TAG","finish updateEmployeeDataInLocalStorage in thread");

                return commentList;
            }
            return null;
        }





        @Override
        protected void onPostExecute(List<Exercise> comments) {
            super.onPostExecute(comments);
            ExerciseListliveData.setValue(comments);
            Log.d("TAG","update updateAlbumDataInLocalStorage in main thread");
            Log.d("TAG", "got items from local db: " + comments.size());

        }
    }


    private void deleteCommentDataInLocalStorage(List<Exercise> data) {
        DeletionTask task = new DeletionTask();
        task.execute(data);
    }




    class DeletionTask extends AsyncTask<List<Exercise>,String,List<Exercise>> {


        @Override
        protected List<Exercise> doInBackground(List<Exercise>[] lists) {
            List<Exercise> data = lists[0];

            for (Exercise comment : data) {
                AppLocalStore.db.exerciseDao().delete(comment);
            }

            List<Exercise> commentList = AppLocalStore.db.exerciseDao().getAll();
            return commentList;
        }


        //Ada, Note step 8
        @Override
        protected void onPostExecute(List<Exercise> comments) {
            super.onPostExecute(comments);
            ExerciseListliveData.setValue(comments);
        }
    }



}




