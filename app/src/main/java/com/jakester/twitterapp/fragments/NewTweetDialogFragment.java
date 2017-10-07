package com.jakester.twitterapp.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.jakester.twitterapp.R;
import com.jakester.twitterapp.managers.PrefsManager;
import com.jakester.twitterapp.models.Tweet;
import com.jakester.twitterapp.models.User;
import com.jakester.twitterapp.util.TwitterContstants;

import org.parceler.Parcels;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Jake on 9/20/2017.
 */

public class NewTweetDialogFragment extends DialogFragment implements View.OnClickListener{
    private EditText mTweetEdit;
    private TextView mCharLimitText, mReplyTo, mUsername, mUserhandle;
    private Button mTweetButton;
    private ImageView mCloseButton;
    private CircleImageView mProfileCircle;
    private int charLimit = 140;
    private int charsLeft = 140;
    private User mUser, mUserReply;
    private boolean mReply;
    public NewTweetDialogFragment(){

    }

    public static NewTweetDialogFragment newInstance(String title){
        NewTweetDialogFragment frag = new NewTweetDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onClick(View view) {
        if(mTweetEdit.length() <= 140) {
            Tweet tweet = new Tweet(mUser, mTweetEdit.getText().toString(), mUserReply.getName());
            FilterDialogListener listener = (FilterDialogListener) getActivity();
            listener.onFinishFilterDialog(tweet,mReply);
            dismiss();
        }
    }

    public interface FilterDialogListener {
        void onFinishFilterDialog(Tweet tweet, boolean reply);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = (User) Parcels.unwrap(getArguments().getParcelable("user"));
        mUserReply = (User) Parcels.unwrap(getArguments().getParcelable("user_reply"));
        mReply = getArguments().getBoolean("reply");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_tweet_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mReplyTo = (TextView) view.findViewById(R.id.tv_reply_to);
        mTweetEdit = (EditText) view.findViewById(R.id.et_tweet);
        mTweetButton = (Button) view.findViewById(R.id.btn_tweet);
        mCloseButton = (ImageView) view.findViewById(R.id.ib_close);
        mCharLimitText = (TextView) view.findViewById(R.id.tv_char_counter);
        mProfileCircle = (CircleImageView) view.findViewById(R.id.cv_profile_image);
        mUsername = (TextView) view.findViewById(R.id.tv_action_name);
        mUserhandle = (TextView) view.findViewById(R.id.tv_action_handle);
        User userToDisplay;
        if(mUserReply != null){
            userToDisplay = mUserReply;
        }
        else{
            userToDisplay = mUser;
        }

        mTweetEdit.setText(PrefsManager.getInstance(getActivity()).getDraft());
        mTweetEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                charsLeft = charLimit-charSequence.length();
                if(charsLeft <= 0){
                    mCharLimitText.setTextColor(Color.RED);
                }
                else{
                    mCharLimitText.setTextColor(getActivity().getResources().getColor(R.color.secondaryTextColor));
                }
                mCharLimitText.setText(Integer.toString(charsLeft));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mTweetButton.setOnClickListener(this);

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTweetEdit.getText().length() > 0){
                    AlertDialog.Builder saveDraft = new AlertDialog.Builder(getActivity());
                    saveDraft.setTitle("Save Draft?")
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    PrefsManager.getInstance(getActivity()).saveDraft(mTweetEdit.getText().toString());
                                    dismiss();
                                }

                            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismiss();
                        }
                    }).show();

                }
                else{
                    dismiss();
                }
            }
        });
        mUsername.setText(userToDisplay.getName());
        mUserhandle.setText(userToDisplay.getScreenName());
        mCharLimitText.setText(Integer.toString(charLimit));
        if(mReply){
            mReplyTo.setVisibility(View.VISIBLE);
            mReplyTo.setText("In reply to " + userToDisplay.getScreenName());
            mTweetEdit.setText(mUserReply.getScreenName());
            mCharLimitText.setText(Integer.toString(charLimit - mUserReply.getScreenName().length()));
        }
        Glide.with(getActivity()).load(userToDisplay.getProfileImage()).into(mProfileCircle);
    }

}
