package com.example.admin.liftapp.Model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;



import java.util.List;

/**
 * Created by admin on 21/04/2018.
 */

@Dao
public interface ExerciseDao {
    @Query("SELECT * FROM Exercise")
    List<Exercise> getAll();

    @Query("SELECT * FROM Exercise WHERE exerciseId IN (:exerciseId)")
    List<Exercise> loadAllByIds(String exerciseId);

    @Query("SELECT * FROM Exercise WHERE trainerEmail IN (:trainerEmail)")
    List<Exercise> loadAllByEmail(String trainerEmail);

    @Query("SELECT * FROM Exercise WHERE exerciseId = :exerciseId")
    Exercise findById(String exerciseId);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Exercise... excercises);

    @Delete
    void delete(Exercise exercise);


}
