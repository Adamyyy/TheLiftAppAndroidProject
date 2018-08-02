package com.example.admin.liftapp.Controller;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.liftapp.Model.Authentication;
import com.example.admin.liftapp.Model.Exercise;
import com.example.admin.liftapp.Model.ExerciseViewModel;
import com.example.admin.liftapp.Model.Model;
import com.example.admin.liftapp.Model.User;
import com.example.admin.liftapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


public class PlanFragment extends Fragment {

    //DATA loading/saving
    private ExerciseViewModel exerciseViewModel;
    List<Exercise> exerciseList = new LinkedList<>();

    ExerciseListAdapter adapter;
    int existingExercise = 0;

    public String email;
    ProgressBar ListProgressBar;
    ProgressBar progressBar;
    private OnFragmentUserInteractionListener mListener;
    private String userEmailWithOutDot;


    //UI
    private Button buttonCancel;
    private Button buttonSave;
    private EditText bench_weight;
    private EditText bench_sets;
    private EditText bench;
    private EditText deadlift_weight;
    private EditText deadlift_sets;
    private EditText deadlift;
    private EditText overhead_weight;
    private EditText overhead_sets;
    private EditText overhead;
    private EditText squat_weight;
    private EditText squat_sets;
    private EditText squat;

    private List<EditText> texts;

    boolean Flag = false;

    private static int Load_Image_results = 1;

    private static final String TAG = "PlanFragment";

    public interface OnFragmentUserInteractionListener {
        void showPlanFragment();
        void showLoginFragment();
    }


    public void onDestroy() {
        super.onDestroy();
        if (email != null) {
            Model.instance().cancellGetAllExercises();
        }
    }

    public static PlanFragment newInstance() {
        PlanFragment fragment = new PlanFragment();
        return fragment;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);
        progressBar = view.findViewById(R.id.plan_progressbar);
        email = Authentication.getUserEmail();

        bench = view.findViewById(R.id.plan_bench);
        bench_sets = view.findViewById(R.id.plan_bench_sets);
        bench_weight = view.findViewById(R.id.plan_bench_weight);

        deadlift = view.findViewById(R.id.plan_deadlift);
        deadlift_sets = view.findViewById(R.id.plan_deadlift_sets);
        deadlift_weight = view.findViewById(R.id.plan_deadlift_weight);

        overhead = view.findViewById(R.id.plan_overhead);
        overhead_sets = view.findViewById(R.id.plan_overhead_sets);
        overhead_weight = view.findViewById(R.id.plan_overhead_weight);

        squat = view.findViewById(R.id.plan_squat);
        squat_sets = view.findViewById(R.id.plan_squat_sets);
        squat_weight = view.findViewById(R.id.plan_squat_weight);

        texts = new ArrayList<>();
        texts.add(bench_weight);
        texts.add(bench_sets);
        texts.add(bench);
        texts.add(deadlift_weight);
        texts.add(deadlift_sets);
        texts.add(deadlift);
        texts.add(overhead_weight);
        texts.add(overhead_sets);
        texts.add(overhead);
        texts.add(squat_weight);
        texts.add(squat_sets);
        texts.add(squat);

