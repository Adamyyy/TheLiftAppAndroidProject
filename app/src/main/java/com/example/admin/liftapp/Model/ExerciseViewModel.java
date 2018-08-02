package com.example.admin.liftapp.Model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

/**
 * Created by admin on 09/05/2018.
 */


public class ExerciseViewModel extends ViewModel {
    private LiveData<List<Exercise>> exers;

    public ExerciseViewModel() {

        //step 3
        // Adam Note
        //In the constructor we generate the employeeList with the data from the EmployeeRepository
       String email = Authentication.getUserEmail();
        if (email == null) {

        }
        else {
            String[] separated = email.split("\\.");
            String user = separated[0] + separated[1];
            exers = ExerciseRepository.instance.getAllExercises(user);
            //Process step 2
        }
    }

    //step 4
    //Adam Note return the EmployeeList created from the EmployeeRepository in the constructor
    public LiveData<List<Exercise>> getExercisesList() {
        return exers;
    }

}