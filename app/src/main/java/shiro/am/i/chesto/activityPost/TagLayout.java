package shiro.am.i.chesto.activityPost;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import shiro.am.i.chesto.PostList;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.activityMain.MainActivity;
import shiro.am.i.chesto.retrofitDanbooru.Post;

/**
 * Created by UGZ on 9/1/2016.
 * TODO: do not split from here, save string array in Post object
 */
final class TagLayout extends FlowLayout {

    private static final PostList postList = PostList.getInstance();
    private final Context mContext = getContext();
    private final LayoutInflater inflater = LayoutInflater.from(mContext);

    public TagLayout(Context context) {
        super(context);
    }

    public TagLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TagLayout(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    void setCurrentPost(final int postIndex) {
        final Post post = postList.get(postIndex);
        removeAllViews();

        addLabel("Copyrights:");
        addTags(post.getTagStringCopyright(), R.layout.item_tag_copyright);
        addLabel("Characters:");
        addTags(post.getTagStringCharacter(), R.layout.item_tag_character);
        addLabel("Artist:");
        addTags(post.getTagStringArtist(), R.layout.item_tag_artist);
        addLabel("Tags:");
        addTags(post.getTagStringGeneral(), R.layout.item_tag_general);
    }

    private void addLabel(String label) {
        add(label, R.layout.item_tag_label);
    }

    private void addTags(String tags, int layoutId) {
        for (final String tag : tags.split(" ")) {
            add(tag, layoutId).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(
                            new Intent(Intent.ACTION_SEARCH,
                                    Uri.parse(tag),
                                    mContext,
                                    MainActivity.class)
                    );
                }
            });
        }
    }

    private TextView add(String text, int layoutId) {
        TextView textView = (TextView) inflater.inflate(layoutId, this, false);
        textView.setText(text);
        addView(textView);
        return textView;
    }
}
