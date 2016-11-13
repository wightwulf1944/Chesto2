package shiro.am.i.chesto.retrofitDanbooru;

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
    private int imageWidth;
    @SerializedName("image_height")
    private int imageHeight;
    @SerializedName("tag_string_artist")
    private String tagStringArtist;
    @SerializedName("tag_string_character")
    private String tagStringCharacter;
    @SerializedName("tag_string_copyright")
    private String tagStringCopyright;
    @SerializedName("tag_string_general")
    private String tagStringGeneral;
    @SerializedName("file_url")
    private String largeFileUrl;
    @SerializedName("preview_file_url")
    private String smallFileUrl;

    private static final String BASE_URL = "http://danbooru.donmai.us";

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            Post x = (Post) obj;
            return id == x.getId();
        } else {
            return false;
        }
    }

    public boolean hasFileUrl() {
        return smallFileUrl != null && largeFileUrl != null;
    }

    public Uri getUri() {
        return Uri.parse(BASE_URL + "/posts/" + id);
    }

    public int getId() {
        return id;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
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

    public String getLargeFileUrl() {
        return BASE_URL + largeFileUrl;
    }

    public String getSmallFileUrl() {
        return BASE_URL + smallFileUrl;
    }
}
