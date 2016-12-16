package shiro.am.i.chesto.activityPost;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import shiro.am.i.chesto.R;
import shiro.am.i.chesto.activityMain.MainActivity;
import shiro.am.i.chesto.retrofitDanbooru.Post;

/**
 * Created by Shiro on 16/12/2016.
 */

final class TagLayoutDecorator {

    private final FlowLayout mLayout;
    private final Context mContext;
    private final LayoutInflater inflater;

    TagLayoutDecorator(FlowLayout layout) {
        mLayout = layout;
        mContext = mLayout.getContext();
        inflater = LayoutInflater.from(mContext);
    }

    void setPost(Post post) {
        mLayout.removeAllViews();
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
        
        for (final String tag : tags.split(" ")) {
            addView(tag, layoutId).setOnClickListener(view ->
                    mContext.startActivity(
                            new Intent(
                                    Intent.ACTION_SEARCH,
                                    Uri.parse(tag),
                                    mContext,
                                    MainActivity.class
                            )
                    )
            );
        }
    }

    private TextView addView(String text, int layoutId) {
        TextView textView = (TextView) inflater.inflate(layoutId, mLayout, false);
        textView.setText(text);
        mLayout.addView(textView);
        return textView;
    }
}
