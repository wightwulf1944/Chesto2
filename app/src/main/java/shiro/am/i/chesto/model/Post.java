package shiro.am.i.chesto.model;

import android.net.Uri;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Shiro on 7/29/2016.
 */
public class Post extends RealmObject {

    private static final String BASE_URL = "http://danbooru.donmai.us";

    @PrimaryKey
    private int id;
    private int width;
    private int height;
    private String fileName;
    private int tagCount;
    private String tagStringArtist;
    private String tagStringCharacter;
    private String tagStringCopyright;
    private String tagStringGeneral;
    private String tagStringMeta;
    private boolean hasLarge;
    private String webUrl;
    private String smallFileUrl;
    private String largeFileUrl;
    private String originalFileUrl;

    public Post() {
        // no arg constructor required by Realm
    }

    public Post(PostJson postJson) {
        id = postJson.id;
        width = postJson.width;
        height = postJson.height;
        fileName = postJson.id + "." + postJson.fileExt;
        tagCount = postJson.tagCount;
        tagStringArtist = postJson.tagStringArtist;
        tagStringCharacter = postJson.tagStringCharacter;
        tagStringCopyright = postJson.tagStringCopyright;
        tagStringGeneral = postJson.tagStringGeneral;
        tagStringMeta = postJson.tagStringMeta;
        hasLarge = postJson.hasLarge;
        webUrl = BASE_URL + "/posts/" + postJson.id;
        smallFileUrl = BASE_URL + postJson.previewFileUrl;
        largeFileUrl = BASE_URL + postJson.largeFileUrl;
        originalFileUrl = BASE_URL + postJson.fileUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public Uri getWebUri() {
        return Uri.parse(getWebUrl());
    }

    public String getFileName() {
        return fileName;
    }

    public int getTagCount() {
        return tagCount;
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

    public String getTagStringMeta() {
        return tagStringMeta;
    }

    public boolean hasLargeFileUrl() {
        return hasLarge;
    }

    public String getSmallFileUrl() {
        return smallFileUrl;
    }

    public String getLargeFileUrl() {
        return largeFileUrl;
    }

    public String getOriginalFileUrl() {
        return originalFileUrl;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else {
            Post x = (Post) obj;
            return id == x.getId();
        }
    }
}
