package shiro.am.i.chesto.engine;

import java.util.ArrayList;
import java.util.HashMap;

import shiro.am.i.chesto.model.Post;

/**
 * Created by Subaru Tashiro on 6/13/2017.
 */

final class SearchResults {

    private final ArrayList<Post> list = new ArrayList<>();
    private final HashMap<Post, Integer> map = new HashMap<>();

    void ensureCapacity(int capacity) {
        list.ensureCapacity(capacity);
    }

    int indexOf(Post post) {
        if (map.containsKey(post)) {
            return map.get(post);
        } else {
            return -1;
        }
    }

    void add(Post post) {
        map.put(post, list.size());
        list.add(post);
    }

    void set(int index, Post post) {
        map.remove(post);
        map.put(post, index);
        list.set(index, post);
    }

    Post get(int index) {
        return list.get(index);
    }

    void clear() {
        list.clear();
        map.clear();
    }

    int size() {
        return list.size();
    }
}
