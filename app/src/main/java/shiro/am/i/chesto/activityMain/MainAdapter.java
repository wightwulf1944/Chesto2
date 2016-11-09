package shiro.am.i.chesto.activityMain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import shiro.am.i.chesto.PostStore;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.activityPost.PostActivity;

/**
 * Created by Shiro on 8/4/2016.
 */
final class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private final AppCompatActivity mParent;

    MainAdapter(AppCompatActivity parent) {
        mParent = parent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new ImageView(mParent));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(mParent)
                .load(PostStore.get(position).getPreviewFileUrl())
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_broken)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return PostStore.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imageView;

        ViewHolder(ImageView v) {
            super(v);
            imageView = v;
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mParent, PostActivity.class);
            intent.putExtra("default", getAdapterPosition());
            mParent.startActivityForResult(intent, 0);
        }
    }
}
