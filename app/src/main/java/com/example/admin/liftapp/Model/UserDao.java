package com.example.admin.liftapp.Model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;


@Dao
public interface UserDao {


    @Query("SELECT * FROM User")
    List<User> getAll();

    //ADAM NOTE
    //THIS GETS AN ARRAY OF INTS (LETS NOT USE IT)
    @Query("SELECT * FROM User WHERE userName IN (:userName)")
    List<User> loadAllByIds(int[] userName);

    //NOT SURE ABOUT THIS ONE
    @Query("SELECT * FROM User WHERE userName IN (:userName)")
    List<User> loadAllByIds(String userName);



    //THIS LOOKS GOOD
    @Query("SELECT * FROM User WHERE userName = :userName")
    User findByuserName(String userName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(User... users);

    @Delete
    void delete(User user);



  //Eliav Code with current project alterations
    /*
    @Query("SELECT * FROM User")
    List<User> getAll();


    //ADAM NOTE MIGHT BE WRONG CHECK THIS LATER!
    @Query("SELECT * FROM User WHERE userName IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM User WHERE userName = :userName")
    User findById(String userName);

    @Insert
    void insertAll(User... employees);

    @Delete
    void delete(User employee);
*/


    /*
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(User... users);
     */
}
