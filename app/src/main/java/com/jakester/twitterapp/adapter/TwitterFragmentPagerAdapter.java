package com.jakester.twitterapp.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jakester.twitterapp.fragments.HomeTimelineFragment;
import com.jakester.twitterapp.fragments.MentionsTimelineFragment;

/**
 * Created by Jake on 10/5/2017.
 */

public class TwitterFragmentPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[] { "Home", "Mentions"};
    private Context context;

    public TwitterFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return new HomeTimelineFragment();
        }
        else if(position == 1){
            return new MentionsTimelineFragment();
        }
        else{
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
