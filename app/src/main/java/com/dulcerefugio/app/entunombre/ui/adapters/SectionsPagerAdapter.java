package com.dulcerefugio.app.entunombre.ui.adapters;

import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.activities.fragments.PictureListFragment;
import com.dulcerefugio.app.entunombre.activities.fragments.PictureListFragment_;
import com.dulcerefugio.app.entunombre.activities.fragments.VideoListFragment_;
import com.orhanobut.logger.Logger;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

	private final int TABS_NUMBER=2;
	private Context mContext;
	private Fragment[] fragments;
	
	public SectionsPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		mContext = context;
		fragments = new Fragment[TABS_NUMBER];
	}

	@Override
	public Fragment getItem(int position) {
		TabFragments tabFragment = TabFragments.values()[position];
        Fragment fragment = fragments[position];

		if(fragment != null)
			return fragment;

		switch(tabFragment){
			case BUILD_PICTURE:
				fragments[position] = PictureListFragment_.builder().build();
                break;
			case VIDEO_LIST:
				fragments[position] = VideoListFragment_.builder().build();
                break;
            case ABOUT_US:
                fragments[position] = PictureListFragment_.builder().build();
                break;
		}

        return fragments[position];
	}

	@Override
	public int getCount() {
		// Show 3 total pages.
		return TABS_NUMBER;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return mContext.getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return mContext.getString(R.string.title_section2).toUpperCase(l);
		case 2:
			return mContext.getString(R.string.title_section3).toUpperCase(l);
		}
		return null;
	}
	
	public enum TabFragments{
		BUILD_PICTURE(0), VIDEO_LIST(1), ABOUT_US(3);
		
		private int tabsFragmentId;
		
		private TabFragments(int tabsFragmentId){
			this.tabsFragmentId = tabsFragmentId;
		}
		
		public TabFragments getTabFragmentById(int id){
			switch(id){
				case 0:
					return this.BUILD_PICTURE;
				case 1:
					return this.VIDEO_LIST;
				default:
					return null;
			}
		}
		
		public int getTabsFragmentId(){
			return this.tabsFragmentId;
		}
	}
}
