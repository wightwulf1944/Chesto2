package shiro.am.i.chesto.activitymain;

import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;

import shiro.am.i.chesto.model.Post;
import shiro.am.i.chesto.viewmodel.PostAlbum;

/**
 * Created by Shiro on 5/4/2017.
 */

final class RatioDelegate implements GreedoLayoutSizeCalculator.SizeCalculatorDelegate {

    private static final double RATIO_MIN = 0.5;
    private static final double RATIO_MAX = 5;
    private static final double RATIO_DEFAULT = 1.0;

    private final PostAlbum postAlbum;

    RatioDelegate(PostAlbum postAlbum) {
        this.postAlbum = postAlbum;
    }

    @Override
    public double aspectRatioForIndex(int i) {
        if (i >= postAlbum.size()) {
            return RATIO_DEFAULT;
        } else {
            Post post = postAlbum.get(i);
            double ratio = (double) post.getWidth() / post.getHeight();

            if (ratio < RATIO_MIN) return RATIO_MIN;
            else if (ratio > RATIO_MAX) return RATIO_MAX;
            else return ratio;
        }
    }
}
