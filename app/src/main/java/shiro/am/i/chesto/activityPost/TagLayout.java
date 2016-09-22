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
 * Created by Shiro on 9/1/2016.
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

        addCategory("Copyrights: ", R.layout.item_tag_copyright, post.getTagStringCopyright());
        addCategory("Characters: ", R.layout.item_tag_character, post.getTagStringCharacter());
        addCategory("Artist:", R.layout.item_tag_artist, post.getTagStringArtist());
        addCategory("Tags:", R.layout.item_tag_general, post.getTagStringGeneral());
    }

    private void addCategory(String label, int layoutId, String tags) {
        if (tags.isEmpty()) {
            return;
        }

        addView(label, R.layout.item_tag_label);
        addTags(tags, layoutId);
    }

    private void addTags(String tags, int layoutId) {
        for (final String tag : tags.split(" ")) {
            addView(tag, layoutId).setOnClickListener(new OnClickListener() {
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

    private TextView addView(String text, int layoutId) {
        TextView textView = (TextView) inflater.inflate(layoutId, this, false);
        textView.setText(text);
        addView(textView);
        return textView;
    }
}
