package com.dulcerefugio.app.entunombre.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dulcerefugio.app.entunombre.R;
import com.dulcerefugio.app.entunombre.activities.MainActivity;

public class BuildPictureFragment extends Fragment {

		public static final String ARG_SECTION_NUMBER = "1234000";
		
		private BuildPictureListeners mCallbacks;

		public BuildPictureFragment() {
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			mCallbacks = (MainActivity) activity;
		}
    
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.f_main_build_picture, container, false);

			initialize(rootView);
			
			return rootView;
		}
		
		private void initialize(View rootView){
			
			RelativeLayout containerGetPhoto = (RelativeLayout) rootView.findViewById(R.id.containerGetPhoto);
			containerGetPhoto.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
				
					mCallbacks.OnGeneratePictureClick();
				}
				
			});
			
		}
		
		public interface BuildPictureListeners{
			void OnGeneratePictureClick();
		}
		
	}