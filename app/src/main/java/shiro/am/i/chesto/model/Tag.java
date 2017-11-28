package shiro.am.i.chesto.model;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Shiro on 7/29/2016.
 */
public class Tag extends RealmObject {

    @PrimaryKey private int id;

    @Index private int postCount;

    private String name;

    private String postCountStr;

    public Tag() {
        // no arg constructor required by Realm
    }

    public Tag(TagJson tagJson) {
        id = tagJson.id;
        postCount = tagJson.postCount;
        name = tagJson.name;
        postCountStr = String.valueOf(postCount);

        if (postCount >= 1_000) {
            postCountStr = postCountStr.charAt(0) + "." + postCountStr.charAt(1);
            if (postCount < 1_000_000) {
                postCountStr += "k";
            } else {
                postCountStr += "m";
            }
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPostCountStr() {
        return postCountStr;
    }
}
