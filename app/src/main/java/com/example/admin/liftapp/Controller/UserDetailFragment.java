package com.example.admin.liftapp.Controller;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.liftapp.Model.Authentication;
import com.example.admin.liftapp.Model.Model;
import com.example.admin.liftapp.Model.User;
import com.example.admin.liftapp.Model.UserViewModel;
import com.example.admin.liftapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


public class UserDetailFragment extends Fragment {

    List<User> userList = new LinkedList<>();
    private UserViewModel userViewModel; //Adam Note this is the viewmodel for the Fragment it is created in the Onattach and holds data for fragement
    public String email;


    ProgressBar progressBar;
    private OnFragmentUserInteractionListener mListener;
    View view;

    private User currentUser;
    private EditText userName;
    private EditText height;
    private EditText weight;
    private TextView birth;
    private EditText claim;
    private ImageView userImage;
    int existingUser = 0;

    Bitmap imageBitmap;
    private String imgUrl;
    private static int Load_Image_results = 1;

    private static final String TAG = "UserDetailFragment";
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public interface OnFragmentUserInteractionListener {

        void showaTraineraListFragment();
        void showPlanFragment();
        void showLoginFragment();
    }

    public static UserDetailFragment newInstance() {
        UserDetailFragment fragment = new UserDetailFragment();
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
        view = inflater.inflate(R.layout.fragment_user_detail, container, false);
        progressBar = view.findViewById(R.id.userp_progressbar);
        email = Authentication.getUserEmail();
        if (email == null) {
            Toast.makeText(getActivity(), "Sign in first!", Toast.LENGTH_SHORT).show();
            mListener.showLoginFragment();
        } else {
            userName = view.findViewById(R.id.user_userName_et);
            height = view.findViewById(R.id.user_height_et);
            weight = view.findViewById(R.id.user_weight_et);
            birth = view.findViewById(R.id.newstudent_bdate_et);
            claim = view.findViewById(R.id.user_claimToFame_et);
            userImage = view.findViewById(R.id.user_avatar_img);

            birth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, mDateSetListener, year, month, day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });

            mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Log.d("TAG", "Year = " + year + "month = " + month + "dOM = " + dayOfMonth);
                    int realMonth = month + 1;
                    String date = " " + dayOfMonth + "/" + realMonth + "/" + year;
                    birth.setText(date);


                }
            };


            view.findViewById(R.id.newstudent_Image_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", "Button pressed");
                    dispatchTakePictureIntent();


                }
            });


            view.findViewById(R.id.newstudent_save_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    final String text = userName.getText().toString();
                    if (text.equals("")) {
                        Toast.makeText(MyApplication.getMyContext(), "Invalid Username", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (existingUser == 0) {
                        for (User user : userList) {
                            if (text.equals(user.userName)) {
                                Toast.makeText(MyApplication.getMyContext(), "UserName Already Exists", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

                    progressBar.setVisibility(View.VISIBLE);
                    User toAdd = new User();
                    toAdd.setUserName(text);
                    toAdd.setEmail(email);
                    toAdd.setBirhday(birth.getText().toString());
                    toAdd.setHeight(height.getText().toString());
                    toAdd.setWeight(weight.getText().toString());
                    toAdd.setClaim(claim.getText().toString());
                    if (imgUrl != null) {
                        toAdd.setImageUrl(imgUrl);
                    }

                    Model.instance().addUser(toAdd, new Model.OnCreation() {
                        @Override
                        public void onCompletion(boolean success) {

                            if (success == true) {
                                existingUser = 1;
                                userName.setEnabled(false);
                                Log.d("TAG", "created user");
                                Toast.makeText(getActivity(), "User Details Updated!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Log.d("TAG", "failed to create user");
                                Toast.makeText(getActivity(), "Failed to create user!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });

                }
            });
            view.findViewById(R.id.newstudent_cancel_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    final String text = userName.getText().toString();
                    if (text.equals("")) {
                        Toast.makeText(MyApplication.getMyContext(), "Invalid Username", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (existingUser == 0) {
                        Toast.makeText(MyApplication.getMyContext(), "UserName Was Never Registered", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    Model.instance().removeUser(currentUser, new Model.onDeletion() {
                        @Override
                        public void onDeletion(boolean success) {
                            if (success == true) {
                                List<User> toDelete = new ArrayList<>();
                                toDelete.add(currentUser);
                                userViewModel.deleteUserList(toDelete);
                                Log.d("TAG", "deleted user");
                                Toast.makeText(MyApplication.getMyContext(), "User Details deleted!", Toast.LENGTH_SHORT).show();
                                Authentication.signOut();
                                // mListener.showLoginFragment(); //Adam note after successfully deleting user we tell the listener (activity) to change fragment to LOgIn Fragment
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Log.d("TAG", "failed to delete user");
                                Toast.makeText(getActivity(), "Failed to delete user!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    mListener.showLoginFragment(); //Adam note after successfully deleting user we tell the listener (activity) to change fragment to LOgIn Fragment

                    progressBar.setVisibility(View.GONE);

                }
            });


            view.findViewById(R.id.user_avatar_img).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
            return view;
    }



    static final int REQUEST_IMAGE_CAPTURE = 1;
    final static int RESAULT_SUCCESS = -1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESAULT_SUCCESS) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            userImage.setImageBitmap(imageBitmap);
            if (imageBitmap != null) {
                Model.instance().saveImage(imageBitmap, email, new Model.SaveImageListener() {
                    @Override
                    public void complete(String url) {
                        progressBar.setVisibility(View.VISIBLE);
                        imgUrl = url;
                        progressBar.setVisibility(View.GONE); //first

                    }

                    @Override
                    public void fail() {
                        Toast.makeText(MyApplication.getMyContext(), "Error handling image", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
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
        //Adam Note - Here we connect fragement to its viewModel that holds the live data and that employeeListViewModel is of type EmployeeListViewModel step 1
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        //Adam Note - Here we activate the getEmployeeList function from the viewModel which returns the LiveData step 2

        //Process step 1
        userViewModel.getUsersList().observe(this, new Observer<List<User>>() {



            //Adam Note when there is change to database from liveData after we started obsrving they are acsessed here
            @Override
            public void onChanged(@Nullable List<User> users) {
                userList = users;
                Log.d("TAG","Got Users");
         //       progressBar.setVisibility(View.VISIBLE);
               //   User toDisplay = new User();
                  for (User user : userList)
                  {
                      if (user.email.equals(email)){
                          existingUser = 1;
                          currentUser = user;
                      userName.setText(user.userName);
                      userName.setEnabled(false);
                      height.setText(user.height);
                      weight.setText(user.weight);
                      birth.setText(user.birthday);
                      claim.setText(user.claim);
                          if (user.imageUrl !=null) {
                              if (!(user.imageUrl.equals(""))){
                                  imgUrl = user.imageUrl;
                              Model.instance().getImage(user.imageUrl, new Model.GetImageListener() {
                                  @Override
                                  public void onSuccess(Bitmap image) {

                                      userImage.setImageBitmap(image);
                                      progressBar.setVisibility(View.GONE);
                                  }
                                  @Override
                                  public void onFail() {
                                      progressBar.setVisibility(View.GONE);
                                  }
                              });
                          }
                  }
                      }
                  }

            }
        });


    }


}
