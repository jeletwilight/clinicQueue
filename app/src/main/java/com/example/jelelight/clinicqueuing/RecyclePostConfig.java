package com.example.jelelight.clinicqueuing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclePostConfig {

    private Context mContext;
    private RecyclePostConfig.PostAdapter mPostAdapter;

    public void setConfig(RecyclerView recyclerView, Context context, List<Post> posts, List<String> keys){
        mContext = context;
        mPostAdapter = new PostAdapter(posts,keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mPostAdapter);
    }

    class PostItemView extends RecyclerView.ViewHolder {

        private String key;

        private TextView mKey;
        private TextView mAuthor;
        private TextView mText;
        private TextView mTime ;

        public PostItemView(final ViewGroup parent){
            super(LayoutInflater.from(mContext).inflate(R.layout.post_list_item,parent,false));
            mKey = (TextView) itemView.findViewById(R.id.key_txtview);
            mAuthor = (TextView) itemView.findViewById(R.id.author_txtview);
            mText = (TextView) itemView.findViewById(R.id.text_txtview);
            mTime = (TextView) itemView.findViewById(R.id.time_textView);
        }

        public void bind(Post post,String key){
            mKey.setText(key);
            mAuthor.setText(post.getAuthor());
            mText.setText(post.getText());
            mTime.setText(post.getTime());
            this.key = key;
        }

    }

    class PostAdapter extends RecyclerView.Adapter<RecyclePostConfig.PostItemView>{
        private List<Post> mPostList;
        private List<String> mKeys;

        public PostAdapter(List<Post> mPostList, List<String> mKeys) {
            this.mPostList = mPostList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public RecyclePostConfig.PostItemView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new RecyclePostConfig.PostItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull PostItemView postItemView, int i) {
            postItemView.bind(mPostList.get(i), mKeys.get(i));
        }

        @Override
        public int getItemCount() {
            return mPostList.size();
        }
    }
}
