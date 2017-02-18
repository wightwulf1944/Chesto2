package shiro.am.i.chesto.retrofitDanbooru;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Shiro on 7/29/2016.
 */
public interface Danbooru {

    @GET("posts.json?limit=100")
    Observable<List<Post>> getPosts(@Query("tags") String tags, @Query("page") int page);

    @GET("tags.json?search[order]=count&search[hide_empty]=yes")
    Observable<List<Tag>> searchTags(@Query("search[name_matches]") String tags);
}
