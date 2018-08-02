package com.example.admin.liftapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.view.View;
import com.example.admin.liftapp.Controller.LoginFragment;
import com.example.admin.liftapp.Controller.MyApplication;
import com.example.admin.liftapp.Controller.PlanFragment;
import com.example.admin.liftapp.Controller.TrainerListFragment;
import com.example.admin.liftapp.Controller.UserDetailFragment;
import com.example.admin.liftapp.Model.Authentication;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements TrainerListFragment.OnFragmentUserListInteractionListener,LoginFragment.OnFragmentLoginInteractionListener,UserDetailFragment.OnFragmentUserInteractionListener,PlanFragment.OnFragmentUserInteractionListener {


    private Button buttonLogin;
    private Button buttonSocial;
    private Button buttonTrainer;
    private Button buttonPlan;
    private Button buttonUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication application= new MyApplication();
        buttonSocial = findViewById(R.id.List_Button_Nav);
        buttonLogin = findViewById(R.id.Login_Button_Nav);
        buttonPlan = findViewById(R.id.Routine_Button_Nav);
        buttonUser = findViewById(R.id.User_Button_Nav);

        buttonSocial.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               showaTraineraListFragment();
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginFragment();
            }
        });
        buttonPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlanFragment();
            }
        });

        buttonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserFragment();
            }
        });

                // if (Authentication.isSignedIn() == false) {
            showLoginFragment();
        ///}
        //else {
          //  showUserFragment();
        //}
    }

    public void showLoginFragment() {
        getSupportFragmentManager()
                .beginTransaction()
               .replace(R.id.container, LoginFragment.newInstance())
                .commit();
    }


    public void showUserFragment() {
        Log.d("TAG","Got to showUserFragment");
        getSupportFragmentManager().beginTransaction().replace(R.id.container, UserDetailFragment.newInstance()).commit();

    }



    @Override
    public void showaTraineraListFragment() {
        Log.d("TAG", "Got to showTrainerFragment");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, TrainerListFragment.newInstance())
                .commit();
    }

    @Override
    public void showPlanFragment() {
        Log.d("TAG","Got to showPlanFragment");
        getSupportFragmentManager().beginTransaction().replace(R.id.container, PlanFragment.newInstance()).commit();
    }
}
