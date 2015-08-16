package com.dulcerefugio.app.entunombre.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.dulcerefugio.app.entunombre.R;

public class VideoPlayerActivity extends YouTubeBaseActivity
        implements OnInitializedListener,
        YouTubePlayer.PlayerStateChangeListener{

	private static final int RECOVERY_DIALOG_REQUEST = 1;
    private static final String TAG = "VIDEO_PLAY_ACTIVITY";
    private String videoId = null;
    public static final String VIDEO_ID = "VIDEO_ID";
    private final String YOUTUBE_API_KEY = "AIzaSyCk4nUUrXlUqZ6NFtsVfeEjVaRy7ckvw_0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_video_player);
		YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		youTubeView.initialize(YOUTUBE_API_KEY, this);
        this.videoId = getIntent().getExtras().getString(MainActivity.VIDEO_URL_PLAY);

    }

	@Override
	public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        player.setPlayerStateChangeListener(this);
		if (!wasRestored) {
			try {
			      player.loadVideo(videoId);
			} catch (IllegalStateException ise){
                Log.e(TAG, ise.getMessage());
                return;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
			}
        }
	}

	@Override
	public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
		if (errorReason.isUserRecoverableError()) {
			errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();

		} else {
			String errorMessage = String.format("El video no esta disponible en estos momentos", errorReason.toString());
            Log.e(TAG, errorMessage);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RECOVERY_DIALOG_REQUEST) {
			// Retry initialization if user performed a recovery action
			getYouTubePlayerProvider().initialize(YOUTUBE_API_KEY, this);
		}
	}

	private Provider getYouTubePlayerProvider() {
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        Log.d(TAG, "errorReason: "+errorReason.toString());
        if(errorReason.equals(YouTubePlayer.ErrorReason.NOT_PLAYABLE) ||
           errorReason.equals(YouTubePlayer.ErrorReason.INTERNAL_ERROR) ||
           errorReason.equals(YouTubePlayer.ErrorReason.BLOCKED_FOR_APP) ||
           errorReason.equals(YouTubePlayer.ErrorReason.USER_DECLINED_RESTRICTED_CONTENT)){

            Intent returnIntent = new Intent();
            Bundle args = new Bundle();
            args.putString(VIDEO_ID,videoId);
            returnIntent.putExtras(args);
            finish();
        }

        if(errorReason.equals(YouTubePlayer.ErrorReason.NETWORK_ERROR) ||
           errorReason.equals(YouTubePlayer.ErrorReason.UNEXPECTED_SERVICE_DISCONNECTION) ||
           errorReason.equals(YouTubePlayer.ErrorReason.UNKNOWN)){

            Log.d(TAG, "NETWORK ERROR");
            Intent returnIntent = new Intent();
            Bundle args = new Bundle();
            args.putString(VIDEO_ID,videoId);
            returnIntent.putExtras(args);
            finish();
        }
    }
}
