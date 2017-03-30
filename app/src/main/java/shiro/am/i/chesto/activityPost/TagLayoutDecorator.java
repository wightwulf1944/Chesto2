package shiro.am.i.chesto.activitypost;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import shiro.am.i.chesto.R;
import shiro.am.i.chesto.activitymain.MainActivity;
import shiro.am.i.chesto.models.Post;

/**
 * Created by Shiro on 16/12/2016.
 */

final class TagLayoutDecorator {

    private final FlexboxLayout mLayout;
    private final Context mContext;
    private final LayoutInflater inflater;
    private final int copyrightTextColor;
    private final int characterTextColor;
    private final int artistTextColor;
    private final int generalTextColor;

    TagLayoutDecorator(FlexboxLayout layout) {
        mLayout = layout;
        mContext = mLayout.getContext();
        inflater = LayoutInflater.from(mContext);
        copyrightTextColor = ContextCompat.getColor(mContext, R.color.tag_text_color_copyright);
        characterTextColor = ContextCompat.getColor(mContext, R.color.tag_text_color_character);
        artistTextColor = ContextCompat.getColor(mContext, R.color.tag_text_color_artist);
        generalTextColor = ContextCompat.getColor(mContext, R.color.tag_text_color_general);
    }

    void setPost(Post post) {
        mLayout.removeAllViews();
        addCategory("Copyrights: ", copyrightTextColor, post.getTagStringCopyright());
        addCategory("Characters: ", characterTextColor, post.getTagStringCharacter());
        addCategory("Artist:", artistTextColor, post.getTagStringArtist());
        addCategory("Tags:", generalTextColor, post.getTagStringGeneral());
    }

    private void addCategory(String labelString, int textColor, String tags) {
        if (tags.isEmpty()) {
            return;
        }

        addTextView(labelString, R.layout.item_label);

        for (String tagString : tags.split(" ")) {
            TextView tagTextView = addTextView(tagString, R.layout.item_tag);
            tagTextView.setTextColor(textColor);
            tagTextView.setOnClickListener(v -> mContext.startActivity(
                    new Intent(
                            Intent.ACTION_SEARCH,
                            Uri.parse(tagString),
                            mContext,
                            MainActivity.class
                    )
            ));
        }
    }

    private TextView addTextView(String text, int layoutId) {
        TextView textView = (TextView) inflater.inflate(layoutId, mLayout, false);
        textView.setText(text);
        mLayout.addView(textView);
        return textView;
    }
}
