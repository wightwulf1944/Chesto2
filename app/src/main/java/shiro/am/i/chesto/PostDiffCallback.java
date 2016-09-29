package shiro.am.i.chesto;

import android.support.v7.util.DiffUtil;

import java.util.List;

import shiro.am.i.chesto.retrofitDanbooru.Post;

/**
 * Created by UGZ on 9/22/2016.
 */

public final class PostDiffCallback extends DiffUtil.Callback {

    private List<Post> oldPostList;
    private List<Post> newPostList;

    public PostDiffCallback(List<Post> before, List<Post> after) {
        oldPostList = before;
        newPostList = after;
    }

    @Override
    public int getOldListSize() {
        return oldPostList.size();
    }

    @Override
    public int getNewListSize() {
        return newPostList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        final int oldPostId = oldPostList.get(oldItemPosition).getId();
        final int newPostId = newPostList.get(newItemPosition).getId();
        return oldPostId == newPostId;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }
}
