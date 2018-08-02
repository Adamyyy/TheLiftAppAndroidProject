package com.example.admin.liftapp.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Comment;

import java.io.ByteArrayOutputStream;

/**
 * Created by admin on 10/05/2018.
 */

public class ModelFirebase {

    public ModelFirebase() {

    }

    public static void cancelGetAllUsers() {
    UserFirebase.cancellGetAllSUsers();
    }




    public static void cancelGetAllExercises() {
        ExerciseFirebase.cancelGetAllExercises();
    }

    /*

     UserFirebase.addUser(user, UserFirebase.OnCreationComment() {
            @Override
            public void onCompletion(boolean success) {
                listener.onCompletion(success);
            }
        });
     */

    public interface OnCreation{
        public void onCompletion(boolean success);
    }

    public interface onDeletion{
        public void onDeletion(boolean success);
    }

    public void deleteUser(User userName, final onDeletion listener) {
        UserFirebase.removeUser(userName, new UserFirebase.OnDeletionUser() {
            @Override
            public void onDeletion(boolean success) {
                listener.onDeletion(success);
            }
        });

    }

    public void addUser(User user, final OnCreation listener) {
        UserFirebase.addUser(user, new UserFirebase.OnCreationUser() {
            @Override
            public void onCompletion(boolean success) {
                listener.onCompletion(success);
            }
        });

    }

    public void addExercise(Exercise exercise, final OnCreation listener) {
        ExerciseFirebase.addExercise(exercise, new ExerciseFirebase.OnCreationExercise() {
            @Override
            public void onCompletion(boolean success) {
                listener.onCompletion(success);
            }
        });

    }


    public void getImage(String url, final Model.GetImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(3* ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                listener.onSuccess(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Log.d("TAG",exception.getMessage());
                listener.onFail();
            }
        });
    }

    public void saveImage(Bitmap imageBmp, String name, final Model.SaveImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference imagesRef = storage.getReference().child("images").child(name);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.fail();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                listener.complete(downloadUrl.toString());
            }
        });
    }
}
