package shiro.am.i.chesto.retrofitdanbooru;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Shiro on 7/29/2016.
 */
public class Post extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private int id;

    @SerializedName("image_width")
    private int width;
    @SerializedName("image_height")
    private int height;
    @SerializedName("file_ext")
    private String fileExt;

    @SerializedName("tag_string_artist")
    private String tagStringArtist;
    @SerializedName("tag_string_character")
    private String tagStringCharacter;
    @SerializedName("tag_string_copyright")
    private String tagStringCopyright;
    @SerializedName("tag_string_general")
    private String tagStringGeneral;

    @SerializedName("has_large")
    private boolean hasLarge;
    @SerializedName("preview_file_url")
    private String smallFileUrl;
    @SerializedName("large_file_url")
    private String largeFileUrl;
    @SerializedName("file_url")
    private String originalFileUrl;

    private static final String BASE_URL = "http://danbooru.donmai.us";

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else {
            Post x = (Post) obj;
            return id == x.getId();
        }
    }

    public boolean hasFileUrl() {
        return smallFileUrl != null && largeFileUrl != null;
    }

    public String getWebUrl() {
        return BASE_URL + "/posts/" + id;
    }

    public Uri getWebUri() {
        return Uri.parse(getWebUrl());
    }

    public String getFileName() {
        return String.format("%s.%s", id, fileExt);
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTagStringArtist() {
        return tagStringArtist;
    }

    public String getTagStringCharacter() {
        return tagStringCharacter;
    }

    public String getTagStringCopyright() {
        return tagStringCopyright;
    }

    public String getTagStringGeneral() {
        return tagStringGeneral;
    }

    public boolean hasLargeFileUrl() {
        return hasLarge;
    }

    public String getSmallFileUrl() {
        return BASE_URL + smallFileUrl;
    }

    public String getLargeFileUrl() {
        return BASE_URL + largeFileUrl;
    }

    public String getOriginalFileUrl() {
        return BASE_URL + originalFileUrl;
    }
}
