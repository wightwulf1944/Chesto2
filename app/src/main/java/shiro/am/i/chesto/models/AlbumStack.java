package shiro.am.i.chesto.models;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by Shiro on 3/31/2017.
 * maintains an instance of the global album stack
 */

public final class AlbumStack {

    private static final Deque<PostAlbum> stack = new LinkedList<>();

    public static void push(PostAlbum postAlbum) {
        stack.addFirst(postAlbum);
    }

    public static void pop() {
        stack.pollFirst();
    }

    public static PostAlbum getTop() {
        return stack.getFirst();
    }
}
