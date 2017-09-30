package com.jakester.twitterapp.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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


import com.jakester.twitterapp.R;
import com.jakester.twitterapp.models.Tweet;

import java.util.Calendar;

/**
 * Created by Jake on 9/20/2017.
 */

public class NewTweetDialogFragment extends DialogFragment implements View.OnClickListener{
    private EditText mTweetEdit;
    private TextView mCharLimitText;
    private Button mTweetButton;
    private ImageView mCloseButton;
    int charLimit = 140;

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
            Tweet tweet = new Tweet();
            FilterDialogListener listener = (FilterDialogListener) getActivity();
            listener.onFinishFilterDialog(tweet);
            dismiss();
        }
    }

    public interface FilterDialogListener {
        void onFinishFilterDialog(Tweet tweet);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_new_tweet_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTweetEdit = (EditText) view.findViewById(R.id.et_tweet);
        mTweetButton = (Button) view.findViewById(R.id.btn_tweet);
        mCloseButton = (ImageView) view.findViewById(R.id.ib_close);
        mCharLimitText = (TextView) view.findViewById(R.id.tv_char_counter);
        mCharLimitText.setText(Integer.toString(charLimit));
        mTweetEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.length() > start){
                    charLimit--;
                }
                else{
                    charLimit++;
                }
                if(charSequence.length() >= 140){
                    mCharLimitText.setTextColor(Color.RED);
                }
                else{
                    mCharLimitText.setTextColor(getActivity().getResources().getColor(R.color.secondaryTextColor));
                }
                mCharLimitText.setText(Integer.toString(charLimit));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mTweetButton.setOnClickListener(this);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

}
