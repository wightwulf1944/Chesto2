package shiro.am.i.chesto.model;

import com.squareup.moshi.Json;

/**
 * Created by Shiro on 11/28/2017.
 * Java representation of json response for Moshi consumption
 */

public class PostJson {

    @Json(name = "id") int id;
    @Json(name = "image_width") int width;
    @Json(name = "image_height") int height;
    @Json(name = "file_ext") String fileExt;
    @Json(name = "is_pending") boolean isPending;
    @Json(name = "is_deleted") boolean isDeleted;
    @Json(name = "is_banned") boolean isBanned;
    @Json(name = "is_flagged") boolean isFlagged;
    @Json(name = "rating") String rating;
    @Json(name = "tag_count") int tagCount;
    @Json(name = "tag_count_artist") int tagCountArtist;
    @Json(name = "tag_count_character") int tagCountCharacter;
    @Json(name = "tag_count_copyright") int tagCountCopyright;
    @Json(name = "tag_count_general") int tagCountGeneral;
    @Json(name = "tag_count_meta") int tagCountMeta;
    @Json(name = "tag_string_artist") String tagStringArtist;
    @Json(name = "tag_string_character") String tagStringCharacter;
    @Json(name = "tag_string_copyright") String tagStringCopyright;
    @Json(name = "tag_string_general") String tagStringGeneral;
    @Json(name = "tag_string_meta") String tagStringMeta;
    @Json(name = "has_large") boolean hasLarge;
    @Json(name = "preview_file_url") String previewFileUrl;
    @Json(name = "large_file_url") String largeFileUrl;
    @Json(name = "file_url") String fileUrl;

    public boolean hasImageUrls() {
        return previewFileUrl != null && largeFileUrl != null;
    }
}
