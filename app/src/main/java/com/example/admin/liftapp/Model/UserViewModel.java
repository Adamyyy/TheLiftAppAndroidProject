package com.example.admin.liftapp.Model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

/**
 * Created by admin on 09/05/2018.
 */


public class UserViewModel extends ViewModel {
    private LiveData<List<User>> users;

    public UserViewModel() {
        //step 3
        // Adam Note
        //In the constroctor we generate the employeeList with the data from the EmployeeRepository
        users = UserRepository.instance.getAllUsers();
        //Process step 2
    }


    //step 4
    //Adam Note return the EmployeeList created from the EmployeeRepository in the constructor
    public LiveData<List<User>> getUsersList() {
        return users;
    }
    public void deleteUserList(List<User> data) {
        UserRepository.instance.deleteLocally(data);
      //  return users;
    }


}