package com.example.admin.liftapp.Controller;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.admin.liftapp.Model.Model;
import com.example.admin.liftapp.Model.User;
import com.example.admin.liftapp.Model.UserViewModel;
import com.example.admin.liftapp.R;

import org.w3c.dom.Comment;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainerListFragment extends Fragment {

    private OnFragmentUserListInteractionListener mListener;
    List<User> userList = new LinkedList<>();
    UserListAdapter adapter;
    ProgressBar ListProgressBar;
    private UserViewModel userViewModel;

    public TrainerListFragment() {
        // Required empty public constructor
        //blabla
    }




    public interface OnFragmentUserListInteractionListener {
        void showUserFragment();

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       View view = inflater.inflate(R.layout.fragment_trainer_list, container, false);
        ListProgressBar = view.findViewById(R.id.user_list_progressbar);
        ListProgressBar.setVisibility(View.VISIBLE);
        ListView list = view.findViewById(R.id.trainer_list);
        adapter = new UserListAdapter();
        list.setAdapter(adapter);
        ListProgressBar.setVisibility(View.GONE);
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Model.instance().cancellGetAllUsers();
    }

    public static TrainerListFragment newInstance() {
        TrainerListFragment fragment = new TrainerListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentUserListInteractionListener) {
            mListener = (OnFragmentUserListInteractionListener) context;
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
                if (adapter != null) adapter.notifyDataSetChanged();
            }
        });

    }



    class UserListAdapter extends BaseAdapter {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        @Override
        public int getCount() {
            return userList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }


//fa

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.user_list_row, null);
            }
            ListProgressBar.setVisibility(View.VISIBLE);

            //TextView messageText = (TextView)convertView.findViewById(R.id.message_text);
            TextView userName = (TextView)convertView.findViewById(R.id.user_list_username);
            TextView userClaim = (TextView)convertView.findViewById(R.id.user_list_claim);

            TextView userHeightWeight = (TextView) convertView.findViewById(R.id.user_list_height_and_weight);
            TextView  userBirthday = (TextView) convertView.findViewById(R.id.user_list_birthday);
            final ImageView userImage = (ImageView) convertView.findViewById(R.id.imageView2);

            final User user = userList.get(position);

            userImage.setTag(user.imageUrl);
            userName.setText(user.getUserName());
            userClaim.setText(user.getClaim());
            userHeightWeight.setText(user.getHeight() + ", "+ user.getWeight());
            userBirthday.setText(user.getBirthday());

            userImage.setImageResource(R.drawable.traineravatar);
            if (user.imageUrl !=null) {
                if (!(user.imageUrl.equals(""))) {
                    Model.instance().getImage(user.imageUrl, new Model.GetImageListener() {
                        @Override
                        public void onSuccess(Bitmap image) {
                            if (userImage.getTag().equals(user.imageUrl)) {
                                userImage.setImageBitmap(image);
                            }
                        }

                        @Override
                        public void onFail() {

                        }
                    });
                }
            }
            ListProgressBar.setVisibility(View.GONE);


            return convertView;
        }
    }
}
