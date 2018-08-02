package com.example.admin.liftapp.Model;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.admin.liftapp.Controller.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by admin on 10/05/2018.
 */

public class Model {
    private static Model instance = new Model();

    ModelFirebase modelFirebase;

    private Model() {
        this.modelFirebase  = new ModelFirebase();

    }
    public static Model instance() {
        return instance;
    }


    public void writeToSharedPreferences(String name, String key, String value) {
        SharedPreferences ref = MyApplication.getMyContext().getSharedPreferences(name,MODE_PRIVATE);
        SharedPreferences.Editor ed = ref.edit();
        ed.putString(key, value);
        ed.commit();
    }

    public void cancellGetAllUsers() {
        ModelFirebase.cancelGetAllUsers();
    }



    public void cancellGetAllExercises() {
        ModelFirebase.cancelGetAllExercises();

    }

    public interface OnCreation{
        public void onCompletion(boolean success);
    }

    public void addUser(User user, final OnCreation listener) {
        //this.databaseFirebase.addAlbum(album);
        modelFirebase.addUser(user, new ModelFirebase.OnCreation() {
            @Override
            public void onCompletion(boolean success) {
                listener.onCompletion(success);
            }
        });
    }

    public interface onDeletion{
        public void onDeletion(boolean success);

    }



    public void removeUser(User userName, final onDeletion listener) {
        modelFirebase.deleteUser(userName, new ModelFirebase.onDeletion() {
            @Override
            public void onDeletion(boolean success) {

                listener.onDeletion(success);
            }
        });


    }


    public void addExercise(Exercise exercise, final OnCreation listener) {
        modelFirebase.addExercise(exercise, new ModelFirebase.OnCreation() {
            @Override
            public void onCompletion(boolean success) {
                listener.onCompletion(success);
            }
        });


    }

    public interface SaveImageListener {
        void complete(String url);
        void fail();
    }

    public void saveImage(final Bitmap imageBmp, final String name, final SaveImageListener listener) {
        modelFirebase.saveImage(imageBmp, name, new SaveImageListener() {
            @Override
            public void complete(String url) {
                String fileName = URLUtil.guessFileName(url, null, null);
                saveImageToFile(imageBmp,fileName);
                listener.complete(url);
            }

            @Override
            public void fail() {
                listener.fail();
            }
        });


    }

    private void saveImageToFile(Bitmap imageBitmap, String imageFileName){
        try {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File imageFile = new File(dir,imageFileName);
            imageFile.createNewFile();

            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            addPicureToGallery(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addPicureToGallery(File imageFile){
        //add the picture to the gallery so we dont need to manage the cache size
        Intent mediaScanIntent = new
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        MyApplication.getMyContext().sendBroadcast(mediaScanIntent);
    }


    private Bitmap loadImageFromFile(String imageFileName){
        Bitmap bitmap = null;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir,imageFileName);
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(inputStream);
            Log.d("tag","got image from cache: " + imageFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public interface GetImageListener{
        void onSuccess(Bitmap image);
        void onFail();
    }

    public void getImage(final String url, final GetImageListener listener) {
        //check if image exsist localy
        String fileName = URLUtil.guessFileName(url, null, null);
        Bitmap image = loadImageFromFile(fileName);

        if (image != null){
            Log.d("TAG","getImage from local success " + fileName);
            listener.onSuccess(image);
        }else {
            modelFirebase.getImage(url, new GetImageListener() {
                @Override
                public void onSuccess(Bitmap image) {
                    String fileName = URLUtil.guessFileName(url, null, null);
                    Log.d("TAG","getImage from FB success " + fileName);
                    saveImageToFile(image,fileName);
                    listener.onSuccess(image);
                }

                @Override
                public void onFail() {
                    Log.d("TAG","getImage from FB fail ");
                    listener.onFail();
                }
            });
        }
    }
}
