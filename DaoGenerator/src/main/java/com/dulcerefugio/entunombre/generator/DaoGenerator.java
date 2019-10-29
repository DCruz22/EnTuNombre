package com.dulcerefugio.entunombre.generator;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoGenerator {

    public static void main(String args[]) throws Exception {

        //Schema will be the version of the database and the package name where all the DAO's will be placed
        Schema schema = new Schema(1, "com.dulcerefugio.app.etn.data.dao");

        //=======================  Entities  ============================

        //YoutubeVideo
        Entity youtubeVideo = schema.addEntity("YoutubeVideo");
        youtubeVideo.setTableName("youtube_videos");
        youtubeVideo.addStringProperty("video_id").primaryKey();
        youtubeVideo.addStringProperty("title");
        youtubeVideo.addStringProperty("description");
        youtubeVideo.addStringProperty("thumbnail_url");
        youtubeVideo.addDateProperty("created_at");

        Entity generatedImages  = schema.addEntity("GeneratedImages");
        generatedImages.setTableName("generated_images");
        generatedImages.addIdProperty().autoincrement().primaryKey();
        generatedImages.addStringProperty("path");
        generatedImages.addStringProperty("date");

        String n = "";
        String[] values = n.split(",");

        new de.greenrobot.daogenerator.DaoGenerator().generateAll(schema, args[0]);
    }
}
