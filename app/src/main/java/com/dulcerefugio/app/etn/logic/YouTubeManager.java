package com.dulcerefugio.app.etn.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


import android.util.Log;

import com.dulcerefugio.app.etn.data.dao.YoutubeVideo;
import com.dulcerefugio.app.etn.util.ConfigProperties;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.common.collect.Lists;


public class YouTubeManager {

    /**
     * Define a global variable that identifies the name of a file that
     * contains the developer's API key.
     */
    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;
    private static final String YOUTUBE_API_KEY = ConfigProperties.getInstance().YOUTUBE_API_KEY;
    private static final String TAG = "YoutubeManager";
    public static final String CHANNEL_ID = ConfigProperties.getInstance().YOUTUBE_CHANNEL_ID;

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * display the name and thumbnail image of each video in the result set.
     *
     * @param queryTerm specifies the query str to search.
     * @param channelId specifies the YoutubeChannelId.
     */
    public List<YoutubeVideo> getVideosFromYoutubeChannel(String queryTerm, String channelId) {

        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                @Override
                public void initialize(com.google.api.client.http.HttpRequest httpRequest) throws IOException {

                }
            }).setApplicationName("youtube-cmdline-search-sample").build();

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");
            search.setChannelId(channelId);
            // Set your developer key from the Google Developers Console for
            // non-authenticated requests. See:
            // https://console.developers.google.com/
            Log.d("",YOUTUBE_API_KEY);
            search.setKey(YOUTUBE_API_KEY);
            search.setQ(queryTerm);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url,snippet/description)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                return Lists.reverse(getYoutubeVideoList(searchResultList.iterator()));
            }
        } catch (GoogleJsonResponseException e) {
            Log.e(TAG,"There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            Log.e(TAG,"There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    /**
     * Prints out all results in the Iterator. For each result, print the
     * title, video ID, and thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     *
     * @param iteratorSearchResults Youtube API resultset
     **/
    private List<YoutubeVideo> getYoutubeVideoList(Iterator<SearchResult> iteratorSearchResults) {

      List<YoutubeVideo> videoList = new ArrayList<YoutubeVideo>();

        if (!iteratorSearchResults.hasNext()) {
            Log.d(TAG, "No videos found in Youtube");
            return new ArrayList<>();
        }

        while (iteratorSearchResults.hasNext()) {
            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {

                String videoId = rId.getVideoId();
                String title = singleVideo.getSnippet().getTitle();
                String description = singleVideo.getSnippet().getDescription();

                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                String thumbUrl = thumbnail.getUrl();

                videoList.add(new YoutubeVideo(videoId, title, description, thumbUrl, new Date()));
            }
        }

        return videoList;
    }

}