package shiro.am.i.chesto.model;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Shiro on 7/29/2016.
 */
public interface Danbooru {

    @GET("posts.json?limit=100")
    Observable<List<PostJson>> getPosts(@Query("tags") String tags, @Query("page") int page);

    @GET("tags.json?search[order]=count&search[hide_empty]=yes")
    Observable<List<TagJson>> searchTags(@Query("search[name_matches]") String tags);
}
