
package com.alexshr.popularmovies.data;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class Review {

    @SerializedName("author")
    @Getter
    @Setter
    private String author;
    @SerializedName("content")
    @Getter
    @Setter
    private String content;
    @SerializedName("id")
    @Getter
    @Setter
    private String id;
    @SerializedName("url")
    @Getter
    @Setter
    private String url;
}
