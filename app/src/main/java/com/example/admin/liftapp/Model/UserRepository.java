package com.example.admin.liftapp.Model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.admin.liftapp.Controller.MyApplication;

import org.w3c.dom.Comment;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by admin on 09/05/2018.
 */

public class UserRepository {


    //Adam Note repositories are singleTon hence we use instance.
    public static final UserRepository instance = new UserRepository();


    UserRepository() {
    }

    MutableLiveData<List<User>> UserListliveData;

    public LiveData<List<User>> getUserList() {
        synchronized (this) {                                // Adam Note Synchronized means only one thread can enter function (critical section)
            if (UserListliveData == null) {             //Adam Note if the employeeListliveData isnt null it means it was already created and can be sent to ViewModel's
                UserListliveData = new MutableLiveData<List<User>>();
                UserFirebase.getAllUsersAndObserve(new UserFirebase.Callback<List<User>>() {

                    @Override
                    public void onComplete(List<User> data) {
                        if (data != null) UserListliveData.setValue(data); //Adam note = sends data to listening fragments (mostly via ViewModel)
                        Log.d("TAG", "got user data");
                    }

                    @Override
                    public void onDelete(List<User> data) {
                        Log.d("TAG", "got deleted user data");

                    }
                });
            }
        }
        return UserListliveData;
    }



    public LiveData<List<User>> getAllUsers() {
        synchronized (this) {
            if (UserListliveData == null) {
                UserListliveData = new MutableLiveData<List<User>>();
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

                //2. get all users records that where updated since last update date
                UserFirebase.getAllUsersAndObserve(new UserFirebase.Callback<List<User>>() {
                    @Override
                    public void onComplete(List<User> data) {
                        if (data != null && data.size() > 0) {
                            //3. update the local DB
                            float reacentUpdate = recentUpdate;
                            addCommentDataInLocalStorage(data);
                            for (User user : data) {
                               // addCommentDataInLocalStorage(data);
                                if (user.lastUpdated > reacentUpdate) {
                                    reacentUpdate = user.lastUpdated;
                                }
                                Log.d("TAG", "updating: " + user.toString());
                            }

                        }


                    }

                    @Override
                    public void onDelete(List<User> data) {
                        Log.d("TAG", "got deleted user data");
                        deleteLocally(data);
                    }
                });
            }
        }
        return UserListliveData;
    }


    //ADAM NOTE IMPORATANT STEP 6
    private void addCommentDataInLocalStorage(List<User> data) {
        AddingTask task = new AddingTask();
       // task.setAlbumId(albumId);
        task.execute(data);
    }

  //  public static void deleteLocally(User userName)


    public void deleteLocally(List<User> data) {
        DeletionTask task = new DeletionTask();
        task.execute(data);
    }




    class DeletionTask extends AsyncTask<List<User>,String,List<User>> {


        @Override
        protected List<User> doInBackground(List<User>[] lists) {
            List<User> data = lists[0];

            for (User comment : data) {
                AppLocalStore.db.userDao().delete(comment);
            }

            List<User> commentList = AppLocalStore.db.userDao().getAll();
         //   UserListliveData.setValue(commentList);
            return commentList;
        }


        //Ada, Note step 8
        @Override
        protected void onPostExecute(List<User> comments) {
            super.onPostExecute(comments);
            UserListliveData.setValue(comments);
        }
    }

    class AddingTask extends AsyncTask<List<User>,String,List<User>> {

        @Override
        protected List<User> doInBackground(List<User>[] lists) {
            Log.d("TAG","starting updateAlbumDataInLocalStorage in thread");
            if (lists.length > 0) {
                List<User> data = lists[0];
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

                    for (User comment : data) {
                        if (comment.getUserName() != null) {

                            AppLocalStore.db.userDao().insertAll(comment);
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
                List<User> commentList = AppLocalStore.db.userDao().getAll();
                Log.d("TAG","finish updateEmployeeDataInLocalStorage in thread");

                return commentList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<User> comments) {
            super.onPostExecute(comments);
            UserListliveData.setValue(comments);
            Log.d("TAG","update updateAlbumDataInLocalStorage in main thread");
            Log.d("TAG", "got items from local db: " + comments.size());

        }
    }

}




