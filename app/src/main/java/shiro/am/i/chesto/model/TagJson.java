package shiro.am.i.chesto.model;

import com.squareup.moshi.Json;

/**
 * Created by Shiro on 11/28/2017.
 */

public class TagJson {

    @Json(name = "id") int id;
    @Json(name = "name") String name;
    @Json(name = "post_count") int postCount;
    @Json(name = "category") int category;
}