        if (email == null) {
            Toast.makeText(getActivity(), "Sign in first!", Toast.LENGTH_SHORT).show();
            mListener.showLoginFragment();
        } else {
            String[] separated = email.split("\\.");
            String emailToId = separated[0] + separated[1];
            userEmailWithOutDot = emailToId;


            view.findViewById(R.id.plan_button_give).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Authentication.isSignedIn()) {
                        progressBar.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "Sign in first!", Toast.LENGTH_SHORT).show();
                        mListener.showLoginFragment();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        calcPlan();
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Calculated new weights. Now lift them app!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            view.findViewById(R.id.plan_button_save).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Authentication.isSignedIn()) {
                        progressBar.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "Sign in first!", Toast.LENGTH_SHORT).show();
                        mListener.showLoginFragment();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        //TODO: Need to check what was added, and later maybe do an update function only update the existing ex
                        addExerciseToFB(bench.getText().toString(), bench_sets.getText().toString(), bench_weight.getText().toString(), getString(R.string.ex_bench), email);
                        addExerciseToFB(deadlift.getText().toString(), deadlift_sets.getText().toString(), deadlift_weight.getText().toString(), getString(R.string.ex_deadlift), email);
                        addExerciseToFB(overhead.getText().toString(), overhead_sets.getText().toString(), overhead_weight.getText().toString(), getString(R.string.ex_overhead), email);
                        addExerciseToFB(squat.getText().toString(), squat_sets.getText().toString(), squat_weight.getText().toString(), getString(R.string.ex_squat), email);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });

            view.findViewById(R.id.plan_button_reset).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    resetPlan();
                    progressBar.setVisibility(View.GONE);
                }
            });

            view.findViewById(R.id.plan_button_default).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    defaultPlan();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        return view;
    }

    private void addExerciseToFB(String reps, String sets, String weight, String name, String mail) {
        Exercise toAdd = new Exercise();


        toAdd.exerciseId = userEmailWithOutDot + " " + name;
        toAdd.setName(name);
        toAdd.setReps(reps);
        toAdd.setSets(sets);
        toAdd.setWeight(weight);
        toAdd.setTrainerEmail(mail);

        //Add exercise to firebase
        Model.instance().addExercise(toAdd, new Model.OnCreation() {
            @Override
            public void onCompletion(boolean success) {

                if (success == true) {
                    Log.d("TAG", "created exercise");
                    Toast.makeText(getActivity(), "Plan saved!", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d("TAG", "failed to create exercise");
                    Toast.makeText(getActivity(), "Failed to save plan!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentUserInteractionListener) {
            mListener = (OnFragmentUserInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

            //Adam Note - Here we connect fragment to its viewModel that holds the live data and that employeeListViewModel is of type EmployeeListViewModel step 1
            exerciseViewModel = ViewModelProviders.of(this).get(ExerciseViewModel.class);

            //Adam Note - Here we activate the getEmployeeList function from the viewModel which returns the LiveData step 2

        if (exerciseViewModel.getExercisesList() == null)
            return;
            //Process step 1
            exerciseViewModel.getExercisesList().observe(this, new Observer<List<Exercise>>() {

                //Adam Note when there is change to database from liveData after we started observing they are accessed here
                @Override
                public void onChanged(@Nullable List<Exercise> exercises) {
                    exerciseList = exercises;

                    bench_weight.setText(exercises.get(0).getWeight());
                    bench_sets.setText(exercises.get(0).getSets());
                    bench.setText(exercises.get(0).getReps());
                    deadlift_weight.setText(exercises.get(1).getWeight());
                    deadlift_sets.setText(exercises.get(1).getSets());
                    deadlift.setText(exercises.get(1).getReps());
                    overhead_weight.setText(exercises.get(2).getWeight());
                    overhead_sets.setText(exercises.get(2).getSets());
                    overhead.setText(exercises.get(2).getReps());
                    squat_weight.setText(exercises.get(3).getWeight());
                    squat_sets.setText(exercises.get(3).getSets());
                    squat.setText(exercises.get(3).getReps());

                    Log.d("TAG", "Got exercises");
                    if (adapter != null) adapter.notifyDataSetChanged();
                }
            });
    }

    class ExerciseListAdapter extends BaseAdapter {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        @Override
        public int getCount() {
            return exerciseList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.user_list_row, null);

            return convertView;
        }
    }

    /////////////////////////////////PLAN FUNCS/////////////////////////////////
    private void resetPlan() {
        for (EditText text:texts) {
            text.setText("0");
        }
    }

    private void defaultPlan() {
        for (EditText text:texts) {
            text.setText("5");
        }
        bench_weight.setText("20");
        deadlift_weight.setText("20");
        overhead_weight.setText("20");
        squat_weight.setText("20");
    }

    private void calcPlan() {
        bench_weight.setText(Float.toString(calcSet(10.0f, strToFlt(bench_weight))));
        deadlift_weight.setText(Float.toString(calcSet(10.0f, strToFlt(deadlift_weight))));
        overhead_weight.setText(Float.toString(calcSet(10.0f, strToFlt(overhead_weight))));
        squat_weight.setText(Float.toString(calcSet(10.0f, strToFlt(squat_weight))));
    }

    private float strToFlt(TextView txt) {
        if (txt.getText().toString().equals("") || txt.getText().toString().equals("0") || txt.getText().toString().equals("0.0"))
            return 5.0f;
        return Float.parseFloat(txt.getText().toString());
    }

    private float calcSet(Float baseWeight, float weightVar) {

        float set = (float) Math.round(weightVar);

        set *= 1.25f;

        set /= 10f;
        set = (float) Math.round(set * 8f) / 8f;
        set *= 10f;

        return set;
    }

}
