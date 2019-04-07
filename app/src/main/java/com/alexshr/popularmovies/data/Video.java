package com.alexshr.popularmovies.data;

import com.alexshr.popularmovies.AppConfig;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

public class Video {

    @SerializedName("id")
    @Getter
    @Setter
    private String id;
    @SerializedName("iso_639_1")
    @Getter
    @Setter
    private String iso6391;
    @SerializedName("iso_3166_1")
    @Getter
    @Setter
    private String iso31661;
    @SerializedName("key")
    @Getter
    @Setter
    private String key;
    @SerializedName("name")
    @Getter
    @Setter
    private String name;
    @SerializedName("site")
    @Getter
    @Setter
    private String site;
    @SerializedName("size")
    @Getter
    @Setter
    private int size;
    @SerializedName("type")
    @Getter
    @Setter
    private String type;

    public Video() {
        this.site = "";
        this.size = 0;
        this.iso31661 = "";
        this.name = "";
        this.id = "";
        this.type = "";
        this.iso6391 = "";
        this.key = "";
    }

    public String getThumbnailUrl() {
        String url = String.format(AppConfig.YOUTUBE_THUMBNAIL, key);
        Timber.d("thumbnailUrl=%s", url);
        return url;
    }

    public String getVideoUrl() {
        String url = String.format(AppConfig.YOUTUBE_VIDEO, key);
        Timber.d("videoUrl=%s", url);
        return url;
    }

}