package shiro.am.i.chesto.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Shiro on 7/29/2016.
 */
public class Tag extends RealmObject {
    @PrimaryKey
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("post_count")
    private int postCount;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPostCount() {
        return postCount;
    }

    public String getPostCountStr() {
        String postCountStr = String.valueOf(postCount);
        if (postCount >= 1_000) {
            postCountStr = postCountStr.charAt(0) + "." + postCountStr.charAt(1);
            if (postCount < 1_000_000) {
                return postCountStr + "k";
            } else {
                return postCountStr + "m";
            }
        } else {
            return postCountStr;
        }
    }
}
